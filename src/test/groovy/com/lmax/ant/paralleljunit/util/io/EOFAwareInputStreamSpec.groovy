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

import spock.lang.Specification
import spock.lang.Subject

import static java.util.concurrent.TimeUnit.MILLISECONDS

class EOFAwareInputStreamSpec extends Specification {

    InputStream delegate = Mock()
    @Subject
    EOFAwareInputStream inputStream = new EOFAwareInputStream(delegate)

    def 'Available always returns one and does not interact with delegate'() {

        when:
        int available = inputStream.available()

        then:
        available == 1
        0 * delegate._
    }

    def 'Should detect EOF when read() returns -1'() {

        given:
        delegate.read() >> -1

        expect:
        inputStream.read() == -1
        inputStream.waitFor(0, MILLISECONDS) == true
    }

    def 'Should detect EOF when read() throws IOException'() {

        given:
        delegate.read() >> { throw new IOException() }

        when:
        inputStream.read()

        then:
        thrown(IOException)
        inputStream.waitFor(0, MILLISECONDS) == true
    }

    def 'Should not detect EOF on normal read()'() {
        given:
        delegate.read() >> 32

        expect:
        inputStream.read() == 32
        inputStream.waitFor(0, MILLISECONDS) == false
    }

    def 'Should detect EOF when read(byte[]) returns -1'() {

        given:
        delegate.read(_ as byte[]) >> -1

        expect:
        inputStream.read(new byte[2]) == -1
        inputStream.waitFor(0, MILLISECONDS) == true
    }

    def 'Should detect EOF when read(byte[]) throws IOException'() {

        given:
        delegate.read(_ as byte[]) >> { throw new IOException() }

        when:
        inputStream.read(new byte[2])

        then:
        thrown(IOException)
        inputStream.waitFor(0, MILLISECONDS) == true
    }

    def 'Should not detect EOF on normal read(byte[])'() {
        given:
        delegate.read(_ as byte[]) >> 32

        expect:
        inputStream.read(new byte[2]) == 32
        inputStream.waitFor(0, MILLISECONDS) == false
    }

    def 'Should detect EOF when read(byte[], int, int) returns -1'() {

        given:
        delegate.read(_ as byte[], _ as Integer, _ as Integer) >> -1

        expect:
        inputStream.read(new byte[2], 0, 2) == -1
        inputStream.waitFor(0, MILLISECONDS) == true
    }

    def 'Should detect EOF when read(byte[], int, int) throws IOException'() {

        given:
        delegate.read(_ as byte[], _ as Integer, _ as Integer) >> { throw new IOException() }

        when:
        inputStream.read(new byte[2], 0, 2)

        then:
        thrown(IOException)
        inputStream.waitFor(0, MILLISECONDS) == true
    }

    def 'Should not detect EOF on normal read(byte[], int, int)'() {
        given:
        delegate.read(_ as byte[], _ as Integer, _ as Integer) >> 32

        expect:
        inputStream.read(new byte[2], 0, 2) == 32
        inputStream.waitFor(0, MILLISECONDS) == false
    }
}
