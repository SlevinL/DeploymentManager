package de.bas.deploymentmanager.logic.domain.project.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class VersionTest {

    @Test
    void name() {
        Version version = new Version("1.7.5-59");

        assertSame(1, version.getMajorVersion());
        assertSame(7, version.getMinorVersion());
        assertSame(5, version.getIncrementalVersion());
        assertEquals(false, version.isSnapshot());
    }

    @Test
    void name1() {
        Version version = new Version("1.7.5");

        assertSame(1, version.getMajorVersion());
        assertSame(7, version.getMinorVersion());
        assertSame(5, version.getIncrementalVersion());
        assertEquals(false, version.isSnapshot());
    }

    @Test
    void name2() {
        Version version = new Version("1.7.5-SNAPSHOT");

        assertSame(1, version.getMajorVersion());
        assertSame(7, version.getMinorVersion());
        assertSame(5, version.getIncrementalVersion());
        assertEquals(true, version.isSnapshot());
    }
}