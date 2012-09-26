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
