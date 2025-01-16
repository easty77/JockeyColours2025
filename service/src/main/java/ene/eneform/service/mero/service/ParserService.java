package ene.eneform.service.mero.service;

import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ParserService {
    private SAXParser parser;
    public ParserService() {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        try {
            parser = parserFactory.newSAXParser();
        } catch (Exception saxe) {
            System.out.println("Error setting up the XML Parser. Loading aborted.");
        }
    }
    public synchronized void parse(InputStream is, DefaultHandler dh) throws SAXException, IOException
    {
        parser.parse(new InputSource(is), dh);
    }
}
