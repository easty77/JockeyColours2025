/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.config;

import java.awt.Shape;
import java.awt.geom.Area;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import javax.xml.parsers.SAXParser;

import ene.eneform.mero.config.ConfigXML;
import ene.eneform.mero.config.ENEShapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Simon
 */
public class SvgXML extends ConfigXML {
    
   private Area m_area = null;

   public SvgXML(SAXParser parser, String strFileName, String strShapeDirectory)
    {
        super(parser, strShapeDirectory + "/" + strFileName);
     }
    public boolean load(InputStream is)
    {
        setHandler(new SvgXMLHandler());
        try
        {
            parse(new InputSource(is), m_handler);
        }
        catch(Exception e)
        {
         return false;   
        }
        return true;
    }
    public boolean load()
    {
        InputStream is = loadFile(m_strFileName);
        return load(is);
    }
    public Area getArea()
    {
        return m_area;
    }
    private synchronized void parse(InputSource is, DefaultHandler dh) throws SAXException, IOException {
        XMLReader reader = m_parser.getXMLReader();
        reader.setEntityResolver(new DummyEntityResolver());
        reader.setContentHandler(dh);
        reader.parse(is);
    }
    private class SvgXMLHandler extends DefaultHandler {
    
        private Attributes m_currentText = null;
        private Attributes m_currentTSpan = null;
         @Override
        public void startDocument()
        {
         }

        @Override
        public void endDocument()
        {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
            if ("svg".equals(qName))
            {
            }
            else if ("g".equals(qName))
            {
            }
            else if ("defs".equals(qName))
            {
            }
            else if ("pattern".equals(qName))
            {
             }
            else if ("path".equals(qName))
            {
                // attributes: d, style
                addShape(ENEShapeUtils.processPathAttributes(attributes));
            }
            else if ("polygon".equals(qName))
            {
                // attributes: fill, points
                addShape(ENEShapeUtils.processPolygonAttributes(attributes));
            }
            else if ("polyline".equals(qName))
            {
                // attributes: fill, points
                addShape(ENEShapeUtils.processPolylineAttributes(attributes));
            }
            else if ("line".equals(qName))
            {
                // attributes: stroke, x1? x2, y1, y2
                addShape(ENEShapeUtils.processLineAttributes(attributes));
            }
            else if ("ellipse".equals(qName))
            {
               addShape(ENEShapeUtils.processEllipseAttributes(attributes));
            }
            else if ("rect".equals(qName))
            {
                // attributes: x, y, width, height, style
                addShape(ENEShapeUtils.processRectangleAttributes(attributes));
            }
            else if ("circle".equals(qName))
            {
                // attributes: x, y, width, height, style
                addShape(ENEShapeUtils.processCircleAttributes(attributes));
            }
            else if ("text".equals(qName))
            {
                // attributes: x, y plus content
                m_currentText = attributes;
            }
           else if ("tspan".equals(qName))
            {
                m_currentTSpan = attributes;
            }
           else if ("use".equals(qName))
            {
            }
            else if ("linearGradient".equals(qName))
            {
            }
            else if ("radialGradient".equals(qName))
            {
            }
            else if ("stop".equals(qName))
            {
             }
            else if ("clipPath".equals(qName))
            {
            }
            else if ("image".equals(qName))
            {
            }
        }
        @Override
        public void endElement(String uri, String localName,
                String qName) throws SAXException
        {
        }
        @Override
        public void characters(char[] chars, int start, int length) throws SAXException
        {
            String strChars = new String(chars, start, length);
            if (m_currentTSpan != null)
            {
                addShape(ENEShapeUtils.processTextAttributes(m_currentTSpan, strChars));
                m_currentTSpan = null;
             }
            else if (m_currentText != null)
            {
                addShape(ENEShapeUtils.processTextAttributes(m_currentText, strChars));
                m_currentText = null;
            }                
        }
        
        private void addShape(Shape shape)
        {
            if (shape != null)
            {
                if (m_area == null)
                    m_area = new Area(shape);
                else
                    m_area.add(new Area(shape));
            }
        }
    }
private class DummyEntityResolver implements EntityResolver, Serializable
{
 public InputSource resolveEntity(String publicID, String systemID)
        throws SAXException {
        
        return new InputSource(new StringReader(""));
    }
}
}
