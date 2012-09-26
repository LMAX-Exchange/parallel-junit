package com.lmax.ant.paralleljunit.util;

import java.util.concurrent.ThreadFactory;

public class DaemonThreadFactory implements ThreadFactory
{
    public Thread newThread(final Runnable r)
    {
        final Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    }
}
