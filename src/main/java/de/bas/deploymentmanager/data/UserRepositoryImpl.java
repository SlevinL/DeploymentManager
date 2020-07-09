package de.bas.deploymentmanager.data;

import de.bas.deploymentmanager.logic.domain.dicd.control.UserRepository;
import de.bas.deploymentmanager.logic.domain.dicd.entity.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;

public class UserRepositoryImpl implements UserRepository {

    @Inject
    @ConfigProperty(name = "jenkins.user")
    private String loginName;

    @Inject
    @ConfigProperty(name = "jenkins.apikey")
    private String apiKey;


    @Override
    public User getUserByLoginName() {
        User user = new User();
        user.setLoginName(loginName);
        user.setApiToken(apiKey);
        return user;
    }

    @Override
    public User save(User user) {
        return user;
    }
}
