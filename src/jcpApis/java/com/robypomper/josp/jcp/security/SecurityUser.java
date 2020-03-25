package com.robypomper.josp.jcp.security;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;


/**
 * Utils class that return the user id of the user authenticated with JCP security.
 *
 * The user id returned refers to original user logged in to application client
 * even if the method {@link #getUserID()} was call from the Resource Server.
 */
public class SecurityUser {

    public static final String USER_ANONYMOUS = "AnonymousUser";


    // Current user getters

    private static Map<String,Object> getUserAttrs() throws AuthNotFoundException, UserNotAuthenticated {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof OAuth2AuthenticationToken) {
            return ((DefaultOAuth2User) auth.getPrincipal()).getAttributes();

        } else if (auth instanceof JwtAuthenticationToken) {
            return ((JwtAuthenticationToken) auth).getTokenAttributes();

        } else if (auth instanceof AnonymousAuthenticationToken) {
            throw new UserNotAuthenticated((AnonymousAuthenticationToken) auth);

        } else {
            throw new AuthNotFoundException(auth);
        }
    }

    public static String getUserID() throws AuthNotFoundException, UserNotAuthenticated {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof OAuth2AuthenticationToken)
            return (String) getUserAttrs().get("auth_provider_unique");

        else if (auth instanceof JwtAuthenticationToken)
            return (String) getUserAttrs().get("sub");

        else if (auth instanceof KeycloakAuthenticationToken)
            return ((KeycloakPrincipal<? extends KeycloakSecurityContext>) auth.getPrincipal()).getKeycloakSecurityContext().getToken().getSubject();

        else throw new AuthNotFoundException(auth);
    }

    public static String getUserName() throws AuthNotFoundException, UserNotAuthenticated {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof OAuth2AuthenticationToken)
            return (String) getUserAttrs().get("auth_provider_username");

        else if (auth instanceof JwtAuthenticationToken)
            return (String) getUserAttrs().get("preferred_username");

        else if (auth instanceof KeycloakAuthenticationToken)
            return ((KeycloakPrincipal) auth.getPrincipal()).getName();

        else throw new AuthNotFoundException(auth);
    }

    public static Collection<String> getUserRoles() throws AuthNotFoundException, UserNotAuthenticated {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof OAuth2AuthenticationToken)
            return Arrays.asList(((String) getUserAttrs().get("realm_access")).split(" "));

        else if (auth instanceof JwtAuthenticationToken)
            return Arrays.asList(((String) getUserAttrs().get("realm_access")).split(" "));

        else if (auth instanceof KeycloakAuthenticationToken)
            return ((SimpleKeycloakAccount) auth.getDetails()).getRoles();

        else throw new AuthNotFoundException(auth);
    }

    public static Collection<String> getUserScopes() throws AuthNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof OAuth2AuthenticationToken
                || auth instanceof JwtAuthenticationToken) {
            Collection<String> authority = new ArrayList<>();
            auth.getAuthorities().forEach(grantedAuthority -> authority.add(grantedAuthority.getAuthority()));
            return authority;
        }

        else if (auth instanceof KeycloakAuthenticationToken)
            return Arrays.asList(((KeycloakPrincipal<? extends KeycloakSecurityContext>) auth.getPrincipal()).getKeycloakSecurityContext().getToken().getScope().split("\\s"));

        else throw new AuthNotFoundException(auth);
    }

    public static String getUserClientId() throws AuthNotFoundException, UserNotAuthenticated {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof OAuth2AuthenticationToken
                || auth instanceof JwtAuthenticationToken) {
            if (getUserAttrs().get("clientId") != null)
                return (String) getUserAttrs().get("clientId");     // Client Credential Flow
            if (getUserAttrs().get("azp") != null)
                return (String) getUserAttrs().get("azp");               // Auth Code Flow
        }

        else if (auth instanceof KeycloakAuthenticationToken)
            return ((KeycloakPrincipal<? extends KeycloakSecurityContext>) auth.getPrincipal()).getKeycloakSecurityContext().getToken().getIssuedFor();

        else throw new AuthNotFoundException(auth);

        return null;
    }


    // Not found exception

    public static class AuthNotFoundException extends Exception {
        public AuthNotFoundException(Authentication auth) {
            super(String.format("No authorization found in class %s",auth!=null?auth.getClass().getName():"NULL"));
        }
    }

    public static class UserNotAuthenticated extends Exception {
        public UserNotAuthenticated(AnonymousAuthenticationToken auth) {
            super("User not authenticated");
        }
    }

}
