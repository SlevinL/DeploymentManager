package de.bas.deploymentmanager.logic.domain.dicd.control;

import de.bas.deploymentmanager.logic.domain.dicd.entity.User;

public interface UserRepository {

    /**
     * Gibt einen Defaultuser zurück
     *
     * @return User
     */
    User getUserByLoginName();

    User save(User user);
}
