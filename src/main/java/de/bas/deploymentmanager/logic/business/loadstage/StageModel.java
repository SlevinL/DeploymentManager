package de.bas.deploymentmanager.logic.business.loadstage;

import de.bas.deploymentmanager.logic.domain.stage.entity.Stage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
/**
 * Das Stagemodel lädt alle relevanten Informationen
 */
public class StageModel {
    private Stage stage;
}
