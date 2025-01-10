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
    
    private static CareerEnvironment sm_careerEnvironment = null;

    private static SAXParser sm_parser = null;
    private ConfigCareer m_configCareer = null;

     public static synchronized CareerEnvironment getInstance()
    {
        if (sm_careerEnvironment == null)
        {
            sm_careerEnvironment = new CareerEnvironment();
            sm_careerEnvironment.initialise();
        }

        return sm_careerEnvironment;
    }
    private CareerEnvironment()
    {
        // load configuration files
        //System.out.println(System.getProperty("user.dir"));

    }
    private void initialise()
    {
        // load configuration files
        //System.out.println(System.getProperty("user.dir"));

        boolean bSuccess = createParsers();
        if (bSuccess)
        {
            String strFileName = ENEColoursEnvironment.getInstance().getFilename("career_defns");
            m_configCareer = new ConfigCareer(sm_parser, strFileName);
            m_configCareer.load();
        }
    }
    public void reset()
    {
        sm_careerEnvironment = null;
    }
    private boolean createParsers()
    {
       SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	try {
	  sm_parser = parserFactory.newSAXParser();
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
    return m_configCareer.getCareer(strId);
}
public ArrayList<String> getCareerNames()
{
    return m_configCareer.getCareerNames();
}

public String getCareerRaceName(String strKey)
{
     return m_configCareer.getCareerRaceName(strKey);
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
public JSONObject generateLatestCareerUpdates(ENEStatement statement)
{
    int nDays = JCEventsFactory.getEventDayInterval(statement, "latest_career_generate");
    JSONObject obj = generateCareerUpdates(statement, nDays);
    JCEventsFactory.updateEventDate(statement, "latest_career_generate");
    return obj;
}
public JSONObject generateCareerUpdates(ENEStatement statement, int nDays)
{
    if (nDays > 0)
    {
        ArrayList<String> alNames = SmartformRaceFactory.getDateRunnerNames(statement, nDays);
        return generateCareerUpdates(statement, alNames);
    }
    return new JSONObject();
}
public String generateCareer(ENEStatement statement, String strHorse)
{
    String strReturnURL = null;
    CareerDefinition career = CareerEnvironment.getInstance().getCareer(strHorse);
    if (career != null)
    {
        try
        {
          (new CareerSVGFactory(statement, career, true)).generateCareer(false); 
            String strDirectory = "horses";
            if (career.getType().equalsIgnoreCase("meeting"))
                strDirectory = "meetings";
            String strServerName = ENEColoursEnvironment.getInstance().getVariable("SERVER_NAME");
            //ExecuteURL.openBrowser(strServerName + strDirectory + "/" + career.getName().replace(" ", "_") + ".html");
           strReturnURL = strServerName + strDirectory + "/" + career.getName().replace(" ", "_") + ".html";    // return url of updated page
        }
        catch(Exception e)
        {
            System.out.println("Error creating Career SVG for: " + strHorse);
            e.printStackTrace();
        }
    }
    else
        System.out.println("Career not found: " + strHorse);
    
    return strReturnURL;
}
public JSONObject generateCareerUpdates(ENEStatement statement, List<String> alNames)
{
    JSONObject obj = new JSONObject();
    JSONArray  pages = new JSONArray();
    for(int i = 0; i < alNames.size(); i++)
    {
        String strHorse = alNames.get(i).trim();
        String strReturnURL = generateCareer(statement, strHorse);
        if(strReturnURL != null)
            pages.put(strReturnURL);
    } 
    if (pages.length() > 0)
        obj.put("pages", pages);
    
    return obj;
}
public void generateMeetingUpdates(ENEStatement statement, String strCourse, int nMonth, int nYear)
{
    generateMeetingUpdates(statement, strCourse, nMonth, nYear, nYear);
}
public void generateMeetingUpdates(ENEStatement statement, String strCourse, int nMonth, int nStartYear, int nEndYear)
{
    for (int i = nStartYear; i <= nEndYear; i++)
    {
        CareerDefinition career = new MeetingDefinition(strCourse.replace("_", " ") + " " + i, strCourse + "|" + nMonth + "|" + i);
        try
        {
          (new CareerSVGFactory(statement, career, true)).generateCareer(false); 
          String strDirectory = "meetings";
          String strServerName = ENEColoursEnvironment.getInstance().getVariable("SERVER_NAME");
          ExecuteURL.openBrowser(strServerName + strDirectory + "/" + career.getName().replace(" ", "_") + ".html");
        }
        catch(Exception e)
        {
            System.out.println("Error creating Meeting Career SVG for: " + career.getName());
            e.printStackTrace();
        }
    } 
    
}
public void generateMeetingUpdates(ENEStatement statement, String strName, int nMeeting)
{
    CareerDefinition career = new DayDefinition(nMeeting, strName);
    try
    {
      (new CareerSVGFactory(statement, career, true)).generateCareer(false); 
      String strDirectory = "meetings";
      String strServerName = ENEColoursEnvironment.getInstance().getVariable("SERVER_NAME");
      ExecuteURL.openBrowser(strServerName + strDirectory + "/" + career.getId() + ".html");
    }
    catch(Exception e)
    {
        System.out.println("Error creating Meeting Career SVG for: " + career.getName());
        e.printStackTrace();
    }
}
public void insertCareerHorses(ENEStatement statement)
{
    int nInserts = CareerDBFactory.insertCareerNames(statement);
    //if (nInserts > 0)
        CareerDBFactory.insertCareerHorses(statement);
}
}
