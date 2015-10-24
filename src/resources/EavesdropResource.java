package resources;

import services.EavesdropConstants;
import services.EavesdropService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("/")
public class EavesdropResource {

    @GET
    @Path("/{project}/meetings/{year}")
    @Produces("application/xml")
    public Response getMeeting(@PathParam("project") String project, @PathParam("year") String year) {
        EavesdropService eavesdropService = new EavesdropService();
        String url = eavesdropService.buildUrl(project, "meetings", year);

        ArrayList<String> links = eavesdropService.getLinks(url);
        String content = eavesdropService.getMeetingResponse(project, links);

        Response response = Response.status(200).entity(content).build();
        return response;
    }

    @GET
    @Path("/{project}/irclogs")
    @Produces("application/xml")
    public Response getIrcLog(@PathParam("project") String project) {
        EavesdropService eavesdropService = new EavesdropService();
        String url = eavesdropService.buildUrl(project, "irclogs", null);

        ArrayList<String> links = eavesdropService.getLinks(url);
        String content = eavesdropService.getIrcLogResponse(project, links);

        Response response = Response.status(200).entity(content).build();
        return response;
    }

    @GET
    @Path("/getUnion")
    @Produces("application/xml")
    public Response getUnion() {
        String meetingUrl = EavesdropConstants.MEETINGS_URL;
        String ircLogsUrl = EavesdropConstants.IRCLOGS_URL;

        EavesdropService eavesdropService = new EavesdropService();
        ArrayList<String> projects = eavesdropService.getProjects(meetingUrl);

        /* combine with irc logs */
        for(String project: eavesdropService.getProjects(ircLogsUrl)) {
            projects.add(project);
        }

        String content = eavesdropService.getUnionResponse(ircLogsUrl, projects);

        Response response = Response.status(200).entity(content).build();
        return response;
    }

}