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
