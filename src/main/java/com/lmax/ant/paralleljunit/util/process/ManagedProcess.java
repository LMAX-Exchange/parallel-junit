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

package com.lmax.ant.paralleljunit.util.process;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import com.lmax.ant.paralleljunit.util.io.EOFAwareInputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;


import static java.util.concurrent.TimeUnit.SECONDS;

public class ManagedProcess
{
    private final Process process;
    private final ProcessDestroyer processDestroyer;
    private final ExecuteStreamHandler streamHandler;
    private final ExecuteWatchdog watchdog;
    private final Collection<EOFAwareInputStream> processStreams;
    private final ExecutorService executorService;

    public ManagedProcess(final Process process, final ProcessDestroyer processDestroyer, final ExecuteStreamHandler streamHandler, final ExecuteWatchdog watchdog,
                          final Collection<EOFAwareInputStream> processStreams, final ExecutorService executorService)
    {
        this.process = process;
        this.processDestroyer = processDestroyer;
        this.streamHandler = streamHandler;
        this.watchdog = watchdog;
        this.processStreams = processStreams;
        this.executorService = executorService;
    }

    public void close()
    {
        waitForProcess();
        waitForStreams();
    }

    private void waitForProcess()
    {
        final Future<?> processFinished = executorService.submit(new ProcessFinishedWaiter(process));

        try
        {
            processFinished.get(2, SECONDS);
        }
        catch (InterruptedException e)
        {
            process.destroy();
        }
        catch (TimeoutException e)
        {
            process.destroy();
        }
        catch (ExecutionException e)
        {
            process.destroy();
            throw new BuildException("Error waiting for process to finish", e);
        }
        finally
        {
            watchdog.stop();
            processDestroyer.remove(process);
        }
    }

    private void waitForStreams()
    {
        for (final EOFAwareInputStream processStream : processStreams)
        {
            try
            {
                processStream.waitFor(2, SECONDS);
            }
            catch (InterruptedException e)
            {
                //om nom nom
            }
        }
        streamHandler.stop();
    }
}
