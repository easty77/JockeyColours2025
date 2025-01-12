/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Simon
 */
public class ConfigOrganisations extends ConfigXML {
    // no language
    private HashMap<String, ENEOrganisation> m_hmOrganisations = new HashMap<String, ENEOrganisation>();
    
    public ConfigOrganisations(ENEColoursEnvironment environment, SAXParser parser, String fileName)
    {
        super(parser, fileName);

        setHandler(new ENEOrganisationsHandler(environment.getConfigColours()));
        loadXML();
    }
    public Set<String> getOrganisations()
    {
        return m_hmOrganisations.keySet();
    }
    public ENEOrganisation getOrganisation(String strOrganisation)
    {
        return m_hmOrganisations.get(strOrganisation);
    }

private class ENEOrganisationsHandler extends DefaultHandler implements Serializable
{
    private ENEOrganisation m_currentOrganisation = null;
    private ENEOrganisationList m_currentOrganisationList = null;
    private String m_strCurrentList="";
    private String m_strCurrentLanguage = "";
    
    private transient ConfigColours m_cc;
    
    public ENEOrganisationsHandler(ConfigColours cc)
    {
        m_cc = cc;
    }
   @Override public void startDocument ()
    {
    }

    @Override public void endDocument ()
    {
    }

    @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        int nIndex = attributes.getIndex("id");
        String strId = "";
        if (nIndex >= 0)
            strId = attributes.getValue(nIndex);
        if ("organisation".equals(qName))       // organisation -> new organisation
        {
            m_currentOrganisation = new ENEOrganisation(strId);
            m_hmOrganisations.put(strId, m_currentOrganisation);
            m_strCurrentLanguage = attributes.getValue("language");
         }
        else if ("colours".equals(qName) || "jacket".equals(qName) ||
                "sleeves".equals(qName) || "cap".equals(qName)) // colours, jacket, sleeves, cap -> get list
        {
            m_strCurrentList = qName;
            m_currentOrganisationList = m_currentOrganisation.getList(qName);
        }
        else if ("colour".equals(qName))
        {
            if (m_cc.hasColour(strId, m_strCurrentLanguage))
                m_currentOrganisationList.addItem(strId);   // validate against colour list
        }
        else if ("pattern".equals(qName))
        {
            m_currentOrganisationList.addItem(strId);  // currently no validation
        }
     }

    @Override public void endElement(String uri, String localName, String qName) throws SAXException
    {
    }

    @Override public void characters(char[] chars, int start, int length) throws SAXException
    {
        // currently no content in node
    }
}
}
