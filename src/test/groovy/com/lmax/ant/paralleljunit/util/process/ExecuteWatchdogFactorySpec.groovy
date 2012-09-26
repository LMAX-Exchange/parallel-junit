package com.lmax.ant.paralleljunit.util.process;


import com.lmax.ant.paralleljunit.ParallelJUnitTaskConfig
import org.apache.tools.ant.taskdefs.ExecuteWatchdog
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static java.lang.Integer.MAX_VALUE

public class ExecuteWatchdogFactorySpec extends Specification {

    @Shared
    private Random random = new Random()
    private ParallelJUnitTaskConfig config = Mock()

    @Subject
    private ExecuteWatchdogFactory factory = new ExecuteWatchdogFactory()

    @Unroll('Creates NoOpExecuteWatchdog when timeout is #timeout')
    def 'Creates NoOpExecuteWatchdog when timeout <= 0'() {

        given:
        config.timeout >> timeout

        expect:
        factory.create(config) in NoOpExecuteWatchdog

        where:
        timeout << [0, -1, -466, -random.nextInt(MAX_VALUE)]
    }

    @Unroll('Creates ExecuteWatchdog when timeout is #timeout')
    def 'Creates ExecuteWatchdog when timeout > 0'() {

        given:
        config.timeout >> timeout

        expect:
        factory.create(config).class == ExecuteWatchdog

        where:
        timeout << [1, 32, random.nextInt(MAX_VALUE -1 ) + 1]
    }
}
