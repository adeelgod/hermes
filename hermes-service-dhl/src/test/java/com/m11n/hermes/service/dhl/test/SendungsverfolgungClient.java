package com.m11n.hermes.service.dhl.test;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Created by spaddo on 1/19/15.
 */
public class SendungsverfolgungClient {
    private static Scanner scan = new Scanner(System.in);

    //Setzen sie die Zugangsdaten �ber /Einstellungen/Zugangsdaten.txt
    private static String TNT_USERNAME = "";
    private static String TNT_PASSWD = "";
    private static String CIG_USERNAME = "";
    private static String CIG_PASSWD = "";

    private static String LANG_CODE = "de";
    private static String Sendungsnummer = "00340433836393297502";

    private static final String D_GET_PIECE_NAME = "d-get-piece";
    private static final String D_GET_STATUS_FOR_PUBLIC_USER_NAME = "get-status-for-public-user";
    private static final String D_GET_PIECE_DETAIL_NAME = "d-get-piece-detail";

    private static final String SENDUNGSVERFOLGUNG_URL = "https://cig.dhl.de/services/sandbox/rest/sendungsverfolgung";


    private static byte readMainMenuInput() {
        if (scan == null) {
            System.err.println("Keine Konsole.");
            System.exit(1);
        }

        System.out.println("Sendungsverfolgung Operationen: " + "\n" +
                "1 - d-get-piece" + "\n" +
                "2 - d-get-piece-detail" + "\n" +
                "3 - d-get-status-for-public-user" + "\n" +
                "0 - Programm beenden" + "\n");
        System.out.print("Waehlen Sie die gewünschte Operation: ");
        String choice = scan.nextLine();
        try {
            return Byte.parseByte(choice);
        } catch (Exception e) {
            return -1;
        }
    }

    private static String readSendungsnummerInput() {
        if (scan == null) {
            System.err.println("Keine Konsole.");
            System.exit(1);
        }

        System.out.println("Sendungsnummer (siehe Forum) eingeben: " + "\n");
        String choice = scan.nextLine();
        return choice;
    }

    public static void main(String[] args) {

        // Default Credentials laden
        Credentials creds = new Credentials();

        // promt to verify CIG Credentials
        creds = readCredentialsInput(creds);

        // set BasicAuth Username and Password
        CIG_USERNAME = creds.getCig_username();
        CIG_PASSWD = creds.getCig_pass();

        // set Sendungsverfolgungs Credentials
        TNT_USERNAME = creds.getUsername();
        TNT_PASSWD = creds.getPass();

        try {
            byte input = 0;
            do {
                input = readMainMenuInput();
                switch (input) {
                    case 1:
                        Sendungsnummer = readSendungsnummerInput();
                        runDGetPiece(TNT_USERNAME, TNT_PASSWD, LANG_CODE, Sendungsnummer);
                        break;
                    case 2:
                        Sendungsnummer = readSendungsnummerInput();
                        runDGetPieceDetail(TNT_USERNAME, TNT_PASSWD, LANG_CODE, Sendungsnummer);
                        break;
                    case 3:
                        Sendungsnummer = readSendungsnummerInput();
                        runDGetStatusPublicUser(TNT_USERNAME, TNT_PASSWD, LANG_CODE, Sendungsnummer);
                        break;
                }
                if (input == -1)
                    System.out.println("Bitte, korrigieren Sie Ihre Eingabe!");
                else if (input != 0) {
                    System.out.print("Bitte, drücken Sie die Eingabe-Taste um fortzufahren.");
                    scan.nextLine();
                }
            } while (input != 0);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void runDGetStatusPublicUser(String appname, String passwd, String langCode, String pieceCode) {
        Document requestDoc = SendungsverfolgungRequestBuilder.createRequestPublicDOM(D_GET_STATUS_FOR_PUBLIC_USER_NAME, appname, passwd, langCode, pieceCode);

        streamRequest(requestDoc);

    }


    private static void runDGetPiece(String appname, String passwd, String langCode, String pieceCode) {
        Document requestDoc = SendungsverfolgungRequestBuilder.createRequestDOM(D_GET_PIECE_NAME, appname, passwd, langCode, pieceCode);

        streamRequest(requestDoc);
    }


    private static void runDGetPieceDetail(String appname, String passwd, String langCode, String pieceCode) {
        Document requestDoc = SendungsverfolgungRequestBuilder.createRequestDOM(D_GET_PIECE_DETAIL_NAME, appname, passwd, langCode, pieceCode);

        streamRequest(requestDoc);
    }


    private static void streamRequest(Document requestDoc) {
        // write the content to console
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("encoding", "ISO-8859-1");
            DOMSource source = new DOMSource(requestDoc);
            StringWriter doc2stringWriter = new StringWriter();
            transformer.transform(source, new StreamResult(doc2stringWriter));
            String request = doc2stringWriter.getBuffer().toString();
            System.out.println("Request:");
            System.out.println(request);
            String docAsString = request.replaceAll("\n|\r", "");
            URL url = new URL(SENDUNGSVERFOLGUNG_URL + "?xml=" + URLEncoder.encode(docAsString, "ISO-8859-1"));
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setRequestProperty("Accept", "application/xml");
            String encoded = Base64.encodeBase64String((CIG_USERNAME + ":" + CIG_PASSWD).getBytes());
            httpConn.setRequestProperty("Authorization", "Basic " + encoded);
            httpConn.connect();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(httpConn.getInputStream());
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            Writer out = new StringWriter();
            tf.transform(new DOMSource(doc), new StreamResult(out));
            System.out.println("Response: ");
            System.out.println(out.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Credentials readCredentialsInput(Credentials creds) {
        if (scan == null) {
            System.err.println("Keine Konsole.");
            System.exit(1);
        }

        System.out.println("Bitte EntwicklerID und Password prüfen");
        System.out.println("EntwicklerID: " + creds.getCig_username());
        System.out.println("Password:     " + creds.getCig_pass());
        System.out.println("Sind die Angaben korrekt? (Y/n):");

        String input = scan.nextLine();

        if (!(input.equalsIgnoreCase("y") || input.isEmpty())) {
            System.out.println("EntwicklerID eingeben:");
            creds.setCig_Username(scan.nextLine());
            System.out.println("Password eingeben:");
            creds.setCig_Pass(scan.nextLine());
        }
        return creds;


    }
}
