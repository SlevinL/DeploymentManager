package de.bas.deploymentmanager.data;

import de.bas.deploymentmanager.logic.domain.project.control.ImageRepository;
import de.bas.deploymentmanager.logic.domain.project.entity.Image;
import de.bas.deploymentmanager.logic.domain.project.entity.Tag;
import de.bas.deploymentmanager.logic.domain.project.entity.Version;
import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImageDeleteException;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class ImageRepositoryImpl extends AbstractRepository implements ImageRepository {


    @Override
    public List<Image> getImagesForProject(Long projectId) {
        TypedQuery<Image> selectAll = entityManager.createQuery("SELECT image FROM Image image WHERE image.projectId = :applicationId " +
                "ORDER BY image.tag.version.majorVersion, image.tag.version.minorVersion, image.tag.version.incrementalVersion, image.tag.buildNumber desc ", Image.class);
        selectAll.setParameter("applicationId", projectId);
        return selectAll.getResultList();
    }

    @Override
    public Optional<Image> getLastImageOfVersion(Long applicationId, Version version) {
        TypedQuery<Image> selectAll = entityManager.createQuery("SELECT image FROM Image image " +
                "WHERE image.projectId = :applicationId " +
                "AND image.tag.version.majorVersion = :majorVersion " +
                "AND image.tag.version.minorVersion = :minorVersion " +
                "AND image.tag.version.incrementalVersion = :incrementalVersion " +
                "ORDER BY image.tag.buildNumber DESC", Image.class);
        selectAll.setParameter("applicationId", applicationId);
        selectAll.setParameter("majorVersion", version.getMajorVersion());
        selectAll.setParameter("minorVersion", version.getMinorVersion());
        selectAll.setParameter("incrementalVersion", version.getIncrementalVersion());
        List<Image> resultList = selectAll.getResultList();
        if (resultList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(resultList.get(0));
    }

    @Override
    public Image save(Image image) {
        Image saved = entityManager.merge(image);
        entityManager.flush();
        return saved;
    }

    @Override
    public Image getImageByIdentifierTag(String identifier, Tag tag) {
        TypedQuery<Image> selectAll = entityManager.createQuery("SELECT image FROM Image image, Project app " +
                "WHERE app.id = image.projectId " +
                "AND app.identifier =:identifier " +
                "AND image.tag =:tag", Image.class);
        selectAll.setParameter("identifier", identifier);
        selectAll.setParameter("tag", tag);
        return selectAll.getSingleResult();
    }

    @Override
    public Image getImageByProjectIdTag(Long applicationId, Tag tag) {
        TypedQuery<Image> selectAll = entityManager.createQuery("SELECT image FROM Image image " +
                "WHERE image.projectId =:applicationId " +
                "AND image.tag =:tag", Image.class);
        selectAll.setParameter("applicationId", applicationId);
        selectAll.setParameter("tag", tag);
        return selectAll.getSingleResult();


    }

    @Override
    public void delete(Long id) throws ImageDeleteException {
        try {
            entityManager.remove(entityManager.find(Image.class, id));
            entityManager.flush();
        } catch (PersistenceException e) {
            throw new ImageDeleteException(e.getMessage());
        }
    }

    @Override
    public Image getById(Long imageId) {
        return entityManager.find(Image.class, imageId);
    }

    @Override
    public void deleteByProjectId(Long projectId) throws ImageDeleteException {
        List<Image> imagesForProject = getImagesForProject(projectId);
        for (Image image : imagesForProject) {
            delete(image.getId());
        }
    }
}
