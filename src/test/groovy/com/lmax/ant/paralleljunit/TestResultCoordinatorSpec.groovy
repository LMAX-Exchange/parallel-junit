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
import org.apache.tools.ant.BuildException
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest
import com.lmax.ant.paralleljunit.remote.TestResult
import org.apache.tools.ant.ProjectComponent
import org.apache.tools.ant.Location
import org.apache.tools.ant.Project

class TestResultCoordinatorSpec extends Specification {

    ParallelJUnitTaskConfig parallelJUnitTaskConfig = Mock()
    JUnitTest test = Mock()

    @Subject
    TestResultCoordinator testResultCoordinator = new TestResultCoordinator()

    def 'continues to process test results if the current one is success'() {

        when:
        testResultCoordinator.notifyResult(test, TestResult.SUCCESS, parallelJUnitTaskConfig)

        then:
        testResultCoordinator.shouldContinue() == true
    }

    def 'does not continue to process test results if the current one is error and test is configured to stop on errors'() {

        given:
        test.haltonerror >> true

        when:
        testResultCoordinator.notifyResult(test, TestResult.ERROR, parallelJUnitTaskConfig)

        then:
        testResultCoordinator.shouldContinue() == false
    }

    def 'does not continue to process test results if the current one has failures and test is configured to stop on failures'() {

        given:
        test.haltonfailure >> true

        when:
        testResultCoordinator.notifyResult(test, TestResult.FAILURE, parallelJUnitTaskConfig)

        then:
        testResultCoordinator.shouldContinue() == false
    }

    // If we crash we want to stop running and blow up with error explaining that we crashed and which test is the culprit of the crash. Subsequent happenings should not affect reporting of crash
    //On evaluating end result of the run, we should set the appropriate properties and throw the BuildException with the information collected if we are halting
    def 'stops when a test has crashed and reports its name'() {

        given:
        test.name >> 'testNam!'
        ProjectComponent projectComponent = Mock()
        Location testLocation = Mock()
        parallelJUnitTaskConfig.getProjectComponent() >> projectComponent
        projectComponent.location >> testLocation
        BuildException expectedException = new BuildException('testNam! FAILED (crashed)', testLocation)

        testResultCoordinator.notifyResult(test, TestResult.CRASHED, parallelJUnitTaskConfig)

        when:
        testResultCoordinator.onFinished(parallelJUnitTaskConfig)

        then:
        BuildException exception = thrown()
        exception.toString() == expectedException.toString()
    }

    def 'on test failure, report the failure'() {

        given:
        test.failureProperty >> 'failedProperty'
        ProjectComponent projectComponent = Mock()
        parallelJUnitTaskConfig.getProjectComponent() >> projectComponent
        Project project = Mock()
        projectComponent.getProject() >> project

        testResultCoordinator.notifyResult(test, TestResult.FAILURE, parallelJUnitTaskConfig)

        when:
        testResultCoordinator.onFinished(parallelJUnitTaskConfig)

        then:
        1 * project.setNewProperty('failedProperty', 'true')
    }

    def 'on test error, report the error'() {

        given:
        test.errorProperty >> 'errorProperty'
        ProjectComponent projectComponent = Mock()
        parallelJUnitTaskConfig.getProjectComponent() >> projectComponent
        Project project = Mock()
        projectComponent.getProject() >> project

        testResultCoordinator.notifyResult(test, TestResult.ERROR, parallelJUnitTaskConfig)

        when:
        testResultCoordinator.onFinished(parallelJUnitTaskConfig)

        then:
        1 * project.setNewProperty('errorProperty', 'true')
    }
}