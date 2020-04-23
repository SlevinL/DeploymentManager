package de.bas.deploymentmanager.logic.domain.stage.control;

import de.bas.deploymentmanager.logic.domain.project.entity.Image;
import de.bas.deploymentmanager.logic.domain.stage.boundary.StageService;
import de.bas.deploymentmanager.logic.domain.stage.entity.App;
import de.bas.deploymentmanager.logic.domain.stage.entity.Host;
import de.bas.deploymentmanager.logic.domain.stage.entity.Stage;
import de.bas.deploymentmanager.logic.domain.stage.entity.StageEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
public class StageServiceImpl implements StageService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final StageRepository stageRepository;
    private final AppRepository appRepository;
    private final HostRepository hostRepository;

    @Inject
    public StageServiceImpl(StageRepository stageRepository, AppRepository appRepository, HostRepository hostRepository) {
        this.stageRepository = stageRepository;
        this.appRepository = appRepository;
        this.hostRepository = hostRepository;
    }

    @Override
    public void imageDeployed(String identifier, Image image, String hostName, String port) {
        Host host = hostRepository.getHostByName(hostName);
        deleteOldVersion(host, identifier);
        addNewVersion(identifier, image, port, host);

    }

    private void deleteOldVersion(Host host, String identifier) {
        if (host.getApplications() == null || host.getApplications().isEmpty()) {
            return;
        }
        Optional<App> oldVersion = host.getApplications().stream().filter(app -> app.getProjectIdentifier().equals(identifier)).findFirst();
        oldVersion.ifPresent(app -> {
            appRepository.delete(app);
            log.info("Alte Version wurde vom Host {} entfernt", host.getUrl());
        });
    }

    private void addNewVersion(String identifier, Image image, String port, Host host) {
        App neuesDeployment = new App();
        neuesDeployment.setCreateTime(LocalDateTime.now());
        neuesDeployment.setPort(port);
        neuesDeployment.setImage(image.getImageWithTag());
        neuesDeployment.setImageId(image.getId());
        neuesDeployment.setName(identifier);
        neuesDeployment.setProjectIdentifier(identifier);
        neuesDeployment.setHostId(host.getId());
        appRepository.save(neuesDeployment);
        log.info("Neue Version der Application {} wurde auf {} deployed", identifier, host.getUrl());
    }

    @Override
    public Stage getStage(StageEnum stage) {
        return stageRepository.getStage(stage);

    }

    @Override
    public List<App> getAppsForProject(String projectIdentifier) {
        List<App> appList = appRepository.getByProjectIdentifier(projectIdentifier);
        appList.forEach(app -> {
            Stage stage = stageRepository.getStageByHostId(app.getHostId());
            app.setStage(stage.getName());
        });
        return appList;
    }

    @Override
    public Optional<List<App>> isImageDeployed(Long imageId) {
        return Optional.empty();
    }

    @Override
    public void undeployImages(List<Long> imagesToDelete) {
        appRepository.deleteByImageId(imagesToDelete);
    }
}
