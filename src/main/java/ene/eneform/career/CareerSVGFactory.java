/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.career;

import ene.eneform.colours.bos.AdditionalRaceData;
import ene.eneform.colours.bos.ENEColoursDBEnvironment;
import ene.eneform.colours.database.*;
import ene.eneform.colours.web.rp.RacingPostCourse;
import ene.eneform.mero.colours.ENERacingColours;
import ene.eneform.mero.config.ENEColoursEnvironment;
import ene.eneform.mero.factory.SVGFactoryUtils;
import ene.eneform.mero.service.MeroService;
import ene.eneform.smartform.bos.*;
import ene.eneform.smartform.factory.SmartformHorseFactory;
import ene.eneform.utils.*;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGSVGElement;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;


/**
 *
 * @author Simon
 */
public class CareerSVGFactory {
    private MeroService meroService;
    
    @Value("${ene.eneform.mero.SVG_OUTPUT_DIRECTORY}")
    private static String SVG_OUTPUT_DIRECTORY;
    @Value("${ene.eneform.mero.SVG_IMAGE_PATH}")
    private static String SVG_IMAGE_PATH;

    private static boolean sm_b3RowHeader = false;  // revamped 2 row header is superior!
    private static boolean sm_bQuestionMark = false;     // display missing runners with question marks
    private static boolean sm_bDisplayViewbox = false;
    private static final Color sm_colColumnHeaders = Color.BLUE;
    private static final Color sm_colRowHeaders = Color.BLACK;
    private static final Color sm_colAlternateLines = null;             // ColourUtils.getRGBColour("#FFFDD0")
    private static final Color sm_colColumnHeaderSeparators = null;     // Color.BLUE
    private static final Color sm_colColumnHeaderBackground = ColourUtils.getRGBColour("#CCFFFF");  // null;     //
    
    public static String OUTPUT_FONT = Font.SANS_SERIF;
    public static String OUTPUT_FONT_NAME="Arial";  // previously Serif
    public static String HEADING_FONT = Font.SERIF;
    public static String HEADING_FONT_NAME="Serif";  // previously Serif
    
    // Headings
    public static final int SVG_MERO_SEASON_OFFSET = 150;
    public static final int SVG_MERO_MONTHNAME_OFFSET = 25;     // replace with -30?
    public static final int SVG_ROW_HEADER_ORIGIN_X = 50;
    public static final int SVG_COLUMN_HEADER_ORIGIN_Y = 40;

    // Width of 1-2-3 race
    public static final int SVG_MERO_123_WIDTH = 210;
    public static final int SVG_MERO_123_WIDTH_EXTRA = 30;
    public static final int SVG_MERO_123_HEIGHT = 345;
   
    // Height of 1-2-3 race
    public static final int SVG_MERO_123_2ROW_HEADER = 80;   // race header
    public static final int SVG_MERO_123_3ROW_HEADER = 110;   // race header (increase to 3 lines)
    public static final int SVG_MERO_MEETING_HEADER = 50;
    public static final int SVG_MERO_123_FOOTER = 35;   // horse name
    public static final int SVG_MERO_123_OLD_FOOTER = 30;   // horse name

    // Implicit offset when drawing MERO silks
    public static final int SVG_MERO_X_OFFSET = 17;   
    public static final int SVG_MERO_Y_2ROW_OFFSET = 120;    // 20150723 reduce to 80 from 120
    public static final int SVG_MERO_Y_3ROW_OFFSET = 90;   
    public static final int SVG_MERO_Y_MEETING_OFFSET = 150;


    public static final int SVG_MERO_123_MEETING_ORIGIN_X = 40;   //  to align Meeting definition correctly (only used in drawColumnHeader - strange??)
    public static final int SVG_MERO_123_ORIGIN_X = 175;   // used for non-Meeting definitions
    
    
    public static final int SVG_MERO_123_ORIGIN_Y = 150;    // how far Mero draws from the y-axis
    public static final int MERO_TABLE_OFFSET = 100;
    public static final int SEASON_ROW_TITLE_WIDTH = 150;   // TO DO: what is the correct value? 

    private static int [] sm_aBackgroundColours = {0xD3D3D3, 0x22FE3C, 0xF8F45D, 0xFEB511, 0xA2CAE3};   // grey, green, yellow, orange, light blue
    private static String [] sm_astrBackgroundColours = {"#D3D3D3", "#22FE3C", "#F8F45D", "#FEB511", "#A2CAE3"};   // grey, green, yellow, orange, light blue

    private ENEStatement m_statement;
    private String m_strId;
    private String m_strHorse;
    CareerDefinition m_career;
    private ArrayList<String> m_alDescriptions = new ArrayList<String>();
    // horse career only
    private SmartformHorse m_horse = null;

    private double m_dxTranslate = 0;    // keep track of translations
    private double m_dyTranslate = 0;
    private double m_dyRowTranslate = 0;    // translation within current row
    
    private boolean m_bShowPosition;
    private int m_nCellRunners;
    private int m_nCellWidthx;       // width of a race cell
    private int m_nCellHeight;      // height of a race cell

    private Rectangle m_viewBox = null;
    private boolean m_bRowTitles;
    private boolean m_bColumnTitles;
    private int m_nRowTitleWidth = 0;
    private int m_nViewBoxXoffset = 0;
    
    private int m_nHeaderHeight = sm_b3RowHeader ? SVG_MERO_123_3ROW_HEADER : SVG_MERO_123_2ROW_HEADER;        // either 2 row or 3 row header
    private int m_nFullHeight = SVG_MERO_123_HEIGHT + SVG_MERO_123_FOOTER + m_nHeaderHeight;
    private int m_nYMeroOffset = sm_b3RowHeader ? SVG_MERO_Y_3ROW_OFFSET : SVG_MERO_Y_2ROW_OFFSET;
    
    
    private String m_strSVGContent = "";

    private int m_nReferenceCount = 0;
    
    private boolean m_bText = true;
    
    private String m_strLanguage = ENEColoursEnvironment.DEFAULT_LANGUAGE;
    private Document m_document = null;
    private Element m_meroDefs = null;
    private SVGGraphics2D m_svgGenerator = null;
    private SVGGeneratorContext m_ctx = null;

    private HashMap<String,Element> m_hmDefinitions = new HashMap<String,Element>();    // definitions shared by multiple SVGs e.g. shadows 
    
    private String m_strFilter = "";
    private int m_nFilterCount = 0;
    
    private Color m_colColumnHeaderBackground = null;
    // for generating HTML filters
    private CountedSet m_setSeason = new CountedSet(new TreeSet<String>());
    private CountedSet m_setRaceType = new CountedSet(new TreeSet<String>());
    private CountedSet m_setCourse = new CountedSet(new TreeSet<String>());
    private CountedSet m_setCountry = new CountedSet(new TreeSet<String>());
    private CountedSet m_setGoing = new CountedSet(new TreeSet<String>(new RunnerAttributeComparator(sm_astrGoing)));
    private CountedSet m_setDistance = new CountedSet(new TreeSet<String>(new RunnerAttributeComparator(sm_astrDistance)));
    private CountedSet m_setGroupRace = new CountedSet(new TreeSet<String>(new RunnerAttributeComparator(sm_astrGroupRace)));
    private CountedSet m_setDirection = new CountedSet(new TreeSet<String>());
    private CountedSet m_setDaysSinceRan = new CountedSet(new TreeSet<String>(new RunnerAttributeComparator(sm_astrDaysSinceRan)));
    private CountedSet m_setDistanceTravelled = new CountedSet(new TreeSet<String>(new RunnerAttributeComparator(sm_astrDistanceTravelled)));
    private CountedSet m_setFinishPosition = new CountedSet(new TreeSet<String>(new RunnerAttributeComparator(sm_astrFinishPosition)));
    private CountedSet m_setNrRunners = new CountedSet(new TreeSet<String>(new RunnerAttributeComparator(sm_astrNrRunners)));
    private CountedSet m_setPositionInBetting = new CountedSet(new TreeSet<String>(new RunnerAttributeComparator(sm_astrPositionInBetting)));
    private CountedSet m_setStartingPrice = new CountedSet(new TreeSet<String>(new RunnerAttributeComparator(sm_astrStartingPrice)));
    private CountedSet m_setDistanceWon = new CountedSet(new TreeSet<String>(new RunnerAttributeComparator(sm_astrDistanceWon)));
    private CountedSet m_setTack = new CountedSet(new TreeSet<String>());    // alphabetical order
    private CountedSet m_setJockey = new CountedSet(new TreeSet<String>());    // alphabetical order
    private CountedSet m_setTrainer = new CountedSet(new TreeSet<String>());    // alphabetical order
    private CountedSet m_setOwner = new CountedSet(new TreeSet<String>());    // alphabetical order
    private CountedSet m_setAgeRange = new CountedSet(new TreeSet<String>());    // alphabetical order
    
    private static String [] sm_astrGoing = {"Hard", "Firm", "Good/Firm", "Good", "Good/Yielding", "Good/Soft", "Yielding", "Yielding/Soft", "Soft", "Very Soft", "Soft/Heavy", "Heavy", "Fast", "Standard/Fast", "Standard", "Standard/Slow", "Slow"};
    private static String [] sm_astrDistanceTravelled = {"LT 30 miles", "31 - 50 miles", "51 - 100 miles", "101 - 150 miles", "151 - 200 miles", "GT 200 miles"};
    private static String [] sm_astrDaysSinceRan = {"LT 8 days", "8-14 days", "15-30 days", "31-50 days", "51-80 days", "81-150 days", "GT 150 days"};
    private static String [] sm_astrGroupRace = {"Group One", "Group Two", "Group Three", "Listed", "Ungraded"};
    private static String [] sm_astrDistance = {"5 Furlongs", "6 Furlongs", "7 Furlongs", "Mile", "9 Furlongs", "1&frac14; miles", "11 Furlongs", "1&frac12; miles", "1&frac34; miles", "2 miles", "2&frac14; miles", "2&frac12; miles", "2&frac34; miles", "3 miles", "3&frac14; miles", "3&frac12; miles", "3&frac34; miles", "4 miles+"};
    private static String [] sm_astrFinishPosition = {"Winner", "Placed", "Unplaced", "DNF"};
    private static String [] sm_astrNrRunners = {"1-5", "6-9", "10-15", "16-22", "23+"};
    private static String [] sm_astrPositionInBetting = {"Favourite", "2nd Fav", "3rd Fav", "Mid-range", "Outsider"};
    private static String [] sm_astrStartingPrice = {"Odds-On", "2/1-", "3/1-", "6/1-", "10/1-", "16/1-", "20/1+"};
    private static String [] sm_astrDistanceWon = {"Photo-finish", "&frac12; length", "1 length", "2 lengths", "3-5 lengths", "GT 5 lengths"};

    private String m_strLegend = "";
    private String m_strProfile = "";
    private static final String sm_strLegendGroup = "<span style=\"font-size:16px; background-color:#22FE3C;\">&nbsp;Group 1&nbsp;</span>&nbsp;<span style=\"font-size:16px; background-color:#F8F45D;\">&nbsp;Group 2&nbsp;</span>&nbsp;<span style=\"font-size:16px; background-color:#FEB511;\">&nbsp;Group 3&nbsp;</span>&nbsp;<span style=\"font-size:16px; background-color:#A2CAE3;\">&nbsp;Listed&nbsp;</span>&nbsp;<span style=\"font-size:16px; background-color:#D3D3D3;\">&nbsp;Other&nbsp;</span>";
    private static final String sm_strLegendFlat = "<span style=\"font-size:16px; background-color:#D3D3D3; color:blue;\">&nbsp;Flat&nbsp;</span>";
    private static final String sm_strLegendAWFlat = "<span style=\"font-size:16px; background-color:#D3D3D3; color:#5D3360;\">All Weather Flat&nbsp;</span>";
    private static final String sm_strLegendChase = "<span style=\"font-size:16px; background-color:#D3D3D3; color:black;\">&nbsp;Chase&nbsp;</span>";
    private static final String sm_strLegendHurdle = "<span style=\"font-size:16px; background-color:#D3D3D3; color:#603311;\">Hurdle&nbsp;</span>";
    private static final String sm_strLegendNHFlat = "<span style=\"font-size:16px; background-color:#D3D3D3; color:green;\">NH Flat</span>";
    private static final String sm_strLegendSingleSeparator = "&nbsp;";
    private static final String sm_strLegendMultiSeparator = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
    private String[] [] sm_astrOwnerNames = {
    
    };
    private String[] [] sm_astrCourseNames = {
        {"Royal Ascot", "Ascot"},
        {"Epsom Downs", "Epsom"}
    };
       
    public CareerSVGFactory(MeroService meroService, ENEStatement statement, CareerDefinition career, boolean bText)
    {
        this.meroService = meroService;
        m_statement = statement;
        m_career = career;
        m_strId = career.getId();
        m_strHorse = career.getName();

        m_bText = bText;
        
        career.expand(statement);
        if (m_career.isMeeting())
        {
            // need to expand
            
            m_nHeaderHeight = SVG_MERO_MEETING_HEADER;   // meeting header has one row
            // force recalculation
            m_nFullHeight = SVG_MERO_123_HEIGHT + SVG_MERO_123_FOOTER + m_nHeaderHeight;
             m_nYMeroOffset = SVG_MERO_Y_MEETING_OFFSET;
        }
        // format configuration
        String strFormat = m_career.getFormat();
        if ("".equals(strFormat))
            strFormat = "1-2-3";
        m_nCellRunners = strFormat.split("-").length;
        m_nCellHeight = SVG_MERO_123_HEIGHT + ("1".equals(strFormat) ? 0 : m_nHeaderHeight) + SVG_MERO_123_FOOTER;      // to do: variable for race header on or off

        m_bShowPosition = m_career.getShowPosition();
        m_bRowTitles = m_career.hasRowTitles();
        m_bColumnTitles = m_career.hasColumnTitles();
    }
    

            
public void generateCareer(boolean bCompress) throws IOException
{
    // SVG created for all races in a horse's career
    generateCareerSVG(bCompress);
    if (m_bText)
        generateCareerHTML();
}
public void generateCareerSVG(boolean bCompress) throws IOException
{
    // SVG created for all races in a horse's career
    String strDirectory="horses";
    if (m_career.isMeeting())
        strDirectory = "meetings";
    String strFullDirectory = SVG_OUTPUT_DIRECTORY + SVG_IMAGE_PATH + strDirectory;
    if (!m_bText)
        strFullDirectory += "/notext";

    String strSVG = generateCareerString();
    
    strSVG = SVGFactoryUtils.processSVGString(strSVG, bCompress);
    FileUtils.writeFile(strFullDirectory + "/" + m_strId + ".svg", strSVG, StandardCharsets.UTF_8, true);   // overwrite
}
private String createOption(String strOption, int nCount)
{
    String strValue = convertOptionValue(strOption);
    String strName = strOption;
    if (nCount > 0)
        strName += (" (" + nCount + ")");
    return "<option value=\"" + strValue + "\">" + strName + "</option>"; 
}
private String convertOptionValue(String strOption)
{
    return strOption.replace("&frac", "FRAC").replace("+", "PLUS").replace(";", "");
}

public String generateCareerString() throws IOException
{
    // generates row of colours for the runners - no text
    DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();

    // Create an instance of org.w3c.dom.Document.
    m_document = domImpl.createDocument(SVGFactoryUtils.SVG_NAMESPACE, "svg", null);

    Element title = m_document.createElementNS(null, "title");
    title.setTextContent(m_strHorse);
    m_document.getDocumentElement().appendChild(title);

    // no images
    m_ctx = SVGGeneratorContext.createDefault(m_document);
    m_svgGenerator = new SVGGraphics2D(m_ctx, false);

    // Available fonts
/*    String[] astrFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    for(int i = 0; i < astrFonts.length; i++)
    {
        System.out.println(i + ": " + astrFonts[i]);
    } */

    Element tlg = m_svgGenerator.getTopLevelGroup();
    tlg.setAttributeNS(null, "id", "career");   // top-level g
    
    if (m_bText)
    {
        Element script = m_document.createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "script");
        script.setAttributeNS(SVGFactoryUtils.XLINK_NAMESPACE, "href", "/js/career_utils.js");
        //Node scriptCode = m_document.createCDATASection("function ShowTooltip(evt, id, mask) { var colours1=document.getElementById(id); colours1.setAttributeNS(null, 'mask', 'url(#mask' + mask + ')'); }\nfunction HideTooltip(evt, id) { var colours1=document.getElementById(id);	colours1.removeAttributeNS(null, 'mask');}");
        //script.appendChild(scriptCode);    
        tlg.appendChild(script);

    }
    m_meroDefs = m_document.createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "defs");
    m_meroDefs.setAttributeNS(null, "id", "meroDefs");
    tlg.appendChild(m_meroDefs);
    
    m_svgGenerator.setTopLevelGroup(tlg);

    int nTotalRows = m_career.getTotalRows(m_nFullHeight);
    m_viewBox = createViewBox(nTotalRows);   // add 10 to be sure that all is displayed (but sometimes (Found.html) seems to need 50!

    if (m_career.getNrTables() > 1)
        m_colColumnHeaderBackground = sm_colColumnHeaderBackground;
    Iterator<CareerDefinition.CareerTable> iter = m_career.getTableIterator();
    int nMaxColumns = 0;
    int nTable = 0;
    while(iter.hasNext())
    {
        CareerDefinition.CareerTable table = iter.next();
        nTable++;

        if (nTable > 1)
        {
             careerTranslate(0, MERO_TABLE_OFFSET);
        }

        int nTableOffset = (int) m_dyTranslate;
        // create g representing a table
        tlg = m_svgGenerator.getTopLevelGroup();
        Element g = m_svgGenerator.getDOMFactory().createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "g");
        g.setAttributeNS(null, "transform", "translate(0," + nTableOffset + ")");   
        g.setAttributeNS(null, "id", String.valueOf(nTable));
        g.setAttributeNS(null, "class", "table");
        tlg.appendChild(g);
        m_svgGenerator.setTopLevelGroup(g);
        
        int nTotalColumns = table.getNrColumns();
        if (nTotalColumns > nMaxColumns)
            nMaxColumns = nTotalColumns;
        
        // draw grid lines
        //drawGridLines(m_svgGenerator, table, nViewBoxWidth, nXOrigin);
        
        if (m_bColumnTitles)
        {
            int nHeaderHeight = drawRowHeader(table, nTableOffset);
          //  if(nTable > 1)
          //      careerTranslate(0, nHeaderHeight);
        }

        // to do: build SmartformHorse object from data stored in Career defintion
        if (!m_career.isMeeting())
        {
            //m_horse = ((HorseCareerDefinition)m_career).getHorse();
             m_horse = SmartformHorseFactory.findCareerHorse(m_statement, m_career.getName(), null);
            if (m_horse == null)
            {
                m_horse = SmartformHorseFactory.findRunnerLatest(m_statement, m_career.getName());
            }
        }

        int nRows = 0;
        int nSeasons = table.getNrRows();
        Iterator<CareerDefinition.CareerRow> rowIter = table.getRowIterator();
        int nPixelsRowHeight = 0;
        CareerDefinition.CareerRow previousRow = null;
        CareerDefinition.CareerRow row = null;
        while(rowIter.hasNext())
        {
            //int nCellWidth = SVG_MERO_123_WIDTH * column.getMeroWidth() + SVG_MERO_123_WIDTH_EXTRA;
            previousRow = row;
            row = rowIter.next();
            nRows++;
            int nRowHeight = row.getHeight();
            if ((nSeasons > 3) && (nRowHeight == 2) && (nPixelsRowHeight == m_nFullHeight * nRowHeight) && (previousRow != null && !CareerDefinition.cellClash(previousRow, row)))
            {
                // two consecutive double height rows
                careerTranslate(0, -m_nFullHeight/2);    // overlap
            }
            nPixelsRowHeight = m_nFullHeight * nRowHeight;
            
            int nTotalRowWidth = drawRowSeason(table, row, nTableOffset, nTotalRows, nRows, m_viewBox.width, nPixelsRowHeight);
            
            // end of a row
            careerTranslate(-nTotalRowWidth, nPixelsRowHeight);
        }

        // adjust viewbox for row titles
        m_viewBox.width += m_nRowTitleWidth;
        m_viewBox.x -= m_nRowTitleWidth;

        if (false && m_bColumnTitles)
        {
            // Month headings at bottom too
                Color saveColour = m_svgGenerator.getColor();
                m_svgGenerator.setColor(ColourUtils.getRGBColour("#CCFFFF"));
                m_svgGenerator.fillRect(SVG_ROW_HEADER_ORIGIN_X, 0, m_viewBox.width, SVG_MERO_123_ORIGIN_Y-SVG_COLUMN_HEADER_ORIGIN_Y);
                m_svgGenerator.setColor(saveColour);
                Iterator<CareerDefinition.CareerColumn> iterColumns = table.getColumnIterator();
                while(iterColumns.hasNext())
                {
                    CareerDefinition.CareerColumn column = iterColumns.next();
                    drawColumnHeader(column, false);
                } 
        }
        m_svgGenerator.setTopLevelGroup(tlg);
        // end of a table
    }
    

    careerTranslate(-m_dxTranslate, -m_dyTranslate);
 
    /* Draw vertical scale
    Font saveFont = m_svgGenerator.getFont();
    m_svgGenerator.setFont(new Font(OUTPUT_FONT, Font.PLAIN, 12));
    for(int i = -200; i < nViewBoxWidth; i+=100)
    {
        m_svgGenerator.drawLine(-95, i, -105, i);
        m_svgGenerator.drawString(String.valueOf(i), -90, i);
    }
    m_svgGenerator.setFont(saveFont);  */

    Iterator<String> defsIter = m_hmDefinitions.keySet().iterator();
    while(defsIter.hasNext())
    {
        String strDef = defsIter.next();
        m_meroDefs.appendChild(m_hmDefinitions.get(strDef));
    }
 
    System.out.println("Mero Origin: " +SVG_MERO_123_ORIGIN_X + "," + SVG_MERO_123_ORIGIN_Y);
    System.out.println("Translate: " +m_dxTranslate + "," + m_dyTranslate);
    System.out.println("Rows: " +nTotalRows/2 + ", Columns: " + nMaxColumns);
    System.out.println("Viewbox: " + m_viewBox.x + ", " + m_viewBox.y + ", " + m_viewBox.width + ", " + m_viewBox.height);

    if (sm_bDisplayViewbox)
    {
        Color saveColor = m_svgGenerator.getColor();
        m_svgGenerator.setColor(Color.GREEN);  
        m_svgGenerator.drawRect(m_viewBox.x, m_viewBox.y, m_viewBox.width, m_viewBox.height);   // add 10 for increase in FOOTER height
        m_svgGenerator.setColor(saveColor); 
    }

    // used for mouseover of Silks
    Element mask1 = SVGFactoryUtils.createMaskElement(m_svgGenerator.getDOMFactory(), "mask1", 0, 0, m_viewBox.width, m_viewBox.height);
    Element rect1 = SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), "mask1r", 0, 0, m_viewBox.width, m_viewBox.height, "#333333", false);
    mask1.appendChild(rect1);
    m_meroDefs.appendChild(mask1);
    
    // Used for blanking out races that don't match selection criteria
    Element mask2 = SVGFactoryUtils.createMaskElement(m_svgGenerator.getDOMFactory(), "mask2", 0, 0, m_viewBox.width, m_viewBox.height);
    Element rect2 = SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), "mask2r", 0, 0, m_viewBox.width, m_viewBox.height, "#444444", false);
    mask2.appendChild(rect2);
    m_meroDefs.appendChild(mask2);

    // In Single Format the cap is on the right, with text description underneath, so need to adapt viewBox
    // 20171026: Use of loadZoomPan pushes the imaghe right, so adjust the width (add 180) to cope with that
    String strViewBox = (m_viewBox.x) + " " + m_viewBox.y + " " + (m_viewBox.width + 180) + " " + String.valueOf(m_viewBox.height);
    
    //m_strSVGContent = outputSVG(m_svgGenerator, strViewBoxDefn);
    SVGSVGElement root = (SVGSVGElement) m_svgGenerator.getRoot();
    root.setAttributeNS(null, "viewBox", strViewBox);
    if (m_bText)
    {
        root.setAttributeNS(null, "onload", "svgInit(evt)");
        // 20160728: don't seem to need display=none - and can only use svg files as images if not set
        //root.setAttributeNS(null, "display", "none");       // initially hide content - will be made visible after processing done in svgInit
    }
    //m_strSVGContent = SVGFactoryUtils.convertm_svgGenerator2String(m_svgGenerator, root, false);
    m_strSVGContent = SVGFactoryUtils.convertSVGNode2String(root, false);
    
    return m_strSVGContent;
}
private Rectangle createViewBox(int nTotalRows)
{
    // if no row titles then x offset = SVG_MERO_123_ORIGIN_X and width is simply that of career
    // m_nRowTitleWidth is added on later if applies
    // TO DO: how to use m_nViewBoxXoffset?
    int nX = SVG_MERO_123_ORIGIN_X + 10;
    // SVG_ROW_HEADER_ORIGIN_X + m_nViewBoxXoffset;   // (m_bRowTitles ? -SVG_MERO_123_ORIGIN_X : SVG_MERO_123_ORIGIN_X) + m_nViewBoxXoffset;   // what is 180?? try 0
    int nWidth = m_career.getWidth() + 10;   //  + (SVG_MERO_123_ORIGIN_X - SVG_ROW_HEADER_ORIGIN_X);


    int nY = m_bColumnTitles ? -(SVG_MERO_123_ORIGIN_Y - SVG_COLUMN_HEADER_ORIGIN_Y) : 0; // -45 replaces -(SVG_MERO_123_ORIGIN_Y - SVG_COLUMN_HEADER_ORIGIN_Y)
    int nHeight = ((int)(nTotalRows * m_nFullHeight)/2) - nY + (m_bColumnTitles ? ((m_career.getTableCount() - 1) * MERO_TABLE_OFFSET) : 0);   // number rows + number headers
    
    return new Rectangle(nX, nY, nWidth, nHeight);
}

private int drawRowHeader(CareerDefinition.CareerTable table, int nTableOffset) throws IOException
{
    int nHeaderHeight = SVG_MERO_123_ORIGIN_Y-SVG_COLUMN_HEADER_ORIGIN_Y-10;
    
    Element tlg = m_svgGenerator.getTopLevelGroup();
    Element g = m_svgGenerator.getDOMFactory().createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "g");
    g.setAttributeNS(null, "transform", "translate(0," + ((int) m_dyTranslate - nTableOffset) + ")");   

    g.setAttributeNS(null, "height", "100");
    g.setAttributeNS(null, "class", "row");
    g.setAttributeNS(null, "subclass", "header");
    tlg.appendChild(g);

    m_svgGenerator.setTopLevelGroup(g);
    if (m_colColumnHeaderBackground != null)
    {
        Color saveColour = m_svgGenerator.getColor();
        m_svgGenerator.setColor(m_colColumnHeaderBackground);
        m_svgGenerator.fillRect(SVG_MERO_123_ORIGIN_X, SVG_COLUMN_HEADER_ORIGIN_Y-SVG_MERO_123_ORIGIN_Y, table.getWidth(), nHeaderHeight);   // -45 replaces 
        m_svgGenerator.setColor(saveColour); 
    }
    Iterator<CareerDefinition.CareerColumn> iterColumns = table.getColumnIterator();
    int nColumnOffset = 0;
    int nColumnWidth = 0;
    while(iterColumns.hasNext())
    {
        CareerDefinition.CareerColumn column = iterColumns.next();
        nColumnOffset = column.getOffset();
        nColumnWidth = column.getCellWidth();
        if (sm_colColumnHeaderSeparators != null)
            drawColumnTitleSeparator(sm_colColumnHeaderSeparators, nColumnOffset, true);
        drawColumnHeader(column, true);
    }
    if (sm_colColumnHeaderSeparators != null)
        drawColumnTitleSeparator(sm_colColumnHeaderSeparators, nColumnOffset + nColumnWidth, true);

    m_svgGenerator.setTopLevelGroup(tlg);
    
    return nHeaderHeight;
}
private int drawRowSeason(CareerDefinition.CareerTable table, CareerDefinition.CareerRow row, int nTableOffset, int nTotalRows, int nRows, int nViewBoxWidth, int nPixelsRowHeight) throws IOException
{
    String strRowTitle=row.getTitle();
    String strRowName = row.getName();
    if (strRowName != null)
        m_strHorse = strRowName;            // override career "name" with row name - for case when multiple horses

    // create g containing season
    Element tlg = m_svgGenerator.getTopLevelGroup();
    Element g = m_svgGenerator.getDOMFactory().createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "g");
    g.setAttributeNS(null, "transform", "translate(0," + ((int) m_dyTranslate - nTableOffset) + ")");

    g.setAttributeNS(null, "class", "row");
    g.setAttributeNS(null, "subclass", "season");
    g.setAttributeNS(null, "id", row.getShortTitle());
    g.setAttributeNS(null, "height", String.valueOf(nPixelsRowHeight));
    tlg.appendChild(g);
    m_svgGenerator.setTopLevelGroup(g);

    int nColumns = 1;
    int nRowHeight = row.getHeight();
    int nTotalRowWidth = 0;
    boolean bZeroColumn = false;
    // background
    if ((sm_colAlternateLines != null) && (nRows%2 != 1))
    {
        Color saveColour = m_svgGenerator.getColor();
        m_svgGenerator.setColor(sm_colAlternateLines);
        m_svgGenerator.fillRect(SVG_ROW_HEADER_ORIGIN_X, 0, nViewBoxWidth, nRowHeight * m_nFullHeight);
        m_svgGenerator.setColor(saveColour);
    }

    // write row title at beginning of line
    int nRowTitleWidth = 0;
    if (m_bRowTitles)
    {
        if (m_career.isMeeting())
            nRowTitleWidth = displayMeetingRowTitle(strRowTitle);
        else
            nRowTitleWidth = displayRowTitle(strRowTitle, nRowHeight, m_career.hasHorizontalRowTitles());
    }
    if (nRowTitleWidth > m_nRowTitleWidth)
        m_nRowTitleWidth = nRowTitleWidth;
    
    Iterator<CareerDefinition.CareerRow.CareerCell> iterCell = row.getCellIterator();
    while(iterCell.hasNext())
    {
        CareerDefinition.CareerColumn column = table.getColumnByPosition(nColumns);
        CareerDefinition.CareerRow.CareerCell cell = iterCell.next();
        String strCell=cell.getContent();
        int nColspan = cell.getColSpan();

        int nTotalCellWidth = table.getCellWidth(nColumns, nColspan, bZeroColumn);
        nTotalRowWidth += nTotalCellWidth;

        if (nColspan == 0)
            bZeroColumn = !bZeroColumn;         // is there a half cell open

        int nRaces = cell.getRaceCount();
        if (nRaces > 0)
        {
            Iterator<CareerDefinition.CareerRow.CareerCell.CareerRace> iterRaces = cell.getRaceIterator();
            resetCareerRowTranslate();
            int nRaceCount = 0;
            int nxRaceOffset = 0;
            while(iterRaces.hasNext())
            {
                m_setSeason.addValue(row.getShortTitle());
                CareerDefinition.CareerRow.CareerCell.CareerRace race = iterRaces.next();
                String strRaceTitle = race.getTitle();
                String strSource = race.getSource();
                String strRace=race.getContent();
                nxRaceOffset = 0;
                 try
                 {
                     int nRace = Integer.parseInt(strRace);
                     AdditionalRaceInstance arl = AdditionalRaceLinkFactory.getAdditionalRaceLinkObject(m_statement, nRace, strSource);
                     if (arl != null)
                     {
                         AdditionalRaceData ard = AdditionalRaceDataFactory.createAdditionalRaceData(m_statement, nRace, strSource);
                         if (nRaces == 1)   
                         {
                             if ((column.getMeroWidth() == 4) && !bZeroColumn)
                                nxRaceOffset =  SVG_MERO_123_WIDTH/2; // special case, 1 cell in double row, place centrally
                             careerTranslate(nxRaceOffset, 0);    // place in centre of wider column
                             careerRowTranslate((nRowHeight > 1) ? (m_nFullHeight * 0.5) : 0);    // place in centre of wider column
                             drawRaceCell(arl, ard, strRaceTitle, nColumns);
                             careerRowTranslate((nRowHeight > 1) ? (-m_nFullHeight * 0.5) : 0);    // place underneath previous
                         }
                         else 
                         {
                             if (nRaceCount > 0)
                             {
                                 careerRowTranslate(m_nFullHeight);    // place underneath previous
                                 // draw arrow
                                 if (column.getMeroWidth() == 4)
                                 {
                                    careerTranslate(SVG_MERO_123_ORIGIN_X, 0);    // move right one square
                                    Stroke saveStroke = m_svgGenerator.getStroke();
                                    m_svgGenerator.setStroke(new BasicStroke(5)); 
                                    m_svgGenerator.drawLine(SVG_MERO_123_WIDTH/2, 0, SVG_MERO_123_WIDTH/2, m_nFullHeight/2);
                                    m_svgGenerator.drawLine(SVG_MERO_123_WIDTH/2, m_nFullHeight/2, SVG_MERO_123_WIDTH, m_nFullHeight/2);
                                    m_svgGenerator.drawLine(SVG_MERO_123_WIDTH - 20, m_nFullHeight/2 - 20, SVG_MERO_123_WIDTH, m_nFullHeight/2);
                                    m_svgGenerator.drawLine(SVG_MERO_123_WIDTH - 20, m_nFullHeight/2 + 20, SVG_MERO_123_WIDTH, m_nFullHeight/2);
                                    m_svgGenerator.setStroke(saveStroke);
                                    nxRaceOffset = SVG_MERO_123_WIDTH;
                                    careerTranslate(nxRaceOffset - SVG_MERO_123_ORIGIN_X, 0);    // move right one square
                                 }
                             }

                            drawRaceCell(arl, ard, strRaceTitle, nColumns + 1);
                         }
                     }
                     else
                     {
                         String strRaceNotFound = "Race not found: " + strSource + "-" + nRace;
                         cell.setContent(strRaceNotFound);
                         System.out.println("generateCareer " + strRaceNotFound);
                         drawTextCell(cell, ((nColumns > 0) ? SVG_MERO_123_WIDTH_EXTRA : 0) + 175, 100, nTotalCellWidth);
                     }
                     nRaceCount++;
                 }
                 catch(NumberFormatException e)
                 {

                 }
            }
            careerTranslate(-nxRaceOffset, 0);      //-(nRaceCount - 1) * m_nFullHeight);    // put back to top left of cell
            resetCareerRowTranslate();
        }
        else
        {
            String strJockeyColours = cell.getJockeyColours();
            if (!"".equals(strJockeyColours))
            {
                generateSVGMero(strJockeyColours, strCell, "cell title?", nTotalRows, nColumns+1);
            }
            else 
            {
                if ("PEDIGREE".equalsIgnoreCase(strCell))
                {
                    drawPedigreeCell(m_career.getPedigree(), ((nColumns > 0) ? SVG_MERO_123_WIDTH_EXTRA : 0) + 175, 100, nTotalCellWidth);
                }
                else if (strCell.indexOf("LEGEND") >= 0)
                {
                    drawLegend(strCell, ((int)m_dxTranslate) + ((nColumns > 0) ? SVG_MERO_123_WIDTH_EXTRA : 0) + 175, ((int)m_dyTranslate) + 100, nTotalCellWidth);
                }
                else if ("COLOURS".equalsIgnoreCase(strCell))
                {
                    drawColoursCell(m_career.getColours(), m_career.getName(), ((nColumns > 0) ? SVG_MERO_123_WIDTH_EXTRA : 0) + 175, 100, nTotalCellWidth);
                }
                else if (!"".equals(strCell))
                {
                    drawTextCell(cell, ((nColumns > 0) ? SVG_MERO_123_WIDTH_EXTRA : 0) + 175, 100, nTotalCellWidth);
                }
            }
         }  
        careerTranslate(nTotalCellWidth, 0);

        if (nColspan == 0)
        {
            nColumns += (bZeroColumn ? 0 : 1);
        }
        else
            nColumns+=nColspan;
        // end of a cell

    } 
    
    m_svgGenerator.setTopLevelGroup(tlg);

    return nTotalRowWidth;
}
private void drawGridLines(CareerDefinition.CareerTable table, int nViewBoxWidth, int nXOrigin)
{
        Stroke saveStroke = m_svgGenerator.getStroke();
        m_svgGenerator.setStroke(new BasicStroke(3));
        ArrayList<Integer> alRows = table.getRowSeparators(m_nCellHeight);
        for(int i = 0; i < alRows.size(); i++)
        {
            int nRowSeparator = alRows.get(i);
            m_svgGenerator.drawLine(nXOrigin, nRowSeparator, nViewBoxWidth + nXOrigin, nRowSeparator);   // top
            ArrayList<Integer> alColumns = table.getColumnSeparators(SVG_MERO_123_ORIGIN_X);
            for(int j = 0; j < alColumns.size(); j++)
            {
              int nColumnSeparator = alColumns.get(j);
              //m_svgGenerator.setStroke(new BasicStroke(3));
              int nTop = nRowSeparator - 5;
              int nBottom = nRowSeparator + 5;
              if (i == 0)
              {
                  nTop = nRowSeparator;
                  nBottom = nRowSeparator + 10;
              }
              else if (i == (alRows.size() - 1))
              {
                  nTop = nRowSeparator - 10;
                  nBottom = nRowSeparator;
              }
              m_svgGenerator.drawLine(nColumnSeparator, nTop, nColumnSeparator, nBottom);
            }
        }
        m_svgGenerator.setStroke(saveStroke); 
    
}
private void drawColumnTitleSeparator(Color colour, int nColumnOffset, boolean bTop)
{
    Stroke saveStroke = m_svgGenerator.getStroke();
    Color saveColor = m_svgGenerator.getColor();
    m_svgGenerator.setStroke(new BasicStroke(5)); 
    m_svgGenerator.setColor(colour);
    m_svgGenerator.drawLine(SVG_MERO_123_ORIGIN_X + nColumnOffset, SVG_COLUMN_HEADER_ORIGIN_Y-SVG_MERO_123_ORIGIN_Y + 3, SVG_MERO_123_ORIGIN_X + nColumnOffset, -13);
    m_svgGenerator.setColor(saveColor);
    m_svgGenerator.setStroke(saveStroke);
}
private int drawRaceCell(AdditionalRaceInstance race, AdditionalRaceData ard, String strRaceTitle, int nColumns) throws IOException
{
        Element tlg = m_svgGenerator.getTopLevelGroup();
        SmartformColoursRunner hrunner = ENEColoursRunnerFactory.getSmartformRaceRunnerName(m_statement, race, m_strHorse, false);   // Not just finishers
        
        // create g containing race
        Element g = m_svgGenerator.getDOMFactory().createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "g");
        
        g.setAttributeNS(null, "id", String.valueOf(race.getRaceId()));
        if (m_bText)
        {
            g.setAttributeNS(null, "class", "race");

            String strFinishPosition = "";
            String strDaysSinceRan = "";
            String strDistanceTravelled = "";
            boolean bMeeting = false;
            if (m_career.isMeeting())
            {
                ArrayList<SmartformColoursRunner> runners = ENEColoursRunnerFactory.getSmartformRaceRunners(m_statement, race, 1);
                hrunner = runners.get(0);
                bMeeting = true;
                // override title from career xml with value from additional_race_data table
                if (ard != null)
                    strRaceTitle = ard.getTitle();
            }
            if(hrunner != null)
            {
                strFinishPosition = convertFinishPosition(hrunner.getFinishPositionString(), race.getNrRunners());
                g.setAttributeNS(null, "finish_position", strFinishPosition);
                m_setFinishPosition.addValue(strFinishPosition);
                
                strDistanceTravelled = convertDistanceTravelled(hrunner.getDistanceTravelled());
                g.setAttributeNS(null, "distance_travelled", convertOptionValue(strDistanceTravelled));
                m_setDistanceTravelled.addValue(strDistanceTravelled);
                
                strDaysSinceRan = convertDaysSinceRan(hrunner.getDaysSinceRan());
                g.setAttributeNS(null, "days_since_ran", convertOptionValue(strDaysSinceRan));
                m_setDaysSinceRan.addValue(strDaysSinceRan);

                SmartformTack tack =hrunner.getTack();
                g.setAttributeNS(null, "tack", convertTack(tack));
                
                // to do:
                int nPositionInBetting = hrunner.getPositionInBetting();
                String strPositionInBetting = convertPositionInBetting(nPositionInBetting);
                m_setPositionInBetting.addValue(strPositionInBetting);
                g.setAttributeNS(null, "position_in_betting", convertOptionValue(strPositionInBetting));
                
                double dStartingPrice = hrunner.getStartingPriceDecimal();
                String strStartingPrice = convertStartingPrice(dStartingPrice);
                m_setStartingPrice.addValue(strStartingPrice);
                g.setAttributeNS(null, "starting_price", convertOptionValue(strStartingPrice));
                if (m_career.isMeeting())
                {
                    double dDistanceWon = hrunner.getDistanceWon();
                    String strDistanceWon = convertDistanceWon(dDistanceWon);
                    m_setDistanceWon.addValue(strDistanceWon);
                    g.setAttributeNS(null, "distance_won", convertOptionValue(strDistanceWon));
                }
                else
                {
                    if (hrunner.getFinishPosition() != 1)
                    {
                        double dDistanceBeaten = hrunner.getDistanceBehindWinner();
                        String strDistanceBeaten = convertDistanceWon(dDistanceBeaten);
                        m_setDistanceWon.addValue(strDistanceBeaten);
                        g.setAttributeNS(null, "distance_beaten", convertOptionValue(strDistanceBeaten));
                    }
                }
                //

                if (ard != null)
                {
                    String strAgeRange = ard.getAgeRange();
                    m_setAgeRange.addValue(strAgeRange);
                    g.setAttributeNS(null, "age_range", strAgeRange);
                }
                else
                    System.out.println("No ARD: " + race.getTitle());
                String strDistance = convertDistance(race.getDistanceFurlongs());
                g.setAttributeNS(null, "distance", convertOptionValue(strDistance));
                m_setDistance.addValue(strDistance);
                int nGroup = race.getGroupRace();
                String strGroupRace = convertGroupRace(nGroup);
                g.setAttributeNS(null, "group_race", strGroupRace);
                m_setGroupRace.addValue(strGroupRace);
                String strNrRunners = convertNrRunners(race.getNrRunners());
                g.setAttributeNS(null, "nr_runners", convertOptionValue(strNrRunners));
                m_setNrRunners.addValue(strNrRunners);
                String strJockeyName = WikipediaFactory.getFullJockeyName(m_statement, hrunner.getJockeyName());
                g.setAttributeNS(null, "jockey", strJockeyName);
                m_setJockey.addValue(strJockeyName);
                String strTrainerName = WikipediaFactory.getFullTrainerName(m_statement, hrunner.getTrainerName());
                g.setAttributeNS(null, "trainer", strTrainerName);
                m_setTrainer.addValue(strTrainerName);
                String strRaceType= convertRaceType(race.getRaceType());
                g.setAttributeNS(null, "race_type", strRaceType);
                m_setRaceType.addValue(strRaceType);
                if (!bMeeting)
                {
                     
                    String strCourse = race.getCourse().replace("_", " ");
                    for(int i = 0; i < sm_astrCourseNames.length; i++)
                    {
                        if (sm_astrCourseNames[i][0].equalsIgnoreCase(strCourse))
                        {
                            strCourse= sm_astrCourseNames[i][1];
                            break;
                        }
                    }
                    g.setAttributeNS(null, "course", strCourse);
                    m_setCourse.addValue(strCourse);
                    RacingPostCourse course = ENEColoursDBEnvironment.getInstance().getRPCourseBySFName(strCourse, strRaceType);
                    String strCountry = convertCountry(course.getCountry());
                    g.setAttributeNS(null, "country", strCountry);
                    m_setCountry.addValue(strCountry);
                    
                    String strGoing = race.getShortGoing();
                    if (strGoing == null)
                    {
                        System.out.println("Null Going");
                        strGoing="";
                    }
                    g.setAttributeNS(null, "going", strGoing.trim());
                    m_setGoing.addValue(strGoing);
                    String strDirection = race.getDirection();
                    if ("".equals(strDirection))
                        strDirection = "Unknown";
                    g.setAttributeNS(null, "direction", strDirection);
                    m_setDirection.addValue(strDirection);
                }
                else
                {
                    String strOwnerName = hrunner.getPrimaryOwner();    // name stored in wikipedia_images table
                    if (!"".equals(strOwnerName))
                    {
                        if (strOwnerName.indexOf("_") > 0)
                        {
                            strOwnerName = strOwnerName.substring(0, strOwnerName.indexOf("_"));  // remove suffices like _white_cap
                        }
                    }
                    else 
                        strOwnerName = hrunner.getOwnerName();
                    for(int i = 0; i < sm_astrOwnerNames.length; i++)
                    {
                        if (sm_astrOwnerNames[i][0].equalsIgnoreCase(strOwnerName))
                        {
                            strOwnerName= sm_astrOwnerNames[i][1];
                            break;
                        }
                    }
                    g.setAttributeNS(null, "owner", strOwnerName);
                    m_setOwner.addValue(strOwnerName);
                }
                
           }
         }
        
        tlg.appendChild(g);
        m_svgGenerator.setTopLevelGroup(g);

        int nMeroWidth;
        //SmartformRace race = SmartformRaceFactory.createSmartformRace(m_statement, nRace);
       if ("2-3".equals(m_career.getFormat()))
       {
             nMeroWidth = generateSVGMero23(race, strRaceTitle);
       }
       else if ("1".equals(m_career.getFormat()))
       {
           nMeroWidth = generateSVGMero1(race, strRaceTitle);
       }
       else
       {
           nMeroWidth = generateSVGMero123(race, strRaceTitle, hrunner);
       }

    // reset top level group
    m_svgGenerator.setTopLevelGroup(tlg);

    return nMeroWidth;
}

private int displayMeetingRowTitle(String strRowTitle)
{
    int nyTitle = SVG_MERO_123_HEIGHT/2 + 50;
    String[] astrTitles = strRowTitle.split("\\|");
    
    Element tlg = m_svgGenerator.getTopLevelGroup();
    tlg = SVGFactoryUtils.addTextElement(m_svgGenerator.getDOMFactory(), tlg, astrTitles[0], 
                            SVG_ROW_HEADER_ORIGIN_X - 70, nyTitle,  
                            "black", 80, 
                            "middle", true);
            
    if (astrTitles.length > 1)
    {
        tlg = SVGFactoryUtils.addTextElement(m_svgGenerator.getDOMFactory(), tlg, astrTitles[1], 
                            SVG_ROW_HEADER_ORIGIN_X - 70, nyTitle + 50, 
                            "black", 40, 
                            "middle", true);
    }
    if (astrTitles.length > 2)
    {
        tlg = SVGFactoryUtils.addTextElement(m_svgGenerator.getDOMFactory(), tlg, astrTitles[2], 
                            SVG_ROW_HEADER_ORIGIN_X - 70, nyTitle + 100, 
                            "black", 40, 
                            "middle", false);
    }
    m_svgGenerator.setTopLevelGroup(tlg);

  /*  int nFontSize=80;
    Font saveFont = m_svgGenerator.getFont();
    Font font = new Font(HEADING_FONT_NAME, Font.BOLD,  nFontSize);
    m_svgGenerator.setFont(font);
    m_svgGenerator.drawString(astrTitles[0], SVG_ROW_HEADER_ORIGIN_X - 250, nyTitle);
    m_svgGenerator.setFont(saveFont); 
    if (astrTitles.length > 1)
    {
        String strSubTitle = astrTitles[1];
        AttributedString s = new AttributedString(strSubTitle);
        s.addAttribute(TextAttribute.FAMILY, OUTPUT_FONT);
        s.addAttribute(TextAttribute.SIZE, 40);
        s.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

        s.addAttribute(TextAttribute.FOREGROUND, sm_colRowHeaders);
        m_svgGenerator.drawString(s.getIterator(), SVG_ROW_HEADER_ORIGIN_X - 160, nyTitle + 50);
    } 
    if (astrTitles.length > 2)
    {
        String strSubTitle = astrTitles[2];
        AttributedString s = new AttributedString(strSubTitle);
        s.addAttribute(TextAttribute.FAMILY, OUTPUT_FONT);
        s.addAttribute(TextAttribute.SIZE, 40);
        s.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

        s.addAttribute(TextAttribute.FOREGROUND, sm_colRowHeaders);
        m_svgGenerator.drawString(s.getIterator(), SVG_ROW_HEADER_ORIGIN_X - 160, nyTitle + 100);
    } */
    
    // TO DO: calculate actual width
    return 270;
}
private int displayRowTitle(String strRowTitle, int nRowHeight, boolean bHorizontal)
{
    int nyTitle  = (nRowHeight == 2) ? 250 : 0;
    if (bHorizontal)
    {
        Font saveFont = m_svgGenerator.getFont();
         Font font = new Font(HEADING_FONT_NAME, Font.BOLD,  100);
         m_svgGenerator.setFont(font);
        Rectangle2D rect = m_svgGenerator.getFontMetrics(font).getStringBounds(strRowTitle, m_svgGenerator);  

        AttributedString s = new AttributedString(strRowTitle);
        s.addAttribute(TextAttribute.FAMILY, OUTPUT_FONT);
        s.addAttribute(TextAttribute.SIZE, 100);
        s.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

        s.addAttribute(TextAttribute.FOREGROUND, sm_colRowHeaders);
        m_svgGenerator.drawString(s.getIterator(), 50 - (int) rect.getWidth(), nyTitle + m_nFullHeight/2);
        
        m_svgGenerator.setFont(saveFont);
        
        return SEASON_ROW_TITLE_WIDTH; 
    }
    int nFontSize = 100;
    int nYear = 0;
    String strSubTitle = "";
    int nDivider = strRowTitle.indexOf("|");
    if (nDivider > 0)
    {
        strSubTitle = strRowTitle.substring(nDivider + 1);
        strRowTitle = strRowTitle.substring(0, nDivider);
    }
            
     else if (strRowTitle.length() >= 4)
     {
         // the title may be a season (year for flat, year-year for jumps) - if so calculate the age of the horse at the start
         String strYear = strRowTitle.substring(0, 4);
         try
         {
             nYear = Integer.valueOf(strYear);
         }
         catch(Exception e)
         {
             // No year information
         }
         int nBirthYear = 0;
         if (m_horse != null)
         {
            nBirthYear = m_horse.getYearBorn();
         }
         if ((nYear > 0) && (nBirthYear > 0))
         {
            int nAge = nYear - nBirthYear;
            strSubTitle = nAge +"-y-o";
            if (strRowTitle.length() > 4)
            {
               String strYear1 = strYear.substring(2);
               strRowTitle = strRowTitle.replace(strYear, strYear1);
               nFontSize = 80;
            }
         }
     }
    
     if (strRowTitle.length() > 0)
     {
/*      int nyTitle = (nRowHeight == 1) ? (m_nFullHeight - 200) : (m_nFullHeight * nRowHeight/2);
  AttributedString s = new AttributedString(strRowTitle);
         s.addAttribute(TextAttribute.FAMILY, OUTPUT_FONT);
         s.addAttribute(TextAttribute.SIZE, 80);
         s.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

         s.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
         m_svgGenerator.drawString(s.getIterator(), -SVG_MERO_SEASON_OFFSET, nyTitle);  // different for 2nd table ?
         */
         Font saveFont = m_svgGenerator.getFont();
         Font font = new Font(HEADING_FONT_NAME, Font.BOLD,  nFontSize);
         m_svgGenerator.setFont(font);
        int v=m_svgGenerator.getFontMetrics(font).getAscent() - 10;   // leaves too much space
        int j = 0;
        int k = strRowTitle.length();
        while(j < k) 
        {
            nyTitle += v;
            m_svgGenerator.drawString(strRowTitle.substring(j,j+1), SVG_ROW_HEADER_ORIGIN_X + 20, nyTitle);
            j++;
        }
        m_svgGenerator.setFont(saveFont);
     }
     if (strSubTitle.length() > 0)
     {
             AttributedString s = new AttributedString("(" + strSubTitle + ")");
             s.addAttribute(TextAttribute.FAMILY, OUTPUT_FONT);
             s.addAttribute(TextAttribute.SIZE, 25);
             s.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

             s.addAttribute(TextAttribute.FOREGROUND, sm_colRowHeaders);
             m_svgGenerator.drawString(s.getIterator(), SVG_ROW_HEADER_ORIGIN_X, nyTitle + 40);
      } 
     
    return SEASON_ROW_TITLE_WIDTH;     
}
public void generateCareerHTML() throws IOException
{
    // to do: customize legend (Group/Grade) , race type based on horse
    // add filters e.g. season based on values existing in report
    
    String strDirectory = "horses";
    if (m_career.isMeeting())
        strDirectory = "meetings";
    
    String strFullDirectory = SVG_OUTPUT_DIRECTORY + "/" + strDirectory;
    // to do: use template_bs
    String strTemplateHTML = strFullDirectory + "/template_bs.html";
    String strOutHTML = strFullDirectory + "/" + m_career.getFileName().replaceAll(" ", "_") + ".html";
    String strTrainerEntry = "";
    String strFullName = m_career.getName();
    if (m_horse != null)
    {
        strFullName = m_horse.getFullName();
        generateProfile(m_horse);
        strTrainerEntry = generateTrainerEntry(m_horse);
    }
    String strFilter = generateFilterBS();
    generateLegend();
    List<String> lstLines = FileUtils.readOriginalFile(strTemplateHTML, "UTF-8");
    ArrayList<String> lns = new ArrayList<String>();
    for (String ln : lstLines){
        String strLine = ln.replace("${CAREER}", m_career.getName()).replace("${FILTER}", strFilter).replace("${MEETING_ID}", m_career.getId()).replace("${MEETING_NAME}", m_career.getName());
        strLine = strLine.replace("${LEGEND}", m_strLegend).replace("${PROFILE}", m_strProfile).replace("${TRAINER}", strTrainerEntry).replace("${FULL_NAME}", strFullName).replace("${SVG_NAME}", m_career.getFileName().replace("_", " "));
        lns.add(strLine);
        
    }
    lstLines.clear();
 
    FileUtils.writeFile(strOutHTML, lns, StandardCharsets.UTF_8, true);    // overwrite
    
}
private void generateLegend()
{
    m_strLegend = "";
    if (m_setRaceType.contains("Flat"))
        m_strLegend += sm_strLegendFlat;
    if (m_setRaceType.contains("AW Flat"))
        m_strLegend += (("".equals(m_strLegend) ? "" : sm_strLegendSingleSeparator) + sm_strLegendAWFlat);
    if (m_setRaceType.contains("Chase"))
        m_strLegend += (("".equals(m_strLegend) ? "" : sm_strLegendSingleSeparator) + sm_strLegendChase);
    if (m_setRaceType.contains("Hurdle"))
        m_strLegend += (("".equals(m_strLegend) ? "" : sm_strLegendSingleSeparator) + sm_strLegendHurdle);
    if (m_setRaceType.contains("NH Flat"))
        m_strLegend += (("".equals(m_strLegend) ? "" : sm_strLegendSingleSeparator) + sm_strLegendNHFlat);
    
    m_strLegend += (sm_strLegendMultiSeparator + sm_strLegendGroup);
    
    if ((!m_setRaceType.contains("Flat")) && (!m_setRaceType.contains("AW Flat")))
        m_strLegend = m_strLegend.replaceAll("Group", "Grade");
}
private String generateTrainerEntry(SmartformHorse horse)
{
    String strTrainerEntry = "";
    if (horse.getTrainer() != null)
        strTrainerEntry = "(" + WikipediaFactory.getFullTrainerName(m_statement, horse.getTrainer().getName())+ ")";

    return strTrainerEntry;
}
private void generateProfile(SmartformHorse horse)
{
    if (horse.getTrainer() != null)
        m_strProfile += generateProfileEntry("Trainer", horse.getTrainer().getName());
    if (horse.getOwner() != null)
        m_strProfile += generateProfileEntry("Owner", horse.getOwner().getName());
    if ((horse.getBreeder() != null) && !"".equals(horse.getBreeder()))
        m_strProfile += generateProfileEntry("Breeder", horse.getBreeder());
    m_strProfile += generateProfileEntry("Colour", WordUtils.capitalizeFully(horse.getLongColour()));
    m_strProfile += generateProfileEntry("Gender", WordUtils.capitalizeFully(horse.getLongGender()));
    if (horse.getSire() != null)
        m_strProfile += generateProfileEntry("Sire", horse.getSire().getFullName());
    if (horse.getDam() != null)
        m_strProfile += generateProfileEntry("Dam", horse.getDam().getFullName());
    String strDamSire = horse.getDamSireName();
    if (strDamSire != null)
        m_strProfile += generateProfileEntry("Dam Sire", strDamSire);
    if (horse.getFoalingDate() != null)
        m_strProfile += generateProfileEntry("Foaling Date", horse.getFormattedFoalingDate(new SimpleDateFormat( "MMMM d yyyy" )));
    else if (m_career.getFoalingDate() != null)
        m_strProfile += generateProfileEntry("Foaling Date", m_career.getFormattedFoalingDate(new SimpleDateFormat( "MMMM d yyyy" )));
}
private String generateProfileEntry(String strName, String strValue)
{
    if (strValue != null)
    {
        String strEntry = ("<tr><td>" + strName + "</td><td>" + strValue + "</td></tr>");
        return strEntry;
    }
    
    return "";
}
private String generateFilterBS()
{
    m_nFilterCount = 0;
    m_strFilter = "<div class=\"row\">";

    if (!m_career.isMeeting())
    {
        generateFilterSelectBS("season", "Season", m_setSeason , 2, false);
        generateFilterSelectBS("finish_position", "Finish Position", m_setFinishPosition, 3, false);
        generateFilterSelectBS("going", "Going", m_setGoing, 3, false);
        generateFilterSelectBS("course", "Course", m_setCourse, 4, false);
    }

   // generateFilterSelectBS("country", "Country", m_setCountry , 3);  // where?

    generateFilterSelectBS("race_type", "Race Type", m_setRaceType, 3, true);
    
    generateFilterSelectBS("distance", "Distance", m_setDistance, 3, false);
    generateFilterSelectBS("group_race", "Group", m_setGroupRace, 3, false);
    
    if (!m_career.isMeeting())
    {
        generateFilterSelectBS("direction", "Direction", m_setDirection, 3, false);
    }
    generateFilterOrderSelectBS("jockey", "Jockey", m_setJockey, 4, false);
    generateFilterSelectBS("nr_runners", "#Runners", m_setNrRunners, 2, false);
    generateFilterOrderSelectBS("trainer", "Trainer", m_setTrainer, 3, true);
    generateFilterSelectBS("days_since_ran", "Days Since Ran", m_setDaysSinceRan , 3, false);
    generateFilterSelectBS("distance_travelled", "Dist. travelled", m_setDistanceTravelled , 3, false);
    generateFilterSelectBS("tack", "Tack", m_setTack, 3, true);
    generateFilterSelectBS("starting_price", "SP", m_setStartingPrice, 3, false);
    generateFilterSelectBS("position_in_betting", "Betting Position", m_setPositionInBetting, 3, false);
    if (m_career.isMeeting())
    {
        generateFilterSelectBS("distance_won", "Distance Won", m_setDistanceWon, 3, false);
    }
    else
    {
        generateFilterSelectBS("distance_beaten", "Distance Beaten", m_setDistanceWon, 3, false);
    }
    // owner needs line to itself!
    generateFilterOrderSelectBS("owner", "Owner", m_setOwner, 9, true);

    m_strFilter += "</div>";

    return m_strFilter;
}
private String generateFilter()
{
    String strFilter = "";
    
    if (!m_career.isMeeting())
    {
        strFilter += generateFilterSelect("season", "Season", m_setSeason , false);
        strFilter += generateFilterSelect("race_type", "Race Type", m_setRaceType, false);
        strFilter += generateFilterSelect("finish_position", "Finish Position", m_setFinishPosition , false);
        strFilter += generateFilterSelect("going", "Going", m_setGoing , true);
        strFilter += generateFilterSelect("country", "Country", m_setCountry , true);
        strFilter += generateFilterSelect("course", "Course", m_setCourse , true);
    }
    strFilter += generateFilterSelect("distance", "Distance", m_setDistance , true);
    strFilter += generateFilterSelect("group_race", "Group", m_setGroupRace, true);
    
    strFilter += generateFilterOrderSelect("jockey", "Jockey", m_setJockey , true);
    strFilter += generateFilterOrderSelect("trainer", "Trainer", m_setTrainer , true);
    strFilter += generateFilterOrderSelect("owner", "Owner", m_setOwner , true);
    strFilter += generateFilterSelect("direction", "Direction", m_setDirection , true);
    strFilter += generateFilterSelect("nr_runners", "#Runners", m_setNrRunners , true);
    strFilter += generateFilterSelect("days_since_ran", "Days Since Ran", m_setDaysSinceRan , true);
    strFilter += generateFilterSelect("distance_travelled", "Dist. travelled", m_setDistanceTravelled , true);
    strFilter += generateFilterSelect("tack", "Tack", m_setTack , true);
    strFilter += generateFilterSelect("starting_price", "SP", m_setStartingPrice , true);
    strFilter += generateFilterSelect("position_in_betting", "Betting Position", m_setPositionInBetting , true);
    if (m_career.isMeeting())
    {
        strFilter += generateFilterSelect("distance_won", "Distance Won", m_setDistanceWon, true);
    }
    else
    {
        strFilter += generateFilterSelect("distance_beaten", "Distance Beaten", m_setDistanceWon, true);
    }
    
    return strFilter;
}

private String generateFilterOrderSelect(String strId, String strName, CountedSet setValues, boolean bExpand)
{
    return generateFilterSelect(strId, strName, new CountedSet(setValues, new WordCounterComparator()), bExpand);
}
private String generateFilterSelect(String strId, String strName, CountedSet setValues, boolean bExpand)
{
    int nDisplay = setValues.size();
    
    if (setValues.size() <= 1)
        return "";
     
    String strSelect ="<span id=\"" + strId + "\"" + (bExpand ? " onclick=\"expandCollapseSelect('" + strId + "')\" >+ " : ">") + strName +"</span>" + (bExpand ? "" : "<br />");
    strSelect+="\n<select name=\"" + strId + "\" id=\"" + strId + "_select\"" + ((nDisplay > 1) ? " multiple=\"multiple\" size=\"" + nDisplay + "\"" : "") + (bExpand ? " class=\"hideable\"" : "") + ">\n";
    if (nDisplay == 1)
        strSelect += "<option value=\"\">All</option>";
    Iterator<String> iter = setValues.iterator();
    while(iter.hasNext())
    {
        String strCurrent = iter.next();
        strSelect += createOption(strCurrent, setValues.getCount(strCurrent));
    }
    strSelect += "</select><br />\n";
    return strSelect;
}
private void generateFilterOrderSelectBS(String strId, String strName, CountedSet setValues, int nColumns, boolean bOptional)
{
    generateFilterSelectBS(strId, strName, new CountedSet(setValues, new WordCounterComparator()), nColumns, bOptional);
}
private void generateFilterSelectBS(String strId, String strName, CountedSet setValues, int nColumns, boolean bOptional)
{
    if (setValues.size() == 0)
        return;
    
    if ((setValues.size() == 1) && (bOptional || setValues.contains("Unknown")))
        return;

    boolean bSingle = ("direction".equals(strId) || "finish_position".equals(strId));
            
    if (m_nFilterCount + nColumns > 12)
    {
        m_strFilter += "<div class=\"clearfix visible-lg-block\"></div>";   // force new line
        m_nFilterCount = nColumns;
    }
    else
        m_nFilterCount += nColumns;
    
    int nDisplay = setValues.size();
    if (nDisplay > 5)
        nDisplay = 5;
    
    String strSelect ="<div class=\"col-sm-" + nColumns + "\"><div style=\"font-weight: bold\">" + strName + "</div>";
    strSelect+="\n<select name=\"" + strId + "\" id=\"" + strId + "_select\"" + ((!bSingle) ? " multiple=\"multiple\" size=\"" + nDisplay + "\"" : "") + ">\n";
    if (bSingle)
        strSelect += "<option value=\"\">All</option>";
    Iterator<String> iter = setValues.iterator();
    while(iter.hasNext())
    {
        String strCurrent = iter.next();
        strSelect += createOption(strCurrent, setValues.getCount(strCurrent));
    }
    strSelect += "</select></div>\n";
    
    m_strFilter += strSelect;
}
private void careerTranslate(double dX, double dY)
{
    m_dxTranslate += dX;    // keep track of translations
    m_dyTranslate += dY;
    m_svgGenerator.translate(dX, 0);    // dy - only navigate across a row - use row offset to handle
}
private void careerRowTranslate(double dY)
{
    m_dyRowTranslate += dY;
    m_svgGenerator.translate(0, dY);    // translate WITHIN a row
}
private void resetCareerRowTranslate()
{
    m_svgGenerator.translate(0, -m_dyRowTranslate);    // translate WITHIN a row
    m_dyRowTranslate = 0;
}

public int generateSVGMero(String strColours, String strName, String strCellTitle, int nRows, double dColumns) throws IOException
{
    
    drawTextHeader1(strCellTitle, dColumns);

    String strId = strColours.toLowerCase().trim();
        if (!m_alDescriptions.contains(strId))
        {
            ENERacingColours colours = null;
            if (!"Unknown".equals(strColours))
                colours = ENERacingColoursFactory.createColours(m_statement, "en", strColours);  
            meroService.addJockeySilks(m_svgGenerator, m_ctx, colours, strId, m_strLanguage, 1, String.valueOf(++m_nReferenceCount), m_hmDefinitions, "white");  // Careers need white background
            m_alDescriptions.add(strId);
        }
        String strDisplayId = nRows + "_" + dColumns + "_1";
        //int nX = (int)((dColumns-1) * m_nCellWidth);
        int nX = (int)m_dxTranslate;
        int nY = ((int)m_dyTranslate) - m_nYMeroOffset;

        displayMeroReference(strDisplayId, strId, nX, nY, null, false);
     displayOldHorseName(m_svgGenerator, strName, SVG_MERO_123_WIDTH, (strName.length() == 18) ? 22 : 24, SVG_MERO_123_WIDTH - 15, 0 + SVG_MERO_123_HEIGHT + m_nHeaderHeight - 5);
    
    return SVG_MERO_123_WIDTH; // return width of Mero
}
public int generateSVGMero1(AdditionalRaceInstance race, String strCellTitle) throws IOException
{
    ArrayList<SmartformColoursRunner> runners = ENEColoursRunnerFactory.getSmartformRaceRunners(m_statement, race, 1);
    
    drawRaceHeader1(strCellTitle, race, null);

    SmartformColoursRunner runner = runners.get(0);
    
    String strColoursId = runner.getJockeyColours().toLowerCase().trim();

    if ("".equals(strColoursId))
    {
        if (!m_alDescriptions.contains(strColoursId))
        {
            ENERacingColours colours = ENERacingColoursFactory.createRunnerColours(ENEColoursEnvironment.DEFAULT_LANGUAGE, runner);

            meroService.addJockeySilks(m_svgGenerator, m_ctx, colours, strColoursId, m_strLanguage, 1, String.valueOf(++m_nReferenceCount), m_hmDefinitions, "white");       // Careers need white background

            m_alDescriptions.add(strColoursId);
        }
        //int nX = (int)((dColumns-1) * m_nCellWidth);
        int nX = (int)m_dxTranslate;
        int nY = ((int)m_dyTranslate) - m_nYMeroOffset;
        String strDisplayId = nX + "_" + nY + "_1";

        displayMeroReference(strDisplayId, strColoursId, nX, nY, null, false);
    }    
    String strName = runner.getName();
    displayOldHorseName(m_svgGenerator, strName, SVG_MERO_123_WIDTH, (strName.length() == 18) ? 22 : 24, SVG_MERO_123_WIDTH - 15, 0 + SVG_MERO_123_HEIGHT + m_nHeaderHeight - 5);
    
    return SVG_MERO_123_WIDTH; // return width of Mero
}
public int generateSVGMero23(AdditionalRaceInstance race, String strCellTitle) throws IOException
{
    ArrayList<SmartformColoursRunner> runners = ENEColoursRunnerFactory.getSmartformRaceRunners(m_statement, race, 3);
    
    drawRaceHeader2(strCellTitle, race, null);
    // displaying 2nd and third, so offset is i - 1 (e.e. 2nd horse is first entry)
    for(int i = 1; i < 3; i++)
    {
       if (i < runners.size())
       {
            SmartformColoursRunner runner = runners.get(i);

            String strColoursId = runner.getJockeyColours().toLowerCase().trim();
            if ("".equals(strColoursId))
            {
                if (m_bShowPosition)         // if position is being shown, need to store colours for each place it appears
                    strColoursId += runner.getFinishPosition();

                if (!m_alDescriptions.contains(strColoursId))
                {
                    ENERacingColours colours = ENERacingColoursFactory.createRunnerColours(ENEColoursEnvironment.DEFAULT_LANGUAGE, runner);

                    meroService.addJockeySilks(m_svgGenerator, m_ctx, colours, strColoursId, m_strLanguage, 1, String.valueOf(++m_nReferenceCount), m_hmDefinitions, "white");   // Careers need white background

                    m_alDescriptions.add(strColoursId);
                }

                //int nX = (int)((dColumns-1) * m_nCellWidth + (i-1) * SVG_MERO_123_WIDTH);
                int nX = (int)m_dxTranslate;
                //int nY = (nRows-1) * SVG_MERO_123_FULL_HEIGHT - SVG_MERO_Y_OFFSET;
                int nY = ((int)m_dyTranslate) - m_nYMeroOffset;
                String strDisplayId = nX + "_" + nY + "_" + i;
                /*String strReference = runner.getJockeyColours().trim().toLowerCase();
                if (m_bShowPosition)
                    strReference += runner.getFinishPosition(); */
                displayMeroReference(strDisplayId, strColoursId, nX, nY, null, false);
            }
            String strName = runner.getName();
            displayOldHorseName(m_svgGenerator, strName, SVG_MERO_123_WIDTH, (strName.length() == 18) ? 22 : 24, SVG_MERO_123_WIDTH - 15, 0 + SVG_MERO_123_HEIGHT + m_nHeaderHeight - 5);
       }
 
       careerTranslate(SVG_MERO_123_WIDTH, 0);
   }

   careerTranslate(-(2 * SVG_MERO_123_WIDTH), 0);     // put back to origin
   
   return 2 * SVG_MERO_123_WIDTH;   // return width
}
public int generateSVGMero123(AdditionalRaceInstance race, String strCellTitle, SmartformColoursRunner hrunner) throws IOException
{
        ArrayList<SmartformColoursRunner> runners = ENEColoursRunnerFactory.getSmartformRaceRunners(m_statement, race, 3);
        System.out.println("generateSVGMero123: " + race.getRaceId() + "-" + runners.size());
        int nMaxRunner = 3;
        if (runners.size() < 3)
            nMaxRunner = runners.size();
        // store images in global definitions
        for (int i = 0; i < nMaxRunner; i++)
        {
            SmartformColoursRunner runner = runners.get(i);
            String strColoursId = runner.getJockeyColours().toLowerCase().trim();
            if ("".equals(strColoursId))
                strColoursId = "Unknown";
            
            if (!m_alDescriptions.contains(strColoursId))
            {
                ENERacingColours colours = null;
                if (!"Unknown".equals(strColoursId))
                    colours = ENERacingColoursFactory.createRunnerColours(ENEColoursEnvironment.DEFAULT_LANGUAGE, runner);
                Element mero = meroService.buildJockeySilks(m_ctx, colours, strColoursId, m_strLanguage, 1, String.valueOf(++m_nReferenceCount), m_hmDefinitions, "white");  // Careers need white background
                m_meroDefs.appendChild(mero);

                m_alDescriptions.add(strColoursId);
            }
        }
        

        // draw white background
        Color saveColor = m_svgGenerator.getColor();
        m_svgGenerator.setColor(Color.WHITE);  
        m_svgGenerator.fillRect(SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET, 0, 3 * SVG_MERO_123_WIDTH, SVG_MERO_123_HEIGHT + m_nHeaderHeight + 10);   // add 10 for increase in FOOTER height
        m_svgGenerator.setColor(saveColor); 

        String strTextColour = getTextColour(race.getRaceType());
        if (m_career.isMeeting())
        {
            // meetings do not have definition in career_defns.xml so look to see if race is already listed for a horse, so can get preferred abbreviated form
            String strCareerRaceTitle = CareerEnvironment.getInstance().getCareerRaceName(race.getSource() + race.getRaceId());
            if (strCareerRaceTitle != null)
                strCellTitle = strCareerRaceTitle;
            drawMeetingRaceHeader(strCellTitle, race, strTextColour);
        }
        else if (sm_b3RowHeader)
            draw3RowRaceHeader(strCellTitle, race, hrunner, strTextColour);
        else
            draw2RowRaceHeader(strCellTitle, race, hrunner, strTextColour);
       
        for(int i = 0; i < 3; i++)
        {
           String strName = "";
           if (i < runners.size())
           {
                SmartformColoursRunner runner = runners.get(i);
                
                if ((i == 2) && (hrunner != null) && (hrunner.getFinishPosition() == 3) && (runner.getFinishPosition() == 3) && !hrunner.getName().equalsIgnoreCase(runner.getName()))
                {
                    // must be a dead heat third, with our feature horse alphabetically 4th
                    runner = runners.get(i+1);
                }
                String strColoursId = runner.getJockeyColours().toLowerCase().trim();
                if ("".equals(strColoursId))
                     strColoursId = "Unknown";
                if (m_bShowPosition)         // if position is being shown, need to store colours for each place it appears
                    strColoursId += runner.getFinishPosition();

                
                if (m_bText || "".equals(strColoursId))
                    displayRunnerDetails(race, runner, i+1);

                int nX = (int)m_dxTranslate;
                int nY = ((int)m_dyRowTranslate) - m_nYMeroOffset;
                String strDisplayId = nX + "_" + (((int)m_dyTranslate) + ((int)m_dyRowTranslate) - m_nYMeroOffset) + "_" + i;
                 
                ArrayList<Element> lstTextElements = new ArrayList<Element>();
                String strRunnerText = runner.getDistanceBeatenString();
                int nFontSize = 20;
                int nOffset = 20;
                Element text = null;
                if (i == 0)
                {
                    nFontSize = 32;
                    nOffset = 20;
                    strRunnerText = "";
                    int nRunners = race.getNrRunners();
                    text = SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), (int) m_dxTranslate + SVG_MERO_123_WIDTH + nOffset, ((int) m_dyRowTranslate) + m_nHeaderHeight + nFontSize, "middle");
                    if (nRunners > 0)
                    {
                        // use tspan
                        text.appendChild(SVGFactoryUtils.createTSpanElement(m_svgGenerator.getDOMFactory(), String.valueOf(nRunners), 
                             strTextColour, 
                            nFontSize, true));
                        text.appendChild(SVGFactoryUtils.createTSpanElement(m_svgGenerator.getDOMFactory(), " ran", 
                             strTextColour, 
                            20, false));     // suffix fontsize should depend on nFontSize (40 for 80)
                    }
                }
                else 
                {
                    if ((strRunnerText.indexOf(" lengths") > 0) || (strRunnerText.indexOf("short ") >= 0))
                    {
                        nFontSize=16;
                    }
                    String[] astrRunnerText = strRunnerText.split("$");     // prevent multiple lines for now            
                    for(int j = 0; j < astrRunnerText.length; j++)
                    {
                        //  m_dyRowTranslate instead of m_dyTranslate
                        text = SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), astrRunnerText[j],  
                                    (int) m_dxTranslate + SVG_MERO_123_WIDTH + nOffset, ((int) m_dyRowTranslate) + m_nHeaderHeight + nFontSize + (j * nFontSize),
                                     strTextColour, nFontSize, "middle", false);

                     }
                }
                lstTextElements.add(text);
               
                displayMeroReference(strDisplayId, strColoursId, nX, nY, lstTextElements, m_bText);     // -m_nYMeroOffset offset instead of nY
                strName = runner.getName();
                       
            }
           else
           {
               if (sm_bQuestionMark)
               {
                    Element tlg = m_svgGenerator.getTopLevelGroup();
                    tlg = SVGFactoryUtils.addTextElement(m_svgGenerator.getDOMFactory(), tlg, "?", 
                            (int)m_dxTranslate + SVG_MERO_123_WIDTH + 80, ((int)m_dyRowTranslate) + SVG_MERO_123_HEIGHT + 40, 
                            "black", 300, 
                            "middle", true);
            
                    m_svgGenerator.setTopLevelGroup(tlg);
                }
               else if (i == 1)
               {
                   if (race.getNrRunners() >= 2)
                       strName = "Finished alone";
                    else
                        strName = "Walked over";
               }
               else if (i == 2)
                {
                    if (runners.size() == 2)
                    {
                        if (race.getNrRunners() >= 3)
                            strName = "Only two finished";
                         else
                             strName = "Only two ran";
                    }
                }
           }

            // Box around silks - draw last
            saveColor = m_svgGenerator.getColor();
            m_svgGenerator.setColor(Color.BLUE);  
            m_svgGenerator.drawRect(SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET, m_nHeaderHeight, SVG_MERO_123_WIDTH, SVG_MERO_123_HEIGHT + 10);   // add 10 for increase in FOOTER height
            m_svgGenerator.setColor(saveColor);

            // m_dyRowTranslate instead of ((int)m_dyTranslate)
            displayHorseName(m_svgGenerator, strName, SVG_MERO_123_WIDTH, (int)(m_dxTranslate) + SVG_MERO_123_WIDTH + SVG_MERO_123_WIDTH/2 - 16, ((int)m_dyRowTranslate) + SVG_MERO_123_HEIGHT + m_nHeaderHeight + 2);
            
           careerTranslate(SVG_MERO_123_WIDTH, 0);
        }

        // draw blue box around race and colours - do it last so not overwritten (when turned to PDF)
        saveColor = m_svgGenerator.getColor();
        m_svgGenerator.setColor(Color.BLUE);  
        Stroke saveStroke = m_svgGenerator.getStroke();
        m_svgGenerator.setStroke(new BasicStroke(2));
        m_svgGenerator.drawRect(-(2 * SVG_MERO_123_WIDTH + SVG_MERO_X_OFFSET), 0, 3 * SVG_MERO_123_WIDTH, SVG_MERO_123_HEIGHT + m_nHeaderHeight + 10);   // add 10 for increase in FOOTER height
        m_svgGenerator.setColor(saveColor); 
        m_svgGenerator.setStroke(saveStroke);

        careerTranslate( -(3 * SVG_MERO_123_WIDTH), 0);     // put back to origin


        return 3 * SVG_MERO_123_WIDTH;   // return width
}
private void displayMeroReference(String strId, String strJockeyColours, int nX, int nY, List<Element> lstText, boolean bDetails)
{
    Element tlg = m_svgGenerator.getTopLevelGroup();

     // Totally unable to get "use" to appear inside g, so need to use x, y co-ords relative to Origin, not to this race origin
    Element g = m_svgGenerator.getDOMFactory().createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "g");

    if (!"".equals(strJockeyColours))
    {
        Element use = m_svgGenerator.getDOMFactory().createElementNS(SVGFactoryUtils.SVG_NAMESPACE, "use");
        use.setAttributeNS(SVGFactoryUtils.XLINK_NAMESPACE, "xlink:href", "#" + strJockeyColours); 
        use.setAttributeNS(null, "x", String.valueOf(nX));
        use.setAttributeNS(null, "y", String.valueOf(nY));

        if (bDetails)   // are details shown behind image?
            setElementMouseOver(use, "colours_" + strId, 1);

        g.appendChild(use);
    }
    if (lstText != null)
    {
        for(int i = 0; i < lstText.size(); i++)
        {
            g.appendChild(lstText.get(i));
        }
    }
    tlg.appendChild(g);
    m_svgGenerator.setTopLevelGroup(tlg);
}

private static void setElementMouseOver(Element element, String strId, int nMask)
{
    element.setAttributeNS(null, "id", strId);
    element.setAttributeNS(null, "onmouseover", "ShowTooltip(evt, '" + strId + "', " + nMask + ")");
    element.setAttributeNS(null, "onmouseout", "HideTooltip(evt, '" + strId + "')");
}
public void drawColumnHeader(CareerDefinition.CareerColumn column, boolean bTop)
{
    int nFontSize = 75;
    String strColumn = column.getContent();
    if (!"".equals(strColumn))
    {
        AttributedString s = new AttributedString(strColumn);
        s.addAttribute(TextAttribute.FAMILY, OUTPUT_FONT);
        s.addAttribute(TextAttribute.SIZE, nFontSize);
        s.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

        s.addAttribute(TextAttribute.FOREGROUND, sm_colColumnHeaders);

       Font font = new Font(OUTPUT_FONT_NAME, Font.BOLD, nFontSize);
       FontMetrics metrics = m_svgGenerator.getFontMetrics(font);
       int nTextWidth = metrics.stringWidth(strColumn);

       m_svgGenerator.drawString(s.getIterator(), (m_career.isMeeting() ? SVG_MERO_123_MEETING_ORIGIN_X : SVG_MERO_123_ORIGIN_X) + column.getOffset() + (column.getCellWidth() - nTextWidth)/2, (bTop ? -SVG_MERO_MONTHNAME_OFFSET : SVG_MERO_MONTHNAME_OFFSET + 50));
    }
}
public void drawColoursCell(String strJockeyColours, String strName, int nXOffset, int nYOffset, int nWidth)
{
    // need to express scale depending on dimensions of cell
    ENERacingColours colours = ENERacingColoursFactory.createColours(m_statement, "en", strJockeyColours);

    meroService.addJockeySilks(m_svgGenerator, m_ctx, colours, "COLOURS", m_strLanguage, 3.8, String.valueOf(++m_nReferenceCount), m_hmDefinitions, "white");        // Careers need white background

    displayMeroReference("colours1", "COLOURS", nXOffset - 1510, nYOffset - 870, null, false);

    displayOldHorseName(m_svgGenerator, strName, SVG_MERO_123_WIDTH, 160, SVG_MERO_123_WIDTH - 15 - 500, 0 + SVG_MERO_123_HEIGHT + m_nHeaderHeight - 5 + 900);
    
    // this appears to the left of the main table so adjust viewbox accordingly
    m_nViewBoxXoffset = -450;
}
public void drawPedigreeCell(CareerDefinition.Pedigree pedigree, int nXOffset, int nYOffset, int nWidth)
{
    Color saveColor = m_svgGenerator.getColor();
    m_svgGenerator.setColor(Color.BLACK);  
    m_svgGenerator.drawRect(SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET, 0, 6 * SVG_MERO_123_WIDTH + SVG_MERO_123_WIDTH_EXTRA, SVG_MERO_123_HEIGHT + m_nHeaderHeight);
    m_svgGenerator.setColor(saveColor);

    String strHorse = pedigree.getGeneration0();    
    String[] astr1G = pedigree.getGeneration1();
    String[] astr2G = pedigree.getGeneration2();
    String[] astr3G = pedigree.getGeneration3();

    nXOffset += 30;
    
    drawPedigreeHorse(strHorse, nXOffset + 30, nYOffset + 120, 28);
        
    //for(int i = 0; i < astrFonts1.length; i++)
    //{
    AttributedString s1 = new AttributedString("{");
    s1.addAttribute(TextAttribute.FAMILY, "MS PMincho");
    s1.addAttribute(TextAttribute.SIZE, 400);
    s1.addAttribute(TextAttribute.WEIGHT, 0.001);        // TextAttribute.WEIGHT_EXTRA_LIGHT
    s1.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
    m_svgGenerator.drawString(s1.getIterator(), nXOffset + 300, nYOffset + 270);
    s1.addAttribute(TextAttribute.SIZE, 220);
    m_svgGenerator.drawString(s1.getIterator(), nXOffset + 630, nYOffset + 300);
    m_svgGenerator.drawString(s1.getIterator(), nXOffset + 630, nYOffset + 100);
    s1.addAttribute(TextAttribute.SIZE, 100);
    m_svgGenerator.drawString(s1.getIterator(), nXOffset + 910, nYOffset + 310);
    m_svgGenerator.drawString(s1.getIterator(), nXOffset + 910, nYOffset + 205);
    m_svgGenerator.drawString(s1.getIterator(), nXOffset + 910, nYOffset + 100);
    m_svgGenerator.drawString(s1.getIterator(), nXOffset + 910, nYOffset + -5);
    //m_svgGenerator.drawString(i + " " + astrFonts1[i], nXOffset + 300 + (i * 300), nYOffset);
    //}
    drawPedigreeHorse(astr1G[0], nXOffset + 410, nYOffset + 10 , 22);
    drawPedigreeHorse(astr1G[1], nXOffset + 410, nYOffset + 230, 22);

    drawPedigreeHorse(astr2G[0], nXOffset + 700, nYOffset - 45, 22);
    drawPedigreeHorse(astr2G[1], nXOffset + 700, nYOffset + 70, 22);
    drawPedigreeHorse(astr2G[2], nXOffset + 700, nYOffset + 160, 22);
    drawPedigreeHorse(astr2G[3], nXOffset + 700, nYOffset + 280, 22);
    
    drawPedigreeHorse(astr3G[0], nXOffset + 950, nYOffset - 60, 20); // 70
    drawPedigreeHorse(astr3G[1], nXOffset + 950, nYOffset - 5, 20);
    drawPedigreeHorse(astr3G[2], nXOffset + 950, nYOffset + 45, 20); // 40
    drawPedigreeHorse(astr3G[3], nXOffset + 950, nYOffset + 100, 20); 
    drawPedigreeHorse(astr3G[4], nXOffset + 950, nYOffset + 150, 20); // 40
    drawPedigreeHorse(astr3G[5], nXOffset + 950, nYOffset + 205, 20); 
    drawPedigreeHorse(astr3G[6], nXOffset + 950, nYOffset + 255, 20); // 40
    drawPedigreeHorse(astr3G[7], nXOffset + 950, nYOffset + 310, 20); // 310
}
public void drawPedigreeHorse(String strHorse, int nXOffset, int nYOffset, int nFontSize)
{
    String[] astrHorse = strHorse.split("\\|");
    AttributedString s = new AttributedString(astrHorse[0]);
    s.addAttribute(TextAttribute.FAMILY, OUTPUT_FONT);
    s.addAttribute(TextAttribute.SIZE, nFontSize);
    s.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD );
    s.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
    m_svgGenerator.drawString(s.getIterator(), nXOffset, nYOffset);
    if (astrHorse.length > 1)
    {
        s = new AttributedString("(b 1996)");
        s.addAttribute(TextAttribute.FAMILY, OUTPUT_FONT);
        s.addAttribute(TextAttribute.SIZE, nFontSize - 5);
        s.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
        s.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
        m_svgGenerator.drawString(s.getIterator(), nXOffset, nYOffset + nFontSize);
    }
 }
public void drawLegend(String strCell, int nXOffset, int nYOffset, int nWidth)
{
    boolean bFlat = (strCell.indexOf("FLAT") >= 0);
    Element tlg = m_svgGenerator.getTopLevelGroup();
    Element g = m_svgGenerator.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");

    int nLegendWidth = 350;
    int nLegendHeight = 50;
    nXOffset = nXOffset + 10;
    nYOffset = nYOffset - 90;

    g = drawLegendTitle(g, nXOffset, nYOffset + 240, nLegendWidth, nLegendHeight);
    g = bFlat ? drawFlatLegend(g, nXOffset + 300, nYOffset, nLegendWidth, nLegendHeight) : drawJumpLegend(g, nXOffset + 300, nYOffset, nLegendWidth, nLegendHeight);
    g = drawGroupLegend(g, nXOffset + 750, nYOffset, nLegendWidth, nLegendHeight, bFlat);
  
    tlg.appendChild(g);
    m_svgGenerator.setTopLevelGroup(tlg);
}
    private Element drawLegendTitle(Element g, int nXOffset, int nYOffset, int nLegendWidth, int nLegendHeight)
    {
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), "LEGEND", 
                    nXOffset, 
                    nYOffset, 
                    "black", 60, "start", true));
        
        return g;
    }
    private Element drawGroupLegend(Element g, int nXOffset, int nYOffset, int nLegendWidth, int nLegendHeight, boolean bFlat)
    {
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), "Race Status", 
                    nXOffset + (nLegendWidth/2), 
                    nYOffset + 40, 
                    "black", 50, "middle", true));
        nYOffset += 70;
        g.appendChild(SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), "G1", nXOffset, nYOffset, nLegendWidth, nLegendHeight, "#22FE3C", true));
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), (bFlat ? "Group" : "Grade") + " One", 
                    nXOffset + (nLegendWidth/2), 
                    nYOffset + 40, 
                    "black", 40, "middle", true));
        g.appendChild(SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), "G2", nXOffset, nYOffset + (nLegendHeight + 10), nLegendWidth, nLegendHeight, "#F8F45D", true));
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), (bFlat ? "Group" : "Grade") + " Two", 
                    nXOffset + (nLegendWidth/2), 
                    nYOffset + (nLegendHeight + 10) + 40, 
                    "black", 40, "middle", true));
        g.appendChild(SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), "G3", nXOffset, nYOffset + (2*(nLegendHeight + 10)), nLegendWidth, nLegendHeight, "#FEB511", true));
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), (bFlat ? "Group" : "Grade") + " Three", 
                    nXOffset + (nLegendWidth/2), 
                    nYOffset + (2*(nLegendHeight + 10)) + 40, 
                    "black", 40, "middle", true));
        g.appendChild(SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), "G4", nXOffset, nYOffset + (3*(nLegendHeight + 10)), nLegendWidth, nLegendHeight, "#A2CAE3", true));
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), "Listed", 
                    nXOffset + (nLegendWidth/2), 
                    nYOffset + (3*(nLegendHeight + 10)) + 40, 
                    "black", 40, "middle", true));
        g.appendChild(SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), "G5", nXOffset, nYOffset + (4*(nLegendHeight + 10)), nLegendWidth, nLegendHeight, "#D3D3D3", true));
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), "Other", 
                    nXOffset + (nLegendWidth/2), 
                    nYOffset + (4*(nLegendHeight + 10)) + 40, 
                    "black", 40, "middle", true));
        
        return g;
    }
   private Element drawFlatLegend(Element g, int nXOffset, int nYOffset, int nLegendWidth, int nLegendHeight)
    {
      g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), "Race Type", 
                    nXOffset + (nLegendWidth/2), 
                    nYOffset + 40, 
                    "black", 50, "middle", true));
      nYOffset += 70;
        // blue, 5D3360
       g.appendChild(SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), "F1", nXOffset, nYOffset + (1*(nLegendHeight + 10)), nLegendWidth, nLegendHeight, "#D3D3D3", true));
       g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), "Flat", 
                    nXOffset + (nLegendWidth/2), 
                    nYOffset + (1*(nLegendHeight + 10)) + 40, 
                    "blue", 40, "middle", true));
       g.appendChild(SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), "F2", nXOffset, nYOffset + (3*(nLegendHeight + 10)), nLegendWidth, nLegendHeight, "#D3D3D3", true));
       g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), "AW Flat", 
                    nXOffset + (nLegendWidth/2), 
                    nYOffset + (3*(nLegendHeight + 10)) + 40, 
                    "#5D3360", 40, "middle", true));
       
       return g;
    }
   private Element drawJumpLegend(Element g, int nXOffset, int nYOffset, int nLegendWidth, int nLegendHeight)
    {
      g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), "Race Type", 
                    nXOffset + (nLegendWidth/2), 
                    nYOffset + 40, 
                    "black", 50, "middle", true));
      nYOffset += 70;
        // blue, 5D3360
       g.appendChild(SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), "F1", nXOffset, nYOffset + (0*(nLegendHeight + 10)), nLegendWidth, nLegendHeight, "#D3D3D3", true));
       g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), "NH Flat", 
                    nXOffset + (nLegendWidth/2), 
                    nYOffset + (0*(nLegendHeight + 10)) + 40, 
                    "green", 40, "middle", true));
       g.appendChild(SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), "Hu", nXOffset, nYOffset + (2*(nLegendHeight + 10)), nLegendWidth, nLegendHeight, "#D3D3D3", true));
       g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), "Hurdle", 
                    nXOffset + (nLegendWidth/2), 
                    nYOffset + (2*(nLegendHeight + 10)) + 40, 
                    "#603311", 40, "middle", true));
       g.appendChild(SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), "Ch", nXOffset, nYOffset + (4*(nLegendHeight + 10)), nLegendWidth, nLegendHeight, "#D3D3D3", true));
       g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), "Chase", 
                    nXOffset + (nLegendWidth/2), 
                    nYOffset + (4*(nLegendHeight + 10)) + 40, 
                    "black", 40, "middle", true));
       
       return g;
    }
public void drawTextCell(CareerDefinition.CareerRow.CareerCell cell, int nXOffset, int nYOffset, int nWidth)
{
    String strContent = cell.getContent();
    String strFontName = cell.getFontName();
    if (strFontName == null)
        strFontName = OUTPUT_FONT_NAME;
    int nFontSize = cell.getFontSize();
    if (nFontSize == 0)
        nFontSize = 30;
    
   Font font = new Font(strFontName, Font.BOLD, nFontSize);
   FontMetrics metrics = m_svgGenerator.getFontMetrics(font);
   int nTextWidth = metrics.stringWidth(strContent);

   if (strContent.indexOf("|") >= 0)   // assumed to be fully formatted
   {
       drawMultiLineText(m_svgGenerator, cell, nXOffset, nYOffset, nWidth, true);
   }
   else if (nTextWidth < nWidth)
   {
        AttributedString s = new AttributedString(strContent);
        s.addAttribute(TextAttribute.FAMILY, OUTPUT_FONT);
        s.addAttribute(TextAttribute.SIZE, 30);
        s.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

        s.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);

        nXOffset = nXOffset + (nWidth - nTextWidth)/2;       // centred
        m_svgGenerator.drawString(s.getIterator(), nXOffset, nYOffset);
   }
   else
   {
       displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.BOLD, strContent, nXOffset, nYOffset, 30, 30, nWidth, false);
   }
    // centred if only one row, wrap around if more
}
public void drawTextHeader1(String strCellTitle, double dColumns)
{
        Element tlg = m_svgGenerator.getTopLevelGroup();
        Element g = m_svgGenerator.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");

        //int nX = (int)((dColumns-1) * m_nCellWidth) + SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET;    // to do: adjust value
        int nX = ((int)m_dxTranslate);
        int nY = ((int)m_dyTranslate);
        
        // no background rectangle
        
        String strTextColour = "blue";

        int nTitleOffset = SVG_MERO_123_WIDTH/2;       // position not displayed in header, using middle display
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strCellTitle, 
                nX + nTitleOffset, 
                nY + m_nHeaderHeight - 10, 
                strTextColour, 60, "middle", false));
    
        tlg.appendChild(g);
        m_svgGenerator.setTopLevelGroup(tlg);
}
public void drawRaceHeader1(String strCellTitle, SmartformBasicRace race, SmartformColoursRunner runner)
{
    // display year of race only
 
        Element tlg = m_svgGenerator.getTopLevelGroup();
        Element g = m_svgGenerator.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");

        //int nX = (int)((dColumns-1) * m_nCellWidth) + SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET;    // to do: adjust value
        int nX = ((int)m_dxTranslate);
        int nY = ((int)m_dyTranslate);
        
        // no background rectangle
        
        String strTextColour = getTextColour(race.getRaceType());

        int nTitleOffset = SVG_MERO_123_WIDTH/2;       // position not displayed in header, using middle display
     
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strCellTitle, 
                nX + nTitleOffset, 
                nY + m_nHeaderHeight - 10, 
                strTextColour, 60, "middle", false));
        
        tlg.appendChild(g);
        m_svgGenerator.setTopLevelGroup(tlg);
}
public void drawRaceHeader2(String strCellTitle, SmartformBasicRace race, SmartformColoursRunner runner)
{
    // cut down version for displaying two horses
       Color saveColor = m_svgGenerator.getColor();
        int nGroupRace = race.getGroupRace();
        
        String strFullTitle = race.getTitle();
        if ((nGroupRace == 0) && (strFullTitle.toLowerCase().indexOf("listed") > 0))    // Listed races indicated by Group 4
            nGroupRace = 4;
        Element tlg = m_svgGenerator.getTopLevelGroup();
        Element g = m_svgGenerator.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");

        //int nX = (int)((dColumns-1) * m_nCellWidth) + SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET;    // to do: adjust value
        int nX = ((int)m_dxTranslate);
        int nY = ((int)m_dyTranslate);

        g.appendChild(SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), null, 
                nX, 
                nY, 
                2 * SVG_MERO_123_WIDTH,
                m_nHeaderHeight,
                sm_astrBackgroundColours[nGroupRace], false));

        String strTextColour = getTextColour(race.getRaceType());
        
        // 1. Race Title
        String strRaceTitle = strCellTitle;
        if ("".equals(strCellTitle))
           strRaceTitle = race.getAbbreviatedTitle();
        
        int nTitleOffset = (2 * SVG_MERO_123_WIDTH)/2;       // position not displayed in header, using middle display
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strRaceTitle, 
                nX + nTitleOffset, 
                nY + m_nHeaderHeight - 40, 
                strTextColour, 30, "middle", true));

        // 2. Course, distance etc
        String strRaceDetails = race.getCourse().replace("_", " ") + ", " + race.getFormattedMeetingDate("MMMM d yyyy");
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strRaceDetails, 
                nX + nTitleOffset, 
                nY + m_nHeaderHeight - 10, 
                strTextColour, 24, "middle", false));
        
        tlg.appendChild(g);
        m_svgGenerator.setTopLevelGroup(tlg);
}
public void draw3RowRaceHeader(String strCellTitle, SmartformBasicRace race, SmartformColoursRunner runner, String strTextColour)
{
        // A Title background
        Color saveColor = m_svgGenerator.getColor();
        int nGroupRace = race.getGroupRace();
        
        String strFullTitle = race.getTitle();
        if ((nGroupRace == 0) && (strFullTitle.toLowerCase().indexOf("listed") > 0))    // Listed races indicated by Group 4
            nGroupRace = 4;

        String strPosition = "";
        boolean bPlaced = true;
        if (runner != null)
        {
            strPosition = SmartformHistoricRunner.convertFinishPositionSingle(runner.getFinishPositionString());
 
            if (!"1".equals(strPosition) && !"2".equals(strPosition) && !"3".equals(strPosition))
            {
                // Only need runner details and mouseover in herader if horse not placed
                bPlaced = false;
                if (m_bText)
                    displayHeaderRunnerDetails(m_svgGenerator, strPosition, race, runner);
            }
         }

        Element tlg = m_svgGenerator.getTopLevelGroup();
        Element g = m_svgGenerator.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");

        if (m_bText && !bPlaced)
            setElementMouseOver(g, "race_" + race.getRaceId(), 2); 

        int nX = (int)m_dxTranslate + SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET;
        int nY = (int)m_dyTranslate;
        g.appendChild(      // header background rectangle
                SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), null,
                nX, 
                nY, 
                3 * SVG_MERO_123_WIDTH, 
                m_nHeaderHeight, 
                sm_astrBackgroundColours[nGroupRace], true));

        // 1. Position
        g.appendChild(createPositionTextElement(m_svgGenerator, strPosition, 
                nX + 40,         // middle alignment
                nY + m_nHeaderHeight - 10, 
                80, 
                strTextColour, true));

        // Line 1: Race Title - move up to above position and use full width
        String strRaceTitle = strCellTitle;
        if ("".equals(strCellTitle))
           strRaceTitle = race.getAbbreviatedTitle();
        
       g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strRaceTitle, 
                nX + (3 * SVG_MERO_123_WIDTH)/2, 
                nY + m_nHeaderHeight - 75, 
                strTextColour, 
                40, "middle", true));
 
        // Line 2:Course             date
        String strCourse = race.getCourse().replace("_", " ").replace("Epsom Downs", "Epsom");
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strCourse, 
                nX + 100, 
                nY + m_nHeaderHeight - 40, 
                strTextColour, 
                35, "start", false));

        String strDate = race.getFormattedMeetingDate("MMMM d yyyy");
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strDate, 
                nX + (3 * SVG_MERO_123_WIDTH) - 5, 
                nY + m_nHeaderHeight - 40, 
                strTextColour, 
                35, "end", false));
 
        // Line 3  Distance     Going      #Runners
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), race.getFormattedDistance(), 
                nX + 60, 
                nY + m_nHeaderHeight - 10, 
                strTextColour, 
                32, "start", false));
        
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), race.getShortGoing(), 
                nX + (3 * SVG_MERO_123_WIDTH)/2, 
                nY + m_nHeaderHeight - 10, 
                strTextColour, 
                32, "middle", false));
 
        int nRunners = race.getNrRunners();
        if (nRunners > 0)
        {
            String strRunners = nRunners + " Ran";
           
            g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strRunners, 
                nX + (3 * SVG_MERO_123_WIDTH) - 5, 
                nY + m_nHeaderHeight - 10, 
                strTextColour, 
                32, "end", false));
         }

        tlg.appendChild(g);
        m_svgGenerator.setTopLevelGroup(tlg);
}
public void drawMeetingRaceHeader(String strCellTitle, SmartformBasicRace race, String strTextColour)
{
    
       // A Title background
        Color saveColor = m_svgGenerator.getColor();
        int nGroupRace = race.getGroupRace();
        
        String strFullTitle = race.getTitle();
        if ((nGroupRace == 0) && (strFullTitle.toLowerCase().indexOf("listed") > 0))    // Listed races indicated by Group 4
            nGroupRace = 4;
        Element tlg = m_svgGenerator.getTopLevelGroup();
        Element g = m_svgGenerator.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");

        int nX = (int)m_dxTranslate + SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET;
        int nY = (int)m_dyRowTranslate;     // ((int)m_dyTranslate);
        int nTitleOffset = 10;       // position  displayed far left in header
 
        g.appendChild(
                SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), null,
                nX, 
                nY, 
                3 * SVG_MERO_123_WIDTH,     
                m_nHeaderHeight, 
                sm_astrBackgroundColours[nGroupRace], false));

                // 2. Race Title
        String strRaceTitle = strCellTitle;
        if ("".equals(strCellTitle))
           strRaceTitle = race.getAbbreviatedTitle();
        
        int nFontSize = 40;
        if (strRaceTitle.length() > 25)
            nFontSize = 36;
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strRaceTitle, 
                nX + nTitleOffset,          // nX + nTitleOffset if "start"
                nY + m_nHeaderHeight - 10, 
                strTextColour, 
                nFontSize, "start", true));

        String strRaceDetails =  race.getLongFormattedDistance(false);
        if ((strRaceTitle.length() > 22) && (strRaceDetails.length() > 12))
            strRaceDetails = race.getFormattedDistance();
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strRaceDetails, 
                nX + (3 * SVG_MERO_123_WIDTH) - 10, 
                nY + m_nHeaderHeight - 10, 
                strTextColour, 
                24, "end", false));
        tlg.appendChild(g);
        
        m_svgGenerator.setTopLevelGroup(tlg);

}
public void draw2RowRaceHeader(String strCellTitle, SmartformBasicRace race, SmartformColoursRunner runner, String strTextColour)
{
        // A Title background
        Color saveColor = m_svgGenerator.getColor();
        int nGroupRace = race.getGroupRace();
        
        String strFullTitle = race.getTitle();
        if ((nGroupRace == 0) && (strFullTitle.toLowerCase().indexOf("listed") > 0))    // Listed races indicated by Group 4
            nGroupRace = 4;

        String strPosition = "";
        boolean bPlaced = true;
        if (runner != null)
        {
            strPosition = SmartformHistoricRunner.convertFinishPositionSingle(runner.getFinishPositionString());
 
            if (!"1".equals(strPosition) && !"2".equals(strPosition) && !"3".equals(strPosition))
            {
               // Only need runner details and mouseover in header if horse not placed
               bPlaced = false;
               if (m_bText)
                    displayHeaderRunnerDetails(m_svgGenerator, strPosition, race, runner);
            }
         }

        Element tlg = m_svgGenerator.getTopLevelGroup();
        Element g = m_svgGenerator.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");

        int nX = (int)m_dxTranslate + SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET;
        int nY = (int)m_dyRowTranslate;     // ((int)m_dyTranslate);
        
        int nTitleOffset = 5;
        if (strPosition.length() == 1)
            nTitleOffset = 70;       // position  displayed far left in header
        else if (strPosition.length() == 2)
            nTitleOffset = 78;      
        
        int nTitleRowHeight = 40;    // height of 1st row
        Element g3 = g;     // The element to be used for writing 3rd block (line 2)
        if (bPlaced)
        {
            // do as single block
            g.appendChild(
                SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), null,
                nX, 
                nY, 
                3 * SVG_MERO_123_WIDTH,     
                m_nHeaderHeight, 
                sm_astrBackgroundColours[nGroupRace], false));
        }
        else
        {
            // split into 3 blocks with just the 2nd line being used for mouseover
            g.appendChild(
                SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), null,
                nX, 
                nY, 
                nTitleOffset + 1,         // add 1 for slight overlap
                m_nHeaderHeight, 
                sm_astrBackgroundColours[nGroupRace], false));
            g.appendChild(
                SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), null,
                nX + nTitleOffset, 
                nY, 
                3 * SVG_MERO_123_WIDTH - nTitleOffset, 
                nTitleRowHeight + 1,        // add 1 for slight overlap
                sm_astrBackgroundColours[nGroupRace], false));
            Element g1 = m_svgGenerator.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");

            g1.appendChild(
                SVGFactoryUtils.createRectElement(m_svgGenerator.getDOMFactory(), null,
                nX + nTitleOffset, 
                nY + nTitleRowHeight, 
                3 * SVG_MERO_123_WIDTH - nTitleOffset, 
                m_nHeaderHeight - nTitleRowHeight, 
                sm_astrBackgroundColours[nGroupRace], false));      // sm_astrBackgroundColours[nGroupRace]
            if (m_bText)
                setElementMouseOver(g1, "race_" + race.getRaceId(), 2); 
            g.appendChild(g1);
            g3 = g1;    // use g1 as element for 2nd line
        }

        // 1. Position
        g.appendChild(createPositionTextElement(m_svgGenerator, strPosition, 
                nX + 35,         // middle alignment
                nY + m_nHeaderHeight - 10, 
                80, strTextColour, true));

        // 2. Race Title
         String strRaceTitle = strCellTitle;
        if ("".equals(strCellTitle))
           strRaceTitle = race.getAbbreviatedTitle();
        
        int nFontSize = 40;
        if (strRaceTitle.length() > 35)
            nFontSize = 32;
        else if (strRaceTitle.length() > 28)
            nFontSize = 36;
        g.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strRaceTitle, 
                nX + (nTitleOffset - ((strRaceTitle.length() > 30) ? 5 : 15)) + ((3 * SVG_MERO_123_WIDTH - (nTitleOffset - 15))/2),          // nX + nTitleOffset if "start"
                nY + m_nHeaderHeight - nTitleRowHeight, 
                strTextColour, 
                nFontSize, "middle", true));

        // 3. Course, distance etc
        // for unplaced horses this line will mouse over to show details underneath
        String strCourse = race.getCourse().replace("_", " ").replace("Epsom Downs", "Epsom");
        g3.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strCourse, 
                nX + nTitleOffset, 
                nY + 0 + m_nHeaderHeight - 10, 
                strTextColour, 
                strCourse.length() >= 10 ? 28 : 32, "start", false));

        String strDate = race.getFormattedMeetingDate("MMMM d");
        String strRaceDetails =  race.getShortFormattedDistance(false) + ", " + race.getShortGoing();
        int nDistanceOffset = nX + nTitleOffset + ((3 * SVG_MERO_123_WIDTH - nTitleOffset)/2);
        //if ((strDate.indexOf("September") >= 0) || (strRaceDetails.length() >= 15))
        nDistanceOffset =  nDistanceOffset - 20;
        
        g3.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strRaceDetails, 
                nDistanceOffset, 
                nY + 0 + m_nHeaderHeight - 10, 
                strTextColour, 
                24, "middle", false));

        g3.appendChild(SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strDate, 
                nX + (3 * SVG_MERO_123_WIDTH) - 5, 
                nY + 0 + m_nHeaderHeight - 10, 
                strTextColour, 
                34, "end", false));

        tlg.appendChild(g);
        m_svgGenerator.setTopLevelGroup(tlg);
}
public void displayRunnerDetails(SmartformBasicRace race, SmartformColoursRunner runner, int nPosition)
{
    boolean bHandicap = race.isHandicap();
    String strRaceType = race.getRaceType();
            
    int nFullLineHeight = 12;   // between different sections
    int nPostLineHeight = 3;   // between different sections
    int nYOffset = m_nHeaderHeight + 30;
    int nXOffset = SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET;

    String strTrainer = runner.getTrainerName().trim();
    if (!"".equals(strTrainer))
    {
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.PLAIN, "Trainer:", nXOffset, nYOffset, 10, 10, SVG_MERO_123_WIDTH - 2, false);
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.BOLD, strTrainer, nXOffset, nYOffset, 14, 12, SVG_MERO_123_WIDTH - 2, true);
        //drawDescriptionSeparator(m_svgGenerator, nYOffset + nPostLineHeight);
        nYOffset += nFullLineHeight;
    }
    String strJockey = runner.getJockeyName().trim();
    if (!"".equals(strJockey))
    {
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.PLAIN, "Jockey:", nXOffset, nYOffset, 10, 10, SVG_MERO_123_WIDTH - 2, false);
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.BOLD, strJockey, nXOffset, nYOffset, 14, 12, SVG_MERO_123_WIDTH - 2, true);
        //drawDescriptionSeparator(m_svgGenerator, nYOffset + nPostLineHeight);
        nYOffset += nFullLineHeight;
    }
    String strOwner = runner.getOwnerName().trim();
    if (!"".equals(strOwner))
    {
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.PLAIN, "Owner:", nXOffset, nYOffset, 10, 10, SVG_MERO_123_WIDTH - 2, false);
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.BOLD, strOwner, nXOffset, nYOffset, 14, 12, SVG_MERO_123_WIDTH - 2, true);
        //drawDescriptionSeparator(m_svgGenerator, nYOffset + nPostLineHeight);
        nYOffset += nFullLineHeight;
    }
    String strColours = runner.getJockeyColours().trim();
    if (!"".equals(strColours))
    {
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.PLAIN, "Colours:", nXOffset, nYOffset, 10, 10, SVG_MERO_123_WIDTH - 2, false);
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.BOLD, strColours, nXOffset, nYOffset, 12, 12, SVG_MERO_123_WIDTH - 2, true);
        //drawDescriptionSeparator(m_svgGenerator, nYOffset + nPostLineHeight);
        nYOffset += nFullLineHeight;
    }      
    String strSP = runner.getFullStartingPrice().trim();
    if (!"".equals(strSP))
    {
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.PLAIN, "SP:", nXOffset, nYOffset, 10, 10, SVG_MERO_123_WIDTH - 2, false);
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.BOLD, strSP, nXOffset + 50, nYOffset + 2, 14, 12, SVG_MERO_123_WIDTH - 2, false);    // on same line
        //drawDescriptionSeparator(m_svgGenerator, nYOffset + nPostLineHeight);
        nYOffset += nFullLineHeight;
    }      
    String strInRaceRunning = runner.getInRaceComment().trim();
    if (!"".equals(strInRaceRunning))
    {
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.PLAIN, "Comments:", nXOffset, nYOffset, 10, 10, SVG_MERO_123_WIDTH - 2, false);
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.BOLD, strInRaceRunning, nXOffset, nYOffset, 12, 12, SVG_MERO_123_WIDTH - 2, true);
        //drawDescriptionSeparator(m_svgGenerator, nYOffset + nPostLineHeight);
        nYOffset += nFullLineHeight;
    }      
    int nWeightPounds = runner.getWeightPounds();
    if (bHandicap)
    {
        if (nWeightPounds > 1)
        {
            nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.PLAIN, "Handicap:", nXOffset, nYOffset, 10, 10, SVG_MERO_123_WIDTH - 2, false);
            nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.BOLD, SmartformRunner.getWeightString(runner.getWeightPounds()) + " (" + runner.getOfficialRating() + ")", nXOffset, nYOffset, 12, 12, SVG_MERO_123_WIDTH - 2, true);
            //drawDescriptionSeparator(m_svgGenerator, nYOffset + nPostLineHeight);
            nYOffset += nFullLineHeight;
        }
    }     
    int nStall = runner.getStall();
    if (("Flat".equals(strRaceType) || "A_W_Flat".equals(strRaceType) || "All Weather Flat".equals(strRaceType)) && (nStall > 0))
    {
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.PLAIN, "Draw:", nXOffset, nYOffset, 10, 10, SVG_MERO_123_WIDTH - 2, false);
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.BOLD, String.valueOf(nStall), nXOffset + 50, nYOffset, 12, 12, SVG_MERO_123_WIDTH - 2, false);   // same line
        //drawDescriptionSeparator(m_svgGenerator, nYOffset + nPostLineHeight);
        nYOffset += nFullLineHeight;
    }      
}
public void displayHeaderRunnerDetails(SVGGraphics2D m_svgGenerator, String strPosition, SmartformBasicRace race, SmartformColoursRunner runner)
{
/*    if (!"".equals(strPosition))
    {
        AttributedString s = new AttributedString(strPosition);
        s.addAttribute(TextAttribute.FAMILY, OUTPUT_FONT);
        s.addAttribute(TextAttribute.SIZE, 80);
        s.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

        s.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);

        m_svgGenerator.drawString(s.getIterator(), SVG_MERO_123_WIDTH - ((strPosition.length() > 1) ? 18 : 7), m_nHeaderHeight);
    }  */
    boolean bHandicap = race.isHandicap();
    String strRaceType = race.getRaceType();
            
    int nFullLineHeight = 10;   // between different sections
    int nXOffset = SVG_MERO_123_WIDTH + 60;
    int nYOffset = (sm_b3RowHeader ? 15 : 50);  // if 3 rows then should mouse over the bottom 2 lines 
    
    String strRunnerDetails = "";
    String strDistanceBeaten = runner.getDistanceBehindWinnerString();
    if ((strDistanceBeaten != null) && !"".equals(strDistanceBeaten))
    {
        strRunnerDetails += "Beaten: " + strDistanceBeaten;
    }
    
    if (!"".equals(strRunnerDetails))
        strRunnerDetails += "  ";
    strRunnerDetails += "Jockey: " + runner.getJockeyName().trim();
    //strRunnerDetails += (", T:" + runner.getTrainerName().trim());
    //strRunnerDetails += (", O:" + runner.getOwnerName().trim());
    //strRunnerDetails += (", " + runner.getJockeyColours().trim());
    String strSP = runner.getStartingPrice().trim();
    if (!"".equals(strSP))
        strRunnerDetails += ("  SP: " + strSP);
    
    int nWeightPounds = runner.getWeightPounds();
    if (bHandicap && (nWeightPounds > 1))
    {
        strRunnerDetails += ("  Hcap: " + SmartformRunner.getWeightString(nWeightPounds) + " (" + runner.getOfficialRating() + ")");
    }      
    int nStall = runner.getStall();
    if (("Flat".equals(strRaceType) || "All Weather Flat".equals(strRaceType)) && (nStall > 0))
    {
        strRunnerDetails += ("  Draw: " + nStall);
    }      
    nYOffset += 5;
    nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.BOLD, strRunnerDetails, nXOffset, nYOffset, 16, 16, SVG_MERO_123_WIDTH * 3 - 70, false);
    nYOffset += nFullLineHeight;
 
    String strInRaceRunning = runner.getInRaceComment().trim();
    if (!"".equals(strInRaceRunning))
    {
        nYOffset = displayWraparoundDescription(m_svgGenerator, OUTPUT_FONT_NAME, Font.BOLD, strInRaceRunning, nXOffset, nYOffset, 12, 12, SVG_MERO_123_WIDTH * 3 - 70, false);
        //drawDescriptionSeparator(m_svgGenerator, nYOffset + nPostLineHeight);
        nYOffset += nFullLineHeight;
    }      
}
public void drawDescriptionSeparator(int nYOffset)
{
    
    Color saveColor = m_svgGenerator.getColor();
    m_svgGenerator.setColor(Color.BLUE);
    m_svgGenerator.drawLine(SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET + 2, nYOffset, 
                            2 * SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET - 2, nYOffset);
    
    m_svgGenerator.setColor(saveColor); 
}
public static int displayWraparoundDescription(SVGGraphics2D g2d, String strFontName, int nFontStyle, String strContent, int nXOffset, int nYOffset, 
            int nMaxFontSize, int nMinFontSize, int nBoxWidth, boolean bAppendLineHeight)
{
     // specify max and min font

    if ("".equals(strContent))
        return nYOffset;
    
    // if won't all fit in one line even with smallest font then wrap around (with which font?)
    String strFormattedContent = strContent;
    int nFontSize = -1;
    if (nMaxFontSize > nMinFontSize)
        nFontSize = calculateFontSize(g2d, strFontName, nFontStyle, strContent, nBoxWidth, nMaxFontSize, nMinFontSize);
    if (nFontSize < 0)
    {
        nFontSize = recalculateFontSize(g2d, strFontName, nFontStyle, strContent, nBoxWidth, nMaxFontSize, nMinFontSize);
        int nWidthChars = calculateCharsWidth(g2d, strFontName, nFontStyle, nBoxWidth, nFontSize);

        strFormattedContent = WordUtils.wrap(strContent, nWidthChars, "|", true);
    }
    int nLineHeight = calculateLineHeight(g2d, strFontName, nFontStyle, nFontSize);
    if(bAppendLineHeight)
    {
        nYOffset +=  nLineHeight;
    }

    nYOffset = drawMultiLineText(g2d, strFontName, nFontStyle, nFontSize, strFormattedContent, nXOffset, nYOffset, nBoxWidth);
    
/*
    String[] astrWords = strContent.split(" ");
    String strCurrent = "";
    int nLines = 0;
    for(int i = 0; i < astrWords.length; i++)
    {
        if (strCurrent.length() + astrWords[i].length() >= nWidthChars)
        {
            AttributedString s = new AttributedString(strCurrent);
            s.addAttributes(mapAttributes, 0, strCurrent.length());
            if (nLines > 0)
                nYOffset += nHalfLineHeight;
            g2d.drawString(s.getIterator(), SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET + ((nLines > 0) ? 3 : 1), 
                    0 + SVG_MERO_123_HEADER + nYOffset);
            strCurrent = astrWords[i];
            nLines++;
        }
        else
        {
            if (!"".equals(strCurrent))
                strCurrent += " ";
            strCurrent += astrWords[i];
        }
    }
    if (!"".equals(strCurrent))
    {
        AttributedString s = new AttributedString(strCurrent);
        s.addAttributes(mapAttributes, 0, strCurrent.length());
        if (nLines > 0)
            nYOffset += nHalfLineHeight;
        g2d.drawString(s.getIterator(), SVG_MERO_123_WIDTH - SVG_MERO_X_OFFSET + ((nLines > 0) ? 3 : 1), 
                0 + SVG_MERO_123_HEADER + nYOffset);
    }
*/    
    return nYOffset;
}
public static int drawMultiLineText(SVGGraphics2D g2d, CareerDefinition.CareerRow.CareerCell cell, int nXOffset, int nYOffset, int nBoxWidth, boolean bCalculate)
{
    String strContent = cell.getContent();
    String[] astrWords = strContent.split("\\|");
    String strFontName = cell.getFontName();
    if (strFontName == null)
        strFontName = OUTPUT_FONT_NAME;
    int nFontSize = cell.getFontSize();
    // to do: add italic etc styles
    int nFontStyle = "bold".equalsIgnoreCase(cell.getFontStyle()) ? Font.BOLD : Font.PLAIN;
    if (nFontSize == 0)
    {
        nFontSize = 30;
    
        // calculate biggest font
        if (bCalculate)
        {
            int nMaxLength = 0;
            String strMaxContent="";
            for(int i = 0; i < astrWords.length; i++)
            {
                if (astrWords[i].length() > nMaxLength)
                {
                    strMaxContent = astrWords[i];
                    nMaxLength = astrWords[i].length();
                }
            }
            int nCalculatedFontSize = calculateFontSize(g2d, strFontName, nFontStyle, strMaxContent, nBoxWidth, 100, nFontSize);
            if (nCalculatedFontSize != -1)
                nFontSize = nCalculatedFontSize;
        }
    }
    
    return drawMultiLineText(g2d, strFontName, nFontStyle, nFontSize, strContent, nXOffset, nYOffset, nBoxWidth);
}
public static int drawMultiLineText(SVGGraphics2D g2d, String strFontName, int nFontStyle, int nFontSize, String strContent, int nXOffset, int nYOffset, int nBoxWidth)
{
    String[] astrWords = strContent.split("\\|");

    Map<TextAttribute, Object> mapAttributes = new HashMap<TextAttribute, Object>();
    mapAttributes.put(TextAttribute.FAMILY, strFontName);
    mapAttributes.put(TextAttribute.SIZE, nFontSize);
    mapAttributes.put(TextAttribute.WEIGHT, (nFontStyle == Font.BOLD) ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR );
    mapAttributes.put(TextAttribute.FOREGROUND, Color.BLACK);

    int nLineHeight = calculateLineHeight(g2d, strFontName, nFontStyle, nFontSize);

    for(int i = 0; i < astrWords.length; i++)
    {
        String strLine = astrWords[i];
        if (!"".equals(strLine))
        {
            AttributedString s = new AttributedString(strLine);
            s.addAttributes(mapAttributes, 0, strLine.length());
            if (i > 0)
                nYOffset += nLineHeight;
            g2d.drawString(s.getIterator(), nXOffset + ((i > 0) ? 3 : 1), nYOffset);    // indent subsequent lines
        }
    }
    
    return nYOffset;
}
public static int calculateLineHeight(SVGGraphics2D g2d, String strFontName, int nFontStyle, int nFontSize)
{
   Font font = new Font(strFontName, nFontStyle, nFontSize);
   FontMetrics metrics = g2d.getFontMetrics(font);
   return metrics.getAscent();
}
public static int calculateFontSize(SVGGraphics2D g2d, String strFontName, int nFontStyle, String strContent, int nMaxWidth, int nMaxFontSize, int nMinFontSize)
{
   Font saveFont = g2d.getFont();
   int nCalculatedFontSize = -1;
   
   for(int nFontSize = nMaxFontSize; nFontSize >= nMinFontSize; nFontSize--)
   {
        Font font = new Font(strFontName, nFontStyle, nFontSize);
        FontMetrics metrics = g2d.getFontMetrics(font);
        g2d.setFont(font);
        int nTextWidth = metrics.stringWidth(strContent);
        if (nTextWidth < nMaxWidth)
        {
            nCalculatedFontSize = nFontSize;
            break;
        }
   }
   
    g2d.setFont(saveFont);

    return nCalculatedFontSize;
}
public static int recalculateFontSize(SVGGraphics2D g2d, String strFontName, int nFontStyle, String strContent, int nMaxWidth, int nMaxFontSize, int nMinFontSize)
{
    // only called if know won't fit one line
   Font saveFont = g2d.getFont();
   int nCalculatedFontSize = nMinFontSize;
   
   for(int nFontSize = nMaxFontSize; nFontSize >= nMinFontSize; nFontSize--)
   {
        Font font = new Font(strFontName, nFontStyle, nMaxFontSize);
        FontMetrics metrics = g2d.getFontMetrics(font);
        g2d.setFont(font);
        int nTextWidth = metrics.stringWidth(strContent);
        if (nTextWidth < nMaxWidth * 1.5)       // wil it fit one 1.5 lines?
        {
            nCalculatedFontSize = nFontSize;
            break;
        }
   }
   
    g2d.setFont(saveFont);

    return nCalculatedFontSize; // min font size if bigger won't fit on two lines
}
public static int calculateCharsWidth(SVGGraphics2D g2d, String strFontName, int nFontStyle, int nMaxWidth, int nFontSize)
{
   Font saveFont = g2d.getFont();
   int nCalculatedCharWidth = -1;
   Font font = new Font(strFontName, nFontStyle, nFontSize);
   FontMetrics metrics = g2d.getFontMetrics(font);
   g2d.setFont(font);
   //String strFullString="aAbcdeEfghijkKlmnopPqrstuUvwxyzZabBcdefFghijklL";
   String strFullString="abcdefghijklmnopqrstuvwxyY, zabBcdefgG, hijkKlmnopqQrst, uUvwxyzZ, hijkKlmnopqQrst, uUvwxyzZ, hijkKlmnopqQrst, uUvwxyzZ";
    
    for (int i = 0; i < strFullString.length(); i++)
    {
        String strTestString = strFullString.substring(i);
        int nTextWidth = metrics.stringWidth(strTestString);
        if (nTextWidth < nMaxWidth)
        {
            nCalculatedCharWidth = strTestString.length() - 1;  // remove 1 to be sure
            break;
        }
    }
    
    g2d.setFont(saveFont);

    return nCalculatedCharWidth;
}
public static void displayOldHorseName(SVGGraphics2D g2d, String strName, int nWidth, int nFontSize, float x, float y)
{
    strName = strName.trim();
    AttributedString s = new AttributedString(strName);
    Font font = new Font("Courier", Font.BOLD, nFontSize);    // 22
    FontMetrics metrics = g2d.getFontMetrics(font);
     //s.addAttribute(TextAttribute.FONT, font);    // doesn't work!
    Font saveFont = g2d.getFont();
     g2d.setFont(font);
     s.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
    AttributedCharacterIterator styledText = s.getIterator();
    int nTextWidth = metrics.stringWidth(strName);
    g2d.drawString(styledText , x + (nWidth - nTextWidth)/2, y);
    
    g2d.setFont(saveFont); 
}
public static void displayHorseName(SVGGraphics2D g2d, String strName, int nWidth, float nX, float nY)
{
    strName = strName.trim();
  
        // Calculate width
        int nFontSize = calculateFontSize(g2d, OUTPUT_FONT_NAME, Font.PLAIN, strName, 230 - 5, 36, 20);   // 230 is max width   // from BOLD
 
        Element tlg = g2d.getTopLevelGroup();
        Element g = g2d.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");

        Element text = g2d.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "text");
        text.setAttributeNS(null, "x", String.valueOf((int) nX));
        text.setAttributeNS(null, "y", String.valueOf((int)nY));     
        String strStyle = "fill: black; stroke: black; font-name: courier; font-size: " + nFontSize + "px; text-anchor: middle;";   // font-weight: bold; 
        text.setAttributeNS(null, "style", strStyle);
        text.setTextContent(strName);
    
        g.appendChild(text);
        tlg.appendChild(g);
        g2d.setTopLevelGroup(tlg);
}
private static Element createPositionTextElement(SVGGraphics2D m_svgGenerator, String strPosition, int nX, int nY, int nFontSize, String strTextColour, boolean bCardinal)
{
       // convert to ordinal
        String strSuffix = "";
        if (bCardinal)
        {
            try
            {
                int nPosition = Integer.parseInt(strPosition);
                strSuffix = StringUtils.getOrdinalSuffix(nPosition);
            }
            catch(NumberFormatException e)
            {
                // no suffix
            }
        }
        Element text1;
        if (!"".equals(strSuffix))
        {
            text1 = SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), nX + ((strPosition.length() >= 2) ? 8 : 0), nY, "middle");
            // use tspan
            text1.appendChild(SVGFactoryUtils.createTSpanElement(m_svgGenerator.getDOMFactory(), strPosition, 
                 strTextColour, 
                nFontSize, true));
            Element tspan = SVGFactoryUtils.createTSpanElement(m_svgGenerator.getDOMFactory(), strSuffix, 
                 strTextColour, 
                28, true);     // suffix fontsize should depend on nFontSize (40 for 80)

           tspan.setAttributeNS(null, "dx", "-4");
           tspan.setAttributeNS(null, "dy", "-35");     // dY should depend on nFontSize (-35 for 80)
           text1.appendChild(tspan);
        }
        else
        {
            text1 = SVGFactoryUtils.createTextElement(m_svgGenerator.getDOMFactory(), strPosition,
                nX, 
                nY, 
                strTextColour, 
                80, "middle", true);
        }
    
        return text1;
}
private String getTextColour(String strRaceType)
{
        String strTextColour = "blue";
        if ("Hurdle".equals(strRaceType))
            strTextColour = "#603311";      // brown
        else if ("Chase".equals(strRaceType))
            strTextColour = "black";
        else if ("National Hunt Flat".equals(strRaceType))
            strTextColour = "green";   // #253101";  // dark green
        else if ("All Weather Flat".equals(strRaceType))
            strTextColour = "#5D3360";   // dark violet
    
        return strTextColour;
}
   public String getSVGContent() {
        return m_strSVGContent;
    }

    public Rectangle getViewBox() {
        return m_viewBox;
    } 
    private String convertPositionInBetting(int nPositionInBetting)
    {
      // "Favourite", "2nd Favourite", "3rd Favourite", "Mid-range", "Outsider"
        if (nPositionInBetting < 1)
            return "Unknown";
        else if (nPositionInBetting == 1)
            return sm_astrPositionInBetting[0];
        else if (nPositionInBetting == 2)
            return sm_astrPositionInBetting[1];
        else if (nPositionInBetting == 3)
            return sm_astrPositionInBetting[2];
        else if (nPositionInBetting < 10)
            return sm_astrPositionInBetting[3];
        else
            return sm_astrPositionInBetting[4];
    }
    private String convertStartingPrice(double dStartingPrice)
    {
        // "Odds-On", "2/1-", "3/1-", "6/1-", "10/1-", "16/1-", "20/1+"
        if (dStartingPrice < 0)
            return "Unknown";
        else if (dStartingPrice <= 2)
            return sm_astrStartingPrice[0];
        else if (dStartingPrice <= 3)
            return sm_astrStartingPrice[1];
        else if (dStartingPrice <= 4)
            return sm_astrStartingPrice[2];
        else if (dStartingPrice <= 7)
            return sm_astrStartingPrice[3];
        else if (dStartingPrice <= 11)
            return sm_astrStartingPrice[4];
        else if (dStartingPrice <= 17)
            return sm_astrStartingPrice[5];
        else
            return sm_astrStartingPrice[6];
    }
    private String convertDistanceWon(double dDistanceWon)
    {
        // "&frac12; lengths-", "1 length-", "2 lengths-", "5 lengths-", "GT 5 lengths"
       if (dDistanceWon < 0)
            return "Unknown";
        else if (dDistanceWon < 0.5)
            return sm_astrDistanceWon[0];
        else if (dDistanceWon == 0.5)
            return sm_astrDistanceWon[1];
        else if (dDistanceWon <= 1)
            return sm_astrDistanceWon[2];
        else if (dDistanceWon <= 2)
            return sm_astrDistanceWon[3];
        else if (dDistanceWon <= 5)
            return sm_astrDistanceWon[4];
        else
            return sm_astrDistanceWon[5];
    }
    private String convertCountry(String strCode)
    {
         if ("AUS".equals(strCode))
             return "Australia";
         else if ("CAN".equals(strCode))
             return "Canada";
         else if ("FR".equals(strCode))
             return "France";
         else if ("GER".equals(strCode))
             return "Germany";
         else if ("HK".equals(strCode))
             return "Hong Kong";
         else if ("IRE".equals(strCode))
             return "Ireland";
         else if ("JPN".equals(strCode))
             return "Japan";
         else if ("SAU".equals(strCode))
             return "Saudi Arabia";
         else if ("SIN".equals(strCode))
             return "Singapore";
         else if ("TUR".equals(strCode))
             return "Turkey";
         else if ("GB".equals(strCode) || "UK".equals(strCode) || "USA".equals(strCode))
             return strCode;
     
         return "Unknown";
    }
    private String convertDistanceTravelled(int nDistanceTravelled)
    {
        if (nDistanceTravelled <= 0)
            return "Unknown";
        else if (nDistanceTravelled <= 30)
            return sm_astrDistanceTravelled[0];
        else if (nDistanceTravelled <= 50)
            return sm_astrDistanceTravelled[1];
        else if (nDistanceTravelled <= 100)
            return sm_astrDistanceTravelled[2];
        else if (nDistanceTravelled <= 150)
            return sm_astrDistanceTravelled[3];
        else if (nDistanceTravelled <= 200)
            return sm_astrDistanceTravelled[4];
        else
            return sm_astrDistanceTravelled[5];
    }
    private String convertGroupRace(int nGroupRace)
    {
        if (nGroupRace == 0)
            return "Ungraded";
        else if (nGroupRace == 1)
            return "Group One";
        else if (nGroupRace == 2)
            return "Group Two";
        else if (nGroupRace == 3)
            return "Group Three";
        else if (nGroupRace == 4)
            return "Listed";
        else
            return "Unknown";
    }
   private String convertDaysSinceRan(int nDaysSinceRan)
    {
        if (nDaysSinceRan <= 0)
            return "Unknown";
        else if (nDaysSinceRan <= 7)
            return sm_astrDaysSinceRan[0];
        else if (nDaysSinceRan <= 14)
            return sm_astrDaysSinceRan[1];
        else if (nDaysSinceRan <= 30)
            return sm_astrDaysSinceRan[2];
        else if (nDaysSinceRan <= 50)
            return sm_astrDaysSinceRan[3];
        else if (nDaysSinceRan <= 80)
            return sm_astrDaysSinceRan[4];
        else if (nDaysSinceRan <= 150)
            return sm_astrDaysSinceRan[5];
        else 
            return sm_astrDaysSinceRan[6];
     }
   private String convertNrRunners(int nRunners)
    {
        if (nRunners <= 5)
            return sm_astrNrRunners[0];
        else if (nRunners <= 9)
            return sm_astrNrRunners[1];
        else if (nRunners <= 15)
            return sm_astrNrRunners[2];
        else if (nRunners <= 22)
            return sm_astrNrRunners[3];
        else 
            return sm_astrNrRunners[4];
     }
    private String convertRaceType(String strRaceType)
    {
        if ("National Hunt Flat".equals(strRaceType))
            return "NH Flat";
        else if ("All Weather Flat".equals(strRaceType))
            return "AW Flat";
        else
            return strRaceType;
    }
    private String convertDistance(int nFurlongs)
    {
        if (nFurlongs <= 4)
            return "Unknown";
        else if (nFurlongs == 8)
            return "Mile";
        else if (nFurlongs == 10)
            return "1&frac14; miles";
        else if (nFurlongs == 12)
            return "1&frac12; miles";
        else if (nFurlongs >= 13 && nFurlongs <= 15)
            return "1&frac34; miles";
        else if (nFurlongs >= 16 && nFurlongs <= 17)
            return "2 miles";
        else if (nFurlongs >= 18 && nFurlongs <= 19)
            return "2&frac14; miles";
        else if (nFurlongs >= 20 && nFurlongs <= 21)
            return "2&frac12; miles";
        else if (nFurlongs >= 22 && nFurlongs <= 23)
            return "2&frac34; miles";
        else if (nFurlongs >= 24 && nFurlongs <= 25)
            return "3 miles";
        else if (nFurlongs >= 26 && nFurlongs <= 27)
            return "3&frac14; miles";
        else if (nFurlongs >= 28 && nFurlongs <= 29)
            return "3&frac12; miles";
        else if (nFurlongs >= 30 && nFurlongs <= 31)
            return "3&frac34; miles";
        else if (nFurlongs >= 32)
            return "4 miles";
        else    
            return nFurlongs + " furlongs";
     }
    private String convertFinishPosition(String strPosition, int nRunners)
    {
        try
        {
            int nPosition = Integer.valueOf(strPosition);
            if (nPosition == 1)
                return "Winner";
            else if ((nPosition <= 2) && (nRunners >= 5))
            {
                return "Placed";
            }
            else if ((nPosition <= 3) && (nRunners >= 8))
            {
                return "Placed";
            }
            else if ((nPosition <= 4) && (nRunners >= 16))      // must be a handicap
            {
                return "Placed";
            }
            else
                return "Unplaced";
        }
        catch(NumberFormatException e)
        {
        }
        
        return "DNF";
    }
    private String convertTack(SmartformTack tack)
    {
        String strTack="";
        if (tack.hasTack())
        {
            if (tack.hasBlinkers())
            {
                strTack+="Blinkers";
                m_setTack.addValue( "Blinkers");
            }
            if (tack.hasCheekPieces())
            {
                if (!"".equals(strTack))
                    strTack+="|";
                strTack+="Cheek Pieces";
                m_setTack.addValue( "Cheek Pieces");
            }
            if (tack.hasEyeCover())
            {
                if (!"".equals(strTack))
                    strTack+="|";
                strTack+="Eye Cover";
                m_setTack.addValue( "Eye Cover");
            }
            if (tack.hasEyeShield())
            {
                if (!"".equals(strTack))
                    strTack+="|";
                strTack+="Eye Shield";
                m_setTack.addValue( "Eye Shield");
            }
             if (tack.hasHood())
            {
                if (!"".equals(strTack))
                    strTack+="|";
                strTack+="Hood";
                m_setTack.addValue( "Hood");
            }
             if (tack.hasPacifiers())
            {
                if (!"".equals(strTack))
                    strTack+="|";
                strTack+="Pacifiers";
                m_setTack.addValue( "Pacifiers");
            }
            if (tack.hasTongueStrap())
            {
                if (!"".equals(strTack))
                    strTack+="|";
                strTack+="Tongue Strap";
                m_setTack.addValue( "Tongue Strap");
            }
            if (tack.hasVisor())
            {
                if (!"".equals(strTack))
                    strTack+="|";
                strTack+="Visor";
                m_setTack.addValue( "Visor");
            }
        }
        else
        {
            strTack="None";
            m_setTack.addValue( "None");
        }
        
        return strTack;
    }
    private class WordCounterComparator implements Comparator<Pair<String,Integer>>
    {
        public int compare(Pair<String,Integer> pairValue1, Pair<String,Integer> pairValue2)
        {
            if (pairValue1.getElement1() == pairValue2.getElement1())
                return pairValue1.getElement0().compareTo(pairValue2.getElement0());
            
            return (pairValue1.getElement1() < pairValue2.getElement1()) ? 1 : -1;  // Highest first
        }
    }
    private class RunnerAttributeComparator implements Comparator<String>
    {
        private String[] m_astrValues;
        public RunnerAttributeComparator(String[] astrValues)
        {
            m_astrValues = astrValues;
        }
        public int compare(String strValue1, String strValue2)
        {
            if (strValue1.equalsIgnoreCase(strValue2))
                return 0;
            
            return (SetUtils.getPositionInArray(m_astrValues, strValue1) < SetUtils.getPositionInArray(m_astrValues, strValue2)) ? -1 : 1;
        }
    }
}
