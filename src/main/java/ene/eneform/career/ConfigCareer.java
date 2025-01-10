/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.career;

import ene.eneform.mero.config.ConfigXML;
import ene.eneform.smartform.bos.SmartformHorse;
import ene.eneform.smartform.bos.SmartformHorseDefinition;
import ene.eneform.smartform.bos.SmartformOwner;
import ene.eneform.smartform.bos.SmartformTrainer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Simon
 */
public class ConfigCareer extends ConfigXML implements Serializable{
    
    private HashMap<String,String> m_hmCareerRaceNames = new HashMap<String,String>();
     private HashMap<String,CareerDefinition> m_hmCareers = new HashMap<String,CareerDefinition>();

   public ConfigCareer(SAXParser parser, String strFileName)
    {
        super(parser, strFileName);
    }
    public boolean load()
    {
        setHandler(new CareerDefinitionsHandler());
        return loadXML();
    }

public CareerDefinition getCareer(String strId)
{
    CareerDefinition defn =  m_hmCareers.get(strId);
    return defn;
}
public ArrayList<String> getCareerNames()
{
    ArrayList<String> alNames = new ArrayList<String>();
    Iterator<CareerDefinition> iter = m_hmCareers.values().iterator();
    while(iter.hasNext())
    {
        CareerDefinition career = iter.next();
        alNames.add(career.getName());
    }
    
    return alNames;
}

public String getCareerRaceName(String strKey)
{
    return m_hmCareerRaceNames.get(strKey);
}
    private class CareerDefinitionsHandler extends DefaultHandler implements Serializable{
    private String m_strCurrentElement = "";
     private CareerDefinition m_currentCareer = null;;
    private CareerDefinition.CareerTable m_currentTable = null;;
    private CareerDefinition.CareerRow m_currentRow = null;;
    private CareerDefinition.CareerColumn m_currentColumn = null;;
    private CareerDefinition.CareerRow.CareerCell m_currentCell = null;;
    private CareerDefinition.CareerRow.CareerCell.CareerRace m_currentRace = null;;
    private CareerDefinition.Pedigree m_pedigree = null;;
    private int m_nTables = 0;
    private int m_nColumns = 0;
    private int m_nRows = 0;
    
    @Override public void startDocument ()
    {
    }

    @Override public void endDocument ()
    {
    }

    @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if ("career".equals(qName))
        {
            m_nTables = 0;
            m_nColumns = 0;
            m_nRows = 0;
            String strId = attributes.getValue("id");
            String strName = attributes.getValue("name");
            System.out.println("Career: " + strName);
            m_currentCareer = new HorseCareerDefinition(strId, strName);
            
             // remove type and data attribute - meeting only
            /*String strType = attributes.getValue("type");
            if (strType != null)
                m_currentCareer.setType(strType);
            // remove data attribute - meeting only
            String strData = attributes.getValue("data");
            if (strData != null)
                m_currentCareer.setData(strData); */
            
            String strOwner = attributes.getValue("owner");
            if (strOwner != null)
                m_currentCareer.setOwner(strOwner);
            String strColours = attributes.getValue("colours");
            if (strColours != null)
                m_currentCareer.setColours(strColours);

            
           String strFoalingDate = attributes.getValue("foaling_date");
           if (strFoalingDate != null)
           {
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calFoalingDate = Calendar.getInstance();
                try
                {
                    calFoalingDate.setTime(formatter.parse(strFoalingDate));
                    
                    m_currentCareer.setFoalingDate(calFoalingDate);
                }
                catch(ParseException e)
                {
                    System.out.println("Invalid foaling date: " + strFoalingDate);
                }
           }

            String strFormat = attributes.getValue("format");
            if (strFormat != null)
                m_currentCareer.setFormat(strFormat);
            String strPosition = attributes.getValue("position");
            if ("yes".equalsIgnoreCase(strPosition))
                m_currentCareer.setShowPosition();
            String strDisplayRowTitle = attributes.getValue("display_row_titles");
            if ("no".equalsIgnoreCase(strDisplayRowTitle))
                m_currentCareer.suppressRowTitles();
            else if ("horizontal".equalsIgnoreCase(strDisplayRowTitle))
                m_currentCareer.horizontalRowTitles();
            String strDisplayColumnTitle = attributes.getValue("display_column_titles");
            if ("no".equalsIgnoreCase(strDisplayColumnTitle))
                m_currentCareer.suppressColumnTitles();
       }
        else if ("pedigree".equals(qName))
        {
            m_pedigree = m_currentCareer.new Pedigree(attributes.getValue("generation0"), 
                    attributes.getValue("generation1"), 
                    attributes.getValue("generation2"), 
                    attributes.getValue("generation3"));
        }
        else if ("table".equals(qName))
        {
            m_nTables++;
            m_nColumns = 0;
            m_nRows = 0;
            // when only one table, usually omitted
            if (m_currentTable != null)
                m_currentCareer.addTable(m_currentTable);
            m_currentTable = m_currentCareer.new CareerTable(m_nTables);
        }
        else if ("columns".equals(qName))
        {
            m_nColumns = 0;
            if (m_currentTable == null)
            {
               m_nTables++;
               m_currentTable = m_currentCareer.new CareerTable(m_nTables);
            }
        }
        else if ("column".equals(qName))
        {
            m_nColumns++;
            m_strCurrentElement = "column";
            int nColSpan = getAttributesInt(attributes, "colspan", 1);
            m_currentColumn = m_currentCareer.new CareerColumn(m_nColumns, nColSpan);
            int nMeroWidth = getAttributesInt(attributes, "merowidth", 3);
            m_currentColumn.setMeroWidth(nMeroWidth);
        }
        else if ("rows".equals(qName))
        {
            m_nRows = 0;
            if (m_currentTable == null)
            {
                m_nTables++;
                m_currentTable = m_currentCareer.new CareerTable(m_nTables);
            }
        }
        else if ("row".equals(qName))
        {
            m_nRows++;
           String strTitle = attributes.getValue("title");
           int nHeight = getAttributesInt(attributes, "height", 1);
           m_currentRow = m_currentCareer.new CareerRow(m_nRows, (strTitle != null) ? strTitle : "", nHeight);
           String strName = attributes.getValue("name");
           if (strName != null)
               m_currentRow.setName(strName);
 
           m_strCurrentElement = "row";
        }
        else if ("cell".equals(qName))
        {
            m_nColumns++;
           int nColSpan = getAttributesInt(attributes, "colspan", 1);
           m_currentCell = m_currentRow.new CareerCell(m_nRows, m_nColumns, nColSpan);
           String strJockeyColours = attributes.getValue("colours");
           if (strJockeyColours != null)
           {
               if ("".equals(strJockeyColours))
                   strJockeyColours = "Unknown";
               
               m_currentCell.setJockeyColours(strJockeyColours);
           }
           String strFontName = attributes.getValue("font");
           if (strFontName != null)
               m_currentCell.setFontName(strFontName);
           String strFontStyle = attributes.getValue("font-style");
           if (strFontStyle != null)
               m_currentCell.setFontStyle(strFontStyle);
           int nFontSize = getAttributesInt(attributes, "font-size", 0);
           if (nFontSize > 0)
               m_currentCell.setFontSize(nFontSize);
           String strAlign = attributes.getValue("align");
           if (strAlign != null)
               m_currentCell.setAlign(strAlign);
           int nRowSpan = getAttributesInt(attributes, "rowspan", 1);
           if (nRowSpan != 0)
               m_currentCell.setRowSpan(nRowSpan);
                
            m_strCurrentElement = "cell";
         }
        else if ("race".equals(qName))
        {
           String strSource = attributes.getValue("source");
           String strTitle = attributes.getValue("title");
           m_currentRace = m_currentCell.new CareerRace(strSource, strTitle);
           m_strCurrentElement = "race";
        }
        else if ("horse".equals(qName))
        {
            // now replaced by career_horse table
           SmartformHorse horse = new SmartformHorse(0, m_currentCareer.getName());
           String strBred = attributes.getValue("bred");
           if (strBred != null)
           {
                horse.setBred(strBred);
           }
           String strOwner = attributes.getValue("owner");
           if (strOwner != null)
           {
               SmartformOwner owner = new SmartformOwner(0);
               owner.setName(strOwner);
               horse.setOwner(owner);
           }
           String strTrainer = attributes.getValue("trainer");
           if (strTrainer != null)
           {
               SmartformTrainer trainer = new SmartformTrainer(0, strTrainer);
               horse.setTrainer(trainer);
           }
           String strSire = attributes.getValue("sire");
           if (strSire != null)
           {
               SmartformHorseDefinition sire = new SmartformHorseDefinition(strSire, 0, null);
               horse.setSire(sire);
               horse.setSireName(strSire);
           }
           String strDam = attributes.getValue("dam");
           if (strDam != null)
           {
               SmartformHorseDefinition dam = new SmartformHorseDefinition(strDam, 0, null);
               horse.setDam(dam);
           }
           String strDamSire = attributes.getValue("damsire");
           if (strDamSire != null)
           {
               SmartformHorseDefinition damsire = new SmartformHorseDefinition(strDamSire, 0, null);
               horse.setDamSire(damsire);
           }
           String strColour = attributes.getValue("colour");
           if (strColour != null)
           {
               horse.setColour(strColour);
           }
           String strGender = attributes.getValue("gender");
           if (strGender != null)
           {
               horse.setGender(strGender.charAt(0));
           }
           String strFoalingDate = attributes.getValue("foaling_date");
           if (strFoalingDate != null)
           {
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calFoalingDate = Calendar.getInstance();
                try
                {
                    calFoalingDate.setTime(formatter.parse(strFoalingDate));
                    
                    horse.setFoalingDate(calFoalingDate);
                }
                catch(ParseException e)
                {
                    System.out.println("Invalid foaling date: " + strFoalingDate);
                }
           }
           ((HorseCareerDefinition)m_currentCareer).setHorse(horse);
        }
}
    @Override public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if ("career".equals(qName))
        {
            if (m_currentTable != null)
            {
                m_currentCareer.addTable(m_currentTable);
                m_currentTable = null;
            }
            // add current document
            m_hmCareers.put(m_currentCareer.getId(), m_currentCareer);
            m_currentCareer = null;
        }
        else if ("pedigree".equals(qName))
        {
            m_currentCareer.setPedigree(m_pedigree);
            m_pedigree = null;
        }
       else if ("table".equals(qName))
        {
            m_currentCareer.addTable(m_currentTable);
            m_currentTable = null;
        }
       else if ("columns".equals(qName))
        {
            m_nColumns = 0;
        }
       else if ("column".equals(qName))
        {
            m_currentTable.addColumn(m_currentColumn);
            m_currentColumn = null;
            m_strCurrentElement = "";
        }
       else if ("rows".equals(qName))
        {
        }
       else if ("row".equals(qName))
        {
            m_nColumns = 0;
            m_currentTable.addRow(m_currentRow);
            m_currentRow = null;
             m_strCurrentElement = "";
        }
        else if ("cell".equals(qName))
        {
            m_currentRow.addCell(m_currentCell);
            m_currentCell = null;
            m_strCurrentElement = "";
        }
        else if ("race".equals(qName))
        {
            m_currentCell.addRace(m_currentRace);
            m_currentRace = null;
            m_strCurrentElement = "";
        }
    }

    @Override public void characters(char[] chars, int start, int length) throws SAXException
    {
        // currently no content in node
        if ("column".equals(m_strCurrentElement))
        {
             String strColumn = new String(chars, start, length);
              m_currentColumn.setContent(strColumn);
        }
        else if ("cell".equals(m_strCurrentElement))
        {
             String strContent = new String(chars, start, length);
              m_currentCell.setContent(strContent);
        }
        else if ("race".equals(m_strCurrentElement))
        {
             String strContent = new String(chars, start, length);
              m_currentRace.setContent(strContent);
              // add race title
              String strRaceTitle = m_hmCareerRaceNames.get(m_currentRace.getKey());
              if (strRaceTitle == null)
                  m_hmCareerRaceNames.put(m_currentRace.getKey(), m_currentRace.getTitle());
              else if (!m_currentRace.getTitle().equals(strRaceTitle))
              {
                  System.out.println(m_currentRace.getKey().substring(0, 2) + " " + m_currentRace.getKey().substring(2) + "-" + m_currentRace.getTitle() + "-" + strRaceTitle);
              }
        }
    }
}    
}
