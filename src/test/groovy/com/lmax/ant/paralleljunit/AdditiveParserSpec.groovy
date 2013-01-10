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

class AdditiveParserSpec extends Specification {

    private NumberParser numberParser = new NumberParser()

    @Subject
    private AdditiveParser negativeAbsoluteParser = new AdditiveParser(numberParser, 8)

    @Unroll('Selects #selectedThreads when defaulted to 8 processors and #param selected')
    def 'Calculates number of "threads" to use based on default'() {

        expect:
        negativeAbsoluteParser.parse(param) == selectedThreads

        where:
        param   | selectedThreads
        '-1'    | 7
        '-4'    | 4
        '-8'    | 0
        '-10'   | -2
        '0'     | 8
        '1'     | 9
        '112'   | 120
    }
}
