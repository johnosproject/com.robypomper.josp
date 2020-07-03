package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.user.JSLUserMngr_002;

import java.util.Scanner;

public class CmdsJCPClient {

    private final JCPClient_Service jcpClient;

    public CmdsJCPClient(JCPClient_Service jcpClient) {
        this.jcpClient = jcpClient;
    }

    // Client connection

    /**
     * Checks and print JCP Client status.
     * <p>
     * The returned string include also if the user is logged or not.
     *
     * @return a pretty printed string containing the JCP client status.
     */
    @Command(description = "Print JCP Client status.")
    public String jcpClientStatus() {
        String s = jcpClient.isConnected() ? "JCP Client is connect" : "JCP Client is NOT connect";
        s += " ";
        s += jcpClient.isAuthCodeFlowEnabled() ? "(user logged in)." : "(user not logged).";
        return s;
    }

    /**
     * Connect the JCP Client to the JCP Cloud.
     * <p>
     * The method called start the authentication process, if it terminate
     * successfully then the client result connected, otherwise it result as
     * disconnected.
     * <p>
     * It always authenticate the service with Client Credential flow. To the
     * otherside, it authenticate also the user with Authentication Code flow
     * only if the refresh token or authentication token are set.
     *
     * @return a string indicating if the client connected successfully or not.
     */
    @Command(description = "Connect JCP Client.")
    public String jcpClientConnect() {
        if (jcpClient.isConnected())
            return "JCP Client already connected.";

        try {
            jcpClient.connect();

        } catch (JCPClient2.ConnectionException | JCPClient2.JCPNotReachableException | JCPClient2.AuthenticationException e) {
            return String.format("Error on JCP Client connection: %s.", e.getMessage());
        }
        return "JCP Client connected successfully.";
    }

    /**
     * Disconnect the JCP Client to the JCP Cloud.
     * <p>
     * The method called reset all client's connection reference.
     * <p>
     * It always disconnect the service authenticated with Client Credential
     * flow. Meanwhile, the Authentication Code flow is disconnected only if it
     * was previously connected.
     *
     * @return a string indicating if the client connected successfully or not.
     */
    @Command(description = "Disconnect JCP Client.")
    public String jcpClientDisconnect() {
        if (!jcpClient.isConnected())
            return "JCP Client already disconnected.";

        jcpClient.disconnect();
        return "JCP Client disconnected successfully.";
    }


    // User login

    /**
     * With this method users can login to JCP cloud thought current service.
     * <p>
     * It print the logging url, that must be visited by the user. Then after
     * the user perform the authentication, in the url bar he can find the
     * authorization code (the authentication server redirect to an url
     * containing the code as param). Finally the user must copy that code in the
     * service console.
     *
     * <b>NB:</b> this method trigger the {@link JCPClient_Service.LoginManager#onLogin()}
     * event, handled by {@link JSLUserMngr_002#onLogin()} method.
     *
     * @return a string indicating if the user was logged in successfully or not.
     */
    @Command(description = "Login user to JCP Client.")
    public String jcpUserLogin() {
        if (jcpClient.isAuthCodeFlowEnabled())
            return "User already logged in.";

        final Scanner in = new Scanner(System.in);

        String url = jcpClient.getLoginUrl();
        System.out.println("Please open following url and login to JCP Cloud");
        System.out.println(url);
        System.out.println("then paste the redirected url 'code' param");
        System.out.print("<<");
        String code = in.nextLine();


        try {
            jcpClient.setLoginCodeAndReconnect(code);

        } catch (JCPClient2.JCPNotReachableException | JCPClient2.ConnectionException | JCPClient2.AuthenticationException e) {
            return String.format("Can't proceed with user login because %s", e.getMessage());
        }

        return "User logged in successfully";
    }

    /**
     * With this method users can logout to JCP cloud thought current service.
     *
     * <b>NB:</b> this method trigger the {@link JCPClient_Service.LoginManager#onLogout()} ()}
     * event, handled by {@link JSLUserMngr_002#onLogout()} method.
     *
     * @return a string indicating if the user was logged out successfully or not.
     */
    @Command(description = "Logout user to JCP Client.")
    public String jcpUserLogout() {
        if (!jcpClient.isAuthCodeFlowEnabled())
            return "User already logged out.";

        jcpClient.userLogout();
        return "User logged out successfully";
    }

}
