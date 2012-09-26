package com.lmax.ant.paralleljunit.util.io

import spock.lang.Specification
import spock.lang.Subject
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler

public class PumpStreamHandlerFactorySpec extends Specification {

    private OutputStream out = Mock()
    private OutputStream err = Mock()

    @Subject
    private PumpStreamHandlerFactory factory = new PumpStreamHandlerFactory(out, err)

    def 'Creates stream handler with given output and error streams'() {

        when:
        ExecuteStreamHandler streamHandler = factory.create()

        then:
        streamHandler.out == out
        streamHandler.err == err
    }
}
