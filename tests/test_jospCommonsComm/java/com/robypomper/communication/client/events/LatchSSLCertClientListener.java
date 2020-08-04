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

package com.robypomper.communication.client.events;

import com.robypomper.communication.client.standard.SSLCertClient;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;

import java.util.concurrent.CountDownLatch;


public class LatchSSLCertClientListener implements SSLCertClient.SSLCertClientListener {

    // Internal vars

    public CountDownLatch onCertificateSend = new CountDownLatch(1);
    public CountDownLatch onCertificateStored = new CountDownLatch(1);


    // Events

    @Override
    public void onCertificateSend() {
        onCertificateSend.countDown();
    }

    @Override
    public void onCertificateStored(AbsCustomTrustManager certTrustManager) {
        onCertificateStored.countDown();
    }

}
