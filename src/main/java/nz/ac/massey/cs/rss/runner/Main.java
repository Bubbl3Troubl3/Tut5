package nz.ac.massey.cs.rss.runner;

import nz.ac.massey.cs.sdc.parsers.Rss;
import nz.ac.massey.cs.sdc.parsers.RssChannel;
import nz.ac.massey.cs.sdc.parsers.RssItem;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Main {
    public static void main(String[] args) throws JAXBException, MalformedURLException{
        JAXBContext jc = JAXBContext.newInstance("nz.ac.massey.cs.sdc.parsers");
        Unmarshaller parser = jc.createUnmarshaller();
        File file = new File("herald.xml");
        URL url = new URL("http://rss.nzherald.co.nz/rss/xml/nzhrsscid_000000005.xml"); //this url has redirected link

        //Rss rss = (Rss) parser.unmarshal(file);       //use this line to read local xml file
        Rss rss = (Rss) parser.unmarshal(getFinalURL(url));          //this is to read xml from url address; use getFinalURL(url) to retrieve the final url
        RssChannel rssChannel = rss.getChannel();

        List<RssItem> rssItemList;
        rssItemList = rssChannel.getItem();

        for (RssItem rssItem : rssItemList){
            List<Object> objList;
            Map<QName, String> attrMap = new HashMap<>();
            objList = rssItem.getTitleOrDescriptionOrLink();
            attrMap = rssItem.getOtherAttributes();

            for (Object obj : objList){
                try {
                    switch (((JAXBElement) obj).getName().toString()) {
                        case "title":
                        case "link":
                        case "description":
                            System.out.println(((JAXBElement) obj).getName() + ": " + ((JAXBElement) obj).getValue());
                        default:
                    }
                }catch(Exception e){
                    /**url link can parse ElementNSImpl objects which cannot be converted to JAXBElement.
                     * it appears info I wanted is not in any of these ElementNSImpl objects
                     * when Exception is thrown due to this, ignore and continue
                     */
                    //e.printStackTrace();
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
