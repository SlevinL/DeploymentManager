package de.bas.deploymentmanager.logic.domain.registry.control;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@RegisterRestClient()
@RegisterClientHeaders(value = HarborBasicAuthHeaderFactory.class)
@Consumes("application/json")
public interface HarborClient {

    @DELETE
    @Path("/projects/{project_name}/repositories/{repository_name}/artifacts/{reference}")
    void deleteArtifact(@PathParam("project_name") String project, @PathParam("repository_name") String repository, @PathParam("reference") String reference);
}
