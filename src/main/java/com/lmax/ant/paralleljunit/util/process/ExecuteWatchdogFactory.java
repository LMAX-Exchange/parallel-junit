package com.lmax.ant.paralleljunit.util.process;

import com.lmax.ant.paralleljunit.ParallelJUnitTaskConfig;

import org.apache.tools.ant.taskdefs.ExecuteWatchdog;

public class ExecuteWatchdogFactory
{
    public ExecuteWatchdog create(final ParallelJUnitTaskConfig config)
    {
        final long timeout = config.getTimeout();
        return timeout > 0 ? new ExecuteWatchdog(timeout) : NoOpExecuteWatchdog.INSTANCE;
    }
}
