package de.bas.deploymentmanager.logic.business.deleteproject;

import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImageDeleteException;

public interface DeleteProjectFlow {

    /**
     * Löscht ein Projekt und alle dazugehörigen Images, Deployments und Apps
     *
     * @param projectId Datenbank Id
     */
    void deleteProject(Long projectId) throws ImageDeleteException;
}
