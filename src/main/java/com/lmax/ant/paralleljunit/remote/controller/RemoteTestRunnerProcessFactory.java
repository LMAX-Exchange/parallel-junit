package com.lmax.ant.paralleljunit.remote.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.lmax.ant.paralleljunit.ParallelJUnitTaskConfig;
import com.lmax.ant.paralleljunit.remote.process.RemoteTestRunner;
import com.lmax.ant.paralleljunit.util.process.DelegatingProcessBuilder;
import com.lmax.ant.paralleljunit.util.process.ProcessBuilderFactory;

import org.apache.tools.ant.BuildException;

public class RemoteTestRunnerProcessFactory
{
    private final ProcessBuilderFactory processBuilderFactory;

    public RemoteTestRunnerProcessFactory(final ProcessBuilderFactory processBuilderFactory)
    {
        this.processBuilderFactory = processBuilderFactory;
    }

    public Process createForkedProcess(final int workerId, final ParallelJUnitTaskConfig config, final int serverPort)
    {
        final List<String> jvmCommand = config.getCommand(RemoteTestRunner.class, workerId, serverPort);

        final DelegatingProcessBuilder processBuilder = processBuilderFactory.createProcessBuilder(jvmCommand);

        final File workingDirectory = config.getDirectory();
        if (workingDirectory != null)
        {
            processBuilder.directory(workingDirectory);
        }

        final Map<String, String> environment = processBuilder.environment();

        if (config.isNewEnvironment())
        {
            environment.clear();
        }

        environment.putAll(config.getEnvironment());

        try
        {
            return processBuilder.start();
        }
        catch (final IOException e)
        {
            throw new BuildException("Error starting forked process.", e);
        }
    }
}
