/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;

import ene.eneform.mero.config.ConfigFile;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.svg.SVGDocument;

/**
 *
 * @author Simon
 */
public class ConfigSvg extends ConfigFile implements Serializable{
    @Value("${ene.eneform.mero.SVG_MERO_DIRECTORY}")
    private static String SVG_MERO_DIRECTORY;
    @Value("${ene.eneform.mero.SVG_SHAPE_DIRECTORY}")
    private static String SVG_SHAPE_DIRECTORY;

     private HashMap<String,String> m_hmSvgFileContent = new HashMap<String,String>();
     // Only store document objects for Mero, as for others want to replace colours, tags etc and not easy to take copy
     private transient HashMap<String,SVGDocument> m_hmSvgDocuments = new HashMap<String,SVGDocument>();
     private transient SAXSVGDocumentFactory m_svgFactory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
     
    ConfigSvg()
     {
    String[] astrSVGFiles = {"jacket", "sleeves", "cap" };
    
    for(int i = 0; i < astrSVGFiles.length; i++)
    {
        String strFileName = astrSVGFiles[i];
        try
         {
             SVGDocument document = loadSVGShapeFile(strFileName , strFileName, SVG_MERO_DIRECTORY);
             m_hmSvgDocuments.put(strFileName, document);
         }
         catch(IOException e)
         {
             System.out.println("IOException loadSVGShapeFile: " + strFileName + "-" + e.getMessage());
         }
     }
}
public boolean loadSVG(String strFileName)
{
    try
    {
        SVGDocument document = loadSVGShapeFile(strFileName);
        m_hmSvgDocuments.put(strFileName, document);
    }
    catch(IOException e)
    {
        System.out.println("IOException loadSVGShapeFile: " + strFileName + "-" + e.getMessage());
        return false;
    }
    
    return true;
}

    public synchronized SVGDocument getSVGDocument(String strShape)
    {
        // after serialization, transient object is null
        if (m_hmSvgDocuments == null)
            m_hmSvgDocuments = new HashMap<String,SVGDocument>();
        if (m_svgFactory == null)
            m_svgFactory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
        
        SVGDocument svgDoc = m_hmSvgDocuments.get(strShape);
        if (svgDoc == null)
        {
           String strSVGContent = m_hmSvgFileContent.get(strShape);
            if (strSVGContent != null)
            {
                // file is available but not the SVG Documeent - as that can't be serialized
                try
                {
                    svgDoc = m_svgFactory.createSVGDocument(".", new StringReader(strSVGContent));
                    m_hmSvgDocuments.put(strShape, svgDoc);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            } 
        }
        
        if ((svgDoc == null) && loadSVG(strShape))
            svgDoc = m_hmSvgDocuments.get(strShape);
        
        // this is returning the cached Document, so if it is updated (tags replaced) then the cached version will be changed 
        //and the updated version (with replaced tags) will be given to next requestor
        // much better to get content and create Document (different each time) from content
        return svgDoc;
    }
    
     private SVGDocument loadSVGShapeFile(String strShape) throws IOException
    {
        return loadSVGShapeFile(strShape, strShape, SVG_SHAPE_DIRECTORY);
    }
    private SVGDocument loadSVGShapeFile(String strShape, String strFileName, String strDirectory) throws IOException
    {
        String strFullFileName =  strDirectory + strFileName + ".svg";
        InputStream is = loadFile(strFullFileName);
        if (is != null)
        {
            String strSVGContent = IOUtils.toString(is, "UTF-8");
            m_hmSvgFileContent.put(strShape, strSVGContent);
            String strURI = loadURL(strFullFileName).toExternalForm();
            SVGDocument document = m_svgFactory.createSVGDocument(strURI);
        
            return document;
        }
        else
        {
            System.out.println("loadSVGShapeFile not found: " + strFullFileName);
        }
        
        return null;
   }
    public InputStream getSVGStream(String strShape)
    {
        InputStream is = null;
        String strShapeDirectory = SVG_SHAPE_DIRECTORY;
        String strFilename = strShapeDirectory + "/" + strShape + ".svg";
        try
        {
            is = new FileInputStream(strFilename);
        } catch (FileNotFoundException e)
        {
            System.out.println("FileNotFoundException: " + strFilename + "-" + e.getMessage());
         }
        return is;
    }
public String getSVGContent(String strShape)
{
    String strContent = "";
    InputStream is = getSVGStream(strShape);
    if (is != null)
    {
        try
        {
            strContent = IOUtils.toString(is, "utf-8"); 
            is.close();
        }
        catch(IOException e)
        {

        }
    }
    return strContent;
}
/*
private GraphicsNode convertSVGContent(String strSVGContent) 
{
    GraphicsNode svgIcon = null;
    try
    {
        Document doc = m_svgFactory.createSVGDocument(".", new StringReader(strSVGContent));
        svgIcon = convertSVGDocument(doc);    
    }
    catch(IOException e)
    {
        
    }
    return svgIcon;
} */
private SVGDocument convertSVGContentDocument(String strSVGContent)
{
    SVGDocument svgDoc = null;
    try
     {
         svgDoc = m_svgFactory.createSVGDocument(".", new StringReader(strSVGContent));
     }
     catch(Exception e)
     {
         e.printStackTrace();
     }
    return svgDoc;
}
/*
private static GraphicsNode convertSVGDocument(Document doc) 
{
    // https://community.oracle.com/thread/1350087?start=0&tstart=0
    GraphicsNode svgIcon = null;
    try 
    {
         UserAgent userAgent = new UserAgentAdapter();
         DocumentLoader loader = new DocumentLoader(userAgent);
         BridgeContext ctx = new BridgeContext(userAgent, loader);
         ctx.setDynamicState(BridgeContext.DYNAMIC);
         GVTBuilder builder = new GVTBuilder();
         svgIcon = builder.build(ctx, doc);
    } 
    catch (Exception excp) 
    {
         svgIcon = null;
         excp.printStackTrace();
    }
    return svgIcon;
} */
public SVGDocument getSVGContentDocument(String strSVGContent) 
{
    return convertSVGContentDocument(strSVGContent);
}
/*
public GraphicsNode getSVGContentGraphicsNode(String strSVGContent) 
{
    return convertSVGContent(strSVGContent);
}

public GraphicsNode getSVGDocumentGraphicsNode(Document doc) 
{
    return convertSVGDocument(doc);
}

public GraphicsNode getSVGGraphicsNode(String strShape) 
{
    Document doc = getSVGDocument(strShape);
    return convertSVGDocument(doc);
}
*/
    private boolean keywordCheck(String strPattern)
    {
        // returns true if DOES NOT contain any keywords
        String[] astrWords = strPattern.split(" ");
        String astrKeywords[] = {"jacket", "sleeves", "cap"};
        for(int i = 0; i < astrKeywords.length; i++)
        {
            for(int j = 0; j < astrWords.length; j++)
            {
                if (astrKeywords[i].equals(astrWords[j]))
                    return false;
            }
        }
        return true;
    }
    private String[] directorySVGFiles(String strDirectory)
    {
       File dir = new File(strDirectory);
       FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".svg");
            }
        };

        return dir.list(filter);

    }
}
