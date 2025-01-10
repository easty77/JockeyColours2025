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
public class ConfigMero extends ConfigXML implements Serializable{
    
    private HashMap<String,String> m_hmFiles = new HashMap<String,String>();
    private HashMap<String,String> m_hmVariables = new HashMap<String,String>();
    
    public ConfigMero(SAXParser parser, String strFileName)
    {
        super(parser, strFileName);
    }
    public boolean load()
    {
        setHandler(new ENEColoursConfigHandler());
        return loadXML();
    }
    public String getFileName(String strKey)
    {
        return m_hmFiles.get(strKey);
    }
    public String getVariable(String strKey)
    {
        return m_hmVariables.get(strKey);
    }
    private class ENEColoursConfigHandler extends DefaultHandler implements Serializable
{
    @Override public void startDocument ()
    {
    }

    @Override public void endDocument ()
    {
    }

    @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if ("file".equals(qName))
        {
            String strId = attributes.getValue("id");
            String strValue = attributes.getValue("value");
            m_hmFiles.put(strId, strValue);
        }
        else if("variable".equals(qName))
        {
            String strId = attributes.getValue("id");
            String strValue = attributes.getValue("value");
            m_hmVariables.put(strId, strValue);
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
