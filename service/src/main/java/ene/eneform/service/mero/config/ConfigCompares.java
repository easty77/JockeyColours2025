/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.mero.config;

import ene.eneform.service.mero.parse.ENEColoursParserCompareAction;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Simon
 */
public class ConfigCompares extends ConfigXML {

    // by language
    private HashMap<String, ENECompares> m_hmLanguages= new HashMap<String, ENECompares>();
 
    public ConfigCompares(ENEColoursEnvironment environment, SAXParser parser, String fileName)
    {
        super(parser, fileName);
        setHandler(new ENEComparesHandler(environment));
        loadXML();
    }
   public ArrayList<ENEColoursParserCompareAction> getCompareList(String strType, String strLanguage)
    {
        return m_hmLanguages.get(strLanguage).m_hmCompares.get(strType);
    }

   private class ENECompares implements Serializable
    {
        private HashMap<String,ArrayList<ENEColoursParserCompareAction>> m_hmCompares = new HashMap<String,ArrayList<ENEColoursParserCompareAction>>(); // keys are ENEJacket, ENESleeves, ENECap
        public ENECompares(HashMap<String,ArrayList<ENEColoursParserCompareAction>> hmCompares)
        {
            m_hmCompares = hmCompares;
        }
    }
private class ENEComparesHandler extends DefaultHandler implements Serializable
{
    private String m_strCurrentLanguage ="";

    private HashMap<String,ArrayList<ENEColoursParserCompareAction>> m_hmCompares = null;

    private String m_strCurrentName = "";
    private String m_strCurrentMatch = "";
    private String m_strCurrentProcess = "";
    private String m_strCurrentPackage = "";
    private boolean m_bIsMatch = false;
    private ArrayList<ENEColoursParserCompareAction> m_alCurrentCompares = null;

    private ENEColoursEnvironment environment;

    public ENEComparesHandler(ENEColoursEnvironment environment)
    {
       this.environment = environment;
    }

    @Override public void startDocument ()
    {
    }

    @Override public void endDocument ()
    {
    }

    @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
       if ("language".equals(qName))
        {
            m_strCurrentLanguage = attributes.getValue("id");
             
            m_hmCompares = new HashMap<String,ArrayList<ENEColoursParserCompareAction>>();
        }
       else if ("jacket".equals(qName))
        {
            m_alCurrentCompares = new ArrayList<ENEColoursParserCompareAction>();
            m_strCurrentPackage = "ENEJacket";
         }
        else  if ("sleeves".equals(qName))
        {
           m_alCurrentCompares = new ArrayList<ENEColoursParserCompareAction>();
            m_strCurrentPackage = "ENESleeves";
        }
        else if ("cap".equals(qName))
        {
            m_alCurrentCompares = new ArrayList<ENEColoursParserCompareAction>();
            m_strCurrentPackage = "ENECap";
        }
        else if("compare".equals(qName))
        {
            String strId = attributes.getValue("id");
            m_strCurrentName = strId;
        }
        else if("process".equals(qName))
        {
            m_strCurrentProcess += "<process>";
        }
        else if ("pattern".equals(qName))
        {
           String strId = attributes.getValue("id");
           String strPropagate = attributes.getValue("propagate");
           if ("no".equalsIgnoreCase(strPropagate))
               m_strCurrentProcess += "<pattern id=\"" + strId + "\" propagate=\"no\">";
           else
               m_strCurrentProcess += "<pattern id=\"" + strId + "\">";
        }
        else if ("colour".equals(qName))
        {
           String strId = attributes.getValue("id");
           m_strCurrentProcess += "<colour id=\"" + strId + "\" />";
        }
        else if ("text".equals(qName))
        {
           String strId = attributes.getValue("id");
           m_strCurrentProcess += "<text id=\"" + strId + "\" />";
        }
        else if ("match".equals(qName))
        {
            m_bIsMatch = true;
        }
     }

    @Override public void endElement(String uri, String localName, String qName) throws SAXException
    {
       if ("language".equals(qName))
        {
            ENECompares ee = new ENECompares(m_hmCompares);
            m_hmLanguages.put(m_strCurrentLanguage, ee);
        }
       else if ("jacket".equals(qName) || "sleeves".equals(qName) || "cap".equals(qName))
        {
            m_hmCompares.put(m_strCurrentPackage, m_alCurrentCompares);
        }
        else if("compare".equals(qName))
        {
            ENEColoursParserCompareAction action = new ENEColoursParserCompareAction(environment, m_strCurrentName, m_strCurrentMatch, m_strCurrentProcess, m_strCurrentLanguage);
            m_alCurrentCompares.add(action);
            m_strCurrentName = "";
            m_strCurrentMatch = "";
            m_strCurrentProcess = "";
        }
       else if("process".equals(qName))
        {
            // copy
           m_strCurrentProcess += "</process>";
        }
        else if ("pattern".equals(qName))
        {
            // copy
           m_strCurrentProcess += "</pattern>";
        }
        else if ("colour".equals(qName))
        {
            // copy
        }
        else if ("match".equals(qName))
        {
            m_bIsMatch = false;
        }
    }

    @Override public void characters(char[] chars, int start, int length) throws SAXException
    {
        if (m_bIsMatch)
        {
            String strChars = new String(chars, start, length);
            strChars = strChars.replace("$COLOUR_LIST", getFullColourListRegEx(m_strCurrentLanguage));
            strChars = strChars.replace("$JACKET_PATTERN_LIST", environment.getPatternListRegEx("ENEJacket", m_strCurrentLanguage));
            strChars = strChars.replace("$SLEEVE_PATTERN_LIST", environment.getPatternListRegEx("ENESleeves", m_strCurrentLanguage));
            strChars = strChars.replace("$CAP_PATTERN_LIST", environment.getPatternListRegEx("ENECap", m_strCurrentLanguage));
            m_strCurrentMatch += strChars;
        }
    }
    private String getFullColourListRegEx(String strLanguage)
    {
    	// including fabrics
        // 20130222 put fabrics first (as longer)
        return environment.getConfigFabrics().getFabricListRegEx(strLanguage) + "|"
                + environment.getConfigColours().getColourListRegEx(strLanguage);
    }
}
}
