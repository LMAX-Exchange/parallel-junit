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
