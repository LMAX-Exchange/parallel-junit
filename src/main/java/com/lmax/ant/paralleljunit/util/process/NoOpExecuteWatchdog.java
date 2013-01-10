/**
 * Copyright 2012-2013 LMAX Ltd.
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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
import org.apache.tools.ant.util.Watchdog;

public final class NoOpExecuteWatchdog extends ExecuteWatchdog
{
    public static NoOpExecuteWatchdog INSTANCE = new NoOpExecuteWatchdog();

    private NoOpExecuteWatchdog()
    {
        super(Long.MAX_VALUE);
    }

    @Override
    public synchronized void start(final Process process)
    {
        //NO-OP
    }

    @Override
    public synchronized void stop()
    {
        //NO-OP
    }

    @Override
    public synchronized void timeoutOccured(final Watchdog w)
    {
        //NO-OP
    }

    @Override
    protected synchronized void cleanUp()
    {
        //NO-OP
    }

    @Override
    public synchronized void checkException() throws BuildException
    {
        //NO-OP
    }

    @Override
    public boolean isWatching()
    {
        //NO-OP
        return false;
    }

    @Override
    public boolean killedProcess()
    {
        //NO-OP
        return false;
    }
}
