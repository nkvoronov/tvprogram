package com.nkvoronov.tvprogram.parser;

import android.content.Context;

import com.nkvoronov.tvprogram.common.Channel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ParserVseTV extends Parser {

    public ParserVseTV(Context context, String outXML, String lang, int countDay, Boolean fullDesc) {
        super(context, outXML, lang, countDay, fullDesc);
    }

    @Override
    public void saveXML() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("tv");
            rootElement.setAttribute("generator-info-name", "vsetv");
            document.appendChild(rootElement);
            getChannels().getXML(document, rootElement);
            getPrograms().getXML(document, rootElement);
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                try {
                    transformer.transform(new DOMSource(document), new StreamResult(new FileOutputStream(getLang())));
                } catch (TransformerException | FileNotFoundException e) {
                    e.fillInStackTrace();
                }
            } catch (TransformerConfigurationException e) {
                e.fillInStackTrace();
            }
        } catch (ParserConfigurationException e) {
            e.fillInStackTrace();
        }

    }

    @Override
    public void getContent() {
        super.getContent();
    }

    @Override
    public void getContentDay(Channel channel, Date date) {
        super.getContentDay(channel, date);
    }

    @Override
    public void runParser() {
        super.runParser();
    }
}
