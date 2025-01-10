/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.factory;

import ene.eneform.mero.action.ENEJacketSVGAction;
import ene.eneform.mero.colours.ENEColoursElementPattern;
import ene.eneform.mero.action.ENEPatternAction;
import ene.eneform.mero.action.ENESVGAction;
import ene.eneform.mero.colours.ENEColoursElement;
import ene.eneform.mero.colours.ENERacingColours;
import ene.eneform.mero.utils.ENEColourItem;
import ene.eneform.mero.config.ENEColoursEnvironment;
import ene.eneform.mero.fabric.ENEFabricItem;
import ene.eneform.mero.utils.ENEFillItem;
import ene.eneform.mero.tartan.ENETartan;
import ene.eneform.mero.tartan.ENETartanItem;
import ene.eneform.mero.tartan.ENETartanUtils;

import java.awt.Point;
import java.awt.Rectangle;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.anim.dom.SVGOMPatternElement;
import org.apache.batik.anim.dom.SVGOMTextElement;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGGElement;

/**
 *
 * @author Simon
 */
public class ENEMeroFactory {
public static final int MERO_VIEWBOX_X = 194; // 
public static final int MERO_VIEWBOX_Y = 200;   // from 205 before 1.1 scale applied
public static final int MERO_VIEWBOX_WIDTH = 207;   // 
public static final int MERO_VIEWBOX_HEIGHT = 320;  // from 315 before 1.1 scale applied
public static final Point MERO_CAP_ORIGIN = new Point(0, -20);    // from (0, 5) before 1.1 scale applied
public static final int MERO_CAP_WIDTH = 60;    // from (0, 5) before 1.1 scale applied

private Document m_document = null;
private ArrayList<String> m_alPatterns = new ArrayList<String>();   // patterns - may need to be made unique
private ArrayList<String> m_alReferences = new ArrayList<String>(); // path references
private ENERacingColours m_racingcolours;

private String m_strLanguage= ENEColoursEnvironment.DEFAULT_LANGUAGE;
private String m_strUniqueId = null;        // needs to be appended to all patterns to guarntee uniqueness withinn a SVG containing multiple Mero images
private Element m_elementDefs = null;
private Element m_elementRoot = null;

private HashMap<String,Element> m_hmParentElementDefs = null;   // for use when multiple Mero images are in a document and share defintions e.g. career

private Point m_capOrigin = MERO_CAP_ORIGIN;

private char SPLIT_CHAR = '-';

public ENEMeroFactory(ENERacingColours racingcolours, String strLanguage)
{
    // for standalone Mero images
    m_racingcolours = racingcolours;
    m_strLanguage = strLanguage;
    DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();

    // Create an instance of org.w3c.dom.Document.
    m_document = domImpl.createDocument(SVGFactoryUtils.SVG_NAMESPACE, "svg", null);
}
public ENEMeroFactory(ENERacingColours racingcolours, String strLanguage, Document document, String strUniqueId, HashMap<String,Element> hmDefintions)
{
    // called when ading multiple Mero images to a document
    m_racingcolours = racingcolours;
    m_strLanguage = strLanguage;
    m_document = document;
    m_strUniqueId = strUniqueId;
    m_hmParentElementDefs = hmDefintions;
 }
public Document getDocument()
{
    return m_document;
}
public void setCapOrigin(Point capOrigin)
{
    m_capOrigin = capOrigin;
}
public static Rectangle getViewBox(Point capOrigin)
{
    // Assumed that the cap is either directly above (0, 5) or top right
    // standard (0, -20): 194, 200, 207, 320
    // side (130, 90): 194, 290, 264, 230
    int nViewBoxX = MERO_VIEWBOX_X;
    
    int nViewboxY = MERO_VIEWBOX_Y;
    int nViewboxHeight = MERO_VIEWBOX_HEIGHT;
    if (capOrigin.getY() > 0)
    {
        nViewboxY = (int) (MERO_VIEWBOX_Y + capOrigin.getY());
        nViewboxHeight = (int) (MERO_VIEWBOX_HEIGHT - capOrigin.getY());
    }

    int nViewboxWidth = MERO_VIEWBOX_WIDTH;
    if (capOrigin.getX() > 70)
       nViewboxWidth  = (int) (MERO_VIEWBOX_WIDTH + (capOrigin.getX() - 70));
 
    return (new Rectangle(nViewBoxX, nViewboxY, nViewboxWidth, nViewboxHeight));
}
public Document generateSVGDocument(String strMeroId, double dScale, String strBackgroundColour)
{
    Element svg = m_document.getDocumentElement();

    if (svg == null)
    {
        svg = m_document.createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "svg");        
        m_document.appendChild(svg);
    }
    Rectangle viewbox = getViewBox(m_capOrigin);    
    
    svg.setAttributeNS(null, "viewBox", (viewbox.getX() + " " + viewbox.getY() + " " + viewbox.getWidth() + " " + viewbox.getHeight()));

    // Wikipedia doesn't like the svg definition that is generated
    svg.setAttributeNS(null, "width", "100%");
    svg.setAttributeNS(null, "height", "100%");
    for(int i = 0; i < SVGFactoryUtils.sm_astrUnwantedSVGAttributes.length; i++)
    {
        // doesn't seem to work, so remove strings before writing
        svg.removeAttribute(SVGFactoryUtils.sm_astrUnwantedSVGAttributes[i]);
        svg.setAttributeNS(null, SVGFactoryUtils.sm_astrUnwantedSVGAttributes[i], "");
     }

    // add g containing everything, so title will work as mouseover 
    m_elementRoot = m_document.createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "g");
    Element title = m_document.createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "title");
    if (m_racingcolours != null)
        title.setTextContent(m_racingcolours.getTitle());
    m_elementRoot.appendChild(title);
 
    Element background = m_document.createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "rect");
    background.setAttributeNS(null, "x", String.valueOf(MERO_VIEWBOX_X));
    background.setAttributeNS(null, "y", String.valueOf(MERO_VIEWBOX_Y));
    background.setAttributeNS(null, "width", String.valueOf(MERO_VIEWBOX_WIDTH));
    background.setAttributeNS(null, "height", String.valueOf(MERO_VIEWBOX_HEIGHT));
    background.setAttributeNS(null, "fill", strBackgroundColour == null ? "none" : strBackgroundColour);
    background.setAttributeNS(null, "stroke", "none");
    m_elementRoot.appendChild(background);

    
    m_elementDefs = m_document.createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "defs");
    m_elementDefs.setAttributeNS(null, "id", "defs1");    // no id as will be one for each Mero

    // defs must come first otherwise Wikipedia won't show patterns
    svg.appendChild(m_elementDefs);
    svg.appendChild(m_elementRoot);

    // For back remove "Cap", and switch id in jacket.svg
    String astrSVG[] = {"Jacket", "Sleeves", "Cap"};     
    for(int i = 0; i < astrSVG.length; i++)
    {
        String strTopLevelElement = astrSVG[i];
        Document svgDoc = ENEColoursEnvironment.getInstance().getSVGDocument(strTopLevelElement.toLowerCase());
        
        Element elementParent = m_elementRoot;
        m_alReferences.add(strTopLevelElement);
        Element newNode = createSVGItem(m_document, svgDoc, strTopLevelElement, strTopLevelElement);
        if (m_racingcolours != null)    // null means just draw silhouette based on shadows
        {
            ENEFillItem colour0 = null;
            ENEColoursElement racingcolours1 = m_racingcolours.getColoursElement(strTopLevelElement);
            if (racingcolours1 != null)
            {
                colour0 = racingcolours1.getColourItem();
            }
            // First the main item, then the patterns

            newNode = customiseSVGItem(m_document, svgDoc, strTopLevelElement, strTopLevelElement, newNode, colour0, null);
            newNode = useDefinition(elementParent, newNode, strTopLevelElement, true);

            // Repeat for each pattern
            if (racingcolours1 != null)
            {
                Iterator<ENEColoursElementPattern> iter = racingcolours1.getPatternIterator();
                while(iter.hasNext())
                {
                    ENEColoursElementPattern pattern = iter.next();
                    ArrayList<ENEFillItem> patternColours = pattern.getColourList();
                    String strMapping = ENEColoursEnvironment.getInstance().getPatternMapping("ENE"  + astrSVG[i], pattern.getPattern(), m_strLanguage);
                    if (strMapping.equals("text"))
                    {
                        createTextPattern(newNode, astrSVG[i], strMapping, pattern.getAdditionalText(), colour0, patternColours);
                     }
                    else
                    {
                        Element elementPattern = createSVGItem(m_document, svgDoc, astrSVG[i], strMapping);
                        if (elementPattern != null)
                        {
                            elementPattern = customiseSVGItem(m_document, svgDoc, astrSVG[i], strMapping, elementPattern, colour0, patternColours);
                            newNode = useDefinition(newNode, elementPattern, strTopLevelElement + SPLIT_CHAR + strMapping, false);
                            //newNode.appendChild(elementPattern);
                        }
                         else
                            System.out.println("generateSVGDocument Pattern not found: " + strMapping); // chevron hoop?
                    }
                }
            }
        }
        else
        {
            newNode = customiseSVGItem(m_document, svgDoc, strTopLevelElement, strTopLevelElement, newNode, null, null);
            newNode = useDefinition(elementParent, newNode, strTopLevelElement, true);
        }
        
        if("Cap".equals(strTopLevelElement))
        {
            String strShadowsId = "Cap_shadows";
            Element shadowNode = (Element) (svgDoc.getElementById(strShadowsId).cloneNode(true));
            if (m_racingcolours == null)
            {
                shadowNode.setAttribute("fill-opacity", "0.25");
                shadowNode.setAttribute("stroke-opacity", "0.25");
                strShadowsId += "_blur";
            }
            m_document.adoptNode(shadowNode);
            useDefinition(newNode, shadowNode, strShadowsId, true);
        }
        else if("Sleeves".equals(strTopLevelElement))   // Sleeves comes after Jacket and shadows are for both - so must do mast otherwise will be overwritten
        {
            // For back replace "Shadows" with "Back_shadows"
            String strShadowsId = "Shadows";
             Element shadowNode = (Element) (svgDoc.getElementById(strShadowsId).cloneNode(true));
            if (m_racingcolours == null)
            {
                shadowNode.setAttribute("fill-opacity", "0.25");
                strShadowsId += "_blur";
            }
            m_document.adoptNode(shadowNode);
            useDefinition(newNode, shadowNode, strShadowsId, true);
        }
        
    }
     if (!"".equalsIgnoreCase(strMeroId))
        m_elementRoot.setAttributeNS(null, "id", strMeroId);
     if (dScale != 1)
        m_elementRoot.setAttributeNS(null, "transform", "scale(" + dScale + ")"); 

     return m_document;
}
private void createTextPattern(Element parentNode, String strElement, String strMapping, String strText, ENEFillItem colour0, ArrayList<ENEFillItem> patternColours)
{
    // font-size, font-family, font-style
     //Element textNode = getSVGActionElement(new ENEJacketSVGAction("text"));
     SVGDocument svgTextDoc = (SVGDocument) SVGDOMImplementation.getDOMImplementation().createDocument(SVGFactoryUtils.SVG_NAMESPACE, "svg", null);
     Element g = svgTextDoc.createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "g");
     svgTextDoc.getDocumentElement().appendChild(g);
     Element textNode = SVGFactoryUtils.createTextElement(svgTextDoc, strText, 0, 0, patternColours.get(0).getText(), 80, "start", true, "Sans-Serif");
     g.appendChild(textNode);

     // calculate size
     Rectangle rect = ENEColoursEnvironment.getInstance().getTextBBox(svgTextDoc, (SVGOMTextElement)textNode);

     ENEPatternAction action = ENEColoursEnvironment.getInstance().getPatternAction("ENE" + strElement, strMapping, "en");
     if ((action == null) || (action.getClass().getName().indexOf("SVGAction") < 0)) // need full name as use enclosed classes
     {
         action = new ENEJacketSVGAction("text");
     }
     // place in specified location
     textNode = getSVGActionElement(svgTextDoc, g, ((ENESVGAction)action).getMeroRectangles(), rect);
     textNode = (Element) textNode.cloneNode(true);
     m_document.adoptNode(textNode);
     useDefinition(parentNode, textNode, "text", true);
}
private Element useDefinition(Element elementTop, Element newNode, String strId, boolean bForce)
{
    Element elementParent = elementTop;
    newNode.setAttributeNS(null, "id", strId);
    if (strId.indexOf("Sleeves" + SPLIT_CHAR) == 0)
        newNode.setAttributeNS(null, "clip-path", "url(#Sleeves_CP)" ); // Doing for Sleeves has tangible benefits, but doing same for Jacket means Seams (and mabybe others) don't appear as presumably outside Jacker cluip-path
    
    
    // can only reuse elements if they have both fill and stroke specified in their outer element
    String strFill = newNode.getAttributeNS(null, "fill");
    String strStroke = newNode.getAttributeNS(null, "stroke");
    if (bForce || ((!"".equals(strFill)) && (strFill.indexOf("url(#") != 0) && (!"".equals(strStroke))))
    {
        if ("Cap".equals(strId))    // everything has to be offset by 5 pixels
        {
            Element g = m_document.createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "g");
            g.setAttributeNS(null, "transform", "translate(" + m_capOrigin.getX() + " " + m_capOrigin.getY() + ") scale(1.0 1.085)"); 
            elementParent.appendChild(g);
            elementParent = g;
        }

        Element use = m_document.createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "use");
        use.setAttributeNS(SVGFactoryUtils.XLINK_NAMESPACE, "xlink:href", "#" + strId); 
        use.setAttributeNS(null, "x", String.valueOf(0));
        use.setAttributeNS(null, "y", String.valueOf(0));

        if (!"none".equals(strFill))
        {
            use.setAttributeNS(null, "fill", strFill);
            newNode.removeAttributeNS(null, "fill");
        }
        if (!"none".equals(strStroke))
        {
            use.setAttributeNS(null, "stroke", strStroke);
            newNode.removeAttributeNS(null, "stroke");
        }
        elementParent.appendChild(use);

        if (m_hmParentElementDefs != null)
        {
            if (!m_hmParentElementDefs.containsKey(strId))
                m_hmParentElementDefs.put(strId, newNode);
        }
        else
            m_elementDefs.appendChild(newNode);
    }
    else
    {
        if (strFill.indexOf("url(#") == 0)
        {
            newNode.setAttributeNS(null, "id", strId + "-" + strFill.substring(5, strFill.length() - 1));
        }
       elementParent.appendChild(newNode);
    }
    
    return elementParent;
}
private void addDefinitionElement(Element newNode, String strId)
{
    if (newNode != null)
    {
        if (m_hmParentElementDefs != null)
        {
            if (!m_hmParentElementDefs.containsKey(strId))
                m_hmParentElementDefs.put(strId, newNode);
        }
        else
            m_elementDefs.appendChild(newNode);
    }
}
private Element createSVGItem(Document document, Document svgDoc, String strElement, String strId)
{
    // parent may be top-level element or defs or child object (Jacket, Sleeves, Cap)
    Element oldNode = svgDoc.getElementById(strId);
    if (oldNode == null)
    {
        //System.out.println("Looking for: " + strId);
        // might be a unstranslated tartan
        if (ENEColoursEnvironment.getInstance().isFabric(strId, "en"))
        {
            strId = ENEColoursEnvironment.getInstance().getFabricItem(strId, "en").getResourceName();
        }
        // or a translated one
        ENETartan tartan = ENEColoursEnvironment.getInstance().getTartan(strId);
        if ((tartan != null) && (m_document.getElementById(strId) == null))
        {
            ENETartanUtils.generateSVGTartanElement(document, m_elementDefs, tartan);
        }
        else
        {
            ENEPatternAction action = ENEColoursEnvironment.getInstance().getPatternAction("ENE" + strElement, strId, "en");
            if ((action != null) && (action.getClass().getName().indexOf("SVGAction") > 0)) // need full name as use enclosed classes
            {
                oldNode = getSVGActionElement((ENESVGAction)action);
            }
        }
    }
    if (oldNode == null)
        return null;
    Element newNode = (Element) oldNode.cloneNode(true);
    // Transfer ownership of the new node into the destination document
    document.adoptNode(newNode);
    
    if ((m_strUniqueId != null) && (newNode instanceof SVGOMPatternElement))
    {
        newNode.setAttributeNS(null, "id", strId +"_" + m_strUniqueId);
    }

    return newNode;
}

private Element customiseSVGItem(Document document, Document svgDoc, String strElement, String strId, Element newNode, ENEFillItem colour0, ArrayList<ENEFillItem> patternColours)
{
    String strDisplay = newNode.getAttributeNS(null, "display");
    if ("none".equals(strDisplay))   // make visible
        newNode.removeAttribute("display");
    if (colour0 != null)
    {
        // Step 1 - convert colours
        newNode = replaceColourReferences(newNode, colour0, patternColours);
        //System.out.println(SVGFactoryUtils.convertSVGNode2String(newNode, false));
        
        // Step 2 - retrieve all patterns, clip-paths etc required
        ArrayList<String> alPatterns = new ArrayList<String>();
        ArrayList<String> alReferences = new ArrayList<String>();
        alReferences = extractReferences(newNode, alReferences);
        for(int i = 0; i < alReferences.size(); i++)
        {
            String strReference = alReferences.get(i);
            Element elementReference = createSVGItem(document, svgDoc, strElement, strReference);
            if (elementReference != null)
            {
                elementReference = customiseSVGItem(document, svgDoc, strElement, strId, elementReference, colour0, patternColours);
                String strRefId = elementReference.getAttributeNS(null, "id");
                addDefinitionElement(elementReference, strRefId);
            }
        }
        alPatterns = extractPatterns(newNode, alPatterns);
        // Step 3 - add patterns and clip-paths
        for(int i = 0; i < alPatterns.size(); i++)
        {
            String strPattern = alPatterns.get(i);
            System.out.println(strPattern);
            Element elementPattern = createSVGItem(document, svgDoc, strElement, strPattern);
            if (elementPattern != null)
            {
              elementPattern = customiseSVGItem(document, svgDoc, strElement, strPattern, elementPattern, colour0, patternColours);
                // patterns must be inside SVG as contain colour-specific data
                String strPatternId = elementPattern.getAttributeNS(null, "id");
                m_elementDefs.appendChild(elementPattern);
                //addNewDefinitionElement(elementPattern, elementPattern.getAttributeNS(null, "id"));
               
                // might be a tartan
                if (ENEColoursEnvironment.getInstance().isFabric(strPattern, "en"))
                    strPattern = ENEColoursEnvironment.getInstance().getFabricItem(strPattern, "en").getResourceName();

                ENETartan tartan = ENEColoursEnvironment.getInstance().getTartan(strPattern);
                if (tartan != null)
                {
                    ENETartanUtils.generateSVGTartanElement(document, m_elementDefs, tartan);
                }
                else
                {
                    System.out.println("Tartan not found: " + strPattern);
                } 
            }
            else
            {
                System.out.println("customiseSVGItem Pattern not found: " + strPattern);
            }
        }
    }
    //System.out.println(SVGFactoryUtils.convertSVGNode2String(newNode, false));
    return newNode;
}
private Element getSVGActionElement(ENESVGAction action)
{
   String strSVGName = action.getSVGName();
   SVGDocument svgdoc = ENEColoursEnvironment.getInstance().getSVGDocument(strSVGName);
    //System.out.println("Pattern svg: " + strId + "-" + strSVGName);
    if (svgdoc != null)
    {
        Element svg = (Element) svgdoc.getDocumentElement();
        NodeList gList = svg.getElementsByTagNameNS(SVGFactoryUtils.SVG_NAMESPACE, "g");
        if (gList.getLength() >= 1)
        {
            Element node = (Element) gList.item(0);
            System.out.println("SVG loaded: " + strSVGName);
    
            Rectangle rect = ENEColoursEnvironment.getInstance().getGBBox(svgdoc, (SVGGElement) node);
            svg = (Element)svg.cloneNode(true);
           return getSVGActionElement(svgdoc, (Element)svg.getElementsByTagNameNS(SVGFactoryUtils.SVG_NAMESPACE, "g").item(0), action.getMeroRectangles(), rect);
        }
    }
    else
    {
        System.out.println("Error loading SVG: " + strSVGName);
    }
   
    return null;
}

private Element getSVGActionElement(Document svgdoc, Element oldNode, Rectangle[] arDisplayRectangles, Rectangle rect )
{
            Rectangle svgRect = null;
            // calculate area
            if (rect != null)
            {
                svgRect = rect;
                System.out.println("Rect: " + svgRect.toString());
            }
 
            Element topG = svgdoc.createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "g");
            oldNode.getParentNode().replaceChild(topG, oldNode);
            topG.setAttributeNS(null, "fill", oldNode.getAttributeNS(null, "fill"));
            topG.setAttributeNS(null, "stroke", oldNode.getAttributeNS(null, "stroke"));
            oldNode.removeAttributeNS(null, "fill");
            oldNode.removeAttributeNS(null, "stroke");
            for (int i = 0; i < arDisplayRectangles.length; i++)
            {
                oldNode = (Element) oldNode.cloneNode(true);
                String strTransform = "";
                Element newG = svgdoc.createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "g");
                if (svgRect != null)
                {
                    strTransform = getSVGTransform(svgRect, arDisplayRectangles[i]);
                    oldNode.setAttributeNS(null, "transform", "translate(" + -svgRect.getX() + ", " + -svgRect.getY() + ")");    // from viewbox
                }
                
                if(!"".equals(strTransform))
                    newG.setAttributeNS(null, "transform", strTransform);   // how to use width, height?

                newG.appendChild(oldNode);
                topG.appendChild(newG);
            }

            return topG;
}
private String getSVGTransform(Rectangle svgRect, Rectangle bounds)
{
    System.out.println("getSVGTransform: " + svgRect.toString() + " - " + bounds.toString());
    String strTransform = "";
    if (bounds.getWidth() > 0)
    {
        // calculate scale
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        int nWidth = (int) svgRect.getWidth(); 
        int nHeight = (int) svgRect.getHeight();
        double dxScale = bounds.getWidth()/nWidth;
        double dyScale = bounds.getHeight()/nHeight;
        double dScale;
        double dyOffset = 0;
        double dxOffset = 0;
        if (dxScale < dyScale)
        {
            dScale = dxScale;
            // need to centre y
            double dNewHeight = svgRect.getHeight() * dScale;
            dyOffset = (bounds.getHeight() - dNewHeight)/2;
        }
        else
        {
            dScale = dyScale;
            // need to centre x
            double dNewWidth = svgRect.getWidth() * dScale;
            dxOffset = (bounds.getWidth() - dNewWidth)/2;
        }
        strTransform = ("translate(" + (int)(bounds.getX() + dxOffset) + ", " + (int)(bounds.getY() + dyOffset) + ")");   // how to use width, height?
        strTransform += (" scale(" + dScale + ")");   // how to use width, height?
    }
    System.out.println("Mero transform " + strTransform);        
    return strTransform;
}
private Element replaceColourReferences(Element element, ENEFillItem colour0, ArrayList<ENEFillItem> patternColours)
{
    String[] astrAttributes = {"fill", "stroke"};
    for(int i = 0; i < astrAttributes.length; i++)
    {
        element = replaceColourReferenceAttribute(element, astrAttributes[i], colour0, patternColours);
    }
    // replace all #colour references with appropriate colours
    NodeList nl1 = element.getChildNodes(); // ElementsByTagNameNS(SVG_NAMESPACE, "path");
    for(int j = 0; j < nl1.getLength(); j++)
    {
        Node n = nl1.item(j);
        if (n instanceof Element)
        {
            Element newNode1 = (Element) n;    // already cloned
            newNode1 = replaceColourReferences(newNode1, colour0, patternColours);
        }
    }
    
    return element;
}
private Element replaceColourReferenceAttribute(Element element, String strAttribute, ENEFillItem colour0, ArrayList<ENEFillItem> patternColours)
{
    String strValue = element.getAttributeNS(null, strAttribute);
    if ((strValue != null) && (!"".equals(strValue)))
    {
        String strConvertedValue = convertColourReference(strValue, colour0, patternColours);
        element.setAttributeNS(null, strAttribute, strConvertedValue);
    }
    
    return element;
}
private String convertColourReference(String strReference, ENEFillItem colour0, ArrayList<ENEFillItem> patternColours)
{
    String strColour = strReference;
    if (strReference.indexOf("url(#colour") == 0)
    {
        String strColourNr = strReference.substring(11, 12);
        try
        {
            int nColour = Integer.parseInt(strColourNr);
            if (nColour == 0)
            {
                String strColour0 = colour0.getText();
                if ((colour0 instanceof ENETartanItem || colour0 instanceof ENEFabricItem)
                    && (ENEColoursEnvironment.getInstance().isFabric(strColour0, "en")))
                {    
                    strColour0 = ENEColoursEnvironment.getInstance().getFabricItem(strColour0, "en").getResourceName();
                    return "url(#" + strColour0  + ")";
                }
                else
                {
                    strColour0 = ((ENEColourItem)colour0).getHexRGB();
                    return strColour0;
                }
            }
            else
            {
                ENEFillItem colourN = patternColours.get(nColour - 1);
                String strColourN = colourN.getText();
                if ((colourN instanceof ENETartanItem)
                    && (ENEColoursEnvironment.getInstance().isFabric(strColourN, "en")))
                {    
                    strColourN = ENEColoursEnvironment.getInstance().getFabricItem(strColourN, "en").getResourceName();
                    return "url(#" + strColourN  + ")";
                }
                else
                {
                    strColourN = ((ENEColourItem)colourN).getHexRGB();
                    return strColourN;
                }
            }
        }
        catch(NumberFormatException e)
        {
            // return original reference
        }
        catch(IndexOutOfBoundsException e)
        {
            // return original reference
        }
    }
    return strColour;
}
private  ArrayList<String> extractPatterns(Element element, ArrayList<String> alPatterns)
{
    String[] astrAttributes = {"fill", "stroke"};
    for (int i = 0; i < astrAttributes.length; i++)
    {
        String strPattern = extractAttributePattern(element, astrAttributes[i]);
        if ((strPattern != null) && !m_alReferences.contains(strPattern))
        {
            alPatterns.add(strPattern);
            m_alPatterns.add(strPattern);
        }
    }
    // ... and for each child element
    NodeList nl1 = element.getChildNodes(); // ElementsByTagNameNS(SVG_NAMESPACE, "path");
    for(int j = 0; j < nl1.getLength(); j++)
    {
        Node n = nl1.item(j);
        if (n instanceof Element)
        {
            Element newNode1 = (Element) n;    // already cloned
            alPatterns = extractPatterns(newNode1, alPatterns);
         }
    }
    
    return alPatterns;
}
private  ArrayList<String> extractReferences(Element element, ArrayList<String> alReferences)
{
    String[] astrAttributes = {"clip-path", "xlink:href"};
     
    for (int i = 0; i < astrAttributes.length; i++)
    {
        String strReference = extractAttributeReference(element, astrAttributes[i]);
        if ((strReference != null) && !m_alReferences.contains(strReference))
        {
            alReferences.add(strReference);
            m_alReferences.add(strReference);
        }
    }
    // ... and for each child element
    NodeList nl1 = element.getChildNodes(); // ElementsByTagNameNS(SVG_NAMESPACE, "path");
    for(int j = 0; j < nl1.getLength(); j++)
    {
        Node n = nl1.item(j);
        if (n instanceof Element)
        {
            Element newNode1 = (Element) n;    // already cloned
            alReferences = extractReferences(newNode1, alReferences);
         }
    }
    
    return alReferences;
}
private String extractAttributePattern(Element element, String strAttribute)
{
    String strValue = element.getAttributeNS(null, strAttribute);
    if ((strValue != null) && (!"".equals(strValue)))
    {
        if (strValue.indexOf("url(#") == 0)
        {
            //System.out.println("extractAttributePattern url: " + strAttribute + "-" + strValue);
            String strPattern = strValue.substring(5, strValue.length() - 1);
            // eliminate any colours
            if ((strPattern.indexOf("colour") != 0))
            {
                if ((m_strUniqueId != null) && (!ENEColoursEnvironment.getInstance().isFabric(strPattern, m_strLanguage))
                     && (!ENEColoursEnvironment.getInstance().isTartan(strPattern)))
                    element.setAttributeNS(null, strAttribute, "url(#" + strPattern + "_" + m_strUniqueId + ")");         // need to make unique
                return strPattern;
            }
        }
    }
    return null;
}
private String extractAttributeReference(Element element, String strAttribute)
{
    String strValue;
    if (strAttribute.indexOf("xlink:") == 0)
    {
        String strAttribute1 = strAttribute.substring(6);
        strValue = element.getAttributeNS(SVGFactoryUtils.XLINK_NAMESPACE, strAttribute1);
    }
    else
    {
        strValue = element.getAttributeNS(null, strAttribute);
    }
    if ((strValue != null) && (!"".equals(strValue)))
    {
        if (strValue.indexOf("url(#") == 0)
        {
            //System.out.println("extractAttributeReference url: " + strAttribute + "-" + strValue);
            return strValue.substring(5, strValue.length() - 1);
        }
        else if (strValue.indexOf("#") == 0)
        {
            //System.out.println("extractAttributeReference #: " + strAttribute + "-" + strValue);
            return strValue.substring(1, strValue.length());
        }
    }
    return null;
}
    public static String addJockeySilks(SVGGraphics2D svgGenerator, SVGGeneratorContext ctx, ENERacingColours colours, String strColours, String strLanguage, double dScale, String strSuffix, HashMap<String,Element> hmDefintions, String strBackgroundColour) 
    {
        // called when ading multiple Mero images to a document
        Element element = buildJockeySilks(ctx, colours, strColours, strLanguage, dScale, strSuffix, hmDefintions, strBackgroundColour);
        svgGenerator.getDOMTreeManager().addOtherDef(element);
        return element.getAttributeNS(null, "id");
    }
    public static Element buildJockeySilks(SVGGeneratorContext ctx, ENERacingColours colours, String strColours, String strLanguage, double dScale, String strSuffix, HashMap<String,Element> hmDefinitions, String strBackgroundColour) {
        // called when ading multiple Mero images to a document
        Document document = ctx.getDOMFactory();
        ENEMeroFactory factory = new ENEMeroFactory(colours, strLanguage, document, strSuffix, hmDefinitions); //use strSuffix to differentiate between multiple instances of the same SVG pattern
        document = factory.generateSVGDocument(strColours, dScale, strBackgroundColour);     // use strColours as id
        return document.getDocumentElement();
    }
 }
