package de.bas.deploymentmanager.frontend.jsf.pipeline;

import de.bas.deploymentmanager.logic.business.build.BuildFlow;
import de.bas.deploymentmanager.logic.business.deleteimage.DeleteImageFlow;
import de.bas.deploymentmanager.logic.business.deploy.DeployFlow;
import de.bas.deploymentmanager.logic.business.loadpipeline.LoadPipelineFlow;
import de.bas.deploymentmanager.logic.business.loadpipeline.PipelineFormModel;
import de.bas.deploymentmanager.logic.business.loadpipeline.ProjectStageModel;
import de.bas.deploymentmanager.logic.domain.project.entity.Image;
import de.bas.deploymentmanager.logic.domain.project.entity.Project;
import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImageDeleteException;
import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImgageNotFoundException;
import de.bas.deploymentmanager.logic.domain.stage.entity.StageEnum;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ViewScoped
@Named
public class PipelineFormBean implements Serializable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Getter
    private List<Image> images;
    @Getter
    private Project project;
    @Getter
    private List<ProjectStageModel> appsDeployed;

    @Inject
    private LoadPipelineFlow loadProjectFlow;

    @Inject
    private DeployFlow deployFlow;

    @Inject
    private BuildFlow buildFlow;

    @Inject
    private DeleteImageFlow deleteImageFlow;

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap();
        String id = params.get("id");
        PipelineFormModel model = loadProjectFlow.load(Long.valueOf(id));
        images = model.getProject().getImages();
        project = model.getProject();
        appsDeployed = model.getDeployedOn();
    }

    public void deploy(Image image, StageEnum stage) throws ImgageNotFoundException {
        log.info("Deploy Projekt {} auf Stage {}", project.getName(), stage);
        deployFlow.deployImage(project.getIdentifier(), stage, image.getTag());
    }

    public void build() {
        buildFlow.build(project.getId());
    }

    public String getStyle(Image image, StageEnum stageEnum) {
        if (image.getDeployments() != null) {
            if (image.getDeployments().stream().anyMatch(deployment -> Objects.equals(deployment.getStage(), stageEnum))) {
                return "bg-green";
            }
        }
        return "";
    }

    public String getIcon(Image image, StageEnum stageEnum) {
        if (image.getDeployments() != null) {
            if (image.getDeployments().stream().anyMatch(deployment -> Objects.equals(deployment.getStage(), stageEnum))) {
                return "fa fa-check";
            }
        }
        return "";
    }

    public void deleteImage(Image image) {
        log.info("Lösche Image {}", image.getImageWithTag());
        try {
            deleteImageFlow.deleteImage(image.getId());
        } catch (ImageDeleteException e) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Löschen fehlgeschlagen", "Das Image konnte nicht gelöscht werden: " + e.getMessage()));
        }
    }

}
