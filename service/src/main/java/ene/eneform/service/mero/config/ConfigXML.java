/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.mero.config;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.io.Serializable;

/**
 *
 * @author Simon
 */
@Slf4j
public abstract class ConfigXML extends ConfigFile implements Serializable{
    
    protected String m_strFileName;
    protected DefaultHandler m_handler = null;
    protected transient SAXParser m_parser;
    
    protected ConfigXML(SAXParser parser, String strFileName)
    {
        m_parser = parser;
        log.info("ConfigXML {}", strFileName);
        m_strFileName = strFileName;
    }

    protected void setHandler(DefaultHandler handler)
    {
        m_handler = handler;
    }
    private boolean initialise()
    {
       SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	try {
	  m_parser = parserFactory.newSAXParser();
	} catch ( ParserConfigurationException pce ) {
	    System.out.println("Error setting up the XML Parser. The parser is not properly configured. Loading aborted.");
            return false;
	} catch ( SAXException saxe ) {
	    System.out.println("Error setting up the XML Parser. Loading aborted.");
            return false;
	}

        return true;
    }

protected boolean loadXML()
    {
        initialise();
        log.info("loadXML {}", m_strFileName);
        InputStream is = loadFile(m_strFileName);
         if (is == null)
         {
             System.out.println("Unable to load " + m_strFileName);
             return false;
         }
	try
        {
	    m_parser.parse(is, m_handler);
	} 
        catch ( Exception e )
        {
	    System.out.println("Unable to load " + m_strFileName + ", probably while performing SAX parsing-" + e.toString());
            e.printStackTrace();
            return false;
	}

        return true;
    }

    public int getAttributesHex(Attributes attributes, String strAttribute, int nDefault) {
        String strValue = attributes.getValue(strAttribute);
        int nValue = nDefault;
        if (strValue != null) {
            try {
                nValue = Integer.parseInt(strValue, 16);
            } catch (NumberFormatException e) {
            }
        }
        return nValue;
    }

    public float getAttributesFloat(Attributes attributes, String strAttribute, float fDefault) {
        String strValue = attributes.getValue(strAttribute);
        float fValue = fDefault;
        if (strValue != null) {
            try {
                fValue = Float.parseFloat(strValue);
            } catch (NumberFormatException e) {
            }
        }
        return fValue;
    }

    public int getAttributesInt(Attributes attributes, String strAttribute, int nDefault) {
        String strValue = attributes.getValue(strAttribute);
        int nValue = nDefault;
        if (strValue != null) {
            try {
                nValue = Integer.parseInt(strValue);
            } catch (NumberFormatException e) {
            }
        }
        return nValue;
    }

}
