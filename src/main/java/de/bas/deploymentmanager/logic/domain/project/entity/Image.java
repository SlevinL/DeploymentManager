package de.bas.deploymentmanager.logic.domain.project.entity;

import de.bas.deploymentmanager.logic.domain.stage.entity.StageEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Table(name = "IMAGE")
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PROJECT_ID")
    private Long projectId;

    @Embedded
    private Tag tag;

    @Column(name = "CREATE_USER")
    private String user;

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @Column(name = "IMAGE")
    private String image;

    @Column(name = "COMMIT_HASH")
    private String commit;

    public String getImageWithTag() {
        return String.format("%s:%s", image, tag);
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private List<Deployment> deployments;

    public Version getVersion() {
        if (tag != null) {
            return tag.getVersion();
        }
        return null;
    }

    public Deployment getDeployment(StageEnum stage) {
        if (deployments != null) {
            return deployments.stream().filter(d -> d.getStage().equals(stage)).findFirst().orElse(null);
        }
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image1 = (Image) o;
        return Objects.equals(id, image1.id) &&
                Objects.equals(projectId, image1.projectId) &&
                Objects.equals(tag, image1.tag) &&
                Objects.equals(user, image1.user) &&
                Objects.equals(createDate, image1.createDate) &&
                Objects.equals(image, image1.image) &&
                Objects.equals(tag, image1.tag) &&
                Objects.equals(commit, image1.commit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectId, tag, user, createDate, image, tag, commit);
    }
}
