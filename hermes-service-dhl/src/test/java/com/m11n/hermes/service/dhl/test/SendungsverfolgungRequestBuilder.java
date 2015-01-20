package com.m11n.hermes.service.dhl.test;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by spaddo on 1/19/15.
 */
public class SendungsverfolgungRequestBuilder {
    private static final String ROOT_ATTR_NAME="data";
    private static final String PASSWD_ATTR_NAME="password";
    private static final String APPNAME_ATTR_NAME="appname";
    private static final String REQUEST_ATTR_NAME="request";
    private static final String LANG_ATTR_NAME="language-code";
    private static final String PIECE_CODE_ATTR_NAME="piece-code";


    public static Document createRequestDOM(String operationName, String appname,
                                            String passwd, String langCode, String pieceCode) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root Element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(ROOT_ATTR_NAME);

            //appname-Attribut hinzufügen
            Attr attr = doc.createAttribute(APPNAME_ATTR_NAME);
            attr.setValue(appname);
            rootElement.setAttributeNode(attr);

            //password-Attribut hinzufügen
            attr = doc.createAttribute(PASSWD_ATTR_NAME);
            attr.setValue(passwd);
            rootElement.setAttributeNode(attr);

            //request-Attribut hinzufügen
            attr = doc.createAttribute(REQUEST_ATTR_NAME);
            attr.setValue(operationName);
            rootElement.setAttributeNode(attr);

            //language-code-Attribut hinzufügen
            attr = doc.createAttribute(LANG_ATTR_NAME);
            attr.setValue(langCode);
            rootElement.setAttributeNode(attr);

            //piece-code-Attribut hinzufügen
            attr = doc.createAttribute(PIECE_CODE_ATTR_NAME);
            attr.setValue(pieceCode);
            rootElement.setAttributeNode(attr);

            doc.appendChild(rootElement);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Document createRequestPublicDOM(String operationName, String appname,
                                                  String passwd, String langCode, String pieceCode) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root Element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(ROOT_ATTR_NAME);

            //appname-Attribut hinzufügen
            Attr attr = doc.createAttribute(APPNAME_ATTR_NAME);
            attr.setValue(appname);
            rootElement.setAttributeNode(attr);

            //password-Attribut hinzufügen
            attr = doc.createAttribute(PASSWD_ATTR_NAME);
            attr.setValue(passwd);
            rootElement.setAttributeNode(attr);

            //request-Attribut hinzufügen
            attr = doc.createAttribute(REQUEST_ATTR_NAME);
            attr.setValue(operationName);
            rootElement.setAttributeNode(attr);

            //language-code-Attribut hinzufügen
            attr = doc.createAttribute(LANG_ATTR_NAME);
            attr.setValue(langCode);
            rootElement.setAttributeNode(attr);

            Element dataElement2 = doc.createElement(ROOT_ATTR_NAME);

            //piece-code-Attribut hinzufügen
            attr = doc.createAttribute(PIECE_CODE_ATTR_NAME);
            attr.setValue(pieceCode);
            dataElement2.setAttributeNode(attr);
            rootElement.appendChild(dataElement2);

            doc.appendChild(rootElement);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
