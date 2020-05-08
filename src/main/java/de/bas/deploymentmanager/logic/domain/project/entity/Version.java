package de.bas.deploymentmanager.logic.domain.project.entity;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class Version {
    @Transient
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Column(name = "MAJOR_VERSION")
    private Integer majorVersion;

    @Column(name = "MINOR_VERSION")
    private Integer minorVersion;

    @Column(name = "INCREMENTAL_VERSION")
    private Integer incrementalVersion;

    @Transient
    private boolean snapshot;

    public Version() {
    }

    public Version(String version) {
        this.snapshot = isSnapshotVersion(version);
        this.majorVersion = getMajorVersion(version);
        this.minorVersion = getMinorVersion(version);
        this.incrementalVersion = getIncrementalVersion(version);


    }

    private boolean isSnapshotVersion(String version) {
        if (version != null && !version.isEmpty()) {
            if (version.contains("SNAPSHOT")) {
                return true;
            }
        }
        return false;
    }

    private int getIncrementalVersion(String version) {
        if (version == null) {
            return 0;
        }
        String[] versionArray = version.split("\\.");
        if (versionArray.length <= 2) {
            return 0;
        } else {
            try {
                String number = versionArray[2];
                String[] split = number.split("-");
                number = split[0];
                return Integer.parseInt(number);
            } catch (NumberFormatException e) {
                log.warn("IncrementalVersion konnte nicht ermittelt werden");
                return 0;
            }
        }

    }

    private int getMajorVersion(String version) {
        if (version == null) {
            return 0;
        }
        String[] versionArray = version.split("\\.");
        try {
            String number = versionArray[0];
            number = number.replace("-SNAPSHOT", "");
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            log.warn("MajorVersion konnte nicht ermittelt werden");
            return 0;
        }
    }

    private int getMinorVersion(String version) {
        if (version == null) {
            return 0;
        }
        String[] versionArray = version.split("\\.");
        if (versionArray.length == 1) {
            return 0;
        } else {
            try {
                String number = versionArray[1];
                number = number.replace("-SNAPSHOT", "");
                return Integer.parseInt(number);
            } catch (NumberFormatException e) {
                log.warn("MinorVersion konnte nicht ermittelt werden");
                return 0;
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s.%s.%s", majorVersion, minorVersion, incrementalVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return Objects.equals(majorVersion, version.majorVersion) &&
                Objects.equals(minorVersion, version.minorVersion) &&
                Objects.equals(incrementalVersion, version.incrementalVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(majorVersion, minorVersion, incrementalVersion);
    }
}
