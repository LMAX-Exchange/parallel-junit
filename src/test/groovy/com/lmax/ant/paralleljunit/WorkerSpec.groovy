package com.lmax.ant.paralleljunit

import com.lmax.ant.paralleljunit.remote.controller.RemoteTestRunnerController
import com.lmax.ant.paralleljunit.remote.controller.RemoteTestRunnerControllerFactory
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest
import spock.lang.Specification
import spock.lang.Subject

import static com.lmax.ant.paralleljunit.remote.TestResult.SUCCESS

class WorkerSpec extends Specification {

    private static final int WORKER_ID = 12
    private RemoteTestRunnerControllerFactory remoteTestRunnerControllerFactory = Mock()
    private Queue<JUnitTest> testQueue = new LinkedList<JUnitTest>()
    private ParallelJUnitTaskConfig config = Mock()
    private TestResultCoordinator testResultCoordinator = Mock()
    private RemoteTestRunnerController remoteTestRunnerController = Mock()

    @Subject
    private Worker worker = new Worker(WORKER_ID, testQueue, config, remoteTestRunnerControllerFactory, testResultCoordinator)

    def 'Runs a test from the queue using a remote test runner controller'() {

        given:
        JUnitTest firstTest = new JUnitTest('first')
        JUnitTest secondTest = new JUnitTest('second')
        testQueue << firstTest << secondTest

        when:
        worker.run()

        then:
        1 * remoteTestRunnerControllerFactory.create(WORKER_ID, config) >> remoteTestRunnerController
        1 * remoteTestRunnerController.execute(firstTest) >> SUCCESS
        1 * remoteTestRunnerController.execute(secondTest) >> SUCCESS
        2 * testResultCoordinator.shouldContinue() >> true
        1 * testResultCoordinator.notifyResult(firstTest, SUCCESS, config)
        1 * testResultCoordinator.notifyResult(secondTest, SUCCESS, config)
        1 * remoteTestRunnerController.close()
    }

    def 'Stops executing tests when test result coordinator says do not continue'() {

        given:
        remoteTestRunnerControllerFactory.create(WORKER_ID, config) >> remoteTestRunnerController
        testQueue << new JUnitTest('first')

        when:
        worker.run()

        then:
        1 * testResultCoordinator.shouldContinue() >> false
        0 * remoteTestRunnerController.execute(_)
    }
}
