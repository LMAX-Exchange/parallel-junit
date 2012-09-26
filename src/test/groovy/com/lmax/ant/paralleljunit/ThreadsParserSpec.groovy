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
