package com.lmax.ant.paralleljunit.util.io

import org.apache.tools.ant.taskdefs.PumpStreamHandler
import spock.lang.Specification
import spock.lang.Subject

class ExecuteStreamHandlerFactorySpec extends Specification {

    private InputStream processOutputStream = Mock()
    private InputStream processErrorStream = Mock()
    private OutputStream processInputStream = Mock()

    private PumpStreamHandlerFactory pumpStreamHandlerFactory = Mock()
    private PumpStreamHandler pumpStreamHandler = Mock()

    @Subject
    private ExecuteStreamHandlerFactory executeStreamHandlerFactory = new ExecuteStreamHandlerFactory(pumpStreamHandlerFactory)

    def 'Creates stream handler, registers process streams and starts the handler'() {

        given:
        pumpStreamHandlerFactory.create() >> pumpStreamHandler

        when:
        executeStreamHandlerFactory.create(processOutputStream, processErrorStream, processInputStream)

        then:
        1 * pumpStreamHandler.setProcessOutputStream(processOutputStream)
        1 * pumpStreamHandler.setProcessErrorStream(processErrorStream)
        1 * pumpStreamHandler.setProcessInputStream(processInputStream)

        then:
        1 * pumpStreamHandler.start()
    }
}
