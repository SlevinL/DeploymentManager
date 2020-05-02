package de.bas.deploymentmanager.logic.domain.project.control;

import de.bas.deploymentmanager.logic.domain.project.entity.Image;
import de.bas.deploymentmanager.logic.domain.project.entity.Tag;
import de.bas.deploymentmanager.logic.domain.project.entity.Version;
import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImageDeleteException;

import java.util.List;
import java.util.Optional;

public interface ImageRepository {
    List<Image> getImagesForProject(Long projectId);

    Optional<Image> getLastImageOfVersion(Long applicationId, Version version);

    Image save(Image image);

    Image getImageByIdentifierTag(String identifier, Tag tag);

    Image getImageByProjectIdTag(Long applicationId, Tag tag);

    void delete(Long id) throws ImageDeleteException;

    Image getById(Long imageId);

    /**
     * Löscht alle Images eines Projects
     *
     * @param projectId Id Project
     */
    void deleteByProjectId(Long projectId) throws ImageDeleteException;

    Image getLatestImage(Long projectId);
}
