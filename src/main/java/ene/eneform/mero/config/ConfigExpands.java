/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.config;

import ene.eneform.mero.config.ConfigColours;
import ene.eneform.mero.config.ConfigFabrics;
import ene.eneform.mero.config.ConfigPatterns;
import ene.eneform.mero.config.ConfigXML;
import ene.eneform.mero.parse.ENEColoursParserExpand;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;

import org.springframework.beans.factory.annotation.Value;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Simon
 */
public class ConfigExpands extends ConfigXML {
    @Value("${ene.eneform.mero.expands}")
    private static String FILE_NAME;

    // by language
    private HashMap<String, ENEExpands> m_hmLanguages = new HashMap<String, ENEExpands>();
 
   public ConfigExpands(SAXParser parser, ConfigColours cc, ConfigPatterns cp, ConfigFabrics cf)
    {
        super(parser, FILE_NAME);
        setHandler(new ENEExpandsHandler(cc, cp, cf));
        loadXML();
    }
    public ArrayList<ENEColoursParserExpand> getExpandList(String strLanguage)
    {
        return m_hmLanguages.get(strLanguage).m_alExpand;
    }

    private class ENEExpands implements Serializable
    {
        private ArrayList<ENEColoursParserExpand> m_alExpand = new ArrayList<ENEColoursParserExpand>();
        public ENEExpands(ArrayList<ENEColoursParserExpand> alExpand)
        {
            m_alExpand = alExpand;
        }
    }
private class ENEExpandsHandler extends DefaultHandler implements Serializable
{
       private String m_strCurrentLanguage ="";
         
        private ArrayList<ENEColoursParserExpand> m_alExpand = null;
        
    private String m_strCurrentElement = "";
    private String m_strCurrentFrom = "";
    private String m_strCurrentTo = "";
    private String m_strCurrentLabel = "";

    private transient ene.eneform.mero.config.ConfigColours m_cc;
    private transient ConfigPatterns m_cp;
    private transient ConfigFabrics m_cf;
    
    public ENEExpandsHandler(ConfigColours cc, ConfigPatterns cp, ConfigFabrics cf)
    {
        m_cc= cc;
        m_cp = cp;
        m_cf = cf;
    }

    @Override public void startDocument ()
    {
    }

    @Override public void endDocument ()
    {
    }

    @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
         m_strCurrentElement = qName;
        if ("language".equals(qName))
        {
            m_strCurrentLanguage = attributes.getValue("id");
            
            m_alExpand = new ArrayList<ENEColoursParserExpand>();
        }
        else if ("from".equals(qName))
        {
            m_strCurrentFrom = "";
        }
        else if ("to".equals(qName))
        {
            m_strCurrentTo = "";
        }
        else if ("label".equals(qName))
        {
            m_strCurrentLabel = "";
         }
    }

    @Override public void endElement(String uri, String localName, String qName) throws SAXException
    {
       if ("language".equals(qName))
        {
            ENEExpands ee = new ENEExpands(m_alExpand);
            m_hmLanguages.put(m_strCurrentLanguage, ee);
        }
       else if ("label".equals(qName))
        {
            ENEColoursParserExpand expand = new ENEColoursParserExpand(m_strCurrentFrom, m_strCurrentTo, m_strCurrentLabel);
            m_alExpand.add(expand);
        }
        m_strCurrentElement = "";
    }

    @Override public void characters(char[] chars, int start, int length) throws SAXException
    {
        if ("from".equals(m_strCurrentElement))
        {
            String strFrom = new String(chars, start, length);
            strFrom = strFrom.replace("$COLOUR_LIST", getFullColourListRegEx(m_strCurrentLanguage));
            strFrom = strFrom.replace("$JACKET_PATTERN_LIST", m_cp.getPatternListRegEx("ENEJacket", m_strCurrentLanguage));
            strFrom = strFrom.replace("$SLEEVE_PATTERN_LIST", m_cp.getPatternListRegEx("ENESleeves", m_strCurrentLanguage));
            strFrom = strFrom.replace("$CAP_PATTERN_LIST", m_cp.getPatternListRegEx("ENECap", m_strCurrentLanguage));
            m_strCurrentFrom += strFrom;
        }
        else if ("to".equals(m_strCurrentElement))
        {
            m_strCurrentTo += new String(chars, start, length);
        }
        else if ("label".equals(m_strCurrentElement))
        {
             m_strCurrentLabel += new String(chars, start, length);
          }

    }
    private String getFullColourListRegEx(String strLanguage)
    {
    	// including fabrics
        // 20130222 put fabrics first (as longer)
        return m_cf.getFabricListRegEx(strLanguage) + "|" + m_cc.getColourListRegEx(strLanguage);
    }

}
}
