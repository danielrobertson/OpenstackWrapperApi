package assign.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

@Path("/")
public class EavesdropRestService {

    @GET
    @Path("/{project}/meetings/{year}")
    @Produces("application/xml")
    public Response getMeeting(@PathParam("project") String project, @PathParam("year") String year) {
        String url = buildUrl(project, "meetings", year);
        ArrayList<String> links = getLinks(url);
        String content = getMeetingResponse(project, links);

        return Response.status(200).entity(content).build();
    }

    private String getMeetingResponse(String project, ArrayList<String> links) {
        StringBuilder content = new StringBuilder();
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        content.append("<project name=").append("\'").append(project).append("\'").append(">");
        for(String link: links) {
            content.append("<link>").append(link).append("</link>");
        }
        content.append("</project>");
        return content.toString();
    }

    @GET
    @Path("/{project}/irclogs")
    @Produces("application/xml")
    public Response getIrcLog(@PathParam("project") String project) {
        String url = buildUrl(project, "irclogs", null);
        ArrayList<String> links = getLinks(url);
        String content = getIrcLogResponse(project, links);
        return Response.status(200).entity(content).build();
    }

    private String getIrcLogResponse(String project, ArrayList<String> links) {
        StringBuilder content = new StringBuilder();
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        content.append("<project name=").append("\'").append(project).append("\'").append(">");
        for(String link: links) {
            content.append("<link>").append(link).append("</link>");
        }
        content.append("</project>");
        return content.toString();
    }

    @GET
    @Path("/")
    public Response getUnion() {
        String urlMeetings = "http://eavesdrop.openstack.org/meetings/";
        String urlIrcLogs = "http://eavesdrop.openstack.org/irclogs/";
        ArrayList<String> meetingLinks = getLinks(urlMeetings);

        for(String link: getLinks(urlIrcLogs)) {
            meetingLinks.add(link);
        }

        return Response.status(200).entity("").build();
    }

    /**
     * Retrieve the links from the openstack website the user has queried
     * @param url
     * @return a list of links
     */
    public ArrayList<String> getLinks(String url) {
        ArrayList<String> list = new ArrayList<String>();
        Document doc = getDocument(url);

        if (doc != null) {
            Elements items = doc.select("tr  td a");

            String link;
            for(Element item: items){
                if (item.toString().contains("Parent Directory")) {
                    String href = item.attr("href");

                    String base = url.substring(0, url.indexOf("org") + 3);
                    link = base + href;
                }
                else {
                    link = url + item.attr("href");
                }
                list.add(link);
            }
        }

        return list;
    }

    /**
     * Opens the openstack page through a JSoup Document
     * @param url of openstack
     * @return JSoup Document representing the openstack page
     */
    protected Document getDocument(String url) {
        Document doc = null;
        try {
            doc = getDocumentHelper(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * Helper that extracts out the static call to JSoup.connect() which is not testable
     * @param url of the user's query
     * @return JSoup Document of the page
     * @throws IOException
     */
    protected Document getDocumentHelper(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    /**
     * Builds and returns the URL for querying openstack
     * @param project of the query to openstack
     * @param type of the query to openstack
     * @param year of the query to openstack
     * @return the url
     */
    protected String buildUrl(String project, String type, String year) {
        String url = null;
        try {
            String eavesdrop = "http://eavesdrop.openstack.org/";

            if(type.equals("irclogs")) {
                url = eavesdrop + type + "/" + project + "/";
            }
            else if(type.equals("meetings") && !isBlank(year)) {
                url = eavesdrop + type + "/" + project + "/" + year + "/";
            }
            else {
                throw new MalformedURLException();
            }
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Helper method to determine if the input fields were left or null
     * @param str the parameter
     * @return true if the parameter is null or empty
     */
    boolean isBlank(String str) {
        return str == null || str.isEmpty();
    }
}