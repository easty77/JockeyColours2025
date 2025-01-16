/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.mero.config;

import ene.eneform.service.mero.tartan.ENETartan;
import ene.eneform.service.mero.tartan.ENETartanUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Simon
 */
public class ConfigTartans extends ConfigXML {
    // no language
        private HashMap<String,ENETartan> m_hmTartans = new HashMap<String,ENETartan>();
    private ArrayList<ENETartan> m_alTartans = new ArrayList<ENETartan>();

    public ConfigTartans(SAXParser parser, String fileName)
    {
        super(parser, fileName);
        setHandler(new ENETartansHandler());
       loadXML();
    }
  public Set<String> getTartans()
    {
        return m_hmTartans.keySet();
    }

   public boolean isTartan(String strTartan)
    {
        return m_hmTartans.containsKey(strTartan);
    }
   public ENETartan getTartan(String strTartan)
    {
        return m_hmTartans.get(strTartan);
    }
   public String getTartanSVG(String strTartan)
    {
        ENETartan tartan = m_hmTartans.get(strTartan);
        String strSett = tartan.extendSett();
        return ENETartanUtils.generateSVGTartan(strSett); 
    }
   public ArrayList<ENETartan> getTartanList()
    {
        return m_alTartans;
    }
private class ENETartansHandler extends DefaultHandler implements Serializable
{
 
    @Override public void startDocument ()
    {
    }

    @Override public void endDocument ()
    {
     }

    @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if ("tartan".equals(qName))
        {
            String strId = attributes.getValue("id");
            String strSett = attributes.getValue("sett");
            String strPivots = attributes.getValue("pivots");
            int nPivots = 1;
            if (strPivots != null)
            {
                try
                {
                    nPivots = Integer.parseInt(strPivots);
                }
                catch(NumberFormatException e)
                {

                }
            }
            String strScaleFactor = attributes.getValue("scalefactor");
            // SE 20150928 change scale factor to 1.0 - for generating SVG
            double dScaleFactor = 1.0;
            if (strScaleFactor != null)
            {
                try
                {
                    dScaleFactor = Double.parseDouble(strScaleFactor);
                }
                catch(NumberFormatException e)
                {

                }
            }


            ENETartan item = new ENETartan(strId, strSett, nPivots, dScaleFactor);
            m_hmTartans.put(strId, item);
            m_alTartans.add(item);
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
