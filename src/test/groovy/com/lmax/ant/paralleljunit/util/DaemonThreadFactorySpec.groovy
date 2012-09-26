package com.lmax.ant.paralleljunit.util;


import spock.lang.Specification
import spock.lang.Subject

public class DaemonThreadFactorySpec extends Specification {

    @Subject
    final DaemonThreadFactory factory = new DaemonThreadFactory()

    def 'Creates daemon threads'() {

        expect:
        factory.newThread({} as Runnable).daemon
    }
}
