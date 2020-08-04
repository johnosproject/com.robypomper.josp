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

package com.robypomper.josp.test.mocks.jsl;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;

import java.util.Map;

public class MockJCPClient_Service implements JCPClient_Service {
    @Override
    public void setServiceId(String srvId) {

    }

    @Override
    public void setUserId(String usrId) {

    }

    @Override
    public void setLoginCodeAndReconnect(String loginCode) throws ConnectionException, AuthenticationException, JCPNotReachableException {}

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void connect() throws JCPNotReachableException, ConnectionException, AuthenticationException {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void addConnectListener(ConnectListener listener) {

    }

    @Override
    public void removeConnectListener(ConnectListener listener) {

    }

    @Override
    public void addDisconnectListener(DisconnectListener listener) {

    }

    @Override
    public void removeDisconnectListener(DisconnectListener listener) {

    }

    @Override
    public boolean isConnecting() {
        return false;
    }

    @Override
    public void startConnectionTimer() {

    }

    @Override
    public void stopConnectionTimer() {

    }

    @Override
    public boolean isClientCredentialFlowEnabled() {
        return false;
    }

    @Override
    public boolean isAuthCodeFlowEnabled() {
        return false;
    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public String getLoginUrl() {
        return null;
    }

    @Override
    public void setLoginCode(String loginCode) {

    }

    @Override
    public void userLogout() {

    }

    @Override
    public void addLoginListener(LoginListener listener) {

    }

    @Override
    public void removeLoginListener(LoginListener listener) {

    }

    @Override
    public void addDefaultHeader(String headerName, String headerValue) {

    }

    @Override
    public void removeDefaultHeader(String headerName) {

    }

    @Override
    public boolean isSessionSet() {
        return false;
    }

    @Override
    public void execReq(Verb reqType, String path) throws ConnectionException, RequestException, ResponseException {

    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path) throws ConnectionException, RequestException, ResponseException {}

    @Override
    public void execReq(Verb reqType, String path, boolean secure) throws ConnectionException, RequestException, ResponseException {

    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path, boolean secure) throws ConnectionException, RequestException, ResponseException {}

    @Override
    public void execReq(Verb reqType, String path, Map<String, String> params, boolean secure) throws ConnectionException, RequestException, ResponseException {

    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path, Map<String, String> params, boolean secure) throws ConnectionException, RequestException, ResponseException {}

    @Override
    public void execReq(Verb reqType, String path, Object objParam, boolean secure) throws ConnectionException, RequestException, ResponseException {

    }

    @Override
    public void execReq(boolean toAuth, Verb reqType, String path, Object objParam, boolean secure) throws ConnectionException, RequestException, ResponseException {}

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, boolean secure) throws ConnectionException, RequestException, ResponseException {
        return null;
    }

    @Override
    public <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, boolean secure) throws ConnectionException, RequestException, ResponseException {
        return null;
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, Map<String, String> params, boolean secure) throws ConnectionException, RequestException, ResponseException {
        return null;
    }

    @Override
    public <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, Map<String, String> params, boolean secure) throws ConnectionException, RequestException, ResponseException {
        return null;
    }

    @Override
    public <T> T execReq(Verb reqType, String path, Class<T> reqObject, Object objParam, boolean secure) throws ConnectionException, RequestException, ResponseException {
        return null;
    }

    @Override
    public <T> T execReq(boolean toAuth, Verb reqType, String path, Class<T> reqObject, Object objParam, boolean secure) throws ConnectionException, RequestException, ResponseException {
        return null;
    }
}
