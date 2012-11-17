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

package com.lmax.ant.paralleljunit

import spock.lang.Specification
import spock.lang.Subject

class ThreadsParserSpec extends Specification {

    private PercentileParser percentileParser = Mock()
    private AdditiveParser negativeAbsoluteParser = Mock()
    private NumberParser positiveAbsoluteParser = Mock()

    @Subject
    private ThreadsParser threadsParser = new ThreadsParser(percentileParser, negativeAbsoluteParser, positiveAbsoluteParser)

    def 'Parses percentile thread parameter'() {

        when:
        int threads = threadsParser.parse '12%'

        then:
        1 * percentileParser.parse('12%') >> 2
        threads == 2
    }

    def 'Parses absolute negative parameter'() {

        when:
        int threads = threadsParser.parse '-2'

        then:
        1 * negativeAbsoluteParser.parse('-2') >> 1
        threads == 1
    }

    def 'Parses absolute positive parameter'() {

        when:
        int threads = threadsParser.parse '4'

        then:
        1 * positiveAbsoluteParser.parse('4') >> 4
        threads == 4
    }
}
