package de.bas.deploymentmanager.logic.domain.dicd.control;

import de.bas.deploymentmanager.logic.domain.dicd.boundary.CiCdService;
import de.bas.deploymentmanager.logic.domain.dicd.entity.User;
import de.bas.deploymentmanager.logic.domain.stage.entity.StageEnum;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@Stateless
public class CiCdServiceImpl implements CiCdService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final JenkinsClient jenkinsClient;
    private final UserRepository userRepository;

    @Inject
    @RestClient
    public CiCdServiceImpl(JenkinsClient jenkinsClient, UserRepository userRepository) {
        this.jenkinsClient = jenkinsClient;
        this.userRepository = userRepository;
    }

    /**
     * Deployed ein Image über den Jenkins
     * @param jobName   JobName
     * @param imageTag  Image des Deployed wird {imgae}:{tag}
     * @param stageEnum Stage ETW INT PRD
     */
    @Override
    public void deployImage(String jobName, String imageTag, StageEnum stageEnum) {
        log.info("Deploy Image {} on Stage {}", imageTag, stageEnum);
        Response response = null;
        switch (stageEnum) {
            case ETW:
                response = jenkinsClient.deploy(jobName, imageTag, true, false, false);
                break;
            case INT:
                response = jenkinsClient.deploy(jobName, imageTag, false, true, false);
                break;
            case PRD:
                response = jenkinsClient.deploy(jobName, imageTag, false, false, true);
                break;
        }
        int statusCode = response.getStatus();
        log.info("Jobname {} wurde mit Tag {} in Stage {} ausgeführt. Statuscode: {}", jobName, imageTag, stageEnum, statusCode);

    }

    /**
     * Startet einen Build auf dem Jenkins über den Restendpoint im Client
     *
     * @param jobName buildJob
     */
    @Override
    public void buildImage(String jobName) {
        Response build = jenkinsClient.build(jobName, true);
        log.info("");
    }

    @Override
    public User getActualUser() {
        return userRepository.getUserByLoginName();
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}