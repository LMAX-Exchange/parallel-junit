/*
 * Copyright 2012 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lmax.ant.paralleljunit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.launch.AntMain;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask.SummaryAttribute;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.types.Assertions;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PropertySet;

import com.lmax.ant.paralleljunit.remote.TestSpecificationFactory;
import com.lmax.ant.paralleljunit.remote.controller.RemoteTestRunnerControllerFactory;
import com.lmax.ant.paralleljunit.remote.controller.RemoteTestRunnerProcessFactory;
import com.lmax.ant.paralleljunit.remote.process.RemoteTestRunner;
import com.lmax.ant.paralleljunit.util.DaemonThreadFactory;
import com.lmax.ant.paralleljunit.util.io.EOFAwareInputStreamFactory;
import com.lmax.ant.paralleljunit.util.io.ExecuteStreamHandlerFactory;
import com.lmax.ant.paralleljunit.util.io.PumpStreamHandlerFactory;
import com.lmax.ant.paralleljunit.util.io.SynchronisedOutputStream;
import com.lmax.ant.paralleljunit.util.net.ConnectionEstablisherFactory;
import com.lmax.ant.paralleljunit.util.process.ExecuteWatchdogFactory;
import com.lmax.ant.paralleljunit.util.process.ManagedProcessFactory;
import com.lmax.ant.paralleljunit.util.process.ProcessBuilderFactory;
import com.lmax.ant.paralleljunit.util.process.ProcessDestroyer;

import static java.lang.Math.max;
import static java.util.Arrays.asList;
import static org.apache.tools.ant.util.LoaderUtils.getClassSource;

public class ParallelJUnitTask extends Task implements ParallelJUnitTaskConfig
{
    private boolean haltOnError = false;
    private String errorProperty;
    private boolean haltOnFailure = false;
    private String failureProperty;
    private boolean filterTrace = true;
    private long timeout = NO_TIMEOUT;
    private File dir;
    private boolean newEnvironment = false;
    private boolean logFailedTests = true;
    private boolean enableTestListenerEvents = false;
    private boolean shuffle = false;
    private final int availableProcessors = Runtime.getRuntime().availableProcessors();
    private int threads = availableProcessors;

    private CommandlineJava commandLine = new CommandlineJava();
    private Environment environment = new Environment();
    private final TestResultCoordinator testResultCoordinator = new TestResultCoordinator();

    private WorkerCoordinator workerCoordinator =
            new WorkerCoordinator(
                    new WorkerFactory(
                            new RemoteTestRunnerControllerFactory(
                                    new ManagedProcessFactory(
                                            new RemoteTestRunnerProcessFactory(
                                                    new ProcessBuilderFactory()),
                                            new ProcessDestroyer(),
                                            new ExecuteStreamHandlerFactory(new PumpStreamHandlerFactory(
                                                    new SynchronisedOutputStream(new LogOutputStream(this, Project.MSG_INFO)), //TODO novakd JUnitLogOutputStream
                                                    new SynchronisedOutputStream(new LogOutputStream(this, Project.MSG_WARN)))),
                                            new ExecuteWatchdogFactory(),
                                            new EOFAwareInputStreamFactory(),
                                            Executors.newCachedThreadPool()),
                                    Executors.newCachedThreadPool(new DaemonThreadFactory()), //TODO novakd does this have to be a deamon threadpool???
                                    ServerSocketFactory.getDefault(),
                                    new ConnectionEstablisherFactory(),
                                    new TestSpecificationFactory()),
                            testResultCoordinator),
                    Executors.newCachedThreadPool(),
                    testResultCoordinator);

    private BatchTestFactory batchTestFactory = new BatchTestFactory();

    private final NumberParser numberParser = new NumberParser();
    private ThreadsParser threadsParser = new ThreadsParser(new PercentileParser(numberParser, availableProcessors),
                                                            new AdditiveParser(numberParser, availableProcessors),
                                                            numberParser);

    private Collection<DelegatingBatchTest> batchTests = new LinkedList<DelegatingBatchTest>();
    private final List<FormatterElement> formatters = new LinkedList<FormatterElement>();
    private Queue<JUnitTest> testQueue;

    public ParallelJUnitTask()
    {
    }

    ParallelJUnitTask(final CommandlineJava commandLine, final Environment environment, final WorkerCoordinator workerCoordinator, final BatchTestFactory batchTestFactory,
                      final ThreadsParser threadsParser, final Collection<DelegatingBatchTest> batchTests)
    {
        this.commandLine = commandLine;
        this.environment = environment;
        this.workerCoordinator = workerCoordinator;
        this.batchTestFactory = batchTestFactory;
        this.threadsParser = threadsParser;
        this.batchTests = batchTests;
    }

    @Override
    public void init() throws BuildException
    {
        super.init();

        final Path remoteTestRunnerClasses = commandLine.createClasspath(getProject()).createPath();
        remoteTestRunnerClasses.setLocation(getClassSource(RemoteTestRunner.class));
        remoteTestRunnerClasses.setLocation(getClassSource(JUnitTest.class));
        remoteTestRunnerClasses.setLocation(getClassSource(AntMain.class));
        remoteTestRunnerClasses.setLocation(getClassSource(Task.class));
    }

    public void setPrintSummary(final SummaryAttribute printSummary)
    {
        if (printSummary.asBoolean())
        {
            final String prefix = printSummary.getValue().equalsIgnoreCase("withoutanderr") ? "OutErr" : "";
            commandLine.createArgument().setValue("formatter=org.apache.tools.ant.taskdefs.optional.junit." + prefix + "SummaryJUnitResultFormatter");
        }
    }

    public void setHaltOnError(final boolean haltOnError)
    {
        this.haltOnError = haltOnError;
    }

    public void setErrorProperty(final String errorProperty)
    {
        this.errorProperty = errorProperty;
    }

    public void setHaltOnFailure(final boolean haltOnFailure)
    {
        this.haltOnFailure = haltOnFailure;
    }

    public void setFailureProperty(final String failureProperty)
    {
        this.failureProperty = failureProperty;
    }

    public void setFilterTrace(final boolean filterTrace)
    {
        this.filterTrace = filterTrace;
    }

    public void setTimeout(final int timeout)
    {
        this.timeout = timeout;
    }

    public void setMaxMemory(final String maxMemory)
    {
        commandLine.setMaxmemory(maxMemory);
    }

    public void setJvm(final String jvm)
    {
        commandLine.setVm(jvm);
    }

    public void setDir(final File dir)
    {
        this.dir = dir;
    }

    public void setNewEnvironment(final boolean newEnvironment)
    {
        this.newEnvironment = newEnvironment;
    }

    public void setShowOutput(final boolean showOutput)
    {
        commandLine.createArgument().setValue("showoutput=" + showOutput);
    }

    public void setCloneVm(final boolean cloneVm)
    {
        commandLine.setCloneVm(cloneVm);
    }

    public void setLogFailedTests(final boolean logFailedTests)
    {
        if (logFailedTests)
        {
            commandLine.createArgument().setValue("logfailedtests=true");
        }
        this.logFailedTests = logFailedTests;
    }

    public void setEnableTestListenerEvents(final boolean enableTestListenerEvents)
    {
        this.enableTestListenerEvents = enableTestListenerEvents;
    }

    public void setShuffle(final boolean shuffle)
    {
        this.shuffle = shuffle;
    }

    public void setThreads(final String threads)
    {
        this.threads = max(1, threadsParser.parse(threads));
    }

    public DelegatingBatchTest createBatchTest()
    {
        final DelegatingBatchTest batchTest = batchTestFactory.createBatchTest(getProject());
        batchTest.setFiltertrace(filterTrace);
        batchTest.setHaltonerror(haltOnError);
        batchTest.setErrorProperty(errorProperty);
        batchTest.setHaltonfailure(haltOnFailure);
        batchTest.setFailureProperty(failureProperty);
        for (final FormatterElement formatter : formatters)
        {
            batchTest.addFormatter(formatter);
        }
        batchTests.add(batchTest);
        return batchTest;
    }

    public void addFormatter(final FormatterElement formatter)
    {
        formatters.add(formatter);
    }

    public Commandline.Argument createJvmArg()
    {
        return commandLine.createVmArgument();
    }

    public void addConfiguredSysProperty(final Environment.Variable sysProp)
    {
        commandLine.addSysproperty(sysProp);
    }


    public void addSysPropertySet(final PropertySet propertySet)
    {
        commandLine.addSyspropertyset(propertySet);
    }

    public void addEnv(final Environment.Variable variable)
    {
        environment.addVariable(variable);
    }

    public Path createBootClassPath()
    {
        return commandLine.createBootclasspath(getProject()).createPath();
    }

    public Path createClasspath()
    {
        return commandLine.createClasspath(getProject()).createPath();
    }

    public void addAssertions(final Assertions assertions)
    {
        if (commandLine.getAssertions() != null)
        {
            throw new BuildException("Only one assertion declaration is allowed");
        }
        commandLine.setAssertions(assertions);
    }

    @Override
    public void execute() throws BuildException
    {
        populateTestQueue();
        workerCoordinator.execute(this);
    }

    public Queue<JUnitTest> getTestQueue()
    {
        return testQueue;
    }

    public List<String> getCommand(final Class<?> mainClass, final int workerId, final int serverPort)
    {
        try
        {
            final CommandlineJava clonedCommandLine = (CommandlineJava)commandLine.clone();

            clonedCommandLine.setClassname(mainClass.getCanonicalName());
            clonedCommandLine.createArgument().setValue("serverPort=" + serverPort);
            clonedCommandLine.createArgument().setValue("workerId=" + workerId);

            final String enableListenerEvents = getProject().getProperty("ant.junit.enabletestlistenerevents");
            if (enableListenerEvents != null)
            {
                if (Project.toBoolean(enableListenerEvents))
                {
                    clonedCommandLine.createArgument().setValue("logtestlistenerevents=true");
                }
            }
            else if (enableTestListenerEvents)
            {
                clonedCommandLine.createArgument().setValue("logtestlistenerevents=true");
            }

            return new ArrayList<String>(asList(clonedCommandLine.getCommandline()));
        }
        catch (final CloneNotSupportedException e)
        {
            // Will not happen - honestly. CommandlineJava is cloneable.
            throw new BuildException("Error cloning commandLine [" + commandLine + "]", e);
        }
    }

    public File getDirectory()
    {
        return dir;
    }

    public boolean isNewEnvironment()
    {
        return newEnvironment;
    }

    public Map<String, String> getEnvironment()
    {
        if (environment.getVariables() == null)
        {
            return Collections.emptyMap();
        }

        final Map<String, String> environmentMap = new HashMap<String, String>();

        for (final String envVariable : environment.getVariables())
        {
            final int equalsSignIndex = envVariable.indexOf('=');
            // Silently ignore envVariable lacking the required `='.
            if (equalsSignIndex != -1)
            {
                environmentMap.put(envVariable.substring(0, equalsSignIndex), envVariable.substring(equalsSignIndex + 1));
            }
        }
        return environmentMap;
    }

    public long getTimeout()
    {
        return timeout;
    }

    public boolean isLogFailedTests()
    {
        return logFailedTests;
    }

    public ProjectComponent getProjectComponent()
    {
        return this;
    }

    public int getThreads()
    {
        return threads;
    }

    private void populateTestQueue()
    {
        final List<JUnitTest> testList = new LinkedList<JUnitTest>();
        for (final DelegatingBatchTest batchTest : batchTests)
        {
            final Enumeration<JUnitTest> enumerationOfTests = batchTest.elements();
            while (enumerationOfTests.hasMoreElements())
            {
                testList.add(enumerationOfTests.nextElement());
            }
        }

        if (shuffle)
        {
            Collections.shuffle(testList);
        }
        testQueue = new ArrayBlockingQueue<JUnitTest>(testList.size() + 1, false, testList);
    }
}
