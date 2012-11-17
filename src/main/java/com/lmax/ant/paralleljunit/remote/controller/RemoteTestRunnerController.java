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

import java.io.IOException;

import com.lmax.ant.paralleljunit.remote.TestResult;
import com.lmax.ant.paralleljunit.remote.TestSpecification;
import com.lmax.ant.paralleljunit.remote.TestSpecificationFactory;
import com.lmax.ant.paralleljunit.remote.process.RemoteTestRunnerCommand;
import com.lmax.ant.paralleljunit.util.net.SocketConnection;
import com.lmax.ant.paralleljunit.util.process.ManagedProcess;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;

public class RemoteTestRunnerController
{
    private final ManagedProcess jvmProcess;
    private final SocketConnection socketConnection;
    private final TestSpecificationFactory testSpecificationFactory;

    public RemoteTestRunnerController(final ManagedProcess jvmProcess, final SocketConnection socketConnection, final TestSpecificationFactory testSpecificationFactory)
    {
        this.jvmProcess = jvmProcess;
        this.socketConnection = socketConnection;
        this.testSpecificationFactory = testSpecificationFactory;
    }

    public TestResult execute(final JUnitTest test)
    {
        final TestSpecification testSpecification = testSpecificationFactory.create(test);

        try
        {
            socketConnection.writeObject(RemoteTestRunnerCommand.RUN_TEST);
            socketConnection.writeObject(testSpecification);
            return socketConnection.readObject();
        }
        catch (final IOException e)
        {
            return TestResult.CRASHED;
        }
        catch (final ClassNotFoundException e)
        {
            // Not going to happen - honestly.
            throw new BuildException("Error communicating with test runner VM", e);
        }
    }

    public void close()
    {
        try
        {
            socketConnection.writeObject(RemoteTestRunnerCommand.EXIT);
            socketConnection.close();
        }
        catch (final IOException e)
        {
            throw new BuildException("Error terminating communication to remote process.", e);
        }
        finally
        {
            jvmProcess.close();
        }
    }
}
