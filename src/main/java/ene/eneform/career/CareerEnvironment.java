/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.career;

import ene.eneform.colours.database.JCEventsFactory;
import ene.eneform.mero.config.ENEColoursEnvironment;
import ene.eneform.smartform.factory.SmartformRaceFactory;
import ene.eneform.utils.ENEStatement;
import ene.eneform.utils.ExecuteURL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Simon
 */
public class CareerEnvironment {

    private SAXParser parser = null;
    private ConfigCareer configCareer = null;

    private CareerEnvironment()
    {
        // load configuration files
        //System.out.println(System.getProperty("user.dir"));
initialise();
    }
    private void initialise()
    {
        // load configuration files
        //System.out.println(System.getProperty("user.dir"));

        boolean bSuccess = createParsers();
        if (bSuccess)
        {
            configCareer = new ConfigCareer(parser);
        }
    }
    public void reset()
    {
        initialise();
    }
    private boolean createParsers()
    {
       SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	try {
	  parser = parserFactory.newSAXParser();
	} catch ( ParserConfigurationException pce ) {
	    System.out.println("Error setting up the XML Parser. The parser is not properly configured. Loading aborted.");
            return false;
	} catch ( SAXException saxe ) {
	    System.out.println("Error setting up the XML Parser. Loading aborted.");
            return false;
	}

        return true;
    }
public CareerDefinition getCareer(String strId)
{
    return configCareer.getCareer(strId);
}
public ArrayList<String> getCareerNames()
{
    return configCareer.getCareerNames();
}

public String getCareerRaceName(String strKey)
{
     return configCareer.getCareerRaceName(strKey);
}

public JSONArray getLatestCareerUpdates(ENEStatement statement)
{
    int nDays = JCEventsFactory.getEventDayInterval(statement, "latest_career_update");
    JSONArray array = getCareerUpdates(statement, nDays);
    JCEventsFactory.updateEventDate(statement, "latest_career_update");
    return array;
}
public JSONArray getCareerUpdates(ENEStatement statement, int nDays)
{
    return SmartformRaceFactory.getDateRunners(statement, getCareerNames(), nDays);
}


public void insertCareerHorses(ENEStatement statement)
{
    int nInserts = CareerDBFactory.insertCareerNames(statement);
    //if (nInserts > 0)
        CareerDBFactory.insertCareerHorses(statement);
}
}
