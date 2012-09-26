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
