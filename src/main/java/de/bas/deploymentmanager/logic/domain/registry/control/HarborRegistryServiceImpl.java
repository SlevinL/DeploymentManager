package de.bas.deploymentmanager.logic.domain.registry.control;

import de.bas.deploymentmanager.logic.domain.registry.boundary.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Stateless
public class HarborRegistryServiceImpl implements RegistryService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final HarborClient harborClient;

    @Inject
    public HarborRegistryServiceImpl(HarborClient harborClient) {
        this.harborClient = harborClient;
    }

    @Override
    public void deleteImage(String image) {
        log.info("Folgendes Image wird aus der Registry gelöscht: {}", image);

        String[] split = image.split("/");
        String project = split[1];
        String repositoryWithTag = split[2];
        String repository = repositoryWithTag.split(":")[0];
        String tag = repositoryWithTag.split(":")[1];

        try {
            harborClient.deleteArtifact(project, repository, tag);
        } catch (WebApplicationException e) {
            if (!(e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode())) {
                throw e;
            }
            log.warn("das Image {} wurde nicht in der Registry gefunden", image);
        }

    }
}
