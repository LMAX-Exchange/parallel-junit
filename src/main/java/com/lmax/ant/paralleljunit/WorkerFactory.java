package com.lmax.ant.paralleljunit;

import com.lmax.ant.paralleljunit.remote.controller.RemoteTestRunnerControllerFactory;

public class WorkerFactory
{
    private final RemoteTestRunnerControllerFactory remoteTestRunnerControllerFactory;
    private final TestResultCoordinator testResultCoordinator;

    public WorkerFactory(final RemoteTestRunnerControllerFactory remoteTestRunnerControllerFactory, final TestResultCoordinator testResultCoordinator)
    {
        this.remoteTestRunnerControllerFactory = remoteTestRunnerControllerFactory;
        this.testResultCoordinator = testResultCoordinator;
    }

    public Worker createWorker(final int id, final ParallelJUnitTaskConfig config)
    {
        return new Worker(id, config.getTestQueue(), config, remoteTestRunnerControllerFactory, testResultCoordinator);
    }
}
