/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.service;

import ene.eneform.colours.domain.RacingColours;
import ene.eneform.mero.action.ENEJacketSVGAction;
import ene.eneform.mero.action.ENEPatternAction;
import ene.eneform.mero.action.ENESVGAction;
import ene.eneform.mero.colours.ENEColoursElement;
import ene.eneform.mero.colours.ENEColoursElementPattern;
import ene.eneform.mero.colours.ENERacingColours;
import ene.eneform.mero.colours.FullRacingColours;
import ene.eneform.mero.config.*;
import ene.eneform.mero.fabric.ENEFabricItem;
import ene.eneform.mero.factory.ENEMeroFactory;
import ene.eneform.mero.factory.SVGFactoryUtils;
import ene.eneform.mero.tartan.ENETartan;
import ene.eneform.mero.tartan.ENETartanItem;
import ene.eneform.mero.tartan.ENETartanUtils;
import ene.eneform.mero.utils.ENEColourItem;
import ene.eneform.mero.utils.ENEFillItem;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.anim.dom.SVGOMPatternElement;
import org.apache.batik.anim.dom.SVGOMTextElement;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGGElement;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.*;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Simon
 */
@Service
public class MeroService {
    private final ENEColoursEnvironment environment;

    @Value("${ene.eneform.mero.MAX_NR_EXPAND_ITERATIONS}")
    private static Integer MAX_NR_EXPAND_ITERATIONS;

    private ConfigExpands configExpands;
    private ConfigPatterns configPatterns;
    private ConfigColours configColours;
    private ConfigFabrics configFabrics;
    private ConfigCompares configCompares;
    private ConfigOrganisations configOrganisations;
    private AbbreviationsHandler abbreviationsHandler;

    public MeroService(ENEColoursEnvironment environment) {
        this.environment = environment;
    }
    public String parseDescription(String strDescription)
    {
        String strLanguage = environment.DEFAULT_LANGUAGE;
        FullRacingColours colours = createFullRacingColours(strLanguage, strDescription, "");
        
        return colours.getDescription();
    }

    public String generateSVGContent(String strDefinition)
    {
        return generateSVGContent(strDefinition, null, null, false);
    }
   public String generateSVGContent(String strDefinition, String strBackgroundColour)
   {
       return generateSVGContent(strDefinition, strBackgroundColour, null, false);
   }
   public Rectangle getViewBox(Point capOrigin)
    {
        ENEMeroFactory factory = new ENEMeroFactory(environment, null, environment.DEFAULT_LANGUAGE);
        if (capOrigin != null)
            factory.setCapOrigin(capOrigin);
        
        return factory.getViewBox(capOrigin);
    }
public String generateSVGContent(String strDefinition, String strBackgroundColour, Point capOrigin, boolean bCompress)
    {
        String strSVGContent = "";
        String strLanguage = environment.DEFAULT_LANGUAGE;
        String[] astrElements = strDefinition.split("\\|");
        ENERacingColours colours = createRacingColours(strLanguage, "",
                astrElements[0],
                astrElements[1],
                astrElements[2]);
        ENEMeroFactory factory = new ENEMeroFactory(environment, colours, strLanguage);
        if (capOrigin != null)
            factory.setCapOrigin(capOrigin);
        Document document = factory.generateSVGDocument("", 1, strBackgroundColour);
        strSVGContent = SVGFactoryUtils.convertSVGNode2String(document, bCompress);
        return strSVGContent;
    }
    public void generateSVG(String strDefinition, String strDirectory, String strFileName, String strBackgroundColour, Point capOrigin, boolean bCompress)
    {
        try
        {
            OutputStreamWriter writer = createWriter(strDirectory, strFileName);
            String strSVG = generateSVGContent(strDefinition, strBackgroundColour, capOrigin, bCompress);
            writer.write(strSVG, 0, strSVG.length());
            writer.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void generatePNG(String strDefinition, String strDirectory, String strFileName, String strBackgroundColour, Point capOrigin, boolean bCompress)
    {
        //String strSVG = generateSVGContent(strDefinition, strBackgroundColour, capOrigin, bCompress);
        try
        {
            String strSVG = Paths.get(strDirectory + "/" + strFileName + ".svg").toUri().toURL().toString();
            TranscoderInput input_svg_image = new TranscoderInput(strSVG);        
            //Step-2: Define OutputStream to PNG Image and attach to TranscoderOutput
            OutputStream png_ostream = new FileOutputStream(strDirectory + "/" + strFileName + ".png");
            TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);              
            // Step-3: Create PNGTranscoder and define hints if required
            PNGTranscoder my_converter = new PNGTranscoder();    
            my_converter.addTranscodingHint(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE, true);
            // Step-4: Convert and Write output
            my_converter.transcode(input_svg_image, output_png_image);
            // Step 5- close / flush Output Stream
            png_ostream.flush();
            png_ostream.close();      
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }       
 private OutputStreamWriter createWriter(String strDirectory, String strFileName) throws FileNotFoundException, UnsupportedEncodingException
   {
       boolean bOverwrite = true;
       OutputStreamWriter writer = null;
       // returns null if file already exists
       strDirectory = strDirectory.replace("\\", "/");
       if (strDirectory.lastIndexOf("/") < (strDirectory.length() - 1))
           strDirectory += "/";
        String strFullName = strDirectory + strFileName + ".svg";
        File f = new File(strFullName);
        //System.out.println("createWriter: " + strFullName);
        if(bOverwrite || (!f.exists())) 
        {
            FileOutputStream fos = new FileOutputStream(strDirectory + strFileName + ".svg");
            writer = new OutputStreamWriter(fos, "UTF-8");
        }
        
       return writer;
   }
public FullRacingColours createFullRacingColours(String language, String description, String owner) {
        return new FullRacingColours(environment,
                    language, description, owner);
}

public ENERacingColours createRacingColours(String language, String description, RacingColours racingColours) {
    if (racingColours != null) {
        return createRacingColours(language, description, racingColours.getJacket(),
                racingColours.getSleeves(), racingColours.getCap());
    }
    else
    {
        return createFullRacingColours(language, description, "").getColours();
    }
    }

    public ENERacingColours createRacingColours(String language, String description, String jacket, String sleeves, String cap) {

            ENERacingColours colours = new ENERacingColours(language,
                    createJacket(language, jacket),
                    createSleeves(language,sleeves),
                    createCap(language, cap));
            colours.setDescription(description);

        //RacingColoursParse.onCreate();

        return colours;

    }
    // from ENEColoursEnvironment
    public ENEColoursElement createJacket(String language, String strDefinition) {
        return new ENEColoursElement(environment, language, ENEColoursElement.JACKET, strDefinition);
    }

    public ENEColoursElement createCap(String language, String strDefinition) {
        return new ENEColoursElement(environment, language, ENEColoursElement.CAP, strDefinition);
    }

    public ENEColoursElement createSleeves(String language, String strDefinition) {
        return new ENEColoursElement(environment, language, ENEColoursElement.SLEEVES, strDefinition);
    }
    public Document generateSVGDocument(ENERacingColours racingcolours, String strLanguage,
                               String strMeroId, double dScale, String strBackgroundColour) {
    ENEMeroFactory factory = new ENEMeroFactory(environment, racingcolours,  strLanguage);
    return factory.generateSVGDocument(strMeroId, dScale, strBackgroundColour);
    }
    public String addJockeySilks(SVGGraphics2D svgGenerator, SVGGeneratorContext ctx, ENERacingColours colours, String strColours, String strLanguage, double dScale, String strSuffix, HashMap<String,Element> hmDefintions, String strBackgroundColour)
    {
        // called when ading multiple Mero images to a document
        Element element = buildJockeySilks(ctx, colours, strColours, strLanguage, dScale, strSuffix, hmDefintions, strBackgroundColour);
        svgGenerator.getDOMTreeManager().addOtherDef(element);
        return element.getAttributeNS(null, "id");
    }
    public Element buildJockeySilks(SVGGeneratorContext ctx, ENERacingColours colours, String strColours, String strLanguage, double dScale, String strSuffix, HashMap<String,Element> hmDefinitions, String strBackgroundColour) {
        // called when ading multiple Mero images to a document
        Document document = ctx.getDOMFactory();
        ENEMeroFactory factory = new ENEMeroFactory(environment, colours, strLanguage, document, strSuffix, hmDefinitions); //use strSuffix to differentiate between multiple instances of the same SVG pattern
        document = factory.generateSVGDocument(strColours, dScale, strBackgroundColour);     // use strColours as id
        return document.getDocumentElement();
    }
}
