package de.bas.deploymentmanager.logic.domain.project.boundary;

import de.bas.deploymentmanager.logic.domain.project.entity.Image;
import de.bas.deploymentmanager.logic.domain.project.entity.NewImageModel;
import de.bas.deploymentmanager.logic.domain.project.entity.Project;
import de.bas.deploymentmanager.logic.domain.project.entity.Tag;
import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImageDeleteException;
import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImgageNotFoundException;
import de.bas.deploymentmanager.logic.domain.stage.entity.Stage;

import java.util.List;

public interface ProjectService {

    /**
     * Holt alle Applications
     *
     * @return .
     */
    List<Project> getAllProjects();

    /**
     * Lädt eine Application inkl. Images
     *
     * @param identifier der Application
     * @return .
     */
    Project getProject(String identifier);

    /**
     * Erstellt ein neues Image und gibt das Tag wieder zurück
     *
     * @param identifier    für die Application
     * @param newImageModel Informationen des Image
     * @return {tag}
     */
    Tag generateNewImage(String identifier, NewImageModel newImageModel);

    /**
     * Erzeugt eine neue Buildnummer und gibt das TAG zurück
     *
     * @param projectIdintifier Projekt
     * @param version           Aktuelle Version
     * @return Tag für das Image
     */
    String gererateNextTag(String projectIdintifier, String version);

    /**
     * Erstellt für ein Image ein Deployment
     *
     * @param identifier .
     * @param tag        Tag
     * @param stage      Stage
     * @return Image inkl. Deployment
     */
    Image markImageAsDeployed(String identifier, Tag tag, Stage stage);

    Project createNewProject(Project model);

    List<Image> getImages(String identifier);

    List<Image> getImages(Long projectId);

    Image getImage(Long applicationId, Tag tag) throws ImgageNotFoundException;

    Project getProject(Long id);

    Project save(Project project);

    /**
     * Prüft ob für das übergebene Projekt ein ImageSync aktiv ist.
     * Wenn ja dann werden für alle weiteren Projekte ebenfalls Images angelegt.
     *
     * @param projectIdentifier Initiles Projekt
     * @param newImageModel     Model für das neue Image
     * @param tagToSync         Tag der erstellt wurde und der gesynced wird
     */
    void syncImages(String projectIdentifier, NewImageModel newImageModel, Tag tagToSync);

    /**
     * Löscht ein Image aus der Datenbank
     * Ein Image kann nur gelöscht werden, wenn es nicht die höchste Buildnummer einer Version ist.
     * Und wenn das Image nicht auf einer Stage deployed ist.
     *
     * @param imageId Id
     */
    void deleteImage(Long imageId) throws ImageDeleteException;

    /**
     * Löscht alle Images und das Project aus der Datenbank.
     *
     * @param projectId
     */
    void deleteProject(Long projectId) throws ImageDeleteException;

    /**
     * Holt das Tag des Images, welches zuletzt gebaut wurde
     *
     * @param identifier
     * @return
     */
    Tag getLatestTag(String identifier);

    Image getImageById(Long imageId);
}
