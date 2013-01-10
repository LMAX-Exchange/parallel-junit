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
/**
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
