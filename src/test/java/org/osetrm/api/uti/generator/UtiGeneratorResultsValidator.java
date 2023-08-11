package org.osetrm.api.uti.generator;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UtiGeneratorResultsValidator {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        int duplicate = 0;
        Set<String> set = new HashSet<>();
        File file = new File("target/jmeter/results.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);
        NodeList responseData = document.getElementsByTagName("responseData");
        for (int i = 0; i < responseData.getLength(); i++) {
            String utiString = responseData.item(i).getTextContent();
            String sub = utiString.substring(utiString.indexOf('0'), utiString.lastIndexOf('"'));
            if (!set.add(sub)) {
                System.out.println("Duplicate Detected: " + sub);
                duplicate++;
            }
        }
        System.out.println("Processed: " + set.size());
        System.out.println("Duplicates: " + duplicate);
    }

}
