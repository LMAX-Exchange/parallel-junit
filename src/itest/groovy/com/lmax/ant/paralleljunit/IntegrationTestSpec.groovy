package com.lmax.ant.paralleljunit

import org.apache.tools.ant.Project
import org.apache.tools.ant.ProjectHelper

import spock.lang.Ignore
import spock.lang.Specification

class IntegrationTestSpec extends Specification {

    @Ignore
    def 'First'() {
        given:
        def project = createAntProject()

        when:
        project.executeTarget("does-not-exist")

        then:
        true
    }

    def 'Second'() {
        given:
        def ant = createAntBuilder()

        when:
        ant.'parallel-junit'(wibble: 'no')

        then:
        true
    }

//    @Subject
//    private AdditiveParser negativeAbsoluteParser = new AdditiveParser(numberParser, 8)
//
//    @Unroll('Selects #selectedThreads when defaulted to 8 processors and #param selected')
//    def 'Calculates number of "threads" to use based on default'() {
//
//        expect:
//        negativeAbsoluteParser.parse(param) == selectedThreads
//
//        where:
//        param   | selectedThreads
//        '-1'    | 7
//        '-4'    | 4
//        '-8'    | 0
//        '-10'   | -2
//        '0'     | 8
//        '1'     | 9
//        '112'   | 120
//    }

    Project createAntProject() {
        def antFile = new File('src/test/resources/build.xml')
        def project = new Project()
        project.init()
        ProjectHelper.projectHelper.parse(project, antFile)
        return project
    }

    AntBuilder createAntBuilder() {
        def ant = new AntBuilder()
        ant.taskdef(resource: 'com/lmax/ant/paralleljunit/antlib.xml')
        return ant
    }
}
