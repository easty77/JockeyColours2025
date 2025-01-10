/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.utils;

/**
 *
 * @author simon
 */
public class HorseRacingUtils {

    public static String convertDecimalToSP(double dSP) {
        dSP = (double) Math.round(dSP * 1000.0) / 1000.0; // 3 decimal places
        String strSP = "";
        if (dSP > 9.5) {
            // to nearest integer
            strSP = String.valueOf((int) Math.ceil(dSP)) + "/1";
        } else if (dSP >= 2.75) {
            // to nearest half point
            if ((dSP - (int) dSP) > 0.5) {
                strSP = String.valueOf((int) Math.ceil(dSP)) + "/1";
            } else if ((dSP - (int) dSP) > 0.0) {
                strSP = String.valueOf((int) Math.ceil(2 * dSP)) + "/2";
            } else {
                strSP = String.valueOf((int) dSP) + "/1";
            }
        } else if (dSP > 1.875) {
            // to nearest quarter point
            if ((dSP - (int) dSP) > 0.75) {
                strSP = String.valueOf((int) Math.round(dSP)) + "/1";
            } else if ((dSP - (int) dSP) >= 0.5) {
                strSP = String.valueOf((int) Math.ceil(4 * dSP)) + "/4";
            } else if ((dSP - (int) dSP) >= 0.25) {
                strSP = String.valueOf((int) Math.ceil(2 * dSP)) + "/2";
            } else if ((dSP - (int) dSP) > 0.0) {
                strSP = String.valueOf((int) Math.ceil(4 * dSP)) + "/4";
            } else {
                strSP = String.valueOf((int) dSP) + "/1";
            }
        } else if (dSP >= 0.9) {
            // to nearest eighth point
            if (dSP > 1.875) {
                strSP = "2/1";
            } else if (dSP > 1.75) {
                strSP = "15/8";
            } else if (dSP > 1.625) {
                strSP = "7/4";
            } else if (dSP > 1.5) {
                strSP = "13/8";
            } else if (dSP > 1.375) {
                strSP = "6/4";
            } else if (dSP > 1.25) {
                strSP = "11/8";
            } else if (dSP > 1.2) {
                strSP = "5/4";
            } else if (dSP > 1.0) {
                strSP = "6/5";
            } else {
                strSP = "Evens";
            }
        } else // odds-on
        {
            if (dSP > 4.0 / 5.0) {
                strSP = "9/10";
            } else if (dSP > 8.0 / 11.0) {
                strSP = "4/5";
            } else if (dSP > 4.0 / 7.0) {
                strSP = "8/11";
            } else if (dSP > 8.0 / 13.0) {
                strSP = "4/6";
            } else if (dSP > 4.0 / 7.0) {
                strSP = "8/13";
            } else if (dSP > 8.0 / 15.0) {
                strSP = "4/7";
            } else if (dSP > 1.0 / 2.0) {
                strSP = "8/15";
            } else if (dSP > 2.0 / 5.0) {
                strSP = "1/2";
            } else if (dSP > 4.0 / 11.0) {
                strSP = "2/5";
            } else if (dSP > 1.0 / 3.0) {
                strSP = "4/11";
            } else // <= 1.0d/3.0d
            {
                strSP = String.valueOf("1/" + (int) (1.0 / dSP));
            }
        }
        //System.out.println("convertDecimalToSP: " + dSP +"-" + strSP + " Diff: " + (convertSP(strSP) - dSP));
        return strSP;
    }

    public static double convertSP(String strStartingPrice) {
        double dSP = -1;
        if (!"".equals(strStartingPrice) && (!"0/0".equals(strStartingPrice))) {
            String strSP = strStartingPrice.toLowerCase().replace("f", "").replace("c", "").replace("j", "");
            if ("evs".equalsIgnoreCase(strSP) || "evens".equalsIgnoreCase(strSP)) {
                dSP = 1.0;
            } else {
                String[] astrSP = strSP.split("/");
                try {
                    if (astrSP.length > 1) {
                        dSP = Double.parseDouble(astrSP[0]) / Double.parseDouble(astrSP[1]);
                    } else {
                        dSP = Double.parseDouble(astrSP[0]);
                    }
                } catch (Exception e) {
                    System.out.println("Bad SP: " + strStartingPrice + "-" + strSP);
                }
            }
        }
        return dSP;
    }

    public static String convertToteToSP(double dSP) {
        String strSP = "";
        if ((dSP - (int) dSP) == 0) {
            strSP = String.valueOf((int) dSP) + "/1";
        } else if ((dSP - (int) dSP) == 0.5) {
            if (dSP == 1.5) {
                strSP = "6/4";
            } else {
                strSP = String.valueOf((int) (2 * dSP)) + "/2";
            }
        } else if (dSP == 0.7) {
            strSP = "8/11";
        } else if (dSP == 1.4) {
            strSP = "11/8";
        } else if ((dSP <= 1.8) && (dSP >= 1.79)) {
            dSP = 1.75;
            strSP = "7/4";
        } else if (dSP == 2.8) {
            strSP = "11/4";
        } else if (dSP == 2.3) {
            dSP = 2.25;
            strSP = "9/4";
        } else if (dSP == 3.3) {
            dSP = 3.33;
            strSP = "100/30";
        }
        return strSP;
    }

    public static double convertDistanceBeaten(String strDistance) {
        double dDistanceBeaten = -1;
        // ParisTurf uses () to show distance beaten when a horse is disqualified
        strDistance = strDistance.replace("(", "");
        strDistance = strDistance.replace(")", "");
        if (!"".equals(strDistance)) {
            String strDistanceBeaten = strDistance.toLowerCase();
            strDistanceBeaten = strDistanceBeaten.replace("\u00bc", ".25");
            strDistanceBeaten = strDistanceBeaten.replace("\u00be", ".75");
            strDistanceBeaten = strDistanceBeaten.replace("\u00bd", ".5");
            strDistanceBeaten = strDistanceBeaten.replace("&#188;", ".25");
            strDistanceBeaten = strDistanceBeaten.replace("&#189;", ".5");
            strDistanceBeaten = strDistanceBeaten.replace("&#190;", ".75");
            strDistanceBeaten = strDistanceBeaten.replace("&frac14;", ".25");
            strDistanceBeaten = strDistanceBeaten.replace("&frac12;", ".5");
            strDistanceBeaten = strDistanceBeaten.replace("&frac34;", ".75");
            strDistanceBeaten = strDistanceBeaten.replace("&frac14", ".25");
            strDistanceBeaten = strDistanceBeaten.replace("&frac12", ".5");
            strDistanceBeaten = strDistanceBeaten.replace("&frac34", ".75");
            strDistanceBeaten = strDistanceBeaten.replace("1/4", ".25");
            strDistanceBeaten = strDistanceBeaten.replace("1/2", ".5");
            strDistanceBeaten = strDistanceBeaten.replace("3/4", ".75");
            strDistanceBeaten = strDistanceBeaten.replace("dht", "0.0");
            strDistanceBeaten = strDistanceBeaten.replace("dh", "0.0");
            strDistanceBeaten = strDistanceBeaten.replace("nose", "0.02");
            strDistanceBeaten = strDistanceBeaten.replace("nse", "0.02");
            strDistanceBeaten = strDistanceBeaten.replace("ns", "0.02");
            strDistanceBeaten = strDistanceBeaten.replace("nez", "0.02");
            strDistanceBeaten = strDistanceBeaten.replace("sh", "0.05");
            strDistanceBeaten = strDistanceBeaten.replace("shd", "0.05");
            strDistanceBeaten = strDistanceBeaten.replace("s.h", "0.05");
            strDistanceBeaten = strDistanceBeaten.replace("cte t\u00eate", "0.05");
            strDistanceBeaten = strDistanceBeaten.replace("t\u00eate", "0.12");
            strDistanceBeaten = strDistanceBeaten.replace("hd", "0.12");
            strDistanceBeaten = strDistanceBeaten.replace("cte enc.", "0.2");
            strDistanceBeaten = strDistanceBeaten.replace("s.nk", ".2");
            strDistanceBeaten = strDistanceBeaten.replace("snk", ".2");
            strDistanceBeaten = strDistanceBeaten.replace("sn", "0.02");
            strDistanceBeaten = strDistanceBeaten.replace("sn", "0.02");
            strDistanceBeaten = strDistanceBeaten.replace("enc.", ".25");
            strDistanceBeaten = strDistanceBeaten.replace("nk", ".25");
            strDistanceBeaten = strDistanceBeaten.replace("ds", "30");
            strDistanceBeaten = strDistanceBeaten.replace("dist", "30");
            strDistanceBeaten = strDistanceBeaten.replace("loin", "30");
            strDistanceBeaten = strDistanceBeaten.replace("d.h", "0");
            strDistanceBeaten = strDistanceBeaten.replace("d.-h.", "0");
            strDistanceBeaten = strDistanceBeaten.replace("kk", "0.05"); // German kK
            strDistanceBeaten = strDistanceBeaten.replace("kh", ".2"); // German kH
            strDistanceBeaten = strDistanceBeaten.replace("k", "0.12"); // German K
            strDistanceBeaten = strDistanceBeaten.replace("h", ".25"); // German H
            strDistanceBeaten = strDistanceBeaten.replace(" ", "");
            try {
                dDistanceBeaten = Double.parseDouble(strDistanceBeaten.replace(" ", ""));
            } catch (Exception e) {
                System.out.println("Bad Distance: " + strDistance + "-" + strDistanceBeaten);
            }
        }
        return dDistanceBeaten;
    }
    
}
