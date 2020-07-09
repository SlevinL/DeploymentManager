package de.bas.deploymentmanager.frontend.jsf.profil;

import de.bas.deploymentmanager.logic.domain.dicd.boundary.CiCdService;
import de.bas.deploymentmanager.logic.domain.dicd.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@ViewScoped
@Named
public class UserFormBean implements Serializable {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Getter
    @Setter
    private User user;

    @Inject
    private CiCdService ciCdService;

    @PostConstruct
    public void init() {
        user = ciCdService.getActualUser();
    }

    public void save() {
        user = ciCdService.saveUser(user);
    }
}
