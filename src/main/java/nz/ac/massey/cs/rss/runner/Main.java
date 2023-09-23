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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Main {
    public static void main(String[] args) throws JAXBException, MalformedURLException {
        JAXBContext jc = JAXBContext.newInstance("nz.ac.massey.cs.sdc.parsers");
        Unmarshaller parser = jc.createUnmarshaller();
        File file = new File("herald.xml");
        //URL url = new URL("http://rss.nzherald.co.nz/rss/xml/nzhrsscid_000000005.xml");
        URL url = new URL("https://www.nzherald.co.nz/arc/outboundfeeds/rss/section/technology/?outputType=xml&_website=nzh");

        //Rss rss = (Rss) parser.unmarshal(file);
        Rss rss = (Rss) parser.unmarshal(url);
        RssChannel rssChannel = rss.getChannel();

        List<RssItem> rssItemList = new ArrayList<>();
        rssItemList = rssChannel.getItem();

        for (RssItem rssItem : rssItemList){
            List<Object> objList = new ArrayList<>();
            Map<QName, String> attrMap = new HashMap<>();
            objList = rssItem.getTitleOrDescriptionOrLink();
            JAXBElement jaxbElement = null;
            for (Object obj : objList){
                try {
                    switch (((JAXBElement) obj).getName().toString()) {
                        case "title":
                        case "link":
                        case "description":
                            System.out.println(((JAXBElement) obj).getName() + ": " + ((JAXBElement) obj).getValue());
                        default:
                            continue;
                    }
                }catch(Exception e){
                    /**url link can parse ElementNSImpl objects which cannot be converted to JAXBElement.
                     * it appears info I wanted is not in any of these ElementNSImpl objects
                     * when Exception is thrown due to this, ignore and continue
                     */
                    //e.printStackTrace();
                    continue;
                }
            }
            System.out.println('\n');
        }
    }
}
