package de.bas.deploymentmanager.logic.business.deploy;

import de.bas.deploymentmanager.logic.domain.project.entity.Tag;
import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImgageNotFoundException;
import de.bas.deploymentmanager.logic.domain.stage.entity.StageEnum;


public interface DeployFlow {
    /**
     * Image wurde erfolgreich auf dem Host deployed
     * <p>
     * Image mit dem Tag wird ein Deployment erstellt
     * Auf dem {@link de.bas.deploymentmanager.logic.domain.stage.entity.Host} wird eine {@link de.bas.deploymentmanager.logic.domain.stage.entity.App} angelegt
     *
     * @param identifier
     * @param stage
     * @param tag
     * @param host
     * @param port
     */
    void imageDeployed(String identifier, StageEnum stage, String tag, String host, String port);

    /**
     * Startet einen JOB im Jenkins und deployed so das Image auf einem Host
     *
     * @param identifier ProjectIdentifier
     * @param stage
     * @param tag
     */
    void deployImage(String identifier, StageEnum stage, Tag tag) throws ImgageNotFoundException;
}
