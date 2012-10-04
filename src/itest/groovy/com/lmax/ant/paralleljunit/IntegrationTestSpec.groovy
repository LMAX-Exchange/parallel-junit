package com.lmax.ant.paralleljunit

import org.apache.tools.ant.BuildException
import org.apache.tools.ant.Project
import org.apache.tools.ant.ProjectHelper

import spock.lang.Ignore
import spock.lang.Specification

class IntegrationTestSpec extends Specification {

    def 'First approach'() {
        // Create the Ant project by hand and call targets on it. Expect a BuildException if an expection is expected
        given:
        def project = createAntProject()

        when:
        project.executeTarget("does-not-exist")

        then:
        thrown(BuildException)
    }

    def 'Second approach'() {
        // Use the Groovy AntBuilder to directly execute the task
        given:
        def ant = createAntBuilder()

        when:
        ant.'parallel-junit'()

        then:
        true
    }

    Project createAntProject() {
        def antFile = new File('src/itest/resources/itest-build.xml')
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
