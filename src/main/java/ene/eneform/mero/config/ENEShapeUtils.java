/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.config;

import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Map;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.AWTPolygonProducer;
import org.xml.sax.Attributes;

/**
 *
 * @author Simon
 */
public class ENEShapeUtils {
    
    public static Shape processPolygonAttributes(Attributes attributes)
    {
        Shape shape = null;
        // points
       String strPoints = attributes.getValue("points");
        if ((strPoints != null) && !"".equals(strPoints))
        {
            shape = convertPolygonPoints(strPoints);
        }

       return shape;
    }
    public static Shape processPolylineAttributes(Attributes attributes)
    {
        // points
        GeneralPath path = new GeneralPath();
        String strPoints = attributes.getValue("points");
        if ((strPoints != null) && !"".equals(strPoints))
        {
            String[] astrPoints = strPoints.split(" ");
            for(int i = 0; i < astrPoints.length; i++)
            {
                String strPoint = astrPoints[i];
                String[] astrCoords = strPoint.split(",");
                if (i == 0)
                    path.moveTo(Float.parseFloat(astrCoords[0]), Float.parseFloat(astrCoords[1]));
                else
                    path.lineTo(Float.parseFloat(astrCoords[0]), Float.parseFloat(astrCoords[1]));
            }
            
            return path;
       }

       return null;
    }
    public static Shape processLineAttributes(Attributes attributes)
    {
        // a Line has no Area - so SVG of only Lines is calculated as having zero size
        GeneralPath path = new GeneralPath();
        String strX1 = attributes.getValue("x1");
        float fX1 = (strX1 != null) ? Float.parseFloat(strX1) : 0.0f;
        String strY1 = attributes.getValue("y1");
        float fY1 = (strY1 != null) ? Float.parseFloat(strY1) : 0.0f;
        String strX2 = attributes.getValue("x2");
        float fX2 = (strX2 != null) ? Float.parseFloat(strX2) : 1.0f;
        String strY2 = attributes.getValue("y2");
        float fY2 = (strY2 != null) ? Float.parseFloat(strY2) : 1.0f;
        path.moveTo(fX1, fY1);
        path.lineTo(fX2, fY2);
        path.closePath();
            
        return path;
    }
        private static Shape convertPolygonPoints(String strPoints)
        {
            // use Batiks classes wherever possible
            Shape shape = null;
               try
               {
                   // AWTPolygonProducer.createShape creates a GeneralPath - prefer a Polygon, but in Java this only supports integer values (not sufficient)
                   shape = AWTPolygonProducer.createShape(new StringReader(strPoints), GeneralPath.WIND_EVEN_ODD);
               }
               catch(NumberFormatException e)
               {
                    System.out.println("convertPathD NumberFormatException: " + e.getMessage());
               }
              catch(IOException e)
               {
                    System.out.println("convertPathD IOException: " + e.getMessage());
               }

            return shape;
    }
    public static Shape processPathAttributes(Attributes attributes)
    {
        Shape shape = null;

        String strD = attributes.getValue("d");
        if ((strD != null) && !"".equals(strD))
        {
            shape = convertPathD(strD);
        }
                 
        return shape;
    }
    private static Shape convertPathD(String strD)
    {
       Shape shape = null;
       try
       {
           shape = AWTPathProducer.createShape(new StringReader(strD), GeneralPath.WIND_EVEN_ODD);
        }
       catch(IOException e)
       {
            System.out.println("convertPathD IOException: " + e.getMessage());
       }
       return shape;
    }
    public static Shape processEllipseAttributes(Attributes attributes)
    {
        Shape shape = null;
        // cx, cy, rx, ry, fill
        float cx = Float.parseFloat(attributes.getValue("cx"));
        float cy = Float.parseFloat(attributes.getValue("cy"));
        float rx = Float.parseFloat(attributes.getValue("rx"));
        float ry = Float.parseFloat(attributes.getValue("ry"));
        shape = new Ellipse2D.Double(cx - rx, cy - ry, rx * 2, ry * 2);

        return shape;
    }
    public static Shape processCircleAttributes(Attributes attributes)
    {
        Shape shape = null;
        // cx, cy, rx, ry, fill
        float cx = Float.parseFloat(attributes.getValue("cx"));
        float cy = Float.parseFloat(attributes.getValue("cy"));
        float r = Float.parseFloat(attributes.getValue("r"));
        shape = new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2);

        return shape;
    }
    public static Shape processRectangleAttributes(Attributes attributes)
    {
        String strX = attributes.getValue("x");
        String strY = attributes.getValue("y");
        String strWidth = attributes.getValue("width");
        String strHeight = attributes.getValue("height");
        Shape shape = new Rectangle2D.Double(
                    (strX != null) ? Double.parseDouble(strX) : 0,
                    (strX != null) ? Double.parseDouble(strY) : 0,
                    Double.parseDouble(strWidth),
                    Double.parseDouble(strHeight)
                    );
        
         return shape;
    }
    public static Shape processTextAttributes(Attributes attributes, String strChars)
    {
        
        int nX = getAttributeInteger(attributes, "x", 0);
        int nY = getAttributeInteger(attributes, "y", 0);
        
        Map<TextAttribute, Object> mapFont = new Hashtable<TextAttribute, Object>();
        String strFontWeight = attributes.getValue("font-weight");
        Float fWeight = null;
        if ("bold".equalsIgnoreCase(strFontWeight))
            fWeight = TextAttribute.WEIGHT_BOLD;

        if (fWeight != null)
            mapFont.put(TextAttribute.WEIGHT, fWeight);

        Float fSize = convertDimension(attributes.getValue("font-size"), null);
        if (fSize != null)
            mapFont.put(TextAttribute.SIZE, fSize);
        String strFontStyle = attributes.getValue("font-style");
        String strFontStretch = attributes.getValue("font-stretch");
        String strFontFamily = attributes.getValue("font-family");
        if ((strFontFamily != null) && !"".equals(strFontFamily))
            mapFont.put(TextAttribute.FAMILY, strFontFamily);

        String strTextAnchor = attributes.getValue("text-anchor");  // start, middle, end

        int nWidth = 60 * strChars.length();   // based on number of characters
        int nHeight = fSize.shortValue();   // based on font-size attribute
        String strHeight = attributes.getValue("height");
        Shape shape = new Rectangle2D.Double(
                    nX,
                    nY - nHeight/2,
                    nWidth,
                    nHeight
                    );
        
         return shape;
    }
    public static Float getAttributeFloat(Attributes attributes, String strParameter, Float fDefaultValue)
    {
        String strValue = attributes.getValue(strParameter);

        return convertFloat(strValue, fDefaultValue);
    }
    public static Float convertFloat(String strValue, Float fDefaultValue)
    {
        Float fValue = fDefaultValue;
        if ((strValue != null) && !"".equals(strValue))
            fValue = Float.parseFloat(strValue);

        return fValue;
    }
    public static Integer getAttributeInteger(Attributes attributes, String strParameter, Integer nDefaultValue)
    {
        String strValue = attributes.getValue(strParameter);

        return convertInteger(strValue, nDefaultValue);
    }
    public static Integer convertInteger(String strValue, Integer nDefaultValue)
    {
        int nValue = nDefaultValue;
        if ((strValue != null) && !"".equals(strValue))
            nValue = Integer.parseInt(strValue);

        return nValue;
    }
    public static Float getAttributeDimension(Attributes attributes, String strParameter, Float fValue)
    {
        String strValue = attributes.getValue(strParameter);
        fValue = convertDimension(strValue, fValue);
        System.out.println("Dimension: " + strParameter + "-" + strValue + "-" + fValue);
        return fValue;
    }
    public static Float convertDimension(String strValue, Float fValue)
    {
        if ((strValue != null) && !"".equals(strValue))
        {
            if (strValue.indexOf("pt") > 0)
            {
                strValue = strValue.replaceAll("pt", "");
            }
            else if(strValue.indexOf("px") > 0)
            {
                strValue = strValue.replaceAll("px", "");
            }

            fValue = Float.parseFloat(strValue);
        }

         return fValue;
    }
}
