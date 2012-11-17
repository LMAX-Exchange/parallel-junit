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

package com.lmax.ant.paralleljunit.util.net

import spock.lang.Specification
import spock.lang.Subject

class SocketConnectionSpec extends Specification {

    private Socket socket = Mock()

    @Subject
    private SocketConnection socketConnection = new SocketConnection(socket)

    def 'Closes Socket on close'() {

        when:
        socketConnection.close()

        then:
        1 * socket.close()
    }

    def 'Does not propagate Exception thrown by closing Socket'() {

        given:
        socket.close() >> { throw new IOException('boom') }

        when:
        socketConnection.close()

        then:
        noExceptionThrown()
    }

    def 'Writes Objects to Socket'() {

        given:
        def object = [question: 'BlahBlah', answer: 'YaddaYadda']
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        socket.outputStream >> outputStream

        when:
        socketConnection.writeObject(object)

        then:
        new ByteArrayInputStream(outputStream.toByteArray()).withObjectInputStream { it.readObject() == object }
    }

    def 'Reads Objects from Socket'() {

        given:
        def object = [question: 'BlahBlah', answer: 'YaddaYadda']
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        outputStream.withObjectOutputStream { it.writeObject(object) }
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())
        socket.inputStream >> inputStream

        expect:
        socketConnection.readObject() == object
    }
}
