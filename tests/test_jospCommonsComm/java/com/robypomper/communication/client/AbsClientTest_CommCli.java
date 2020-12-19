/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.communication.client;

import com.robypomper.josp.states.StateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;


public class AbsClientTest_CommCli extends AbsClientTest_Base {


    // idConnected, connect and disconnect

    @Test
    public void testIsConnected() throws StateException, InterruptedException, IOException, Client.AAAException {
        // Check test client isConnected==false
        assert !clientLatch.isConnected();

        // Start server
        ServerSocket server = new ServerSocket(port);

        // Start test client and check isConnected==true
        clientLatch.connect();
        Assertions.assertTrue(latchCLE.onConnected.await(1, TimeUnit.SECONDS));
        Assertions.assertTrue(clientLatch.isConnected());

        // Stop test client and check isConnected==false
        clientLatch.disconnect();
        Assertions.assertTrue(latchCLE.onDisconnected.await(1, TimeUnit.SECONDS));
        Assertions.assertFalse(clientLatch.isConnected());

        // Stop server
        server.close();
        assert server.isClosed();
    }

    @Test
    public void testConnectToServerNotRunning_FAIL() {
        // Check test client isConnected==false
        assert !clientLatch.isConnected();

        //// Start server DON'T start
        //ServerSocket server = new ServerSocket(PORT);

        // Start test client and check isConnected==true
        Assertions.assertThrows(ConnectException.class, clientLatch::connect);
        // ConnectException extends SocketException
        // SocketException extends IOException

        Assertions.assertFalse(clientLatch.isConnected());
    }

}
