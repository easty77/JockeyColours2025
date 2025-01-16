/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.factory;

import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 *
 * @author Simon
 */
public class SVGFactoryUtils {
    static final String[] sm_astrUnwantedSVGAttributes = {"stroke-dasharray", "shape-rendering", "font-family", "text-rendering", "fill-opacity", "contentScriptType", "color-interpolation", "color-rendering", "preserveAspectRatio", "font-size", "fill", "stroke", "image-rendering", "stroke-miterlimit", "zoomAndPan", "stroke-linecap", "stroke-linejoin", "contentStyleType", "font-style", "stroke-width", "stroke-dashoffset", "font-weight", "stroke-opacity"};
    public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";
    public static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";
    public static final String XLINK_NAMESPACE = "http://www.w3.org/1999/xlink";
    
    public static Element createRectElement(Document doc, String strId, int nX, int nY, int nWidth, int nHeight, String strFillColour, boolean bStroke)
    {
        Element rect = doc.createElementNS(SVG_NAMESPACE, "rect");
        if (strId != null)
            rect.setAttributeNS(null, "id", strId);
        rect.setAttributeNS(null, "x", String.valueOf(nX));
        rect.setAttributeNS(null, "y", String.valueOf(nY));
        rect.setAttributeNS(null, "width", String.valueOf(nWidth));
        rect.setAttributeNS(null, "height", String.valueOf(nHeight));
        String strStyle = "";
        if (!"".equals(strFillColour))
        {
            strStyle = "fill: " + strFillColour + ";";
            if (!bStroke)
                strStyle += " stroke:none;";
        }
        else
        {
            strStyle = "stroke: none; fill: black; fill-opacity: 0.0;";
        }
        rect.setAttributeNS(null, "style", strStyle);
        
        return rect;
    }
    public static Element createMaskElement(Document doc, String strId, int nX, int nY, int nWidth, int nHeight)
    {
        Element mask = doc.createElementNS(SVG_NAMESPACE, "mask");
        if (strId != null)
            mask.setAttributeNS(null, "id", strId);
        mask.setAttributeNS(null, "x", String.valueOf(nX));
        mask.setAttributeNS(null, "y", String.valueOf(nY));
        mask.setAttributeNS(null, "width", String.valueOf(nWidth));
        mask.setAttributeNS(null, "height", String.valueOf(nHeight));
 
        return mask;
    }
    public static Element createTextElement(Document doc, int nX, int nY, String strAlignment)
    {
        // no content - for use with tspan
        Element text = doc.createElementNS(SVG_NAMESPACE, "text");
        text.setAttributeNS(null, "x", String.valueOf(nX));
        text.setAttributeNS(null, "y", String.valueOf(nY));
        String strStyle = "text-anchor: " + strAlignment + ";";
        text.setAttributeNS(null, "style",   strStyle);
        
        return text;
    }
    public static Element addTextElement(Document doc, Element tlg, String strContent, int nX, int nY, String strTextColour, int nFontSize, String strAlignment, boolean bBold)
    {
        // backwards compatibility for CareerSVGFactory
        return addTextElement(doc, tlg, strContent, nX, nY, strTextColour, nFontSize, strAlignment, bBold, null);
    }
    public static Element addTextElement(Document doc, Element tlg, String strContent, int nX, int nY, String strTextColour, int nFontSize, String strAlignment, boolean bBold, String strFontFamily)
    {
         Element g = doc.createElementNS(SVG_NAMESPACE, "g");
         g.appendChild(SVGFactoryUtils.createTextElement(doc, strContent, 
                 nX, nY, 
                 strTextColour, nFontSize, 
                 strAlignment, bBold, strFontFamily)
                 );
         tlg.appendChild(g);
         
         return tlg;
    }
    public static Element createTextElement(Document doc, String strContent, int nX, int nY, String strTextColour, int nFontSize, String strAlignment, boolean bBold)
    {
        // backwards compatibility for CareerSVGFactory
        return createTextElement(doc, strContent, nX, nY, strTextColour, nFontSize, strAlignment, bBold, null);
    }
    public static Element createTextElement(Document doc, String strContent, int nX, int nY, String strTextColour, int nFontSize, String strAlignment, boolean bBold, String strFontFamily)
    {
        Element text = doc.createElementNS("http://www.w3.org/2000/svg", "text");
        text.setAttributeNS(null, "x", String.valueOf(nX));
        text.setAttributeNS(null, "y", String.valueOf(nY));
        
        // 20160427 - implement as separate attrributes, why was single style attribute previously preferred?
        text.setAttributeNS(null, "fill", strTextColour);
        text.setAttributeNS(null, "stroke", strTextColour);
        text.setAttributeNS(null, "font-size", nFontSize + "px");
        if (strFontFamily != null)
            text.setAttributeNS(null, "font-family", strFontFamily);
        if (bBold)
           text.setAttributeNS(null, "font-weight", "bold");
        text.setAttributeNS(null, "text-anchor", strAlignment);
/*        String strStyle = "fill: " + strTextColour + "; stroke: " + strTextColour + ";";
        strStyle += (" font-size: " + nFontSize + "px;");
        if (bBold)
            strStyle += " font-weight: bold;";
        strStyle += (" text-anchor: " + strAlignment + ";");
        text.setAttributeNS(null, "style",   strStyle); */
        text.setTextContent(strContent);
        
        return text;
    }
    public static Element createTSpanElement(Document doc, String strContent, String strTextColour, int nFontSize, boolean bBold)
    {
        Element tspan = doc.createElementNS("http://www.w3.org/2000/svg", "tspan");
         String strStyle = "fill: " + strTextColour + "; stroke: " + strTextColour + ";";
        strStyle += (" font-size: " + nFontSize + "px;");
        if (bBold)
            strStyle += " font-weight: bold;";
        // no alignment, always start (default)
        tspan.setAttributeNS(null, "style",   strStyle);
        tspan.setTextContent(strContent);
            
        return tspan;
    }
/*    public static String convertSVGGenerator2String(SVGGraphics2D svgGenerator, SVGSVGElement root, boolean bCompress) throws IOException
    {
        String strSVG = "";
        try
        {
            StringWriter swriter = new StringWriter();
            svgGenerator.stream(root, swriter);  
            strSVG = swriter.toString();

            strSVG = processSVGString(strSVG, bCompress);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        
        return strSVG;
    } */
public static String convertSVGNode2String(Node node, boolean bCompress)
{
    // node could be a Document or a SVGOMSVGElement
   String strSVG = "";
   try
   {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        
        // optional parameters
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "image/svg+xml");

        DOMSource source = new DOMSource(node);

        StringWriter swriter = new StringWriter();
        StreamResult sresult = new StreamResult(swriter);
        transformer.transform(source, sresult);
        strSVG = swriter.toString();
        
        strSVG = processSVGString(strSVG, bCompress);
    }
    catch(TransformerConfigurationException e)
    {
        
    }
    catch(TransformerException e)
    {
        
    }

   return strSVG;
}
    
public static String processSVGString(String strSVG, boolean bCompress)
{
    // new lines etc
    strSVG = strSVG.replaceAll("\\r\\n[\\s]*>", ">");
    strSVG = strSVG.replaceAll("\\r\\n[\\s]*/>", "/>\r\n");
    strSVG = strSVG.replaceAll("&apos;", "'");

    strSVG = strSVG.replaceAll(" fill=\"\"", " fill=\"none\"");
    strSVG = strSVG.replaceAll(" stroke=\"\"", " stroke=\"none\"");

    // owner SVGs need white background (now transparent for single images loaded to Wikipedia)
    //strSVG = strSVG.replaceAll("<rect fill=\"none\" x=\"194\" width=\"207\" height=\"320\" y=\"200\" stroke=\"none\"/>",
    //        "<rect fill=\"white\" x=\"194\" width=\"207\" height=\"320\" y=\"200\" stroke=\"none\"/>");
                    
    // tidy up loose ends
    for(int i = 0; i < SVGFactoryUtils.sm_astrUnwantedSVGAttributes.length; i++)
    {
        // removeAttribute doesn't seem to work, so remove strings before writing
        String strSearchString = (" " + SVGFactoryUtils.sm_astrUnwantedSVGAttributes[i] + "=\"\"");
        strSVG = strSVG.replaceAll(strSearchString, "");
    } 
    // switch between readable and compressed versions
    if (bCompress)
    {
        // to do: remove ids that aren't referenced
        strSVG = strSVG.replaceAll("\\r\\n", "");
        strSVG = strSVG.replaceAll(">[\\s]+<", "><");
        strSVG = strSVG.replaceAll("[\\s]+", " ");
    }
    else
    {
        // add line feeds for readability
        strSVG = strSVG.replaceAll("</([\\w]*)>", "</$1>\r\n");
        strSVG = strSVG.replaceAll("><", ">\r\n<");
    }

    //StreamResult result = new StreamResult(writer);
    //transformer.transform(source, result);

    return strSVG;
}
static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
}
static public int getIntAttributeNS(Element element, String strNamespace, String strName)
{
    int nValue = -1;
    String strValue = element.getAttributeNS(strNamespace, strName).replace("px", "");
    try
    {
        nValue = Integer.valueOf(strValue);
    }
    catch(NumberFormatException e)
    {
        
    }
    return nValue;
}
}
