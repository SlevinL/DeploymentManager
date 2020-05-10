package de.bas.deploymentmanager.logic.domain.project.boundary;

import de.bas.deploymentmanager.logic.domain.project.control.DeploymentRepository;
import de.bas.deploymentmanager.logic.domain.project.control.ImageRepository;
import de.bas.deploymentmanager.logic.domain.project.control.ProjectRepository;
import de.bas.deploymentmanager.logic.domain.project.control.ProjectServiceImpl;
import de.bas.deploymentmanager.logic.domain.project.entity.Image;
import de.bas.deploymentmanager.logic.domain.project.entity.NewImageModel;
import de.bas.deploymentmanager.logic.domain.project.entity.Project;
import de.bas.deploymentmanager.logic.domain.project.entity.Tag;
import de.bas.deploymentmanager.logic.domain.project.entity.exception.ImgageNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateNewImageTest {

    ProjectService projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ImageRepository imgaeRepository;
    @Mock
    private DeploymentRepository deploymentRepository;
    @Mock
    private Image image;

    @BeforeEach
    void setUp() {
        projectService = new ProjectServiceImpl(projectRepository, imgaeRepository, deploymentRepository);
    }

    @Test
    @DisplayName("Erzeugt für die Version 1.7.5 ein neues Image. Wobei schin ein Image 1.7.5-59 exisitiert")
    void generateNewTag() throws Exception {
        //GIVEN
        Project project = new Project();
        project.setId(1L);
        project.setIdentifier("ident");

        when(projectRepository.getByIfentifier(anyString())).thenReturn(project);
        when(imgaeRepository.getImageByProjectIdTag(anyLong(), any())).thenThrow(new ImgageNotFoundException(""));
        when(image.getTag()).thenReturn(new Tag("1.7.5-59"));
        when(imgaeRepository.getLastImageOfVersion(anyLong(), any())).thenReturn(Optional.of(image));
        //WHEN
        NewImageModel model = new NewImageModel();
        model.setVersion("1.7.5");
        Tag tag = projectService.generateNewImage("ident", model);

        //THEN
        assertSame(60, tag.getBuildNumber());
        assertSame(1, tag.getVersion().getMajorVersion());
        assertSame(7, tag.getVersion().getMinorVersion());
        assertSame(5, tag.getVersion().getIncrementalVersion());

    }

    @Test
    @DisplayName("Erzeugt für die Version 1.7.5-SNAPSHOT ein neues Image. Wobei schon ein Image 1.7.5-59 exisitiert. Buildnummer wird nicht erhöht")
    void name2() throws Exception {
        //GIVEN
        Project project = new Project();
        project.setId(1L);
        project.setIdentifier("ident");

        when(projectRepository.getByIfentifier(anyString())).thenReturn(project);
        when(imgaeRepository.getImageByProjectIdTag(anyLong(), any())).thenThrow(new ImgageNotFoundException(""));
        when(image.getTag()).thenReturn(new Tag("1.7.5-59"));
        when(imgaeRepository.getLastImageOfVersion(anyLong(), any())).thenReturn(Optional.of(image));
        //WHEN
        NewImageModel model = new NewImageModel();
        model.setVersion("1.7.5-SNAPSHOT");
        Tag tag = projectService.generateNewImage("ident", model);

        //THEN
        assertSame(59, tag.getBuildNumber());
        assertSame(1, tag.getVersion().getMajorVersion());
        assertSame(7, tag.getVersion().getMinorVersion());
        assertSame(5, tag.getVersion().getIncrementalVersion());

    }

    @Test
    @DisplayName("Erzeugt für die Version 1.7.5 ein neues Image. Wobei noch ein Image zu der Version existiert")
    void name3() throws Exception {
        //GIVEN
        Project project = new Project();
        project.setId(1L);
        project.setIdentifier("ident");

        when(projectRepository.getByIfentifier(anyString())).thenReturn(project);
        when(imgaeRepository.getImageByProjectIdTag(anyLong(), any())).thenThrow(new ImgageNotFoundException(""));
        when(imgaeRepository.getLastImageOfVersion(anyLong(), any())).thenReturn(Optional.empty());
        //WHEN
        NewImageModel model = new NewImageModel();
        model.setVersion("1.7.5-SNAPSHOT");
        Tag tag = projectService.generateNewImage("ident", model);

        //THEN
        assertSame(1, tag.getBuildNumber());
        assertSame(1, tag.getVersion().getMajorVersion());
        assertSame(7, tag.getVersion().getMinorVersion());
        assertSame(5, tag.getVersion().getIncrementalVersion());

    }

    @Test
    @DisplayName("Erzeugt für die Version 1.7.5-SNAPSHOT ein neues Image. Wobei schon ein Image 1.7.5-59 exisitiert")
    void name4() throws Exception {
        //GIVEN
        Project project = new Project();
        project.setId(1L);
        project.setIdentifier("ident");

        when(projectRepository.getByIfentifier(anyString())).thenReturn(project);
        when(imgaeRepository.getImageByProjectIdTag(anyLong(), any())).thenReturn(image);
        when(imgaeRepository.getLastImageOfVersion(anyLong(), any())).thenReturn(Optional.empty());
        //WHEN
        NewImageModel model = new NewImageModel();
        model.setVersion("1.7.5-SNAPSHOT");
        Tag tag = projectService.generateNewImage("ident", model);

        //THEN
        assertSame(1, tag.getBuildNumber());
        assertSame(1, tag.getVersion().getMajorVersion());
        assertSame(7, tag.getVersion().getMinorVersion());
        assertSame(5, tag.getVersion().getIncrementalVersion());

    }
}