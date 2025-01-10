/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.utils;

import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Simon
 */
public class DateUtils {
    
    private final static String[] sm_astrFranceGalopMonths= {"janv." , "févr." ,"mars" , "avri." ,"mai" ,"juin","juil.","août","sept.","octo.","nove.","déce."};
    private final static String[] sm_astrOfficialMonths= {"janv.", "févr.", "mars", "avr.", "mai", "juin", "juil.", "août", "sept.", "oct.", "nov.", "déc."};
    // 
public static String fixFrenchMonths(String date) {
        for (String mois : DateFormatSymbols.getInstance(Locale.FRENCH).getShortMonths()) {
            if (mois.endsWith(".")) {
                Pattern sansDot = Pattern.compile("(" +
                    Pattern.quote(mois.substring(0, mois.length()-1)) +
                    "(?!\\.))");
                Matcher matcher = sansDot.matcher(date);
                if (matcher.find()) {
                    date = matcher.replaceFirst(mois);
                }
            }
        }
        return date;
    }
}
