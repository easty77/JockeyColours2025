/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.config;

import java.io.Serializable;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;

import ene.eneform.mero.config.ConfigXML;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Simon
 */
public class ConfigAbbreviations extends ConfigXML {
    
     // abbreviations.xml
     private HashMap<String, ENEAbbreviations> m_hmLanguages = new HashMap<String, ENEAbbreviations>();
     
    public ConfigAbbreviations(SAXParser parser, String strFileName)
    {
        super(parser, strFileName);
    }
    public boolean load()
    {
        setHandler(new ENEAbbreviationsHandler());
        return loadXML();
    }
    public String replaceAbbreviation(String strOriginal, String strLanguage)
    {
        ENEAbbreviations abbreviations = m_hmLanguages.get(strLanguage);
        String strUpdated = abbreviations.m_hmAbbreviations.get(strOriginal);
        return (strUpdated == null) ? strOriginal : strUpdated;
    }
private class ENEAbbreviations implements Serializable
{
    HashMap<String, String> m_hmAbbreviations = new HashMap<String, String>();
    public ENEAbbreviations()
    {
    }
 }
private class ENEAbbreviationsHandler extends DefaultHandler implements Serializable
{
        private String m_strCurrentWord = "";
        private String m_strCurrentLanguage ="";

        private ENEAbbreviations m_currentAbbreviations = null;
 

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
            m_currentAbbreviations = new ENEAbbreviations();
         }
        else if ("word".equals(qName))
        {
            String strId = attributes.getValue("value");
            m_strCurrentWord = strId;
        }
        else if("abbreviation".equals(qName))
        {
            String strAbbreviation = attributes.getValue("value");
            m_currentAbbreviations.m_hmAbbreviations.put(strAbbreviation, m_strCurrentWord);
        }
    }

    @Override public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if ("language".equals(qName))
        {
            m_hmLanguages.put(m_strCurrentLanguage, m_currentAbbreviations);
        }
   }

    @Override public void characters(char[] chars, int start, int length) throws SAXException
    {
        // currently no content in node
    }
}
}
