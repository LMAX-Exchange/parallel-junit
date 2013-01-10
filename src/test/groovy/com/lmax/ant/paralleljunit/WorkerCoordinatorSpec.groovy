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
package com.lmax.ant.paralleljunit

import spock.lang.Specification
import spock.lang.Subject

import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import org.apache.tools.ant.BuildException

class WorkerCoordinatorSpec extends Specification
{
    WorkerFactory workerFactory = Mock()
    TestResultCoordinator testResulTCoordinator = Mock()
    ExecutorService executorService = Mock()
    ParallelJUnitTaskConfig config = Mock()

    @Subject
    WorkerCoordinator workerCoordinator = new WorkerCoordinator(workerFactory, executorService, testResulTCoordinator)

    def 'submits workers to execution service when told to execute'() {

        given:
        final int workerId1 = 0
        final int workerId2 = 1

        config.threads >> 2
        Worker worker1 = new Worker(workerId1, null, config, null, null)
        Worker worker2 = new Worker(workerId2, null, config, null, null)
        Future future1 = Mock()
        Future future2 = Mock()

        when:
        workerCoordinator.execute(config)

        then:
        1 * workerFactory.createWorker(workerId1, config) >> worker1
        1 * workerFactory.createWorker(workerId2, config) >> worker2
        1 * executorService.submit(worker1) >> future1
        1 * executorService.submit(worker2) >> future2
    }

    def 'waits for specific timeout for workers to finish'() {

        given:
        final int workerTimeout = 10
        config.getTimeout() >> workerTimeout

        when:
        workerCoordinator.execute(config)

        then:
        1 * executorService.shutdown()
        1 * executorService.awaitTermination(workerTimeout, TimeUnit.MILLISECONDS)
    }

    def 'waits for one day for workers to finish if timeout has not been specified'() {

        given:
        config.getTimeout() >> ParallelJUnitTaskConfig.NO_TIMEOUT

        when:
        workerCoordinator.execute(config)

        then:
        1 * executorService.shutdown()
        1 * executorService.awaitTermination(1, TimeUnit.DAYS)
    }

    def 'throws a BuildException if interrupted when waiting for workers to finish'() {

        given:
        executorService.awaitTermination(_,_) >> { throw new InterruptedException("boom") }

        when:
        workerCoordinator.execute(config)

        then:
        thrown BuildException
    }

    def 'cancels workers when they have finished or timeout'() {

        given:
        config.getThreads() >> 2
        Worker worker1 = Mock()
        Worker worker2 = Mock()
        Future future1 = Mock()
        Future future2 = Mock()
        workerFactory.createWorker(_, _) >>> [worker1, worker2]
        executorService.submit(_) >>> [future1, future2]


        when:
        workerCoordinator.execute(config)

        then:
        1 * future1.cancel(true)
        1 * future2.cancel(true)
    }

    def 'notfies test coordinator that workers have finished'() {

        when:
        workerCoordinator.execute(config)

        then:
        1 * testResulTCoordinator.onFinished(config)
    }
}
