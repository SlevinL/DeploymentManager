package de.bas.deploymentmanager.frontend.jsf.project;

import de.bas.deploymentmanager.logic.business.deleteproject.DeleteProjectFlow;
import de.bas.deploymentmanager.logic.business.loadproject.LoadProjectFlow;
import de.bas.deploymentmanager.logic.business.loadproject.ProjectFormModel;
import de.bas.deploymentmanager.logic.domain.project.entity.Image;
import de.bas.deploymentmanager.logic.domain.project.entity.ImageSync;
import de.bas.deploymentmanager.logic.domain.project.entity.Project;
import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImageDeleteException;
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
import java.util.Map;
import java.util.stream.Collectors;

@ViewScoped
@Named
public class ProjectFormBean implements Serializable {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final LoadProjectFlow loadProjectFlow;
    private final DeleteProjectFlow deleteProjectFlow;

    @Getter
    private ProjectFormModel model;


    public Project getProject() {
        return this.model.getProject();
    }

    @Inject
    public ProjectFormBean(LoadProjectFlow loadProjectFlow, DeleteProjectFlow deleteProjectFlow) {
        this.loadProjectFlow = loadProjectFlow;
        this.deleteProjectFlow = deleteProjectFlow;
    }


    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap();
        String id = params.get("id");
        if (id != null) {
            this.model = loadProjectFlow.load(Long.valueOf(id));
        } else {
            this.model = ProjectFormModel.builder().project(new Project()).selectetSync(new ImageSync(null)).build();
        }
    }

    public void save() {
        model = loadProjectFlow.save(model);
    }

    public String getDeployments(Image image) {
        if (image.getDeployments() != null) {
            return image.getDeployments().stream().map(deployment -> deployment.getStage().name()).collect(Collectors.joining(" : "));
        }
        return null;
    }

    public String deleteProject() {
        try {
            deleteProjectFlow.deleteProject(getProject().getId());
        } catch (ImageDeleteException e) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Löschen fehlgeschlagen", "Das Projekt konnte nicht gelöscht werden: " + e.getMessage()));
        }
        return "table.xhtml";
    }
}
