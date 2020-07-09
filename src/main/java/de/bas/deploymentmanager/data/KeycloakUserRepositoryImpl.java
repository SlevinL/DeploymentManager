package de.bas.deploymentmanager.data;

import de.bas.deploymentmanager.logic.domain.dicd.control.UserRepository;
import de.bas.deploymentmanager.logic.domain.dicd.entity.User;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Stateless
@KeycloakUser
public class KeycloakUserRepositoryImpl extends AbstractRepository implements UserRepository {

    @Resource
    private SessionContext securityContext;


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
