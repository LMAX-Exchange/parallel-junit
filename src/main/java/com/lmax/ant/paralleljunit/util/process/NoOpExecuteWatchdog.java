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
