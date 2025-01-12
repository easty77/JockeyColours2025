/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.service;

import ene.eneform.colours.domain.RacingColours;
import ene.eneform.mero.colours.ENEParsedRacingColours;
import ene.eneform.mero.colours.ENERacingColours;
import ene.eneform.mero.config.*;
import ene.eneform.mero.factory.ENEMeroFactory;
import ene.eneform.mero.factory.SVGFactoryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 *
 * @author Simon
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MeroService {
    private final ENEColoursEnvironment environment;
    private final RacingColoursHandler handler;

    @Value("${ene.eneform.mero.MAX_NR_EXPAND_ITERATIONS}")
    private Integer MAX_NR_EXPAND_ITERATIONS;

    private ConfigExpands configExpands;
    private ConfigPatterns configPatterns;
    private ConfigColours configColours;
    private ConfigFabrics configFabrics;
    private ConfigCompares configCompares;
    private ConfigOrganisations configOrganisations;
    private AbbreviationsHandler abbreviationsHandler;

    public String parseDescription(String strDescription)
    {
        String strLanguage = environment.DEFAULT_LANGUAGE;
        ENEParsedRacingColours colours = handler.createParsedRacingColours(strLanguage, strDescription, "");
        
        return colours.getColours().getDefinition();
    }
    public String generateSVGContentFromDescription(String description) {
        return generateSVGContentFromDefinition(parseDescription(description));
    }

    public String generateSVGContentFromDefinition(String strDefinition)
    {
        log.info("generateSVGContent {}", strDefinition);
        return generateSVGContentFromDefinition(strDefinition, null, null, false);
    }
   public String generateSVGContentFromDefinition(String strDefinition, String strBackgroundColour)
   {
       return generateSVGContentFromDefinition(strDefinition, strBackgroundColour, null, false);
   }
public String generateSVGContentFromDefinition(String strDefinition, String strBackgroundColour, Point capOrigin, boolean bCompress)
    {
        String strSVGContent = "";
        String strLanguage = environment.DEFAULT_LANGUAGE;
        String[] astrElements = strDefinition.split("\\|");
        if (astrElements.length == 3) {
            ENERacingColours colours = createRacingColours(strLanguage, "",
                    astrElements[0],
                    astrElements[1],
                    astrElements[2]);
            ENEMeroFactory factory = new ENEMeroFactory(environment, colours, strLanguage);
            if (capOrigin != null)
                factory.setCapOrigin(capOrigin);
            Document document = factory.generateSVGDocument("", 1, strBackgroundColour);
            strSVGContent = SVGFactoryUtils.convertSVGNode2String(document, bCompress);
        }
        return strSVGContent;
    }
    public void generateSVGFileFromDescription(String description, String strDirectory, String strFileName, String strBackgroundColour, Point capOrigin, boolean bCompress)
    {
        generateSVGFileFromDefinition(parseDescription(description), strDirectory, strFileName, strBackgroundColour, capOrigin, bCompress);
    }
    public void generateSVGFileFromDefinition(String strDefinition, String strDirectory, String strFileName, String strBackgroundColour, Point capOrigin, boolean bCompress)
    {
        try
        {
            OutputStreamWriter writer = createWriter(strDirectory, strFileName);
            String strSVG = generateSVGContentFromDefinition(strDefinition, strBackgroundColour, capOrigin, bCompress);
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
public ENEParsedRacingColours createFullRacingColours(String language, String description, String owner) {
        return handler.createParsedRacingColours(
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

            ENERacingColours colours = handler.createRacingColours(language, description, jacket, sleeves, cap);

        //RacingColoursParse.onCreate();

        return colours;

    }
    // from ENEColoursEnvironment

    public Document generateSVGDocument(ENERacingColours racingcolours, String strLanguage,
                               String strMeroId, double dScale, String strBackgroundColour) {
    ENEMeroFactory factory = new ENEMeroFactory(environment, racingcolours,  strLanguage);
    return factory.generateSVGDocument(strMeroId, dScale, strBackgroundColour);
    }
    public String addJockeySilks(SVGGraphics2D svgGenerator, SVGGeneratorContext ctx, ENERacingColours colours, String strColours, String strLanguage, double dScale, String strSuffix, HashMap<String,Element> hmDefintions, String strBackgroundColour)
    {
        // called when adding multiple Mero images to a document
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
