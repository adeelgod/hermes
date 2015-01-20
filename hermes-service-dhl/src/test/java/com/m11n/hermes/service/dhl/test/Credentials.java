package com.m11n.hermes.service.dhl.test;

import java.io.InputStreamReader;
import java.util.Properties;

public class Credentials {
    private static final String CRED_FILE_PATH = "dhl.ini";
    private static final String UN_PROPERTY_NAME = "tnt_benutzername"; //nicht �ndern
    private static final String PW_PROPERTY_NAME = "tnt_passwort"; //nicht �ndern
    private static final String CIG_UN_PROPERTY_NAME = "cig_devid"; //nicht �ndern
    private static final String CIG_PW_PROPERTY_NAME = "cig_password"; //nicht �ndern
    private static final String UN_WARNING_TXT = "WARNUNG: Benutzername in Datei Zugangsdaten.txt nicht angegeben!";
    private static final String PW_WARNING_TXT = "WARNUNG: Passwort in Datei Zugangsdaten.txt nicht angegeben!";
    private static final String ENCODING = "UTF8";
    private String username = "";
    private String pass = "";
    private String cig_username = "";
    private String cig_pass = "";


    public Credentials() {
        try {
            InputStreamReader credFileReader = new InputStreamReader(Credentials.class.getClassLoader().getResourceAsStream(CRED_FILE_PATH), ENCODING);
            Properties credProps = new Properties();
            credProps.load(credFileReader);
            username = credProps.getProperty(UN_PROPERTY_NAME);
            pass = credProps.getProperty(PW_PROPERTY_NAME);
            cig_username = credProps.getProperty(CIG_UN_PROPERTY_NAME);
            cig_pass = credProps.getProperty(CIG_PW_PROPERTY_NAME);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }


    public String getUsername() {
        if (username.equals(""))
            System.out.println(UN_WARNING_TXT);
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getPass() {
        if (pass.equals(""))
            System.out.println(PW_WARNING_TXT);
        return pass;
    }


    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getCig_username() {
        return cig_username;
    }


    public String getCig_pass() {
        return cig_pass;
    }

    public void setCig_Username(String username) {
        this.cig_username = username;
    }

    public void setCig_Pass(String pass) {
        this.cig_pass = pass;
    }
}
