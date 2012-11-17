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
