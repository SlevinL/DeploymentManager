package de.bas.deploymentmanager.logic.domain.project.control;

import de.bas.deploymentmanager.logic.domain.project.boundary.ProjectService;
import de.bas.deploymentmanager.logic.domain.project.entity.*;
import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImageDeleteException;
import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImgageNotFoundException;
import de.bas.deploymentmanager.logic.domain.stage.entity.Stage;
import de.bas.deploymentmanager.logic.domain.stage.entity.StageEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
public class ProjectServiceImpl implements ProjectService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    private final ProjectRepository projectRepository;
    private final ImageRepository imageRepository;
    private final DeploymentRepository deploymentRepository;

    @Inject
    public ProjectServiceImpl(ProjectRepository projectRepository, ImageRepository imageRepository, DeploymentRepository deploymentRepository) {
        this.projectRepository = projectRepository;
        this.imageRepository = imageRepository;
        this.deploymentRepository = deploymentRepository;
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.getAllProjects();
    }

    @Override
    public Project getProject(String identifier) {
        Project project = projectRepository.getByIfentifier(identifier);
        project.setImages(imageRepository.getImagesForProject(project.getId()));
        return project;
    }

    @Override
    public Tag generateNewImage(String identifier, NewImageModel newImageModel) {
        Project project = null;
        try {
            project = projectRepository.getByIfentifier(identifier);
        } catch (NoResultException e) {
            log.info("Projekt mit den identifier {} wurde nicht gefunden", identifier);
            project = generateNewProject(identifier);
        }

        Tag newTag = generateNewTag(newImageModel.getVersion(), project);

        log.info("Neue Buildnumber ist: {}-{}"
                , newTag.getVersion()
                , newTag.getBuildNumber());

        try {
            Image imageByProjectIdTag = imageRepository.getImageByProjectIdTag(project.getId(), newTag);
            imageByProjectIdTag.setCreateDate(LocalDateTime.now());
            imageByProjectIdTag.setUser(newImageModel.getUser());
            imageByProjectIdTag.setCommit(newImageModel.getCommit());
            imageRepository.save(imageByProjectIdTag);
        } catch (ImgageNotFoundException e) {
            Image image = createNewImage(project.getId()
                    , newTag
                    , newImageModel);
            Image save = imageRepository.save(image);
        }
        return newTag;
    }

    @Override
    public String gererateNextTag(String projectIdintifier, String version) {
        Project project = null;
        try {
            project = projectRepository.getByIfentifier(projectIdintifier);
        } catch (NoResultException e) {
            log.info("Projekt mit den identifier {} wurde nicht gefunden", projectIdintifier);
            project = generateNewProject(projectIdintifier);
        }
        return generateNewTag(version, project).toString();
    }

    /**
     * Gibt die nächste Buildnummer zurück
     *
     * @param versionString .
     * @param project       .
     * @return
     */
    private Tag generateNewTag(String versionString, Project project) {
        Version version = new Version(versionString);

        log.debug("Übergebene Version ist: {}", version);

        Optional<Image> optionalImage = imageRepository.getLastImageOfVersion(project.getId(), version);

        int buildNumber = optionalImage.map(image -> {
            if (version.isSnapshot()) {
                return image.getTag().getBuildNumber();
            } else {
                return image.getTag().getBuildNumber() + 1;
            }
        }).orElse(1);


        return new Tag(version, buildNumber);
    }

    private Project generateNewProject(String identifier) {
        log.info("Neues Projekt wird generiert für Identifier {}", identifier);
        Project project = new Project();
        project.setIdentifier(identifier);
        return projectRepository.save(project);
    }

    @Override
    public Image markImageAsDeployed(String identifier, Tag tag, Stage stage) {
        Image image = imageRepository.getImageByIdentifierTag(identifier, tag);
        addDeployment(image, stage.getName());
        return image;
    }

    private void addDeployment(Image image, StageEnum stage) {
        if (image.getDeployments() != null) {
            Optional<Deployment> deploymentExists = image.getDeployments().stream().filter(deployment -> deployment.getStage().equals(stage)).findAny();
            if (deploymentExists.isPresent()) {
                deploymentExists.get().setCreateTime(LocalDateTime.now());
                deploymentRepository.save(deploymentExists.get());
                return;
            }
        }
        Deployment deployment = new Deployment();
        deployment.setCreateTime(LocalDateTime.now());
        deployment.setStage(stage);
        deployment.setImageId(image.getId());
        deploymentRepository.save(deployment);
    }

    private Image createNewImage(Long projectId, Tag tag, NewImageModel newImageModel) {
        Image image = new Image();
        image.setProjectId(projectId);
        image.setTag(tag);
        image.setCreateDate(LocalDateTime.now());
        image.setUser(newImageModel.getUser());
        image.setImage(newImageModel.getImage());
        image.setCommit(newImageModel.getCommit());
        return image;
    }


    @Override
    public Project createNewProject(Project model) {
        return projectRepository.save(model);
    }

    @Override
    public List<Image> getImages(String identifier) {
        Project project = projectRepository.getByIfentifier(identifier);
        return imageRepository.getImagesForProject(project.getId());
    }

    @Override
    public List<Image> getImages(Long projectId) {
        return imageRepository.getImagesForProject(projectId);

    }

    @Override
    public Image getImage(Long applicationId, Tag tag) throws ImgageNotFoundException {
        return imageRepository.getImageByProjectIdTag(applicationId, tag);
    }

    @Override
    public Project getProject(Long id) {
        return projectRepository.getById(id);
    }

    @Override
    public Project save(Project project) {
        Project save = projectRepository.save(project);
        if (save.getImageSync().isAktiv()) {
            log.info("ImageSync ist aktiviert für {}", save.getImageSync().getPersistedValue());
            saveImageSync(save.getImageSync());
        }
        log.info("Projekt {} wurde mit ID {} gespeichert", save.getIdentifier(), save.getId());
        return save;
    }

    @Override
    public void syncImages(String projectIdentifier, NewImageModel newImageModel, Tag tagToSync) {
        Project project = projectRepository.getByIfentifier(projectIdentifier);
        if (project.getImageSync().isAktiv()) {
            project.getImageSync().getProjekctIdentifiers().stream()
                    .filter(ident -> !ident.equals(projectIdentifier))
                    .forEach(s -> {
                        Project syncProject = projectRepository.getByIfentifier(s);
                        Image newImage = createNewImage(syncProject.getId(), tagToSync, newImageModel);
                        log.info("Speicher neues Imgage durch Sync {}", newImage.getImageWithTag());
                        imageRepository.save(newImage);
                    });

        }
    }

    @Override
    public void deleteImage(Long imageId) throws ImageDeleteException {
        log.info("Lösche Image mit ID: {}", imageId);
        isImageLastBuildnumber(imageId);
        imageRepository.delete(imageId);
    }

    @Override
    public void deleteProject(Long projectId) throws ImageDeleteException {
        imageRepository.deleteByProjectId(projectId);
        log.info("Alle Images für das Project {} gelöscht", projectId);
        projectRepository.delete(projectId);
        log.info("Project erfolgreich gelöscht: {}", projectId);
    }

    @Override
    public Tag getLatestTag(String identifier) {
        Project project = projectRepository.getByIfentifier(identifier);
        Image latest = imageRepository.getLatestImage(project.getId());
        return latest.getTag();
    }

    /**
     * Prüft ob das Image der letzte Build einer Version ist.
     * Wenn ja dann kann das Image nicht gelöscht werden
     *
     * @param imageId
     * @throws ImageDeleteException
     */
    private void isImageLastBuildnumber(Long imageId) throws ImageDeleteException {
        Image image = imageRepository.getById(imageId);
        Optional<Image> lastImageOfVersion = imageRepository.getLastImageOfVersion(image.getProjectId()
                , image.getVersion());
        if (lastImageOfVersion.isPresent()) {
            if (image.equals(lastImageOfVersion.get())) {
                throw new ImageDeleteException("Image ist der aktuelle Build");
            }
        }
    }

    private void saveImageSync(ImageSync imageSync) {
        imageSync.getProjekctIdentifiers().forEach(identifier -> {
            Project project = projectRepository.getByIfentifier(identifier);
            project.setImageSync(imageSync);
            projectRepository.save(project);
        });
    }
}
