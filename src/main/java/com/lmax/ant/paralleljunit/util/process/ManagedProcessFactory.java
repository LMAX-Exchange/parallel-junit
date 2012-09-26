package com.lmax.ant.paralleljunit.util.process;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import com.lmax.ant.paralleljunit.ParallelJUnitTaskConfig;
import com.lmax.ant.paralleljunit.remote.controller.RemoteTestRunnerProcessFactory;
import com.lmax.ant.paralleljunit.util.io.EOFAwareInputStream;
import com.lmax.ant.paralleljunit.util.io.EOFAwareInputStreamFactory;
import com.lmax.ant.paralleljunit.util.io.ExecuteStreamHandlerFactory;

import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;


import static java.util.Arrays.asList;

public class ManagedProcessFactory
{
    private final RemoteTestRunnerProcessFactory remoteTestRunnerProcessFactory;
    private final ProcessDestroyer destroyer;
    private final ExecuteStreamHandlerFactory executeStreamHandlerFactory;
    private final ExecuteWatchdogFactory watchdogFactory;
    private final EOFAwareInputStreamFactory eofAwareInputStreamFactory;
    private final ExecutorService executorService;

    public ManagedProcessFactory(final RemoteTestRunnerProcessFactory remoteTestRunnerProcessFactory, final ProcessDestroyer destroyer, final ExecuteStreamHandlerFactory executeStreamHandlerFactory,
                                 final ExecuteWatchdogFactory watchdogFactory, final EOFAwareInputStreamFactory eofAwareInputStreamFactory, final ExecutorService executorService)
    {
        this.remoteTestRunnerProcessFactory = remoteTestRunnerProcessFactory;
        this.destroyer = destroyer;
        this.executeStreamHandlerFactory = executeStreamHandlerFactory;
        this.watchdogFactory = watchdogFactory;
        this.eofAwareInputStreamFactory = eofAwareInputStreamFactory;
        this.executorService = executorService;
    }

    public ManagedProcess create(final int workerId, final ParallelJUnitTaskConfig config, final int serverPort)
    {
        final Process process = remoteTestRunnerProcessFactory.createForkedProcess(workerId, config, serverPort);

        destroyer.add(process);

        final EOFAwareInputStream processOutputStream = eofAwareInputStreamFactory.create(process.getInputStream());
        final EOFAwareInputStream processErrorStream = eofAwareInputStreamFactory.create(process.getErrorStream());
        final Collection<EOFAwareInputStream> processStreams = asList(processOutputStream, processErrorStream);

        final ExecuteStreamHandler streamHandler = executeStreamHandlerFactory.create(processOutputStream, processErrorStream, process.getOutputStream());

        final ExecuteWatchdog watchdog = watchdogFactory.create(config);
        watchdog.start(process);

        return new ManagedProcess(process, destroyer, streamHandler, watchdog, processStreams, executorService);
    }
}
