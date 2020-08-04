/* *****************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************** */

package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.josp.jsl.JSL;

public class CmdsJSL {

    private final JSL jsl;

    public CmdsJSL(JSL jsl) {
        this.jsl = jsl;
    }

    /**
     * Print current JSL Service status.
     *
     * @return the JSL Service status.
     */
    @Command(description = "Print current JSL Object status.")
    public JSL.Status jslStatus() {
        return jsl.status();
    }

    /**
     * Connect current JSL Service.
     *
     * @return success or error message.
     */
    @Command(description = "Connect current JSL library.")
    public String jslConnect() {
        try {
            jsl.connect();
        } catch (JSL.ConnectException e) {
            return "Error on connecting JSL service because " + e.getMessage();
        }

        if (jsl.status() != JSL.Status.CONNECTED)
            return "JSL service NOT connected.";

        return "JSL service connected successfully.";
    }

    /**
     * Disconnect current JSL Service.
     *
     * @return success or error message.
     */
    @Command(description = "Disconnect current JSL library.")
    public String jslDisconnect() {
        try {
            jsl.disconnect();
        } catch (JSL.ConnectException e) {
            return "Error on disconnecting JSL service because " + e.getMessage();
        }

        if (jsl.status() != JSL.Status.DISCONNECTED)
            return "JSL Service NOT disconnected.";

        return "JSL Service disconnected successfully.";
    }
}
