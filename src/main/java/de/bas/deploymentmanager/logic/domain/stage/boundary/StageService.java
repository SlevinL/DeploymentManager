package de.bas.deploymentmanager.logic.domain.stage.boundary;

import de.bas.deploymentmanager.logic.domain.project.entity.Image;
import de.bas.deploymentmanager.logic.domain.stage.entity.App;
import de.bas.deploymentmanager.logic.domain.stage.entity.Stage;
import de.bas.deploymentmanager.logic.domain.stage.entity.StageEnum;

import java.util.List;
import java.util.Optional;

public interface StageService {

    /**
     * Markiert eine App als Deployed.
     * Markiert
     *
     * @param identifier der Application die deployed wurde
     * @param image      das deployed wurde
     * @param hostName   auf dem deployed wurde
     * @param port       auf dem Host
     */
    void imageDeployed(String identifier, Image image, String hostName, String port);


    Stage getStage(StageEnum stage);

    /**
     * Holt alle aktuellen deployments eines Projektes.
     *
     * @param identifier ProjectIdentifier
     * @return App (deployed on Host in Stage)
     */
    List<App> getAppsForProject(String identifier);


    /**
     * Prüft ob ein Image in einer Stage deployed ist.
     * Ist das der Fall, wird die jeweilige App zurückgegeben.
     *
     * @param imageId DB-Id
     * @return Liste an Apps
     */
    Optional<List<App>> isImageDeployed(Long imageId);

    void undeployImages(List<Long> imagesToDelete);
}
