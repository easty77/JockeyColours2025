/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.config;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 *
 * @author Simon
 */
@Slf4j
public abstract class ConfigFile {
    // functions moved to FileUtils, but Mero package does not include UtilsENELibrary
protected InputStream loadFile(String strFileName)
{
    InputStream is = null;
    try
    {
        log.info("Loading File {}-{}", strFileName, getClass().getClassLoader().getResource(strFileName));
        File file = new File(getClass().getClassLoader().getResource(strFileName).getFile());
        is = new FileInputStream(file);
    }
    catch(FileNotFoundException e)
    {
        System.out.println("FileNotFoundException: " + strFileName);
    }
        return is;
}
    protected URL loadURL(String strFileName)
    {
        URL url = null;
        try
        {
            log.info("Loading URL {}-{}", strFileName, getClass().getClassLoader().getResource(strFileName));
            File file = new File(getClass().getClassLoader().getResource(strFileName).getFile());
            InputStream is = new FileInputStream(file);
            url = file.toURI().toURL();;
        }
        catch(FileNotFoundException e)
        {
            System.out.println("FileNotFoundException: " + strFileName);
        }
        catch(MalformedURLException e)
        {
            System.out.println("MalformedURLException: " + strFileName);
        }
        return url;
    }
}
