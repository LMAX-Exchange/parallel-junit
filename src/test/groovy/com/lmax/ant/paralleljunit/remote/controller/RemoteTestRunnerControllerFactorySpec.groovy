package com.lmax.ant.paralleljunit.remote.controller

import spock.lang.Specification
import spock.lang.Subject
import com.lmax.ant.paralleljunit.util.process.ManagedProcessFactory
import java.util.concurrent.ExecutorService
import javax.net.ServerSocketFactory
import com.lmax.ant.paralleljunit.util.net.ConnectionEstablisherFactory
import com.lmax.ant.paralleljunit.remote.TestSpecificationFactory


class RemoteTestRunnerControllerFactorySpec extends Specification {

    ManagedProcessFactory managedProcessFactory = Mock()
    ExecutorService executorService = Mock()
    ServerSocketFactory serverSocketFactory = Mock()
    ConnectionEstablisherFactory connectionEstablisherFactory = Mock()
    TestSpecificationFactory testSpecificationFactory = new TestSpecificationFactory()

    @Subject
    private RemoteTestRunnerControllerFactory remoteTestRunnerControllerFactory = new RemoteTestRunnerControllerFactory()

    def 'Creates RemoteTestRunnerController'() {

    }
}
