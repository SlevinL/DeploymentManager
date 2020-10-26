package de.bas.deploymentmanager.logic.business.deleteimage;

import de.bas.deploymentmanager.logic.domain.project.boundary.ProjectService;
import de.bas.deploymentmanager.logic.domain.project.entity.Image;
import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImageDeleteException;
import de.bas.deploymentmanager.logic.domain.registry.boundary.RegistryService;
import de.bas.deploymentmanager.logic.domain.stage.boundary.StageService;

import javax.ejb.Stateless;
import javax.inject.Inject;


@Stateless
public class DeleteImageFlowImpl implements DeleteImageFlow {

    private final ProjectService projectService;
    private final StageService stageService;
    private final RegistryService registryService;

    @Inject
    public DeleteImageFlowImpl(ProjectService projectService, StageService stageService, RegistryService registryService) {
        this.projectService = projectService;
        this.stageService = stageService;
        this.registryService = registryService;
    }

    @Override
    public void deleteImage(Long imageId) throws ImageDeleteException {
        if (stageService.isImageDeployed(imageId).isPresent()) {
            throw new ImageDeleteException("Image ist noch deployed");
        }
        Image imageToDelete = projectService.getImageById(imageId);
        projectService.deleteImage(imageId);
        registryService.deleteImage(imageToDelete.getImageWithTag());
    }
}
