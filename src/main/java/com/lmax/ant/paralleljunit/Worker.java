package com.lmax.ant.paralleljunit;

import java.util.Queue;

import com.lmax.ant.paralleljunit.remote.controller.RemoteTestRunnerController;
import com.lmax.ant.paralleljunit.remote.controller.RemoteTestRunnerControllerFactory;

import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;

public class Worker implements Runnable
{
    private final int id;
    private final Queue<JUnitTest> testQueue;
    private final ParallelJUnitTaskConfig config;
    private final RemoteTestRunnerControllerFactory remoteTestRunnerControllerFactory;
    private final TestResultCoordinator testResultCoordinator;

    public Worker(final int id, final Queue<JUnitTest> testQueue, final ParallelJUnitTaskConfig config, final RemoteTestRunnerControllerFactory remoteTestRunnerControllerFactory,
                  final TestResultCoordinator testResultCoordinator)
    {
        this.id = id;
        this.testQueue = testQueue;
        this.config = config;
        this.remoteTestRunnerControllerFactory = remoteTestRunnerControllerFactory;
        this.testResultCoordinator = testResultCoordinator;
    }

    public void run()
    {
        final RemoteTestRunnerController remoteTestRunnerController = remoteTestRunnerControllerFactory.create(id, config);
        try
        {
            executeTestsOnRunner(remoteTestRunnerController);
        }
        finally
        {
            remoteTestRunnerController.close();
        }
    }

    private void executeTestsOnRunner(final RemoteTestRunnerController remoteTestRunnerController)
    {
        JUnitTest test;

        while ((test = testQueue.poll()) != null && testResultCoordinator.shouldContinue() && !Thread.interrupted())
        {
            testResultCoordinator.notifyResult(test, remoteTestRunnerController.execute(test), config);
        }
    }
}
