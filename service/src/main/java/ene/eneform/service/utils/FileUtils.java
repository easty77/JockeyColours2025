/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;

/**
 *
 * @author Simon
 */
public class FileUtils {
    
public static List<String> readOriginalFile(String filename, String strEncoding) throws IOException {
        //FileReader fileReader = new FileReader(filename);
        //BufferedReader bufferedReader = new BufferedReader(fileReader);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
        new FileInputStream(filename), strEncoding
        ));
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines;
    }
public static String readFile(String filename, Charset charset) 
{
    try 
    {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
        new FileInputStream(filename), charset
        ));
        String strContent = "";
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            strContent += (line + "\n");
        }
        bufferedReader.close();
        return strContent;
    } 
    catch (IOException e) 
    {
        System.out.println("IOException: " + e.getMessage());
        return "";
    }
}
private static void setFilePermissions(String strFileName)
{
       try
        {
            Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            perms.add(PosixFilePermission.GROUP_READ);
            perms.add(PosixFilePermission.GROUP_WRITE);
            perms.add(PosixFilePermission.OTHERS_READ);
            perms.add(PosixFilePermission.OTHERS_WRITE);
            Files.setPosixFilePermissions(Paths.get(strFileName), perms);
        }
        catch(IOException e)
        {
            // Windows
            System.out.println("IOException: " + e.getMessage());
        }
        catch(UnsupportedOperationException e)
        {
            // Windows
            System.out.println("UnsupportedOperationException: " + e.getMessage());
        }
    
}
public static boolean writeFile(String strFileName, List<String> lines, Charset charset, boolean bOverwrite)
{
    try 
    {
        if (!bOverwrite)
        {
            File f = new File(strFileName);
            if(f.exists()) 
            {
                return false;
            }
        }

        Files.write(Paths.get(strFileName), lines, charset);
        setFilePermissions(strFileName);
 
    } 
    catch (FileNotFoundException e) 
    {
        System.out.println("FileNotFoundException: " + e.getMessage());
        return false;
    } 
    catch (IOException e) 
    {
        System.out.println("IOException: " + e.getMessage());
        return false;
    }
    return true;
}
public static boolean writeFile(String strFileName, String strContent, Charset charset, boolean bOverwrite)
{
    try 
    {
        System.out.println("writeFile: " + strFileName);
        if (!bOverwrite)
        {
            File f = new File(strFileName);
            if(f.exists()) 
            {
                return false;
            }
        }

        Files.write(Paths.get(strFileName), strContent.getBytes(charset));
        setFilePermissions(strFileName);
    } 
    catch (FileNotFoundException e) 
    {
        System.out.println("writeFile FileNotFoundException: " + e.getMessage());
        return false;
    } 
    catch (IOException e) 
    {
        System.out.println("IOException: " + e.getMessage());
        return false;
    }
    return true;
}
public static InputStream loadFile(String strFileName, Class c)
{
        System.out.println("loadFile: " + strFileName);
        InputStream is = null;
        if (c!= null)
            is = c.getResourceAsStream(strFileName);
        if (is == null)
        {
            try
            {
                if (strFileName.indexOf(":") < 0)
                {
                    // relying on current directory
                    File directory = new File (".");
                     try
                     {
                       System.out.println ("Current directory's canonical path: " + directory.getCanonicalPath());
                       System.out.println ("Current directory's absolute  path: " + directory.getAbsolutePath());
                     }
                     catch(Exception e)
                     {
                       System.out.println("Exceptione is ="+e.getMessage());
                     } 
                } 
              is = new FileInputStream(strFileName);
            }
            catch(FileNotFoundException e)
            {
               System.out.println("FileNotFoundException: " + strFileName);
            }
        }
        if (is == null)
        {
            // relying on classpath
            Properties prop = System.getProperties();
            //System.out.println("Classpath=" + prop.getProperty("java.class.path", null));
            System.out.println("File: " + ClassLoader.getSystemClassLoader().getResource(strFileName));
            is = ClassLoader.getSystemClassLoader().getResourceAsStream(strFileName);
        }
        
        return is;
}
public static URL loadURL(String strFileName, Class c)
{
        System.out.println("loadURL: " + strFileName);
        URL url = null;
        if (c != null)
            url = c.getResource(strFileName);
        if (url == null)
        {
            try
            {
                if (strFileName.indexOf(":") < 0)
                {
                    // relying on current directory
                    File directory = new File (".");
                    try
                     {
                       System.out.println ("Current directory's canonical path: " + directory.getCanonicalPath());
                       System.out.println ("Current directory's absolute  path: " + directory.getAbsolutePath());
                     }
                     catch(Exception e)
                     {
                       System.out.println("Exceptione is ="+e.getMessage());
                     } 
                }
              url = new File(strFileName).toURI().toURL();
            }
            catch(MalformedURLException e)
            {
               System.out.println("MalformedURLException: " + strFileName);
            }
        }
        if (url == null)
        {
            // relying on classpath
            Properties prop = System.getProperties();
            System.out.println("Classpath=" + prop.getProperty("java.class.path", null));

            url = ClassLoader.getSystemClassLoader().getResource(strFileName);
        }
        
        return url;
}
}
