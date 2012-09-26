package com.lmax.ant.paralleljunit.remote

import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest
import spock.lang.Specification
import spock.lang.Subject

class TestSpecificationSpec extends Specification {


    def 'Creates test runner arguments'() {

        given:
        JUnitTest test = Mock()
        test.name >> 'TestingManolo'
        test.filtertrace >> true
        test.haltonerror >> false
        test.haltonfailure >> true
        test.outfile >> 'outfile'
        test.todir >> 'toDir'

        FormatterElement xmlFormatter = new FormatterElement()
        xmlFormatter.classname = 'XmlFormatter'
        xmlFormatter.extension = '.xml'

        FormatterElement jsonFormatter = new FormatterElement()
        jsonFormatter.classname = 'JSONFormatter'
        jsonFormatter.extension = '.json'

        test.formatters >> [xmlFormatter, jsonFormatter]

        @Subject
        TestSpecification testSpecification = new TestSpecification(test)

        expect:
        testSpecification.toArgs() == ['TestingManolo', 'filtertrace=true', 'haltOnError=false', 'haltOnFailure=true',
                                       'formatter=XmlFormatter,toDir/outfile.xml', 'formatter=JSONFormatter,toDir/outfile.json']
    }
}
