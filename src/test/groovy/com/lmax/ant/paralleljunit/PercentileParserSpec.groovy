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
package com.lmax.ant.paralleljunit

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import org.apache.tools.ant.BuildException


class PercentileParserSpec extends Specification {

    private NumberParser numberParser = new NumberParser()

    @Subject
    private PercentileParser percentileParser = new PercentileParser(numberParser, 8)

    @Unroll('Selects #selectedProcessors threads when #percentage selected')
    def 'Calculates number of "threads" to use based on parsed percentage'() {

        expect:
        percentileParser.parse(percentage) == selectedProcessors

        where:
        percentage  | selectedProcessors
        '0%'        | 0
        '25%'       | 2
        '33%'       | 2
        '50%'       | 4
        '75%'       | 6
        '99%'       | 7
        '100%'      | 8
        '200%'      | 16
    }

    @Unroll('Throws BuildException when #percentage selected')
    def 'Throws BuildException when less than 0 percent selected'() {

        when:
        percentileParser.parse(percentage)

        then:
        thrown BuildException

        where:
        percentage << ['-1%', '-100%', '-33%', '-4668%']
    }
}
