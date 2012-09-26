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
