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
package com.lmax.ant.paralleljunit.util.net

import spock.lang.Specification
import spock.lang.Subject

class ConnectionEstablisherSpec extends Specification
{
    ServerSocket serverSocket = Mock()
    @Subject
    ConnectionEstablisher establisher = new ConnectionEstablisher(serverSocket)

    def 'Creates SocketConnection based on inbound connection'() {

        when:
        SocketConnection socketConnection = establisher.call()

        then:
        socketConnection != null
        1 * serverSocket.accept() >> Mock(Socket)
        1 * serverSocket.close()
    }

    def 'Rethrows exception thrown by ServerSocket accept and always close ServerSocket'() {

        given:
        serverSocket.accept() >> { throw new IOException() }

        when:
        establisher.call()

        then:
        thrown(IOException)
        1 * serverSocket.close()
    }

    def 'Ignores IOException thrown by ServerSocket.close'() {

        given:
        serverSocket.close() >> { throw new IOException() }

        when:
        establisher.call()

        then:
        noExceptionThrown()
    }
}
