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
package com.lmax.ant.paralleljunit.remote.process

import spock.lang.Specification
import spock.lang.Subject


class ArgsParserTest extends Specification {

    @Subject
    private ArgsParser argsParser = new ArgsParser()

    def 'Parses parameters for remote test runner'() {

        given:
        String[] args = ['workerId=12', 'someRandom=stuff', 'serverPort=9987', 'moreRandom=staff']

        when:
        RemoteTestRunnerParams remoteTestRunnerParams = argsParser.parseMainArgs(args)

        then:
        remoteTestRunnerParams.workerId == 12
        remoteTestRunnerParams.serverPort == 9987
        remoteTestRunnerParams.testRunnerArguments == ['someRandom=stuff', 'moreRandom=staff']
    }

    def 'Ignores blacklisted arguments'() {

        given:
        String[] args = ['someRandom=stuff', 'filtertrace=false', 'haltOnError=jaja', 'haltOnFailure=doYouSpeakIt']

        expect:
        argsParser.parseMainArgs(args).testRunnerArguments == ['someRandom=stuff']
    }
}
