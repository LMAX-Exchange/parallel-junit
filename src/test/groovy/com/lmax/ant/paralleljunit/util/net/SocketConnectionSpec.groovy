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
