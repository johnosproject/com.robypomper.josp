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

package com.robypomper.josp.jsl.user;

import com.robypomper.discovery.Discover;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.clients.JCPClient2;
import com.robypomper.josp.clients.apis.srv.APIUsrsClient;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.params.usrs.UsrName;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.states.StateException;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 */
public class JSLUserMngr_002 implements JSLUserMngr, JCPClient2.LoginListener {

    // Class constants

    public static final String ANONYMOUS_ID = JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString();
    public static final String ANONYMOUS_USERNAME = JOSPPerm.WildCards.USR_ANONYMOUS_NAME.toString();


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLSettings_002 locSettings;
    private final JCPAPIsClientSrv jcpClient;
    private final APIUsrsClient apiUsrsClient;
    private UsrName user;
    private JSLCommunication comm = null;


    // Constructor

    public JSLUserMngr_002(JSLSettings_002 settings, JCPAPIsClientSrv jcpClient) {
        this.locSettings = settings;
        this.jcpClient = jcpClient;
        apiUsrsClient = new APIUsrsClient(jcpClient);

        if (jcpClient.isAuthCodeFlowEnabled()) {
            log.trace(Mrk_JSL.JSL_USR, "Perform JSLUserMngr login");
            if (jcpClient.isConnected())
                onLogin(jcpClient);
            else
                onLocalLogin();
        } else {
            onLogout(jcpClient);
            log.trace(Mrk_JSL.JSL_USR, "Set JSLUserMngr with anonymous user");
        }

        log.debug(Mrk_JSL.JSL_USR, "Setting login manager to JCPClient");
        this.jcpClient.addLoginListener(this);
        log.debug(Mrk_JSL.JSL_USR, "Login manager set to JCPClient");

        log.info(Mrk_JSL.JSL_USR, String.format("Initialized JSLUserMngr instance for '%s' user with '%s' id", getUsername(), getUserId()));
    }


    // User's info

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserAuthenticated() {
        return jcpClient.isAuthCodeFlowEnabled() && !user.getUsrId().equals(ANONYMOUS_ID)
                && user.isAuthenticated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAdmin() {
        return isUserAuthenticated() && user.isAdmin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMaker() {
        return isUserAuthenticated() && user.isMaker();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDeveloper() {
        return isUserAuthenticated() && user.isDeveloper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserId() {
        return user!=null ? user.getUsrId() : ANONYMOUS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return user!=null ? user.getUsername() : ANONYMOUS_USERNAME;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setCommunication(JSLCommunication communication) {
        this.comm = communication;
    }


    // LoginManager impl

    @Override
    public void onLogin(JCPClient2 jcpClient2) {
        // Cache user's info
        log.debug(Mrk_JSL.JSL_USR, "Caching user's info from JCP");
        try {
            this.user = apiUsrsClient.getUser();
            locSettings.setUsrId(getUserId());
            locSettings.setUsrName(getUsername());

            // Set JCP Client user id header
            jcpClient.setUserId(getUserId());

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.ResponseException | JCPClient2.RequestException e) {
            log.warn(Mrk_JSL.JSL_USR, String.format("Error on getting user id and name from JCP because %s", e.getMessage()), e);
            log.trace(Mrk_JSL.JSL_USR, "Set anonymous user");
            user = UsrName.ANONYMOUS;
        }

        log.info(Mrk_JSL.JSL_USR, String.format("Logged in user '%s' with id '%s'", getUsername(), getUserId()));

        if (comm == null)
            return;

        if (comm.getCloudConnection().isConnected()) {
            try {
                comm.getCloudConnection().disconnect();
            } catch (StateException e) {
                log.warn(Mrk_JSL.JSL_USR, String.format("Error on shutdown cloud communication on updating user id because %s", e.getMessage()), e);
            }
            try {
                comm.getCloudConnection().connect();
            } catch (StateException e) {
                log.warn(Mrk_JSL.JSL_USR, String.format("Error on starting cloud communication on updating user id because %s", e.getMessage()), e);
            }
        }
        if (comm.getLocalConnections().isRunning()) {
            try {
                comm.getLocalConnections().stop();
                comm.getLocalConnections().start();

            } catch (StateException | Discover.DiscoveryException e) {
                log.warn(Mrk_JSL.JSL_USR, String.format("Error on restart local communication on updating user id because %s", e.getMessage()), e);
            }
        }

    }

    @Override
    public void onLogout(JCPClient2 jcpClient2) {
        String loggedUsrId = getUserId();
        String loggedUsername = getUsername();

        user = UsrName.ANONYMOUS;
        locSettings.setUsrId(null);
        locSettings.setUsrName(null);

        // Set JCP Client user id header
        jcpClient.setUserId(null);

        log.info(Mrk_JSL.JSL_USR, String.format("Logged out user '%s' with id '%s'", loggedUsername, loggedUsrId));

        if (comm == null)
            return;

        if (comm.getCloudConnection().isConnected()) {
            try {
                comm.getCloudConnection().disconnect();
            } catch (StateException e) {
                log.warn(Mrk_JSL.JSL_USR, String.format("Error on shutdown cloud communication on updating user id because %s", e.getMessage()), e);
            }
            try {
                comm.getCloudConnection().connect();
            } catch (StateException e) {
                log.warn(Mrk_JSL.JSL_USR, String.format("Error on starting cloud communication on updating user id because %s", e.getMessage()), e);
            }
        }
        if (comm.getLocalConnections().isRunning()) {
            try {
                comm.getLocalConnections().stop();
                comm.getLocalConnections().start();

            } catch (StateException | Discover.DiscoveryException e) {
                log.warn(Mrk_JSL.JSL_USR, String.format("Error on restart local communication on updating user id because %s", e.getMessage()), e);
            }
        }
    }

    /**
     * Method to handle the local user login.
     * <p>
     * This method is called by {@link JSLUserMngr_002} constructor when user is
     * already logged but the JCP client is not connected.
     * <p>
     * It read user's id and username from local settings.
     */
    private void onLocalLogin() {
        // Cache user's info
        log.debug(Mrk_JSL.JSL_USR, "Set user's info from settings");
        user = UsrName.fromLocal(locSettings.getUsrId(),locSettings.getUsrName());

        // Set JCP Client user id header
        jcpClient.setUserId(user.getUsrId());

        log.info(Mrk_JSL.JSL_USR, String.format("Logged in user '%s' with id '%s'", user.getUsername(), user.getUsrId()));
    }

}
