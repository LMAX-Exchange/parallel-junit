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
package com.lmax.ant.paralleljunit.remote.process;

import org.apache.tools.ant.ExitException;

import java.security.Permission;

/**
 * Similar to the ant-junit NoExitSecurityManager
 * Its purpose is to stop Ant exiting on a System.exit() call
 * Slightly more permissive that the Ant version
 */
public class NoExitSecurityManager extends SecurityManager
{
    @Override
    public void checkExit(int status) {
        throw new ExitException(status);
    }

    @Override
    public void checkPermission(Permission perm) {
        //don't check permissions
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        //don't check permissions
    }
}
