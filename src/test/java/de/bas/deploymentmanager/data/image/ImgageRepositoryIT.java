package de.bas.deploymentmanager.data.image;

import de.bas.deploymentmanager.data.AbstarctRepositoryIT;
import de.bas.deploymentmanager.data.ImageRepositoryImpl;
import de.bas.deploymentmanager.logic.domain.project.control.ImageRepository;
import de.bas.deploymentmanager.logic.domain.project.entity.Image;
import de.bas.deploymentmanager.logic.domain.project.entity.Version;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class ImgageRepositoryIT extends AbstarctRepositoryIT {

    private static final String insertImage = "INSERT INTO IMAGE VALUES (DEFAULT, %s, '%s', null, '%s', '%s', %s, %s, %s, %s)";

    ImageRepository repository;

    @BeforeEach
    void setUp() {
        repository = injectEntityManager(new ImageRepositoryImpl());
    }

    @Test
    void getImagesForProject() {
        List<Image> imagesForProject = repository.getImagesForProject(1L);
        Assertions.assertNotNull(imagesForProject);
        Assertions.assertEquals(1, imagesForProject.size());
    }

    @Test
    void sucheAlleImagesEinesProjektes() {
        insertImage(1L, "test", "test", 1, 2, 0, 1);
        List<Image> imagesForProject = repository.getImagesForProject(1L);
        Assertions.assertNotNull(imagesForProject);
        Assertions.assertEquals(2, imagesForProject.size());
    }

    @Test
    @DisplayName("Suche den Aktuellsten Build einer Version")
    void getLastImageForVersion() {
        //GIVEN
        insertImage(1L, 2, 0, 0, 1);
        insertImage(1L, 2, 0, 0, 3);
        insertImage(1L, 2, 0, 0, 3);
        insertImage(1L, 2, 0, 0, 4); //Dieses Image soll gefunden werden
        insertImage(1L, 2, 1, 0, 7);
        insertImage(1L, 2, 1, 0, 10);

        //WHEN
        Version version2_0_0 = getVersion();
        Optional<Image> lastImageOfVersion = repository.getLastImageOfVersion(1L, version2_0_0);

        //THEN
        Assertions.assertTrue(lastImageOfVersion.isPresent());
        Image image2_0_0_4 = lastImageOfVersion.get();
        Assertions.assertSame(4, image2_0_0_4.getTag().getBuildNumber());
    }

    @NotNull
    private Version getVersion() {
        Version version2_0_0 = new Version();
        version2_0_0.setMajorVersion(2);
        version2_0_0.setMinorVersion(0);
        version2_0_0.setIncrementalVersion(0);
        return version2_0_0;
    }

    private void insertImage(Long projectId, int major, int minor, int increment, int build) {
        insertImage(projectId, "user", "image", major, minor, increment, build);
    }

    private void insertImage(Long projectId, String user, String image, int major, int minor, int increment, int build) {
        String sql = String.format(insertImage, projectId, user, image, "commit", major, minor, increment, build);
        executeNativQuery(sql);
    }
}
