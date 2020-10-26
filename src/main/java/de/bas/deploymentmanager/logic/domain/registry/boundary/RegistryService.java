package de.bas.deploymentmanager.logic.domain.registry.boundary;

public interface RegistryService {

    /**
     * Löscht ein Image aus der Registry
     * Ein Image setzt sich zusammen aus: RegistryHost:Port/project/repository:tag
     *
     * @param image z.B. RegistryHost:Port/project/repository:tag
     */
    void deleteImage(String image);
}
