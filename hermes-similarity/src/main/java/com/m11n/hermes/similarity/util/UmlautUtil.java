package com.m11n.hermes.similarity.util;

public class UmlautUtil {
    /**
     * Replaces all german umlaute in the input string with the usual replacement
     * scheme, also taking into account capitilization.
     * A test String such as
     * "Käse Köln Füße Öl Übel Äü Üß ÄÖÜ Ä Ö Ü ÜBUNG" will yield the result
     * "Kaese Koeln Fuesse Oel Uebel Aeue Uess AEOEUe Ae Oe Ue UEBUNG"
     * @param input
     * @return the input string with replaces umlaute
     */
    public static String replace(String input) {

        //replace all lower Umlauts
        String result =
                input
                        .replaceAll("ü", "ue")
                        .replaceAll("ö", "oe")
                        .replaceAll("ä", "ae")
                        .replaceAll("ß", "ss");

        //first replace all capital umlaute in a non-capitalized context (e.g. Übung)
        result =
                result
                        .replaceAll("Ü(?=[a-zäöüß ])", "Ue")
                        .replaceAll("Ö(?=[a-zäöüß ])", "Oe")
                        .replaceAll("Ä(?=[a-zäöüß ])", "Ae");

        //now replace all the other capital umlaute
        result =
                result
                        .replaceAll("Ü", "UE")
                        .replaceAll("Ö", "OE")
                        .replaceAll("Ä", "AE");

        return result;
    }
}
