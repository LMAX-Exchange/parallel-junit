package com.lmax.ant.paralleljunit.util.process

import spock.lang.Specification
import spock.lang.Subject

class ProcessFinishedWaiterSpec extends Specification {

    private Process process = Mock()

    @Subject
    private ProcessFinishedWaiter processFinishedWaiter = new ProcessFinishedWaiter(process)

    def 'Waits for process to finish'() {

        when:
        processFinishedWaiter.run()

        then:
        1 * process.waitFor()
    }

    def 'Squashes InterruptedException while waiting'() {

        given:
        process.waitFor() >> { throw new InterruptedException() }

        when:
        processFinishedWaiter.run()

        then:
        noExceptionThrown()
    }
}
