/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.config;

import ene.eneform.mero.config.ConfigXML;
import ene.eneform.mero.fabric.ENEFabric;
import ene.eneform.mero.fabric.ENEFabricItem;
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
public class ConfigFabrics extends ConfigXML {
    
    // by language
    private HashMap<String, ENEConfigFabrics> m_hmLanguages= new HashMap<String, ENEConfigFabrics>();

 
    public ConfigFabrics(SAXParser parser, String strFileName)
    {
        super(parser, strFileName);
    }
    public boolean load()
    {
        setHandler(new ENEFabricsHandler());
        return loadXML();
    }
   public Set<String> getFabrics(String strLanguage)
    {
        return m_hmLanguages.get(strLanguage).m_hmFabrics.keySet();
    }
   public boolean isFabric(String strFabric, String strLanguage)
    {
        return m_hmLanguages.get(strLanguage).m_hmFabrics.containsKey(strFabric);
    }
   public ENEFabricItem getFabricItem(String strFabric, String strLanguage)
    {
        ENEFabric fabric = m_hmLanguages.get(strLanguage).m_hmFabrics.get(strFabric);
        if (fabric != null)
            return fabric.getFabricItem();
        else
            return null;
    }
    public String getFabricListRegEx(String strLanguage)
    {
        return m_hmLanguages.get(strLanguage).m_strFabricListRegEx;
    }
    private class ENEConfigFabrics implements Serializable
    {
        
   private HashMap<String,ENEFabric> m_hmFabrics = new HashMap<String,ENEFabric>();
    private ArrayList<ENEFabric> m_alFabrics = new ArrayList<ENEFabric>();
    private String m_strFabricListRegEx = "";
    public ENEConfigFabrics(HashMap<String,ENEFabric> hmFabrics, ArrayList<ENEFabric> alFabrics)
     {
         m_hmFabrics = hmFabrics;
         m_alFabrics = alFabrics;
         Iterator<String> iter = m_hmFabrics.keySet().iterator();
        while(iter.hasNext())
        {
            String strFabric = iter.next();
            if (!"".equals(m_strFabricListRegEx))
                m_strFabricListRegEx += "|";
            m_strFabricListRegEx += strFabric;
        }
     }
    }
private class ENEFabricsHandler extends DefaultHandler implements Serializable
{
   private String m_strCurrentLanguage ="";

    private HashMap<String,ENEFabric> m_hmFabrics = null;
    private ArrayList<ENEFabric> m_alFabrics = null;
 
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
             
            m_hmFabrics = new HashMap<String,ENEFabric>();
            m_alFabrics = new ArrayList<ENEFabric>();
            
        }
       else if ("fabric".equals(qName))
        {
            String strId = attributes.getValue("id");
            String strClassName = attributes.getValue("class");
            String strData = attributes.getValue("data");
            ENEFabric fabric = new ENEFabric(strId, strClassName, strData);
            m_hmFabrics.put(strId, fabric);
            m_alFabrics.add(fabric);
         }
    }

    @Override public void endElement(String uri, String localName, String qName) throws SAXException
    {
      if ("language".equals(qName))
        {
            ENEConfigFabrics ecf = new ENEConfigFabrics(m_hmFabrics, m_alFabrics);
            m_hmLanguages.put(m_strCurrentLanguage, ecf);
        }
    }

    @Override public void characters(char[] chars, int start, int length) throws SAXException
    {
        // currently no content in node
    }
}
}
