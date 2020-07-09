package de.bas.deploymentmanager.data;

import de.bas.deploymentmanager.logic.domain.dicd.control.UserRepository;
import de.bas.deploymentmanager.logic.domain.dicd.entity.User;

import javax.ejb.Stateless;
import javax.security.enterprise.SecurityContext;

@Stateless
@KeycloakUser
public class KeycloakUserRepositoryImpl extends AbstractRepository implements UserRepository {


    private SecurityContext securityContext;

    @Override
    public User getUserByLoginName() {
        User user = entityManager.find(User.class, securityContext.getCallerPrincipal().getName());
        if (user == null) {
            user = new User();
            user.setId(securityContext.getCallerPrincipal().getName());
        }
        return user;
    }

    @Override
    public User save(User user) {
        User saved = entityManager.merge(user);
        entityManager.flush();
        return saved;
    }
}
