package com.lmax.ant.paralleljunit.util.net

import spock.lang.Specification

import javax.net.SocketFactory
import spock.lang.Subject

class SocketConnectionFactorySpec extends Specification
{
    private SocketFactory socketFactory = Mock()

    @Subject
    private SocketConnectionFactory factory = new SocketConnectionFactory(socketFactory)

    def 'Creates a SocketConnection connected to localhost on a given port'() {

        given:
        int port = 5485

        when:
        factory.createSocketConnection(port)

        then:
        1 * socketFactory.createSocket(InetAddress.localHost, port)
    }
}
