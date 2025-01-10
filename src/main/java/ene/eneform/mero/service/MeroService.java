/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.service;

import ene.eneform.mero.colours.ENERacingColours;
import ene.eneform.mero.config.ENEColoursEnvironment;
import ene.eneform.mero.factory.ENEMeroFactory;
import ene.eneform.mero.factory.SVGFactoryUtils;
import ene.eneform.mero.parse.ENEColoursParser;
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
public class MeroService implements MeroServiceInterface {
/*    
    public void main(String[] args) {
        if (args.length > 0)
        {
            if ("parse".equals(args[0]))
            {
                if (args.length > 1)
                {
                    String strDefinition  = parseDescription(args[1]);
                    System.out.println(args[1] + "->" + strDefinition);
                }
            }
            else if ("svg".equals(args[0]))
            {
                if (args.length > 3)
                {
                    generateSVG(args[1], args[2], args[3], "white", new Point(0, 5), false);
                }
            }
        }
    } */
    public String parseDescription(String strDescription)
    {
        String strLanguage = ENEColoursEnvironment.DEFAULT_LANGUAGE;
        ENERacingColours colours = new ENEColoursParser(strLanguage, strDescription, "").parse();
        
        return colours.getDefinition();
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
        ENEMeroFactory factory = new ENEMeroFactory(null, ENEColoursEnvironment.DEFAULT_LANGUAGE);
        if (capOrigin != null)
            factory.setCapOrigin(capOrigin);
        
        return factory.getViewBox(capOrigin);
    }
public String generateSVGContent(String strDefinition, String strBackgroundColour, Point capOrigin, boolean bCompress)
    {
        String strSVGContent = "";
        String strLanguage = ENEColoursEnvironment.DEFAULT_LANGUAGE;
        String[] astrElements = strDefinition.split("\\|");
        ENERacingColours colours = ENEColoursEnvironment.getInstance().createRacingColours(strLanguage, ENEColoursEnvironment.getInstance().createJacket(strLanguage, astrElements[0]), ENEColoursEnvironment.getInstance().createSleeves(strLanguage, astrElements[1]), ENEColoursEnvironment.getInstance().createCap(strLanguage, astrElements[2]));
        ENEMeroFactory factory = new ENEMeroFactory(colours, strLanguage);
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
}
