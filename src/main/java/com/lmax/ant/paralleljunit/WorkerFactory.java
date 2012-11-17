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
