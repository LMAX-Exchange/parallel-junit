package com.lmax.ant.paralleljunit.remote.controller

import com.lmax.ant.paralleljunit.remote.TestResult
import com.lmax.ant.paralleljunit.remote.TestSpecification
import com.lmax.ant.paralleljunit.remote.TestSpecificationFactory
import com.lmax.ant.paralleljunit.remote.process.RemoteTestRunnerCommand
import com.lmax.ant.paralleljunit.util.net.SocketConnection
import com.lmax.ant.paralleljunit.util.process.ManagedProcess
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest
import spock.lang.Specification
import spock.lang.Subject

class RemoteTestRunnerControllerSpec extends Specification {

    private ManagedProcess jvmProcess = Mock()
    private SocketConnection socketConnection = Mock()
    private TestSpecificationFactory testSpecificationFactory = Mock()

    @Subject
    private RemoteTestRunnerController remoteTestRunnerController = new RemoteTestRunnerController(jvmProcess, socketConnection, testSpecificationFactory)

    def 'Reports the result of running a given test'() {

        given:
        JUnitTest test = new JUnitTest('manolo')
        TestSpecification testSpecification = new TestSpecification(test)
        TestResult resultFromSocket = TestResult.FAILURE

        when:
        TestResult result = remoteTestRunnerController.execute(test)

        then:
        1 * testSpecificationFactory.create(test) >> testSpecification
        1 * socketConnection.writeObject(RemoteTestRunnerCommand.RUN_TEST)
        1 * socketConnection.writeObject(testSpecification)
        1 * socketConnection.readObject() >> resultFromSocket
        result == resultFromSocket
    }

    def 'Reports CRASHED when writing command throws an exception'() {

        given:
        JUnitTest test = new JUnitTest('manolo')

        when:
        TestResult result = remoteTestRunnerController.execute(test)

        then:
        1 * socketConnection.writeObject(RemoteTestRunnerCommand.RUN_TEST) >> { throw new IOException() }
        result == TestResult.CRASHED
    }

    def 'Reports CRASHED when writing test specification throws an exception'() {

        given:
        JUnitTest test = new JUnitTest('manolo')
        testSpecificationFactory.create(_) >> new TestSpecification(test)
        socketConnection.writeObject(RemoteTestRunnerCommand.RUN_TEST)

        when:
        TestResult result = remoteTestRunnerController.execute(test)

        then:
        1 * socketConnection.writeObject(_ as TestSpecification) >> { throw new IOException() }
        result == TestResult.CRASHED
    }

    def 'Reports CRASHED when reading the result throws an exception'() {

        given:
        JUnitTest test = new JUnitTest('manolo')
        socketConnection.writeObject(_)

        when:
        TestResult result = remoteTestRunnerController.execute(test)

        then:
        1 * socketConnection.readObject() >> { throw new IOException() }
        result == TestResult.CRASHED
    }
}
