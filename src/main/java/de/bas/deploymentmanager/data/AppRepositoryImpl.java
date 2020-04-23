package de.bas.deploymentmanager.data;

import de.bas.deploymentmanager.logic.domain.stage.control.AppRepository;
import de.bas.deploymentmanager.logic.domain.stage.entity.App;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class AppRepositoryImpl extends AbstractRepository implements AppRepository {
    @Override
    public App save(App app) {
        App saved = entityManager.merge(app);
        entityManager.flush();
        return saved;
    }

    @Override
    public void delete(App app) {
        entityManager.remove(app);
    }

    @Override
    public List<App> getByProjectIdentifier(String identifier) {
        TypedQuery<App> apps = entityManager.createQuery("SELECT app FROM App app " +
                "WHERE app.projectIdentifier = :identifier", App.class);
        apps.setParameter("identifier", identifier);
        return apps.getResultList();
    }

    @Override
    public void deleteByImageId(List<Long> imagesToDelete) {
        imagesToDelete.forEach(id -> {
            Query query = entityManager.createQuery("DELETE FROM App app WHERE app.imageId =:imageIds");
            query.setParameter("imageIds", imagesToDelete);
            query.executeUpdate();
        });

    }
}
