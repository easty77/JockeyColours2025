/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.utils;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.svg.SVGRect;

/**
 *
 * @author Simon
 */
public class MeroUtils {

    // ColourUtils
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
    

    public static Color createColor(String strColour)
    {
        return createColor(strColour, 255);
    }
    public static Color createColor(String strColour, int nOpacity) {
        Color colour = null;
        try {
            Field field = Color.class.getField(strColour);
            colour = (Color) field.get(null);
        } catch (Exception e) {
            colour = null;
        }
        if (colour == null) {
            if ((strColour.indexOf("rgb(") == 0) && (strColour.charAt(strColour.length() - 1) == ')')) {
                strColour = strColour.substring(4, strColour.length() - 1);
                String[] astrRGB = strColour.split(",");
                if (astrRGB.length == 3) {
                    colour = new Color(Integer.parseInt(astrRGB[0]), Integer.parseInt(astrRGB[1]), Integer.parseInt(astrRGB[2]));
                }
            } else {
                colour = MeroUtils.getRGBColour(strColour, nOpacity);
            }
        }
        if (colour == null) {
            System.out.println("createColour failed: " + strColour);
        }
        return colour;
    }
    
    // StringUtils
public static List<Integer> indexOfAll(String strInput, String strSearch)
{
    List<Integer> alOccurrences = new ArrayList<Integer>();
    int nIndex = strInput.indexOf(strSearch);
    while (nIndex >= 0) {
        alOccurrences.add(nIndex);
        nIndex = strInput.indexOf(strSearch, nIndex + 1);
    }    
    
    return alOccurrences;
}

    public static Rectangle getViewBoxRectangle(String strViewBox) {
        if (strViewBox != null) {
            String[] astrDimensions = strViewBox.split(" ");
            if (astrDimensions.length == 4) {
                try {
                    int nX = (int)Double.parseDouble(astrDimensions[0]);
                    int nY = (int)Double.parseDouble(astrDimensions[1]);
                    int nWidth = (int)Double.parseDouble(astrDimensions[2]);
                    int nHeight = (int)Double.parseDouble(astrDimensions[3]);
                    Rectangle rectangle = new Rectangle(nX, nY, nWidth, nHeight);
                    return rectangle;
                } catch (NumberFormatException e) {
                    // ignore dimensions parameter
                }
            }
        }
        return null;
    }
    
    // Serializable
    public static boolean serialize(Object obj, String strFileName)
    {
        try
        {
            FileOutputStream fout=new FileOutputStream(strFileName);  
            ObjectOutputStream out=new ObjectOutputStream(fout);  

            out.writeObject(obj);  
            out.flush(); 
            return true;
        }
        catch(Exception e)
        {
            // FileNotFoundException, IOException
            return false;
        }
    }  
    public static Object deserialize(String strFileName)
    {
        try
        {
            ObjectInputStream in=new ObjectInputStream(new FileInputStream(strFileName));  
            Object obj = in.readObject();  
  
            in.close();  
            return obj;
        }
        catch(Exception e)
        {
            // FileNotFoundException, IOException, ClassNotFoundException
            return null;
        }
    }  
 public static Rectangle convertSVGRectangle(SVGRect rect)
 {
    return new Rectangle((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());   
 }
}
