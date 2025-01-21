/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.mero.service;

import ene.eneform.port.in.mero.MeroServiceInterface;
import ene.eneform.port.out.mero.model.ENEParsedRacingColours;
import ene.eneform.service.mero.config.ENEColoursEnvironment;
import ene.eneform.service.mero.factory.ENEMeroFactory;
import ene.eneform.service.mero.factory.SVGFactoryUtils;
import ene.eneform.service.mero.model.colours.ENERacingColours;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.awt.*;
import java.io.*;
import java.nio.file.Paths;

/**
 *
 * @author Simon
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MeroService implements MeroServiceInterface {
    private final ENEColoursEnvironment environment;
    private final RacingColoursHandler handler;

@Override
    public String parseDescription(String strDescription)
    {
        String strLanguage = environment.DEFAULT_LANGUAGE;
        ENEParsedRacingColours colours = handler.createParsedRacingColours(strLanguage, strDescription, "");
        
        return colours.getParseInfo().getDefinition();
    }
    @Override
    public String generateSVGContentFromDescription(String description) {
        return generateSVGContentFromDefinition(parseDescription(description));
    }
@Override
    public String generateSVGContentFromDefinition(String strDefinition)
    {
        log.info("generateSVGContent {}", strDefinition);
        return generateSVGContentFromDefinition(strDefinition, null, null, false);
    }
    @Override
   public String generateSVGContentFromDefinition(String strDefinition, String strBackgroundColour)
   {
       return generateSVGContentFromDefinition(strDefinition, strBackgroundColour, null, false);
   }
   @Override
public String generateSVGContentFromDefinition(String strDefinition, String strBackgroundColour, Point capOrigin, boolean bCompress)
    {
        String strSVGContent = "";
        String strLanguage = environment.DEFAULT_LANGUAGE;
        String[] astrElements = strDefinition.split("\\|");
        if (astrElements.length == 3) {
            ENERacingColours colours = handler.createRacingColours(strLanguage, "",
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
    @Override
    public void generateSVGFileFromDescription(String description, String strDirectory, String strFileName, String strBackgroundColour, Point capOrigin, boolean bCompress)
    {
        generateSVGFileFromDefinition(parseDescription(description), strDirectory, strFileName, strBackgroundColour, capOrigin, bCompress);
    }
    @Override
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
    @Override
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
   @Override
public ENEParsedRacingColours createParsedRacingColours(String language, String description, String owner) {
        return handler.createParsedRacingColours(
                    language, description, owner);
}


}
