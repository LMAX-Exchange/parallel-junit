package com.lmax.ant.taskdefs.optional.junit;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.lmax.ant.paralleljunit.ParallelJUnitTaskConfig;
import com.lmax.ant.paralleljunit.remote.controller.RemoteTestRunnerProcessFactory;
import com.lmax.ant.paralleljunit.remote.process.RemoteTestRunner;
import com.lmax.ant.paralleljunit.util.process.DelegatingProcessBuilder;
import com.lmax.ant.paralleljunit.util.process.ProcessBuilderFactory;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


import static java.util.Arrays.asList;

@RunWith(JMock.class)
public class ProcessFactoryTest
{
    public static final List<String> COMMAND = asList("java", "-cp.", "ForkedJvmTestExecutor");
    private static final int SERVER_PORT = new Random().nextInt(9000);
    private static final int WORKER_ID = 29;

    private final Mockery mockery = new Mockery();

    private RemoteTestRunnerProcessFactory remoteTestRunnerProcessFactory;
    private ProcessBuilderFactory processBuilderFactory;
    private ParallelJUnitTaskConfig config;
    private DelegatingProcessBuilder processBuilder;
    private Map<String, String> environment;
    private Map<String, String> configuredEnvironment;
    private Process process;

    @Before
    public void setUp() throws Exception
    {
        mockery.setImposteriser(ClassImposteriser.INSTANCE);

        processBuilderFactory = mockery.mock(ProcessBuilderFactory.class);
        remoteTestRunnerProcessFactory = new RemoteTestRunnerProcessFactory(processBuilderFactory);

        config = mockery.mock(ParallelJUnitTaskConfig.class);
        processBuilder = mockery.mock(DelegatingProcessBuilder.class);
        process = mockery.mock(Process.class);
        environment = new HashMap<String, String>();
        configuredEnvironment = new HashMap<String, String>();
    }

    @Test
    public void shouldCreateAConfiguredProcess() throws Exception
    {
        final Sequence sequence = mockery.sequence("sequence");
        mockery.checking(new Expectations()
        {
            {
                one(config).getCommand(RemoteTestRunner.class, WORKER_ID, SERVER_PORT);
                inSequence(sequence);
                will(returnValue(COMMAND));

                one(processBuilderFactory).createProcessBuilder(COMMAND);
                inSequence(sequence);
                will(returnValue(processBuilder));

                one(config).getDirectory();
                inSequence(sequence);
                will(returnValue(null));

//                one(processBuilder).environment();
//                inSequence(sequence);
//                will(returnValue(environment));
//
//                one(config).isNewEnvironment();
//                inSequence(sequence);
//                will(returnValue(false));
//
//                one(config).getEnvironment();
//                inSequence(sequence);
//                will(returnValue(configuredEnvironment));

                one(processBuilder).start();
                inSequence(sequence);
                will(returnValue(process));
            }
        });

        assertThat(remoteTestRunnerProcessFactory.createForkedProcess(WORKER_ID, config, SERVER_PORT), is(notNullValue()));
    }

    @Test
    public void shouldSetWorkingDirectoryWhenItIsConfigured() throws Exception
    {
        final File workingDir = new File("/tmp/working");
        mockery.checking(new Expectations()
        {
            {
                one(config).getDirectory();
                will(returnValue(workingDir));

                one(processBuilder).directory(workingDir);
                will(returnValue(processBuilder));

                allowing(config).getCommand(with(any(Class.class)), with(any(int.class)), with(any(int.class)));
                will(returnValue(COMMAND));

                allowing(processBuilderFactory).createProcessBuilder(COMMAND);
                will(returnValue(processBuilder));

                allowing(processBuilder).environment();
                will(returnValue(environment));

                allowing(config).isNewEnvironment();
                will(returnValue(false));

                allowing(config).getEnvironment();
                will(returnValue(configuredEnvironment));

                allowing(processBuilder).start();
                will(returnValue(process));
            }
        });

        remoteTestRunnerProcessFactory.createForkedProcess(WORKER_ID, config, SERVER_PORT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdateProcessEnvironment() throws Exception
    {
        final Map<String, String> processEnvironment = new HashMap<String, String>(environment);
        mockery.checking(new Expectations()
        {
            {
//                one(processBuilder).environment();
//                will(returnValue(processEnvironment));
//
//                one(config).isNewEnvironment();
//                will(returnValue(false));
//
//                one(config).getEnvironment();
//                will(returnValue(configuredEnvironment));

                allowing(processBuilderFactory).createProcessBuilder(with(any(List.class)));
                will(returnValue(processBuilder));

                allowing(config).getCommand(with(any(Class.class)), with(any(int.class)), with(any(int.class)));
                will(returnValue(COMMAND));

                allowing(config).getDirectory();
                will(returnValue(null));

                allowing(processBuilder).start();
                will(returnValue(process));
            }
        });

        remoteTestRunnerProcessFactory.createForkedProcess(WORKER_ID, config, SERVER_PORT);

        final Map<String, String> expectedEnvironment = new HashMap<String, String>(environment);
        expectedEnvironment.putAll(configuredEnvironment);

        assertThat(processEnvironment, is(equalTo(expectedEnvironment)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldOverwriteProcessEnvironmentWhenNewEnvironmentConfigured() throws Exception
    {
        final Map<String, String> processEnvironment = new HashMap<String, String>(environment);
        mockery.checking(new Expectations()
        {
            {
//                one(processBuilder).environment();
//                will(returnValue(processEnvironment));
//
//                one(config).isNewEnvironment();
//                will(returnValue(true));
//
//                one(config).getEnvironment();
//                will(returnValue(configuredEnvironment));

                allowing(processBuilderFactory).createProcessBuilder(with(any(List.class)));
                will(returnValue(processBuilder));

                allowing(config).getCommand(with(any(Class.class)), with(any(int.class)), with(any(int.class)));
                will(returnValue(COMMAND));

                allowing(config).getDirectory();
                will(returnValue(null));

                allowing(processBuilder).start();
                will(returnValue(process));
            }
        });

        remoteTestRunnerProcessFactory.createForkedProcess(WORKER_ID, config, SERVER_PORT);

        assertThat(processEnvironment, is(equalTo(configuredEnvironment)));
    }
}
