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
import java.util.*;

public class Main {
    public static void main(String[] args) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("nz.ac.massey.cs.sdc.parsers");
        Unmarshaller parser = jc.createUnmarshaller();
        File file = new File("herald.xml");
        List<JAXBElement> jaxbElementList = new ArrayList<>();

        Rss rss = (Rss) parser.unmarshal(file);
        RssChannel rssChannel = rss.getChannel();

        List<RssItem> rssItemList = new ArrayList<>();
        rssItemList = rssChannel.getItem();

        int i = 0;
        for (RssItem rssItem : rssItemList){
            List<Object> objList = new ArrayList<>();
            Map<QName, String> attrMap = new HashMap<>();
            objList = rssItem.getTitleOrDescriptionOrLink();
            attrMap = rssItem.getOtherAttributes();
            JAXBElement jaxbElement = null;
            for (Object o : objList){
                switch (((JAXBElement)o).getName().toString()){
                    case "title":
                    case "link":
                    case "description":
                        System.out.println(((JAXBElement)o).getName()+": "+((JAXBElement)o).getValue());
                    default:
                        continue;
                }
            }
            System.out.println('\n');
        }
    }
}