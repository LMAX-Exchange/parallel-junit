package com.lmax.ant.paralleljunit;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.tools.ant.BuildException;


import static com.lmax.ant.paralleljunit.ParallelJUnitTaskConfig.NO_TIMEOUT;

public class WorkerCoordinator
{
    private final WorkerFactory workerFactory;
    private final ExecutorService executorService;
    private final List<Future<?>> workerFutures = new LinkedList<Future<?>>();
    private final TestResultCoordinator testResultCoordinator;

    public WorkerCoordinator(final WorkerFactory workerFactory, final ExecutorService executorService, final TestResultCoordinator testResultCoordinator)
    {
        this.workerFactory = workerFactory;
        this.executorService = executorService;
        this.testResultCoordinator = testResultCoordinator;
    }

    public void execute(final ParallelJUnitTaskConfig config)
    {
        for (int i = 0; i < config.getThreads(); i++)
        {
            final Worker worker = workerFactory.createWorker(i, config);
            workerFutures.add(executorService.submit(worker));
        }

        waitForWorkersToFinish(config);

        testResultCoordinator.onFinished(config);
    }

    private void waitForWorkersToFinish(final ParallelJUnitTaskConfig config)
    {
        executorService.shutdown();
        try
        {
            if (config.getTimeout() == NO_TIMEOUT)
            {
                executorService.awaitTermination(1, TimeUnit.DAYS);
            }
            else
            {
                executorService.awaitTermination(config.getTimeout(), TimeUnit.MILLISECONDS);
            }
        }
        catch (InterruptedException e)
        {
            throw new BuildException("Interrupted while waiting for workers to finish.", e);
        }
        finally
        {
            for (final Future<?> workerFuture : workerFutures)
            {
                workerFuture.cancel(true);
            }
        }
    }
}
