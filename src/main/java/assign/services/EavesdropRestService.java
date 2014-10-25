package assign.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/")
public class EavesdropRestService {

    @GET
    @Path("/{project}/meetings/{year}")
    public Response getMeeting(@PathParam("project") String project, @PathParam("year") int year) {

        String result = "project: " + project + ", year: " + year;

        return Response.status(200).entity(result).build();
    }

    @GET
    @Path("/{project}/irclogs")
    public Response getIrcLog(@PathParam("project") String project) {

        String result = "irclog project: " + project;

        return Response.status(200).entity(result).build();
    }

    @GET
    @Path("/")
    public Response getUnion() {

        String result = "get ALL the projects";

        return Response.status(200).entity(result).build();
    }
}