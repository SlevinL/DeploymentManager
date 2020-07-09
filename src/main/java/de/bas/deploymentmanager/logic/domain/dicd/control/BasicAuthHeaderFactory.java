package de.bas.deploymentmanager.logic.domain.dicd.control;

import de.bas.deploymentmanager.data.KeycloakUser;
import de.bas.deploymentmanager.logic.domain.dicd.entity.User;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Base64;

public class BasicAuthHeaderFactory implements ClientHeadersFactory {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BASIC = "Basic ";

    @Inject
    @KeycloakUser
    private UserRepository userRepository;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> clientOutgoingHeaders) {
        User user = userRepository.getUserByLoginName();
        clientOutgoingHeaders.add(AUTHORIZATION, getAuthorizationValue(user));
        return clientOutgoingHeaders;
    }

    private String getAuthorizationValue(User user) {
        return BASIC + encodeUserToken(user.getLoginName(), user.getApiToken());
    }

    private String encodeUserToken(String user, String token) {
        String userToken = user + ":" + token;
        return new String(Base64.getEncoder().encode(userToken.getBytes()));
    }
}
