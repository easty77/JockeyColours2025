/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.mero.config;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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
        URL url = getClass().getClassLoader().getResource(strFileName);
        if (url != null) {
            File file = new File(url.getFile());
            is = new FileInputStream(file);
        }
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
            URL url1 = getClass().getClassLoader().getResource(strFileName);
            if (url1 != null) {
                File file = new File(url1.getFile());
                InputStream is = new FileInputStream(file);
                url = file.toURI().toURL();
            }
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
