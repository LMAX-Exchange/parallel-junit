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
package com.lmax.ant.paralleljunit.util.io

import org.apache.tools.ant.taskdefs.PumpStreamHandler
import spock.lang.Specification
import spock.lang.Subject

class ExecuteStreamHandlerFactorySpec extends Specification {

    private InputStream processOutputStream = Mock()
    private InputStream processErrorStream = Mock()
    private OutputStream processInputStream = Mock()

    private PumpStreamHandlerFactory pumpStreamHandlerFactory = Mock()
    private PumpStreamHandler pumpStreamHandler = Mock()

    @Subject
    private ExecuteStreamHandlerFactory executeStreamHandlerFactory = new ExecuteStreamHandlerFactory(pumpStreamHandlerFactory)

    def 'Creates stream handler, registers process streams and starts the handler'() {

        given:
        pumpStreamHandlerFactory.create() >> pumpStreamHandler

        when:
        executeStreamHandlerFactory.create(processOutputStream, processErrorStream, processInputStream)

        then:
        1 * pumpStreamHandler.setProcessOutputStream(processOutputStream)
        1 * pumpStreamHandler.setProcessErrorStream(processErrorStream)
        1 * pumpStreamHandler.setProcessInputStream(processInputStream)

        then:
        1 * pumpStreamHandler.start()
    }
}
