/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.service.mero.parse;

import ene.eneform.service.mero.config.ENEColoursEnvironment;
import ene.eneform.service.mero.model.ENEColoursElement;
import ene.eneform.service.mero.model.ENEColoursElementPattern;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Simon
 */
public class ENEColoursParserCompareAction implements Serializable {
private ENEColoursEnvironment environment;

    private String m_strLanguage;
    private String m_strName;
    private String m_strMatch;
    private String m_strProcessXML;
    private Pattern m_pattern;

    public ENEColoursParserCompareAction(ENEColoursEnvironment environment, String strName, String strMatch, String strProcessXML, String strLanguage)
    {
        this.environment = environment;
        m_strName = strName;
        m_strMatch = strMatch;
        m_strProcessXML = strProcessXML;
        m_pattern = Pattern.compile(strMatch);
        m_strLanguage = strLanguage;
    }

    public ENEColoursParserMatch match(ENEColoursElement element, String strDescription)
    {
        Matcher matcher = m_pattern.matcher(strDescription);
        if (matcher.find())
        {
          String strGroup = matcher.group();
          int nStart = matcher.start();
          int nEnd = matcher.end();
          process(matcher, element);
          return new ENEColoursParserMatch(nStart, nEnd, strGroup, m_strName);
        }

        return null;
    }

    private synchronized boolean process(Matcher matcher, ENEColoursElement element)
    {
        // parse processXML to add colours and patterns to element
	try
        {
            InputStream in = new ByteArrayInputStream(m_strProcessXML.getBytes("UTF-8"));
            environment.parse(new BufferedInputStream(in), new CompareProcessHandler(matcher, element));
	}
        catch ( Exception e )
        {
	    System.out.println("Unable to load " + m_strName + ", probably while performing SAX parsing.");
            e.printStackTrace();
            return false;
	}

        return true;
    }

private class CompareProcessHandler extends DefaultHandler
{
    private ENEColoursElement m_element;
    private Matcher m_matcher;
    private ENEColoursElementPattern m_currentPattern = null;

    public CompareProcessHandler(Matcher matcher, ENEColoursElement element)
    {
        m_element = element;
        m_matcher = matcher;
    }
   @Override public void startDocument ()
    {
    }

    @Override public void endDocument ()
    {
    }

    @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if ("pattern".equals(qName))
        {
            String strId = attributes.getValue("id");
            // for split patterns, the syntax is 1+2
            String strPattern = "";
            if (strId.indexOf("+") > 0)
            {
                String[] astrPatterns = strId.split("\\+");
                for(int i = 0; i < astrPatterns.length; i++)
                {
                   strPattern = convertMatcherGroup(strPattern, astrPatterns[i]);
                }
              }
            else
                strPattern = getMatcherGroup(strId);

            boolean bNumberSuffix = false;
            if (strPattern.length() > 0)
            {
                String strLastChar = strPattern.substring(strPattern.length() - 1);
                if ("0123456789".indexOf(strLastChar) >= 0)
                {
                    bNumberSuffix = true;
                    strPattern = strPattern.substring(0, strPattern.length() - 1);
                }
                strPattern = environment.convertSynonym(m_element.getType(), strPattern.trim(), m_strLanguage);
                if (bNumberSuffix)
                    strPattern += strLastChar;
            
                m_currentPattern = new ENEColoursElementPattern(environment, m_strLanguage, strPattern);
                String strPropagate = attributes.getValue("propagate");
                if ("no".equalsIgnoreCase(strPropagate))
                    m_currentPattern.setPropagate(false);
            }
            else
            {
                System.out.println("startElement Empty pattern: " + strId);
            }
        }
        else if ("colour".equals(qName))
        {
            String strId = attributes.getValue("id");
            String strColour = getMatcherGroup(strId);
            if ((strColour != null) && !"".equals(strColour))
            {
                if (m_currentPattern != null)
                {
                    // check that this is not the first pattern colour matching the main colour
                    /* might be tassle, and no harm in drawing if same colour
                     if ((m_currentPattern.getColourCount() == 0) && (m_element.getColourItem() != null) && (m_element.getColourItem().getText().equals(strColour)))
                    {
                        // discard
                    }
                    else */
                        m_currentPattern.setColour(strColour);
                }
                else
                    m_element.setColour(strColour);
            }
        }
        else if ("text".equals(qName))
        {
            String strId = attributes.getValue("id");
            String strAdditionalText = getMatcherGroup(strId).trim();
            if (m_currentPattern != null)
                m_currentPattern.setAdditionalText(strAdditionalText);
        }
     }

    @Override public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if ("pattern".equals(qName))
        {
            m_element.setPattern(m_currentPattern);
            m_currentPattern = null;
        }
    }

    @Override public void characters(char[] chars, int start, int length) throws SAXException
    {
        // currently no content in node
    }

    private String convertMatcherGroup(String strPattern, String strGroup)
    {
       if (strGroup.indexOf('\\') == 0)        // this is a number that should be used as text e.g. hoops3 when three coloured hoops
            strPattern += strGroup.substring(1);
       else
       {
             String strGroup1 = getMatcherGroup(strGroup);
             if (!"".equals(strPattern))
                strPattern += " ";
             strPattern += strGroup1;
         }
        return strPattern;
     }

    private String getMatcherGroup(String strGroup)
    {
            try
            {
                int nGroup = Integer.parseInt(strGroup);
                String strGroup1 = m_matcher.group(nGroup);
                return strGroup1;
            }
            catch(NumberFormatException e)
            {
                // may not be an error: text, black hard-coded values are passed
                System.out.println("NumberFormatException: " + m_strName + "-" + strGroup);
            }
         return strGroup;
     }
}
}
