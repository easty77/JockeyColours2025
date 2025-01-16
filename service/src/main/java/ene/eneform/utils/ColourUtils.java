/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.utils;

import java.awt.*;

/**
 *
 * @author Simon
 */
public class ColourUtils {
    
private static int hexToR(String h) {return Integer.parseInt((cutHex(h)).substring(0,2),16);}
private static int hexToG(String h) {return Integer.parseInt((cutHex(h)).substring(2,4),16);}
private static int hexToB(String h) {return Integer.parseInt((cutHex(h)).substring(4,6),16);}
private static String cutHex(String h) {return (h.charAt(0)=='#') ? h.substring(1,7):h;}

public static String getHexRGB(Color colour)
 {
     return "#" + rgbToHex(colour.getRed(), colour.getGreen(), colour.getBlue());
 }

public static Color getRGBColour(String strRGB)
{
    return getRGBColour(strRGB, 255);
}
public static Color getRGBColour(String strRGB, int nOpacity)
{
    strRGB = expandHex(strRGB);
    strRGB = cutHex(strRGB);
    strRGB = cutHex(strRGB);    // is  this necessary?
    int nR = hexToR(strRGB);
    int nG = hexToG(strRGB);
    int nB = hexToB(strRGB);
    return new Color( nR, nG, nB, nOpacity);
}

    public static String expandHex(String strColour) {
        if (strColour.length() == 3) {
            return strColour.substring(0, 1) + strColour.substring(0, 1) + strColour.substring(1, 2) + strColour.substring(1, 2) + strColour.substring(2) + strColour.substring(2);
        } else if (strColour.charAt(0) == '#' && strColour.length() == 4) {
            return strColour.substring(0, 1) + strColour.substring(1, 2) + strColour.substring(1, 2) + strColour.substring(2, 3) + strColour.substring(2, 3) + strColour.substring(3) + strColour.substring(3);
        } else {
            return strColour;
        }
    }

    static String toHex(int n) {
        n = Math.max(0, Math.min(n, 255));
        char[] c = new char[2];
        c[0] = "0123456789ABCDEF".charAt((n - n % 16) / 16);
        c[1] = "0123456789ABCDEF".charAt(n % 16);
        return new String(c);
    }

    public static String rgbToHex(int r, int g, int b) {
        return toHex(r) + toHex(g) + toHex(b);
    }
}
