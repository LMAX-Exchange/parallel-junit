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
package com.lmax.ant.paralleljunit.util.process

import com.lmax.ant.paralleljunit.ParallelJUnitTaskConfig
import com.lmax.ant.paralleljunit.remote.controller.RemoteTestRunnerProcessFactory
import com.lmax.ant.paralleljunit.util.io.EOFAwareInputStream
import com.lmax.ant.paralleljunit.util.io.EOFAwareInputStreamFactory
import com.lmax.ant.paralleljunit.util.io.ExecuteStreamHandlerFactory
import org.apache.tools.ant.taskdefs.ExecuteWatchdog
import spock.lang.Specification
import spock.lang.Subject

import java.util.concurrent.ExecutorService

class ManagedProcessFactorySpec extends Specification {

    private RemoteTestRunnerProcessFactory remoteTestRunnerProcessFactory = Mock()
    private ProcessDestroyer processDestroyer = Mock()
    private ExecuteStreamHandlerFactory streamHandlerFactory = Mock()
    private ExecuteWatchdogFactory executeWatchdogFactory = Mock()
    private ExecuteWatchdog executeWatchdog = Mock()
    private EOFAwareInputStreamFactory eofAwareInputStreamFactory = Mock()
    private ExecutorService executorService = Mock()
    private ParallelJUnitTaskConfig config = Mock()
    private Process process = Mock()
    private InputStream inputStream = Mock()
    private InputStream errorStream = Mock()
    private OutputStream outputStream = Mock()
    private EOFAwareInputStream eofAwareInputStream = Mock()
    private EOFAwareInputStream eofAwareErrorStream = Mock()

    @Subject
    private ManagedProcessFactory managedProcessFactory = new ManagedProcessFactory(remoteTestRunnerProcessFactory, processDestroyer, streamHandlerFactory,
                                                                                    executeWatchdogFactory, eofAwareInputStreamFactory, executorService)

    def 'Creates managed process'() {
        given:
        int workerId = 4
        int port = 5678
        process.inputStream >> inputStream
        process.errorStream >> errorStream
        process.outputStream >> outputStream

        when:
        ManagedProcess managedProcess = managedProcessFactory.create(workerId, config, port)

        then:
        managedProcess != null
        1 * remoteTestRunnerProcessFactory.createForkedProcess(workerId, config, port) >> process
        1 * processDestroyer.add(process)
        1 * eofAwareInputStreamFactory.create(inputStream) >> eofAwareInputStream
        1 * eofAwareInputStreamFactory.create(errorStream) >> eofAwareErrorStream
        1 * streamHandlerFactory.create(eofAwareInputStream, eofAwareErrorStream, outputStream)
        1 * executeWatchdogFactory.create(config) >> executeWatchdog
        1 * executeWatchdog.start(process)
    }
}
