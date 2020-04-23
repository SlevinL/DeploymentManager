package de.bas.deploymentmanager.logic.domain.project.control;

import de.bas.deploymentmanager.logic.domain.project.entity.Project;

import javax.persistence.NoResultException;
import java.util.List;

public interface ProjectRepository {
    List<Project> getAllProjects();

    /**
     * Sucht ein Projekt anhand des Identifiers
     * @param identifier Projekt
     * @return Projekt
     * @throws NoResultException Wenn kein Projekt gefunden wird
     */
    Project getByIfentifier(String identifier) throws NoResultException;

    Project save(Project project);

    Project getById(Long id);

    void delete(Long projectId);
}
