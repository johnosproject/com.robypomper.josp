package com.robypomper.josp.jcp.external.resources.auth;

import org.springframework.stereotype.Component;


/**
 * Reference to Keycloak implementation of {@link AuthResource} interface.
 */
@Component
public class AuthDefault extends AuthKeycloak {}
