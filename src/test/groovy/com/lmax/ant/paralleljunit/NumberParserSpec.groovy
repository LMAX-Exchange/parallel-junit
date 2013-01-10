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


class NumberParserSpec extends Specification {

    @Subject
    private NumberParser numberParser = new NumberParser()

    @Unroll('Parses #number')
    def 'Parses integer numbers'() {

        expect:
        numberParser.parse(number) == number.toInteger()

        where:
        number << ['0', '-1', '1', '-4324', '8462']
    }

    @Unroll('Throws BuildException when parsing #param')
    def 'Throws BuildException when given string is not parsable to integer'() {

        when:
        numberParser.parse(param)

        then:
        thrown BuildException

        where:
        param << ['1.1', '432a', 'NaN', 'shiz']
    }
}
