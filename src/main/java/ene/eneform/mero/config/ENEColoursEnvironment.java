package ene.eneform.mero.config;

import ene.eneform.mero.action.ENEPatternAction;
import ene.eneform.mero.colours.ENEColoursElement;
import ene.eneform.mero.colours.ENEPattern;
import ene.eneform.mero.colours.ENERacingColours;
import ene.eneform.mero.fabric.ENEFabricItem;
import ene.eneform.mero.parse.ENEColoursParserCompareAction;
import ene.eneform.mero.parse.ENEColoursParserExpand;
import ene.eneform.mero.tartan.ENETartan;
import ene.eneform.mero.utils.ENEColourItem;
import ene.eneform.mero.utils.MeroUtils;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import lombok.Getter;
import org.apache.batik.anim.dom.SVGOMTextElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGRect;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
@Component
@Getter
public class ENEColoursEnvironment implements Serializable {

    @Value("${ene.eneform.mero.DEFAULT_LANGUAGE}")
    public static String DEFAULT_LANGUAGE;
    @Value("${ene.eneform.mero.SVG_MERO_DIRECTORY}")
    private String SVG_MERO_DIRECTORY;
    @Value("${ene.eneform.mero.SVG_SHAPE_DIRECTORY}")
    private String SVG_SHAPE_DIRECTORY;

    private double dTartanShrinkFactor = 4.0;   // to do: read from xml config file

    private Color backgroundColour = Color.white;

    private SAXParser parser;
    private ConfigExpands configExpands;
    private ConfigPatterns configPatterns;
    private ConfigColours configColours;
    private ConfigFabrics configFabrics;
    private ConfigTartans configTartans;
    private ConfigCompares configCompares;
    private ConfigOrganisations configOrganisations;
    private ConfigSvg configSvg;
    private AbbreviationsHandler abbreviationsHandler;

    GVTBuilder builder = null;
    BridgeContext ctx = null;

    public ENEColoursEnvironment() {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        try {
            parser = parserFactory.newSAXParser();
            initialise();
        } catch ( Exception saxe ) {
            System.out.println("Error setting up the XML Parser. Loading aborted.");
        }
    }
private void initialise() {
    configPatterns = new ConfigPatterns(parser);
    configColours = new ConfigColours(parser);
    configFabrics = new ConfigFabrics(parser);
    configTartans = new ConfigTartans(parser);
    abbreviationsHandler = new AbbreviationsHandler(new ConfigAbbreviations(parser), configColours, configFabrics);
    configCompares = new ConfigCompares(parser);
    configExpands = new ConfigExpands(parser, configColours, configPatterns, configFabrics);
    configOrganisations = new ConfigOrganisations(parser);
    configSvg = new ConfigSvg();

}
    public SVGDocument getSVGDocument(String strShape)
    {
        if (configSvg != null)
            return configSvg.getSVGDocument(strShape);

        return null;
    }
    public Area getSVGArea(String strShape)
    {
        SvgXML xml = new SvgXML(parser, strShape, SVG_SHAPE_DIRECTORY);
        xml.load();
        return xml.getArea();
    }
    public Area getSVGArea(InputStream is)
    {
        SvgXML xml = new SvgXML(parser, "", SVG_SHAPE_DIRECTORY);
        xml.load(is);
        return xml.getArea();
    }

public GraphicsNode getSVGGraphicsNode(String strShape) 
{
    SVGDocument doc =  configSvg.getSVGDocument(strShape);
    return createGraphicsNode(doc);
}

public String getSVGContent(String strShape) 
{
    return configSvg.getSVGContent(strShape);
}

public SVGDocument getSVGContentDocument(String strSVGContent) 
{
    return configSvg.getSVGContentDocument(strSVGContent);
}
public GraphicsNode getSVGContentGraphicsNode(String strSVGContent) 
{
    SVGDocument doc = configSvg.getSVGContentDocument(strSVGContent);
    return createGraphicsNode(doc);
}

    public void reset()
    {
        initialise();
    }

    public Color getBackgroundColour()
    {
        return backgroundColour;
    }
    public void setBackgroundColour(Color colour)
    {
        backgroundColour = colour;
    }

    public double getTartanShrinkFactor()
    {
        return dTartanShrinkFactor;
    }
    private SAXParser getParser()
    {
        return parser;
    }
    public synchronized void parse(InputStream is, DefaultHandler dh) throws SAXException, IOException
    {
        parser.parse(new InputSource(is), dh);
    }

    public Set<String> getOrganisations()
    {
        return configOrganisations.getOrganisations();
    }
    public ENEOrganisation getOrganisation(String strOrganisation)
    {
        return configOrganisations.getOrganisation(strOrganisation);
    }
    
   public Set<String> getColours(String strLanguage)
    {
        return configColours.getColours(strLanguage);
    }
   public Set<String> getTartans()
    {
        return configTartans.getTartans();
    }
   public Set<String> getFabrics(String strLanguage)
    {
        return configFabrics.getFabrics(strLanguage);
    }
    public ENEColourItem getColourItem(String strColour, String strLanguage)
    {
        if (strColour.length() == 0)
            return null;
        else if (strColour.charAt(0) == '#')
        {
            return new ENEColourItem(strColour, MeroUtils.createColor(strColour, 255), "");
        }
        else
        {
            return configColours.getColourItem(strColour, strLanguage);
        }
    }
 
   public ENETartan getTartan(String strTartan)
    {
        return configTartans.getTartan(strTartan);
    }
   public boolean isTartan(String strTartan)
    {
         return configTartans.isTartan(strTartan);
    }
   public String getTartanSVG(String strTartan)
    {
         return configTartans.getTartanSVG(strTartan); 
    }
   public ArrayList<ENETartan> getTartanList()
    {
        return configTartans.getTartanList();
    }
   public boolean isFabric(String strFabric, String strLanguage)
    {
         return configFabrics.isFabric(strFabric, strLanguage);
    }
   public ENEFabricItem getFabricItem(String strFabric, String strLanguage)
    {
         return configFabrics.getFabricItem(strFabric, strLanguage);
    }
     public Iterator<ENEColourItem> getColourIterator(String strLanguage)
    {
        //return hmColours.values().iterator();
        return configColours.getColourIterator(strLanguage);
    }
    public boolean isPattern(String strPattern, String strLanguage)
    {
        return configPatterns.isPattern(strPattern, strLanguage);
    }
    public String convertSynonym(String strType, String strSynonym, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        return configPatterns.convertSynonym(strType, strSynonym, strLanguage);
    }
    public String getPatternListRegExAll(String strLanguage)
    {
        return configPatterns.getPatternListRegExAll(strLanguage);
    }
    public String getPatternListRegEx(String strType, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        return configPatterns.getPatternListRegEx(strType, strLanguage);
    }
    public String getPatternMapping(String strType, String strPattern, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        return configPatterns.getPatternMapping(strType, strPattern, strLanguage);
    }
    public ArrayList<ENEPattern> getPatternList(String strType, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        return configPatterns.getPatternList(strType, strLanguage);
    }
   public ArrayList<String> getPatternNameList(String strType, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        return configPatterns.getPatternNameList(strType, strLanguage);
    }

    public ENEPattern getDefaultPattern(String strType, String strPattern)
    {
        return getPattern(strType, strPattern, DEFAULT_LANGUAGE);
    } 
    public ENEPattern getPattern(String strType, String strPattern, String strLanguage)
    {
        return configPatterns.getPattern(strType, strPattern, strLanguage);
    }
    public ENEPatternAction getPatternAction(String strType, String strPattern, String strLanguage)
    {
       return configPatterns.getPatternAction(strType, strPattern, strLanguage);
    }
    public boolean isDerivePattern(String strType, String strPattern, String strLanguage)
    {
        return configPatterns.isDerivePattern(strType, strPattern, strLanguage);
     }
    public boolean isPrimaryPattern(String strType, String strPattern, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
       return configPatterns.isPrimaryPattern(strType, strPattern, strLanguage);
    }
    
     public ArrayList<ENEColoursParserCompareAction> getCompareList(String strType, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
       return configCompares.getCompareList(strType, strLanguage);
    }
/*
    public String getVariable(String strId)
    {
        String strValue = configMero.getVariable(strId);
        if (strValue == null)
        {
             System.out.println("Config Variable not found: " + strId);
             strValue = "";
        }

        return strValue;
    }
    public int getIntegerVariable(String strId)
    {
        int nValue = -1;
        String strValue = configMero.getVariable(strId);
        if (strValue == null)
        {
             System.out.println("Config Integer Variable not found: " + strId);
             return nValue;
        }
        try
        {
            nValue = Integer.parseInt(strValue);
        }
        catch(NumberFormatException e)
        {
            
        }
        return nValue;
    } */
    public InputStream loadShapeSVG(String strShape)
    {
        return configSvg.getSVGStream(strShape);
    }
    

public AffineTransform transformGraphicsNode(Rectangle2D bounds, Rectangle rectangle)
{
   System.out.println("GraphicsNode SVG: " + bounds.getX() + "+" + bounds.getWidth() + " - " + bounds.getY() + "+" + bounds.getHeight());
   double dxScale = rectangle.getWidth()/bounds.getWidth();
   double dyScale = rectangle.getHeight()/bounds.getHeight();
   System.out.println("GraphicsNode Rectangle: " + rectangle.getX() + "+" + rectangle.getWidth() + " - " + rectangle.getY() + "+" + rectangle.getHeight());
   
   // Always preserve dimensions, so take smaller scale factor
   double dScale;
   double dxOffset = 0;
   double dyOffset = 0;
   if (dxScale < dyScale)
   {
       dScale = dxScale;
       // need to centre y
       double dNewHeight = bounds.getHeight() * dScale;
       dyOffset = (rectangle.getHeight() - dNewHeight)/2;
   }
   else
   {
       dScale = dyScale;
       // need to centre x
       double dNewWidth = bounds.getWidth() * dScale;
       dxOffset = (rectangle.getWidth() - dNewWidth)/2;
   }
    double dX = (-bounds.getX() + dxOffset); 
    double dY = (-bounds.getY() + dyOffset);   
    System.out.println("GraphicsNode Transform x: " + dX + " svg: " + bounds.getX() + " centre: " + dxOffset);
    System.out.println("GraphicsNode Transform y: " + dY + " svg: " + bounds.getY() + " centre: " + dyOffset);
    System.out.println("GraphicsNode Scale: " + dScale + " x: " + dxScale + "   y: " + dyScale);
    // operations are performed in reverse order to which added
    // so will scale first and then translate
    AffineTransform transform = new AffineTransform();
    transform.translate(dX, dY);
    transform.scale(dScale, dScale);
    
    return transform;
}

    public GraphicsNode createGraphicsNode(SVGDocument svgDoc) {
        // aka bootSVGDocument
        GraphicsNode rootGN = null;
        
        try
        {
            rootGN = getBuilder().build(getContext(), svgDoc);
        }
        catch(Exception e)
        {
            System.out.println("createGraphicsNode: " + e.getMessage());
        }
        return rootGN;
    }

    BridgeContext getContext() {
        if (ctx == null) {
            UserAgent userAgent;
            DocumentLoader loader;
            userAgent = new UserAgentAdapter();
            loader = new DocumentLoader(userAgent);
            ctx = new BridgeContext(userAgent, loader);
            ctx.setDynamicState(BridgeContext.DYNAMIC);
        }
        return ctx;
    }

    GVTBuilder getBuilder() {
        if (builder == null) {
            builder = new GVTBuilder();
        }
        return builder;
    }

    public Rectangle getGBBox(SVGDocument svgdoc, SVGGElement g)
    {
        createGraphicsNode(svgdoc);
        SVGRect rect = g.getBBox();
        return MeroUtils.convertSVGRectangle(rect);
    }
    public Rectangle getTextBBox(SVGDocument svgdoc, SVGOMTextElement text)
    {
        createGraphicsNode(svgdoc);
        SVGRect rect = text.getBBox();
        return MeroUtils.convertSVGRectangle(rect);
    }
    // moved from ENEColours
    public Color convertColour(String strColour, String strLanguage)
    {
        ENEColourItem item = getColourItem(strColour, strLanguage);

        if (item == null)
            return null;
        else
            return item.getColour();
    }

    public Set<String> getColourNames(String strLanguage)
    {
        return getColours(strLanguage);
    }
    public ArrayList<String> getOrganisationColourNames(String strOrganisation)
    {
        ENEOrganisation organisation = getOrganisation(strOrganisation);
        ENEOrganisationList list = organisation.getList("colours");
        return list.getList();
    }

    public boolean isColour(String strColour, String strLanguage)
    {
        return ((getColourItem(strColour, strLanguage) != null) || false); // ENETartan.isTartan(strColour));
    }

}
