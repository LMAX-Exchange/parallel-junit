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
