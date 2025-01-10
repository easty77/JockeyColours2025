/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.config;

import ene.eneform.mero.config.ConfigXML;
import ene.eneform.mero.config.ENEColoursEnvironment;
import ene.eneform.mero.utils.ENEColourItem;
import ene.eneform.mero.utils.MeroUtils;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Simon
 */
public class ConfigColours extends ConfigXML {
    
    // by language
    private HashMap<String, ENEColoursConfig> m_hmLanguages = new HashMap<String, ENEColoursConfig>();
     
    public ConfigColours(SAXParser parser, String strFileName)
    {
        super(parser, strFileName);
    }
    public boolean load()
    {
        setHandler(new ENEColoursHandler());
        return loadXML();
    }
     public boolean hasColour(String strColour, String strLanguage)
     {
         return m_hmLanguages.get(strLanguage).m_hmColours.containsKey(strColour);
     }
    public Iterator<ENEColourItem> getColourIterator(String strLanguage)
    {
         return m_hmLanguages.get(strLanguage).m_alColours.iterator();
    }
    public Set<String> getColours(String strLanguage)
    {
         return m_hmLanguages.get(strLanguage).m_hmColours.keySet();
    }
    public ENEColourItem getColourItem(String strColour, String strLanguage)
    {
        return m_hmLanguages.get(strLanguage).m_hmColours.get(strColour);
    }
   public String getColourListRegEx(String strLanguage)
    {
    	// problem with order of execution of static elements
        return m_hmLanguages.get(strLanguage).m_strColourListRegEx;
        //return "blue|red|black";
    }
   private class ENEColoursConfig implements Serializable
   {
     private HashMap<String,ENEColourItem> m_hmColours = new HashMap<String,ENEColourItem>();
     private ArrayList<ENEColourItem> m_alColours = new ArrayList<ENEColourItem>();
     private String m_strColourListRegEx = "";
     
     public ENEColoursConfig(HashMap<String,ENEColourItem> hmColours, ArrayList<ENEColourItem> alColours)
     {
         m_hmColours = hmColours;
         m_alColours = alColours;
        Iterator<String> iter = m_hmColours.keySet().iterator();
        while(iter.hasNext())
        {
            String strColour = iter.next();
            if (!"".equals(m_strColourListRegEx))
                m_strColourListRegEx += "|";
            m_strColourListRegEx += strColour;
        }
     }
   }
private class ENEColoursHandler extends DefaultHandler implements Serializable
{
        private String m_strCurrentHue = "";
        private String m_strCurrentLanguage ="";
        
        private HashMap<String,ENEColourItem> m_hmCurrentLanguageColours = null;
        private ArrayList<ENEColourItem> m_alCurrentColours = null;


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
            
            m_hmCurrentLanguageColours = new HashMap<String,ENEColourItem>();
            m_alCurrentColours = new ArrayList<ENEColourItem>();
        }
        else if ("hue".equals(qName))
        {
            String strId = attributes.getValue("id");
            m_strCurrentHue = strId;
        }
        else if("colour".equals(qName))
        {
            String strId = attributes.getValue("id");
            
            String strEquivalent  = attributes.getValue("equivalent");
            ENEColourItem item = null;
            if (strEquivalent != null)
            {
                ENEColourItem equivalent = getColourItem(strEquivalent, ENEColoursEnvironment.DEFAULT_LANGUAGE);
                item = new ENEColourItem(strId, equivalent.getColour(), m_strCurrentHue);
          }
            else
            {
                String strRGB = attributes.getValue("rgb");
                //System.out.println(strId + "-" + strRGB + "-" + strGroup);
                Color color = MeroUtils.getRGBColour(strRGB);
                item = new ENEColourItem(strId, color, m_strCurrentHue);
            }
            m_hmCurrentLanguageColours.put(strId, item);
            m_alCurrentColours.add(item);
        }
    }

    @Override public void endElement(String uri, String localName, String qName) throws SAXException
    {
       if ("language".equals(qName))
        {
            ENEColoursConfig cc = new ENEColoursConfig(m_hmCurrentLanguageColours, m_alCurrentColours);
            m_hmLanguages.put(m_strCurrentLanguage, cc);
        }
    }

    @Override public void characters(char[] chars, int start, int length) throws SAXException
    {
        // currently no content in node
    }
}
}
