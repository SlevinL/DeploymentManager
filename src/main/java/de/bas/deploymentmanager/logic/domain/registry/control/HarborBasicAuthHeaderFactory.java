package de.bas.deploymentmanager.logic.domain.registry.control;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Base64;

public class HarborBasicAuthHeaderFactory implements ClientHeadersFactory {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BASIC = "Basic ";


    @Inject
    @ConfigProperty(name = "harbor.user")
    private String user;

    @Inject
    @ConfigProperty(name = "harbor.passwort")
    private String passwort;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> clientOutgoingHeaders) {
        clientOutgoingHeaders.add(AUTHORIZATION, getAuthorizationValue());
        return clientOutgoingHeaders;
    }

    private String getAuthorizationValue() {
        return BASIC + encodeUserToken(user, passwort);
    }

    private String encodeUserToken(String user, String passwort) {
        String userToken = user + ":" + passwort;
        return new String(Base64.getEncoder().encode(userToken.getBytes()));
    }
}
