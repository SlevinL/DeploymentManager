package de.bas.deploymentmanager.logic.business.deleteproject;

import de.bas.deploymentmanager.logic.domain.project.boundary.ProjectService;
import de.bas.deploymentmanager.logic.domain.project.entity.Image;
import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImageDeleteException;
import de.bas.deploymentmanager.logic.domain.stage.boundary.StageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class DeleteProjectFlowImpl implements DeleteProjectFlow {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ProjectService projectService;
    private final StageService stageService;

    @Inject
    public DeleteProjectFlowImpl(ProjectService projectService, StageService stageService) {
        this.projectService = projectService;
        this.stageService = stageService;
    }

    @Override
    public void deleteProject(Long projectId) throws ImageDeleteException {
        log.info("Project mit ID {} soll gelöscht werden", projectId);
        deleteApps(projectId);
        projectService.deleteProject(projectId);
    }

    /**
     * Holt alle Images eines Projectes und löscht die deployten Apps
     *
     * @param projectId Project ID
     */
    private void deleteApps(Long projectId) {
        List<Image> imagesToDelete = projectService.getImages(projectId);
        List<Long> imageIds = imagesToDelete.stream().map(Image::getId).collect(Collectors.toList());
        stageService.undeployImages(imageIds);
    }
}
