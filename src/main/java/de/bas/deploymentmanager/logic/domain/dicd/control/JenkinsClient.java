package de.bas.deploymentmanager.logic.domain.dicd.control;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RegisterRestClient()
@Path("/job/{jobName}")
@Produces(MediaType.APPLICATION_FORM_URLENCODED)
@RegisterClientHeaders(value = BasicAuthHeaderFactory.class)
public interface JenkinsClient {

    /**
     * Definiert Aufruf an RestEndpoint auf dem Jenkins.
     * Build erfolgt mit Deploy auf ETW falls gewünscht
     * @param jobName
     * @param push
     * @return
     */
    @POST
    @Path("/buildWithParameters")
    Response build(@PathParam("jobName") String jobName, @QueryParam("ETW_DEPLOY") boolean push);

    /**
     * Definiert Aufruf an Restendpoint auf dem Jenkins. Deploy kann durch ganze Kette erfolgen (CD).
     * Das Durch-Deployen auf die Prod wird mit den Parametern vorgenommen
     * @param jobName
     * @param tag
     * @param etwDeploy ja/nein
     * @param intDeploy ja/nein
     * @param prdDeploy ja/nein
     * @return
     */
    @POST
    @Path("/buildWithParameters")
    Response deploy(@PathParam("jobName") String jobName, @QueryParam("TAG") String tag
            , @QueryParam("ETW_DEPLOY") boolean etwDeploy
            , @QueryParam("INT_DEPLOY") boolean intDeploy
            , @QueryParam("PROD_DEPLOY") boolean prdDeploy);
}
