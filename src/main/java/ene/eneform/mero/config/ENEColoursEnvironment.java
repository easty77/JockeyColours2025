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
import org.apache.batik.anim.dom.SVGOMTextElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGRect;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ENEColoursEnvironment implements Serializable, StandardPatternHandler {

    protected static ENEColoursEnvironment sm_coloursEnvironment = null;

    private double m_dTartanShrinkFactor = 4.0;   // to do: read from xml config file

    private Color m_backgroundColour = Color.white;

    private int m_nDebugLevel = 1;      // decreased from 2
        
    private static transient SAXParser sm_parser = null;
    
    public static String DEFAULT_LANGUAGE = "en";
    
    private int m_nMaxExpandIterations = 0;
    
    private ene.eneform.mero.config.ConfigMero m_configMero = null;
    private ene.eneform.mero.config.ConfigSvg m_configSvg = null;
    private ene.eneform.mero.config.ConfigAbbreviations m_configAbbreviations = null;
    private ene.eneform.mero.config.ConfigColours m_configColours = null;
    private ene.eneform.mero.config.ConfigExpands m_configExpands = null;
    private ene.eneform.mero.config.ConfigCompares m_configCompares = null;
    private ene.eneform.mero.config.ConfigOrganisations m_configOrganisations = null;
    private ene.eneform.mero.config.ConfigTartans m_configTartans = null;
    private ene.eneform.mero.config.ConfigFabrics m_configFabrics = null;
    private ene.eneform.mero.config.ConfigPatterns m_configPatterns = null;
    

    public static synchronized ENEColoursEnvironment getInstance()
    {
        if (sm_coloursEnvironment == null)
        {
            sm_coloursEnvironment = new ENEColoursEnvironment();
            sm_coloursEnvironment.initialise();
            //sm_coloursEnvironment = (ENEColoursEnvironment) MeroUtils.deserialize("d:/Users/Simon/Documents/horses/betwise/tmp/config.ser");
        }

        return sm_coloursEnvironment;
    }
    GVTBuilder m_builder = null;
    BridgeContext m_ctx = null;

    protected ENEColoursEnvironment()
    {
    }
    public ENEPatternAction createStandardAction(String strStdClassName, String strCurrentElement)
    {
        // only useful implementation is for AWT version
        return null;
    }
    public void initialise()
    {
        // this has to be public
        // as can't be called because need AWTColoursEnvironment to behave differently

        // load configuration files
        //System.out.println(System.getProperty("user.dir"));

        boolean bSuccess = createParsers();
        if (bSuccess)
        {
          // load references to the files
            m_configMero = new ConfigMero(sm_parser, "enecoloursconfig.xml");
 
            if (m_configMero.load())
            {
                m_nMaxExpandIterations = getIntegerVariable("MAX_NR_EXPAND_ITERATIONS");

                 // use file references from config
                //bSuccess = loadConfigurationFile(m_hmFiles.get("abbreviations"), new ENEAbbreviationsHandler());
                String strFileAbbreviations = m_configMero.getFileName("abbreviations");
                if (strFileAbbreviations.indexOf(".xml") > 0)
                {
                    m_configAbbreviations = new ene.eneform.mero.config.ConfigAbbreviations(sm_parser, strFileAbbreviations);
                    m_configAbbreviations.load();
                }
                else
                {
                    //bSuccess = MeroUtils.serialize(configAbbreviations, getVariable("TMP_OUTPUT_DIRECTORY") + "/abbreviations.ser");
                    //m_configAbbreviations = (ConfigAbbreviations) MeroUtils.deserialize(getVariable("TMP_OUTPUT_DIRECTORY") + "/abbreviations.ser");
                    m_configAbbreviations = (ConfigAbbreviations) MeroUtils.deserialize(strFileAbbreviations);
                }
                String strFileColours = m_configMero.getFileName("colours");
                if (strFileColours.indexOf(".xml") > 0)
                {
                    m_configColours = new ene.eneform.mero.config.ConfigColours(sm_parser, strFileColours);
                    m_configColours.load();
                }
                else
                {
                    m_configColours = (ConfigColours) MeroUtils.deserialize(strFileColours);
                }
                String strFileTartans = m_configMero.getFileName("tartans");
                if (strFileTartans.indexOf(".xml") > 0)
                {
                    m_configTartans = new ene.eneform.mero.config.ConfigTartans(sm_parser, strFileTartans);
                    m_configTartans.load();
                }
                else
                {
                    m_configTartans = (ConfigTartans) MeroUtils.deserialize(strFileTartans);
                }
                String strFileFabrics = m_configMero.getFileName("fabrics");
                if (strFileFabrics.indexOf(".xml") > 0)
                {
                    m_configFabrics = new ene.eneform.mero.config.ConfigFabrics(sm_parser, strFileFabrics);
                    m_configFabrics.load();
                }
                else
                {
                    m_configFabrics = (ConfigFabrics) MeroUtils.deserialize(strFileFabrics);
                }
                String strFilePatterns = m_configMero.getFileName("patterns");
                if (strFilePatterns.indexOf(".xml") > 0)
                {
                    m_configPatterns = new ene.eneform.mero.config.ConfigPatterns(sm_parser, strFilePatterns);
                    m_configPatterns.load(this);
                }
                else
                {
                    m_configPatterns = (ConfigPatterns) MeroUtils.deserialize(strFilePatterns);
                }

                m_configSvg = new ConfigSvg(getVariable("SVG_MERO_DIRECTORY"), getVariable("SVG_SHAPE_DIRECTORY"));
                m_configSvg.loadMeroSVG();
                // load all SVG files listed in config
                //m_configMeroSvg.loadSVG("rising sun");

                String strFileExpands = m_configMero.getFileName("expands");
                if (strFileExpands.indexOf(".xml") > 0)
                {
                    m_configExpands = new ene.eneform.mero.config.ConfigExpands(sm_parser, strFileExpands);
                    m_configExpands.load(m_configColours, m_configPatterns, m_configFabrics);
                }
                else
                {
                    m_configExpands = (ConfigExpands) MeroUtils.deserialize(strFileExpands);
                }
                String strFileCompares = m_configMero.getFileName("compares");
                if (strFileCompares.indexOf(".xml") > 0)
                {
                    m_configCompares = new ene.eneform.mero.config.ConfigCompares(sm_parser, strFileCompares);
                    m_configCompares.load(m_configColours, m_configPatterns, m_configFabrics);
                }
                else
                {
                    m_configCompares = (ConfigCompares) MeroUtils.deserialize(strFileCompares);
                }
                String strFileOrganisations = m_configMero.getFileName("organisations");
                if (strFileOrganisations.indexOf(".xml") > 0)
                {
                    m_configOrganisations = new ene.eneform.mero.config.ConfigOrganisations(sm_parser, strFileOrganisations);
                    m_configOrganisations.load(m_configColours);
                }
                else
                {
                    m_configOrganisations = (ConfigOrganisations) MeroUtils.deserialize(strFileOrganisations);
                }
             }
        }
    }
    
    private boolean createParsers()
    {
       SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	try {
	  sm_parser = parserFactory.newSAXParser();
	} catch ( ParserConfigurationException pce ) {
	    System.out.println("Error setting up the XML Parser. The parser is not properly configured. Loading aborted.");
            return false;
	} catch ( SAXException saxe ) {
	    System.out.println("Error setting up the XML Parser. Loading aborted.");
            return false;
	}


        return true;
    }
    public SVGDocument getSVGDocument(String strShape)
    {
        if (m_configSvg != null)
            return m_configSvg.getSVGDocument(strShape);

        return null;
    }
    public Area getSVGArea(String strShape)
    {
        ene.eneform.mero.config.SvgXML xml = new ene.eneform.mero.config.SvgXML(sm_parser, strShape, getVariable("SVG_SHAPE_DIRECTORY"));
        xml.load();
        return xml.getArea();
    }
    public Area getSVGArea(InputStream is)
    {
        ene.eneform.mero.config.SvgXML xml = new SvgXML(sm_parser, "", getVariable("SVG_SHAPE_DIRECTORY"));
        xml.load(is);
        return xml.getArea();
    }

public GraphicsNode getSVGGraphicsNode(String strShape) 
{
    SVGDocument doc =  m_configSvg.getSVGDocument(strShape);
    return createGraphicsNode(doc);
}

public String getSVGContent(String strShape) 
{
    return m_configSvg.getSVGContent(strShape);
}

public SVGDocument getSVGContentDocument(String strSVGContent) 
{
    return m_configSvg.getSVGContentDocument(strSVGContent);
}
public GraphicsNode getSVGContentGraphicsNode(String strSVGContent) 
{
    SVGDocument doc = m_configSvg.getSVGContentDocument(strSVGContent);
    return createGraphicsNode(doc);
}
    public String replaceAbbreviation(String strOriginal, String strLanguage)
    {
     if (m_configAbbreviations != null)
        return m_configAbbreviations.replaceAbbreviation(strOriginal, strLanguage);

    return null;
    }

    public String getFilename(String strFile)
    {
        return m_configMero.getFileName(strFile);
    }
    public int getMaxNrExpandIterations()
    {
        return m_nMaxExpandIterations;
    }
    public void reset()
    {
        sm_coloursEnvironment = null;
    }

    public Color getBackgroundColour()
    {
        return m_backgroundColour;
    }
    public void setBackgroundColour(Color colour)
    {
        m_backgroundColour = colour;
    }

    public double getTartanShrinkFactor()
    {
        return m_dTartanShrinkFactor;
    }
    private SAXParser getParser()
    {
        return sm_parser;
    }
    public synchronized void parse(InputStream is, DefaultHandler dh) throws SAXException, IOException
    {
        sm_parser.parse(new InputSource(is), dh);
    }
    public void trace(String strText)
    {
        if (m_nDebugLevel > 1)
            System.out.println(strText);
    }
    public void debug(String strText)
    {
        if (m_nDebugLevel > 0)
            System.out.println(strText);
    }

    public Set<String> getOrganisations()
    {
        return m_configOrganisations.getOrganisations();
    }
    public ENEOrganisation getOrganisation(String strOrganisation)
    {
        return m_configOrganisations.getOrganisation(strOrganisation);
    }
    
   public Set<String> getColours(String strLanguage)
    {
        return m_configColours.getColours(strLanguage);
    }
   public Set<String> getTartans()
    {
        return m_configTartans.getTartans();
    }
   public Set<String> getFabrics(String strLanguage)
    {
        return m_configFabrics.getFabrics(strLanguage);
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
            return m_configColours.getColourItem(strColour, strLanguage);
        }
    }
 
   public ENETartan getTartan(String strTartan)
    {
        return m_configTartans.getTartan(strTartan);
    }
   public boolean isTartan(String strTartan)
    {
         return m_configTartans.isTartan(strTartan);
    }
   public String getTartanSVG(String strTartan)
    {
         return m_configTartans.getTartanSVG(strTartan); 
    }
   public ArrayList<ENETartan> getTartanList()
    {
        return m_configTartans.getTartanList();
    }
   public boolean isFabric(String strFabric, String strLanguage)
    {
         return m_configFabrics.isFabric(strFabric, strLanguage);
    }
   public ENEFabricItem getFabricItem(String strFabric, String strLanguage)
    {
         return m_configFabrics.getFabricItem(strFabric, strLanguage);
    }
     public Iterator<ENEColourItem> getColourIterator(String strLanguage)
    {
        //return m_hmColours.values().iterator();
        return m_configColours.getColourIterator(strLanguage);
    }
    public boolean isPattern(String strPattern, String strLanguage)
    {
        return m_configPatterns.isPattern(strPattern, strLanguage);
    }
    public String convertSynonym(String strType, String strSynonym, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        return m_configPatterns.convertSynonym(strType, strSynonym, strLanguage);
    }
    public String getPatternListRegExAll(String strLanguage)
    {
        return m_configPatterns.getPatternListRegExAll(strLanguage);
    }
    public String getPatternListRegEx(String strType, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        return m_configPatterns.getPatternListRegEx(strType, strLanguage);
    }
    public String getPatternMapping(String strType, String strPattern, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        return m_configPatterns.getPatternMapping(strType, strPattern, strLanguage);
    }
    public ArrayList<ENEPattern> getPatternList(String strType, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        return m_configPatterns.getPatternList(strType, strLanguage);
    }
   public ArrayList<String> getPatternNameList(String strType, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        return m_configPatterns.getPatternNameList(strType, strLanguage);
    }

    public ENEPattern getDefaultPattern(String strType, String strPattern)
    {
        return getPattern(strType, strPattern, DEFAULT_LANGUAGE);
    } 
    public ENEPattern getPattern(String strType, String strPattern, String strLanguage)
    {
        return m_configPatterns.getPattern(strType, strPattern, strLanguage);
    }
    public ENEPatternAction getPatternAction(String strType, String strPattern, String strLanguage)
    {
       return m_configPatterns.getPatternAction(strType, strPattern, strLanguage);
    }
    public boolean isDerivePattern(String strType, String strPattern, String strLanguage)
    {
        return m_configPatterns.isDerivePattern(strType, strPattern, strLanguage);
     }
    public boolean isPrimaryPattern(String strType, String strPattern, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
       return m_configPatterns.isPrimaryPattern(strType, strPattern, strLanguage);
    }
    
    public String getColourListRegEx(String strLanguage)
    {
    	// problem with order of execution of static elements
        return m_configColours.getColourListRegEx(strLanguage);
        //return "blue|red|black";
    }


    public String getFabricListRegEx(String strLanguage)
    {
        return m_configFabrics.getFabricListRegEx(strLanguage);
    }

    public String getFullColourListRegEx(String strLanguage)
    {
    	// including fabrics
        // 20130222 put fabrics first (as longer)
        return getFabricListRegEx(strLanguage) + "|" + getColourListRegEx(strLanguage);
    }
    public ArrayList<ENEColoursParserExpand> getExpandList(String strLanguage)
    {
        return m_configExpands.getExpandList(strLanguage);
    }
    public ArrayList<ENEColoursParserCompareAction> getCompareList(String strType, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
       return m_configCompares.getCompareList(strType, strLanguage);
    }

    public String getVariable(String strId)
    {
        String strValue = m_configMero.getVariable(strId);
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
        String strValue = m_configMero.getVariable(strId);
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
    }
    public InputStream loadShapeSVG(String strShape)
    {
        return m_configSvg.getSVGStream(strShape);
    }
    
   public ENEColoursElement createJacket(String strLanguage) {
        return new ENEColoursElement(strLanguage, ENEColoursElement.JACKET);
    }

    public ENEColoursElement createJacket(String strLanguage, String strDefinition) {
        return new ENEColoursElement(strLanguage, ENEColoursElement.JACKET, strDefinition);
    }

    public ENEColoursElement createCap(String strLanguage) {
        return new ENEColoursElement(strLanguage, ENEColoursElement.CAP);
    }

    public ENEColoursElement createCap(String strLanguage, String strDefinition) {
        return new ENEColoursElement(strLanguage, ENEColoursElement.CAP, strDefinition);
    }

    public ENEColoursElement createSleeves(String strLanguage) {
        return new ENEColoursElement(strLanguage, ENEColoursElement.SLEEVES);
    }

    public ENEColoursElement createSleeves(String strLanguage, String strDefinition) {
        return new ENEColoursElement(strLanguage, ENEColoursElement.SLEEVES, strDefinition);
    }

   public ENERacingColours createRacingColours(String strLanguage, String strDescription, String strOwner)
    {
        return new ENERacingColours(strLanguage, strDescription, strOwner);
    }
    public ENERacingColours createRacingColours(String strLanguage, ENEColoursElement jacket, ENEColoursElement sleeves, ENEColoursElement cap)
    {
         return new ENERacingColours(strLanguage, jacket, sleeves, cap);
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
        if (m_ctx == null) {
            UserAgent userAgent;
            DocumentLoader loader;
            userAgent = new UserAgentAdapter();
            loader = new DocumentLoader(userAgent);
            m_ctx = new BridgeContext(userAgent, loader);
            m_ctx.setDynamicState(BridgeContext.DYNAMIC);
        }
        return m_ctx;
    }

    GVTBuilder getBuilder() {
        if (m_builder == null) {
            m_builder = new GVTBuilder();
        }
        return m_builder;
    }

    public Rectangle getGBBox(SVGDocument svgdoc, SVGGElement g)
    {
        ENEColoursEnvironment.getInstance().createGraphicsNode(svgdoc);
        SVGRect rect = g.getBBox();
        return MeroUtils.convertSVGRectangle(rect);
    }
    public Rectangle getTextBBox(SVGDocument svgdoc, SVGOMTextElement text)
    {
        ENEColoursEnvironment.getInstance().createGraphicsNode(svgdoc);
        SVGRect rect = text.getBBox();
        return MeroUtils.convertSVGRectangle(rect);
    }
}
