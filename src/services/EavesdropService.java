package services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 *  A class with various services for the eavesdrop resources to use
 */
public class EavesdropService {

    public String getMeetingResponse(String project, ArrayList<String> links) {
        StringBuilder content = new StringBuilder();
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        content.append("<project name=").append("\'").append(project).append("\'").append(">");
        for(String link: links) {
            content.append("<link>").append(link).append("</link>");
        }
        content.append("</project>");

        return content.toString();
    }

    public String getIrcLogResponse(String project, ArrayList<String> links) {
        StringBuilder content = new StringBuilder();
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        content.append("<project name=").append("\'").append(project).append("\'").append(">");
        for(String link: links) {
            content.append("<link>").append(link).append("</link>");
        }
        content.append("</project>");

        return content.toString();
    }

    public ArrayList<String> getProjects(String url) {
        ArrayList<String> projects = new ArrayList<String>();
        Document doc = getDocument(url);

        if(doc != null) {
            Elements items = doc.select("tr td a");

            for(Element item: items) {
                if (item.toString().contains("Parent Directory"))
                    continue;
                String name = item.text();
                projects.add(name);
            }
        }

        return projects;
    }

    /**
     * Build an XML response from the union of the meeting and
     */
    public String getUnionResponse(String urlIrcLogs, ArrayList<String> links) {
        StringBuilder content = new StringBuilder();
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        content.append("<projects>");
        for(String link: links) {
            content.append("<project>").append(link).append("</project>");
        }
        content.append("</projects>");

        for(String link: getLinks(urlIrcLogs)) {
            links.add(link);
        }

        return content.toString();
    }

    /**
     * Retrieve the links from the openstack website the user has queried
     */
    public ArrayList<String> getLinks(String url) {
        ArrayList<String> list = new ArrayList<String>();
        Document doc = getDocument(url);

        if (doc != null) {
            Elements items = doc.select("tr  td a");

            String link;
            for(Element item: items){
                if (item.toString().contains("Parent Directory"))
                    continue;
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
    private Document getDocument(String url) {
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
    private Document getDocumentHelper(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    /**
     * Builds and returns the URL for querying openstack
     * @param project of the query to openstack
     * @param type of the query to openstack
     * @param year of the query to openstack
     * @return the url
     */
    public String buildUrl(String project, String type, String year) {
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
    private boolean isBlank(String str) {
        return str == null || str.isEmpty();
    }

}
