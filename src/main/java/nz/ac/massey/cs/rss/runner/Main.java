package nz.ac.massey.cs.rss.runner;

import nz.ac.massey.cs.sdc.parsers.Rss;
import nz.ac.massey.cs.sdc.parsers.RssChannel;
import nz.ac.massey.cs.sdc.parsers.RssItem;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import javax.xml.bind.*;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Main {
    public static void main(String[] args) throws JAXBException, MalformedURLException{
        JAXBContext jc = JAXBContext.newInstance("nz.ac.massey.cs.sdc.parsers");
        Unmarshaller parser = jc.createUnmarshaller();
        File file = new File("herald.xml");
        URL url = new URL("http://rss.nzherald.co.nz/rss/xml/nzhrsscid_000000005.xml"); //this url has redirected link to an RSS XML source.

        //Rss rss = (Rss) parser.unmarshal(file);                       //uncomment this line to read local xml file example
        Rss rss = (Rss) parser.unmarshal(getFinalURL(url));             //this is to read xml from url address; use getFinalURL(url) to retrieve the final url
        RssChannel rssChannel = rss.getChannel();

        List<RssItem> rssItemList;
        rssItemList = rssChannel.getItem();

        int i = 0;
        for (RssItem rssItem : rssItemList){
            i++;
            System.out.println(i);
            List<Object> objList;
            objList = rssItem.getTitleOrDescriptionOrLink();
            int j = 0;
            for (Object obj : objList){
                try {
                    if (obj instanceof JAXBElement ) {
                        switch (((JAXBElement) obj).getName().toString()) {
                            case "title":
                            case "link":
                            case "description":
                                System.out.println(((JAXBElement) obj).getName() + ": " + ((JAXBElement) obj).getValue());
                            default:
                        }
                    }
                    /**
                     * for XML self closing tags with attributes, they are parsed as "ElementNSimpl" object type, but it seems can only be detected as Element type
                     * e.g. <media:content url="https://www.nzherald.co.nz/resizer/R-Pw53rIHRHyX4ZN4PDljIjXdlc=/460x230/smart/filters:quality(70)/cloudfront-ap-southeast-2.images.arcpublishing.com/nzme/6IRKU5MJV5FXTM4UQ2VIYBWY2A.jpg" type="image/jpeg" height="230" width="460"/>
                     * These tags are parsed into Element objects, each has a NamedNodeMap data through .getAttributes() method. Then use .getNamedItem("url").getNodeValue to retrieve image/video link info.
                     */
                    else if(obj instanceof Element) {
                        if (((Element) obj).getLocalName().contains("content")) {
                            System.out.println("Media content" + " " + (j+1) + ":");
                            //System.out.println(((Element) obj).getTagName());
                            NamedNodeMap nodeMap = ((Element) obj).getAttributes();
                            System.out.println(nodeMap.getNamedItem("url").getNodeValue());
                            j++;
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            System.out.println('\n');
        }
    }

    /**
     * Below method is obtained from internet; to retrieve the final URL if it's redirected.
     * @param url
     * @return
     */
    public static URL getFinalURL (URL url){
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
            con.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            con.addRequestProperty("Referer", "https://www.google.com/");
            con.connect();
            //con.getInputStream();
            int resCode = con.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_SEE_OTHER
                    || resCode == HttpURLConnection.HTTP_MOVED_PERM
                    || resCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                String Location = con.getHeaderField("Location");
                if (Location.startsWith("/")) {
                    Location = url.getProtocol() + "://" + url.getHost() + Location;
                }
                return getFinalURL(new URL(Location));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return url;
    }
}
