/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.career;

import ene.eneform.colours.database.AdditionalRaceLinkFactory;
import ene.eneform.colours.database.ENEColoursRunnerFactory;
import ene.eneform.colours.database.JCEventsFactory;
import ene.eneform.colours.database.WikipediaFactory;
import ene.eneform.mero.config.ENEColoursEnvironment;
import ene.eneform.smartform.bos.AdditionalRaceInstance;
import ene.eneform.smartform.bos.SmartformColoursRunner;
import ene.eneform.smartform.bos.SmartformRunner;
import ene.eneform.utils.ENEStatement;
import ene.eneform.utils.FileUtils;
import ene.eneform.utils.JSONUtils;
import ene.eneform.utils.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author simon
 */
public class CareerJSONFactory {
    private static final HashMap<String,String[]> sm_hmNHTopics;
    private static final HashMap<String,String[]> sm_hmFlatTopics;

    static {
    String[] astrNHCategories=new String[]{"juvenile_hurdle", "novice_hurdle","hurdle","novice_chase","chase"};
    String[] astrNHTopics=new String[]{"two_mile","two_half_mile","three_mile"};
    String[] astrFullNHTopics=new String[]{"two_mile","two_half_mile","three_mile", "france", "two_mile_hcap","two_half_mile_hcap","three_mile_hcap"};

    sm_hmNHTopics = new HashMap<String,String[]>();
    sm_hmNHTopics.put("juvenile_hurdle", new String[]{"two_mile", "two_mile_hcap"});
    sm_hmNHTopics.put("hurdle", astrFullNHTopics);
    sm_hmNHTopics.put("chase", new String[]{"two_mile","two_half_mile","three_mile", "france", "two_mile_hcap","two_half_mile_hcap","three_mile_hcap", "national_hcap"});
    sm_hmNHTopics.put("novice_hurdle", astrNHTopics);
    sm_hmNHTopics.put("novice_chase", astrNHTopics); 
    sm_hmNHTopics.put("mares", new String[]{"novice_hurdle","hurdle","novice_chase","chase", "nh_flat"}); 
    sm_hmNHTopics.put("nh_flat", new String[]{"nh_flat"}); 

    String [] astrOlderTopics = {"half", "mile", "quarter", "seven", "sprint", "stayer", "fmshort", "fmlong"}; // early season: older
    String [] astrThreeTopics = {"classics", "derby", "guineas1000", "guineas2000", "oaks", "stleger", 
        "threeyearother", "threeyearsprint"}; // early season: threeyear
    String [] astrTwoTopics = {"twoyearcolt", "twoyearfilly"};      // early season: twoyear
    sm_hmFlatTopics = new HashMap<String,String[]>();
    sm_hmFlatTopics.put("twoyear", astrTwoTopics);        
    sm_hmFlatTopics.put("threeyear", astrThreeTopics);        
    sm_hmFlatTopics.put("older", astrOlderTopics);  
}

    public static JSONArray generateRaceRunnerThumbnailsArray(ENEStatement statement, int nRace, String strSource, boolean bWrite, boolean bOverwrite) {
        JSONArray array = new JSONArray();
        String strFileName = String.valueOf(nRace) + ".json";
        if (!"SF".equals(strSource))
            strFileName = strSource + strFileName;
        strFileName = ENEColoursEnvironment.getInstance().getVariable("JSON_OUTPUT_DIRECTORY") + "races/" + strFileName;
        
        if (bWrite)
        {
            if (!bOverwrite && (new File(strFileName)).exists())
                return array;
        }
        AdditionalRaceInstance arl = AdditionalRaceLinkFactory.getAdditionalRaceLinkObject(statement, nRace, strSource);
        if (arl == null)
            return array;
        boolean bHandicap = arl.isHandicap();
        String strCountry = arl.getCountry(); // 3 letter code, probably wants name?
        ArrayList<String> aRunners = CareerDBFactory.getAllCareerNames(statement);
        ArrayList<SmartformColoursRunner> hRunners = ENEColoursRunnerFactory.getSmartformRaceRunners(statement, arl, -1);
        boolean bComplete = true;
        for (int i = 0; i < hRunners.size(); i++) {
            SmartformColoursRunner runner = hRunners.get(i);
            JSONObject obj = new JSONObject();
            String strName = runner.getName();
            obj.put("horse_name", strName);
            String strFinishPosition = runner.getShortFinishPositionString(); // StringUtils.getOrdinalString(runner.getFinishPosition())
            obj.put("finish_position", strFinishPosition);
            String strDistanceBeaten = runner.getShortDistanceBeatenString();
            if (strDistanceBeaten != null) {
                obj.put("distance_beaten", strDistanceBeaten);
            }
            String strJockeyColours = runner.getJockeyColours();
            if (!"".equals(strJockeyColours)) {
                obj.put("colours", strJockeyColours);
                String strSVGOwner = WikipediaFactory.selectWikipediaOwnerByColours(statement, strJockeyColours, strCountry);
                if (!"".equals(strSVGOwner)) {
                    obj.put("svg_owner_name", strSVGOwner); // say be empty
                }
            }
            if (CareerDBFactory.nameMatch(aRunners, strName) != null) {
                obj.put("html_horse_name", strName.replaceAll(" ", "_"));
            }
            obj.put("age", runner.getAge());
            obj.put("trainer", runner.getTrainerName());
            obj.put("jockey", runner.getJockeyName());
            obj.put("sp", runner.getFullStartingPrice());
            obj.put("weight", SmartformRunner.getWeightString(runner.getWeightPounds()));
            if (bHandicap && runner.getOfficialRating() > 0) {
                obj.put("official_rating", runner.getOfficialRating());
            }
            if (runner.getClothNumber() > 0) {
                obj.put("cloth_nr", runner.getClothNumber());
            }
            if (runner.getStall() > 0) {
                obj.put("stall", runner.getStall());
            }
            if (runner.getTack().hasTack()) {
                obj.put("tack", runner.getTack().getTackString());
            }
            if (!"".equals(runner.getOwnerName())) {
                obj.put("owner", runner.getOwnerName());
            }
            if (!"".equals(runner.getInRaceComment())) {
                obj.put("in_race_comment", runner.getInRaceComment());
            }
            if (obj.get("svg_owner_name") == null)
            {
                System.out.println("Missing svg owner:" + runner.getFinishPositionString() + "-" + runner.getName() + "-" + runner.getOwnerName() + "-" + runner.getJockeyColours());
                bComplete = false;
            }
            array.put(obj);
        }
        if (bWrite && bComplete)
        {
            boolean bCreated = FileUtils.writeFile( strFileName, formatCareerArray(array), StandardCharsets.ISO_8859_1, true);    // always generate
            if (bCreated)
                System.out.println("Generated race file: " + strFileName);
        }
        return array;
    }

    static JSONArray generateRunnerThumbnailsArray(ENEStatement statement, List<CareerHorse> aRunners) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < aRunners.size(); i++) {
            CareerHorse runner = aRunners.get(i);
            JSONObject obj = new JSONObject();
            String strName = runner.getHorseName();
            obj.put("horse_name", strName);
            obj.put("svg_owner_name", runner.getWiOwner());
            obj.put("trainer_name", runner.getTrainerName());
            obj.put("owner_name", runner.getOwnerName());
            obj.put("sire", runner.getSireName());
            if (!"".equals(runner.getDamName())) {
                String strDam = runner.getDamName();
                if (!"".equals(runner.getDamSireName())) {
                    strDam += ("(" + runner.getDamSireName() + ")");
                }
                obj.put("dam_name", strDam);
            }
            List<Pair<String, String>> lstRaces = runner.getMajorRaces();
            if (lstRaces.size() > 0) {
                JSONArray majorraces = new JSONArray();
                Iterator<Pair<String, String>> iter = runner.getMajorRaces().iterator();
                while (iter.hasNext()) {
                    Pair<String, String> pair = iter.next();
                    String strRaceName = pair.getElement0();
                    String strPosition = pair.getElement1();
                    JSONObject major = new JSONObject();
                    major.put("race_name", strRaceName);
                    major.put("position", strPosition);
                    majorraces.put(major);
                }
                obj.put("major_races", majorraces);
            }
            array.put(obj);
        }
        return array;
    }


    public static void generateFlatCareerIndexes(ENEStatement statement, int nYear) {
        generateFlatCareerIndex(statement, nYear, 2);
        generateFlatCareerIndex(statement, nYear, 3);
        generateFlatCareerIndex(statement, nYear, 4);
    }

    public static void generateFlatCareerIndex(ENEStatement statement, int nYear, int nAge) {
        String[] astrCategories = {"", "", "twoyear", "threeyear", "older"};
            JSONArray categories = new JSONArray();
            JSONObject colts = new JSONObject();
            if (nAge > 3)
                colts.put("name", "Colts/Horses/Geldings");
            else
                colts.put("name", "Colts/Geldings");
            colts.put("id", "colts");
            List<CareerHorse> lstSeasonHorses =CareerDBFactory.getSeasonCareerHorses(statement, nYear, "Flat", "", nAge, "CGH");
            colts.put("horses", generateRunnerThumbnailsArray(statement, lstSeasonHorses));
            categories.put(colts);
            JSONObject fillies = new JSONObject();
            if (nAge > 3)
                fillies.put("name", "Fillies/Mares");
            else
                fillies.put("name", "Fillies");
            fillies.put("id", "fillies");
            lstSeasonHorses =CareerDBFactory.getSeasonCareerHorses(statement, nYear, "Flat", "", nAge, "FM");
            fillies.put("horses", generateRunnerThumbnailsArray(statement, lstSeasonHorses));
            categories.put(fillies);
            
            String strFileName = "flat" + nYear + "/" + astrCategories[nAge] + "/catalog.json";
            writeJSONFile("career/" + strFileName, categories);
    }

    public static void generateJumpCareerIndex(ENEStatement statement, int nYear) {
        String[] astrCategories = {"juvenile", "novice", "hurdle", "novice", "chase", "mares"};
        String[] astrRaceTypes = {"Hurdle", "Hurdle", "Hurdle", "Chase", "Chase", "NH"};    // N_H_Flat"

        // to do - get list for each, and then eliminate novices from full lists, and juveniles from novices
        for (int i = 0; i < astrCategories.length; i++)
        {
            JSONObject obj = new JSONObject();
            List<CareerHorse> lstSeasonHorses = CareerDBFactory.getSeasonCareerHorses(statement, nYear, astrRaceTypes[i], "", 0, astrCategories[i]);
            JSONArray array = generateRunnerThumbnailsArray(statement, lstSeasonHorses);
            String strFolder = astrCategories[i];
            if ("novice".equals(strFolder) || "juvenile".equals(strFolder))
                strFolder = astrCategories[i] + "_" + astrRaceTypes[i];
            String strFileName = "jump" + nYear + "/" + strFolder + "/catalog.json";
            writeJSONFile("career/" + strFileName, array);
        }
    }
    public static void generateMeetingFile(ENEStatement statement, String strType, int nYear, String strName, String strCourse, int nMonth) 
    {
        int nPrevious = ("jump".equals(strType) && nMonth > 6) ? 1 : 0;
        
            JSONArray races = generateMeetingRunnerThumbnailsArray(statement, nYear - nPrevious, strCourse, nMonth);
            JSONArray days = new JSONArray();
            JSONObject day = null;
            JSONArray dayraces = new JSONArray();
            String strCurrentDate = null;
            if (races.length() >  0)
            {
                for(int i = 0; i < races.length(); i++)
                {
                    JSONObject race = ((JSONObject) races.get(i));
                    if ((strCurrentDate == null) || !strCurrentDate.equals(race.get("date")))
                    {
                        if (day != null)
                        {
                            day.put("races", dayraces);
                            days.put(day);
                        }
                        day = new JSONObject();
                        day.put("date", race.get("date"));
                        day.put("going", race.get("going"));
                        day.put("course", race.get("course"));
                        dayraces = new JSONArray(); // do not use clear
                        strCurrentDate = (String) race.get("date");
                    }
                    race.remove("date");
                    race.remove("going");
                    race.remove("course");
                    dayraces.put(race);
                }
                day.put("races", dayraces);
                days.put(day);
                String strFileName = "career/" + strType.toLowerCase() + nYear + "/meeting/" + strName.toLowerCase() + ".json";
                writeJSONFile(strFileName, days);
            }
    }
    public static void generateARDFile(ENEStatement statement, String strARDName) 
    {
     
            JSONArray races = generateARDRunnerThumbnailsArray(statement, strARDName);
            JSONArray days = new JSONArray();
            JSONObject day = null;
            JSONArray dayraces = new JSONArray();
            String strCurrentDate = null;
            if (races.length() >  0)
            {
                for(int i = 0; i < races.length(); i++)
                {
                    JSONObject race = ((JSONObject) races.get(i));
                    if ((strCurrentDate == null) || !strCurrentDate.equals(race.get("date")))
                    {
                        if (day != null)
                        {
                            day.put("races", dayraces);
                            days.put(day);
                        }
                        day = new JSONObject();
                        day.put("date", race.get("date"));
                        day.put("going", race.get("going"));
                        day.put("course", race.get("course"));
                        dayraces = new JSONArray(); // do not use clear
                        strCurrentDate = (String) race.get("date");
                    }
                    race.remove("date");
                    race.remove("going");
                    race.remove("course");
                    dayraces.put(race);
                }
                day.put("races", dayraces);
                days.put(day);
                String strFileName = "career/races/" + strARDName.toLowerCase().replace(" ", "_") + ".json";
                writeJSONFile(strFileName, days);
            }
    }
    public static void generateCareerFile(ENEStatement statement, int nYear, String strCategory, String strSubCategory) 
    {
        // type = flat or jump
        // category = threeyear, twoyear, older
        String strStartDate = nYear + "-01-01";
        String strEndDate = (nYear + 1) + "-01-01";
        if (strCategory.indexOf("hurdle") >= 0 || strCategory.indexOf("chase") >= 0 || strCategory.equals("mares") || strCategory.equals("nh_flat"))
        {
            strStartDate = (nYear-1) + "-07-01";
            strEndDate = nYear + "-07-01";
        }

        JSONArray array = generateRaceCategoryArray(statement, strCategory, strSubCategory, strStartDate, strEndDate, true);
        writeJSONFile("career/" + getCareerFileName(strCategory, strSubCategory, nYear), array);
    }
    private static String getCareerFileName(String strCategory, String strSubCategory, String strStartDate)
    {
        String strYear = strStartDate.substring(0, 4);
        
        try
        {
            int nYear = Integer.valueOf(strYear);
            if (strCategory.indexOf("hurdle") >= 0 || strCategory.indexOf("chase") >= 0 || strCategory.equals("mares") || strCategory.equals("nh_flat"))
            {
                String strMonth = strStartDate.substring(5, 7);
                int nMonth = 0;
                try
                {
                    nMonth = Integer.valueOf(strMonth);
                }
                catch(NumberFormatException e)
                {
                    
                }
                if (nMonth >= 7)
                    nYear++;
            }
           return getCareerFileName(strCategory, strSubCategory, nYear);
        }
        catch(NumberFormatException e)
        {
            return "Invalid year";
        }
    }
    private static String getCareerFileName(String strCategory, String strSubCategory, int nYear)
    {
        String strType = "flat";
        if (strCategory.indexOf("hurdle") >= 0 || strCategory.indexOf("chase") >= 0 || strCategory.equals("mares") || strCategory.equals("nh_flat"))
        {
            strType = "jump";
        }
        String strFileName = strType.toLowerCase() + nYear + "/" + strCategory.toLowerCase() + "/" + strSubCategory.toLowerCase().replace("twoyear", "").replace("threeyear", "") + ".json";
        return strFileName;
    }
    public static void updateCareerFile(ENEStatement statement, String strCategory, String strSubCategory, String strStartDate, JSONArray array) 
    {
        updateJSONFile("career/" + getCareerFileName(strCategory, strSubCategory, strStartDate), array);
    }
public static JSONArray generateSeasonRunnerThumbnailsX(ENEStatement statement, int nYear, String strRaceType, String strCountry, int nAge, String strGender)
{
    List<CareerHorse> lstSeasonHorses =CareerDBFactory.getSeasonCareerHorses(statement, nYear, strRaceType, strCountry, nAge, strGender);
    return generateRunnerThumbnailsArray(statement, lstSeasonHorses); 
}
private static JSONArray generateMeetingRunnerThumbnailsArray(ENEStatement statement, int nYear, String strCourse, int nMonth)
{
    List<AdditionalRaceInstance> lstRaces = CareerDBFactory.getMeetingRaces(statement, nYear, strCourse, nMonth);
        JSONArray array = new JSONArray();
        for(int i = 0; i < lstRaces.size(); i++)
        {
            AdditionalRaceInstance ari = lstRaces.get(i);   // contains race_id, source, meeting_date, title
            // full race details
            AdditionalRaceInstance race = AdditionalRaceLinkFactory.getAdditionalRaceLinkObject(statement, ari.getRaceId(), ari.getSource());
            if (race != null)
                ari = race;
            String strRaceTitle = CareerEnvironment.getInstance().getCareerRaceName(ari.getSource() + ari.getRaceId());
            if ((strRaceTitle == null) || ("".equals(strRaceTitle)))
                strRaceTitle = CareerDBFactory.convertRaceTitle(ari, "", "");
            ari.setTitle(strRaceTitle);
            array.put(ari.toJSON("EEEE, MMMM d yyyy"));
        }

    return array;
    
}
public static void generateARDRaceThumbnailFiles(ENEStatement statement, String strARDName, int nMinYear)
{
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d yyyy");
    JSONArray races = generateARDRunnerThumbnailsArray(statement, strARDName);
    for(int i = 0; i < races.length(); i++)
    {
       JSONObject race = ((JSONObject) races.get(i));
       int nRace = (Integer) race.get("id");
       String strSource = (String) race.get("source");
       String strDate = (String) race.get("date");
       try
       {
            Date date = sdf.parse(strDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int nYear = cal.get(Calendar.YEAR);
            if (nYear >= nMinYear)
            {
                 System.out.println(nYear + "-" + nRace + "-" + strSource);
                 generateRaceRunnerThumbnailsArray(statement, nRace, strSource, true, true);
            }
       }
       catch(ParseException e)
       {
           System.out.println("Invalid date: " + strDate);
       }
    }
}
private static JSONArray generateARDRunnerThumbnailsArray(ENEStatement statement, String strARDName)
{
    List<AdditionalRaceInstance> lstRaces = AdditionalRaceLinkFactory.getAdditionalRaceLinks(statement, strARDName);
        JSONArray array = new JSONArray();
        for(int i = 0; i < lstRaces.size(); i++)
        {
            AdditionalRaceInstance ari = lstRaces.get(i);   // contains race_id, source, meeting_date, title
            // full race details
            AdditionalRaceInstance race = AdditionalRaceLinkFactory.getAdditionalRaceLinkObject(statement, ari.getRaceId(), ari.getSource());
            if (race != null)
                ari = race;
            String strRaceTitle = CareerEnvironment.getInstance().getCareerRaceName(ari.getSource() + ari.getRaceId());
            if ((strRaceTitle == null) || ("".equals(strRaceTitle)))
                strRaceTitle = CareerDBFactory.convertRaceTitle(ari, "", "");
            ari.setTitle(strRaceTitle);
            array.put(ari.toJSON("EEEE, MMMM d yyyy"));
        }

    return array;
    
}
public static JSONArray generateRaceCategoryArray(ENEStatement statement, String strCategory, String strSubCategory, String strStartDate, String strEndDate, boolean bFixtures)
{
    List<AdditionalRaceInstance> lstRaces = CareerDBFactory.getCategoryRaces(statement, strCategory, strSubCategory, strStartDate, strEndDate, bFixtures);
        JSONArray array = new JSONArray();
        for(int i = 0; i < lstRaces.size(); i++)
        {
            AdditionalRaceInstance ari = lstRaces.get(i);   // contains race_id, source, meeting_date, title
            // full race details
            AdditionalRaceInstance race = AdditionalRaceLinkFactory.getAdditionalRaceLinkObject(statement, ari.getRaceId(), ari.getSource());
            if (race != null)
                ari = race;
            String strRaceTitle = CareerEnvironment.getInstance().getCareerRaceName(ari.getSource() + ari.getRaceId());
            if ((strRaceTitle == null) || ("".equals(strRaceTitle)))
                strRaceTitle = CareerDBFactory.convertRaceTitle(ari, "", "");
            ari.setTitle(strRaceTitle);
            array.put(ari.toJSON("MMMM d yyyy"));
        }

    return array;
}
private static boolean updateJSONFile(String strFileName, JSONArray array)
{
    String strContent = FileUtils.readFile(ENEColoursEnvironment.getInstance().getVariable("JSON_OUTPUT_DIRECTORY") + strFileName, StandardCharsets.ISO_8859_1);
    JSONArray aRaces = new JSONArray();
    SimpleDateFormat format = new SimpleDateFormat("MMMM d yyyy"); 
        aRaces = new JSONArray(strContent);
    try
    {
        // work our where to insert records
        String strRaceDate = (String)((JSONObject)array.get(0)).get("date");
        Date raceDate = format.parse(strRaceDate);
        int nCount = aRaces.length();     // default is to go at end
        for(int i = 0; i < aRaces.length(); i++)
        {
            JSONObject race = (JSONObject) aRaces.get(i);
            String strDate = (String)((JSONObject)aRaces.get(i)).get("date");
            Date date = format.parse(strDate);
            if (date.after(raceDate))
            {
                nCount = i;
                break;
            }
        }
        aRaces.putAll(array);
        String strNewContent = formatCareerArray(aRaces);
        System.out.println(strFileName);
        return FileUtils.writeFile(ENEColoursEnvironment.getInstance().getVariable("JSON_OUTPUT_DIRECTORY") + strFileName, strNewContent, StandardCharsets.ISO_8859_1, true);
    }
    catch(ParseException e)
    {
        System.out.println("Invalid date in array");
        return false;
    }
}

private static boolean writeJSONFile(String strFileName, JSONArray array)
{
    String strContent = formatCareerArray(array);
    return FileUtils.writeFile(ENEColoursEnvironment.getInstance().getVariable("JSON_OUTPUT_DIRECTORY") + strFileName, strContent, StandardCharsets.ISO_8859_1, true);
}
public static String formatCareerArray(JSONArray array)
{
    String strArray = array.toString();
    strArray = strArray.replaceAll("\\[", "[\n");
    strArray = strArray.replaceAll("\\]", "\n]");
    strArray = strArray.replaceAll(",[\\s]*\\{\"date\"", ",\n{\"date\"");
    strArray = strArray.replaceAll(",[\\s]*\\{\"age_range\"", ",\n{\"age_range\"");
    return strArray;
}

    private static String loadStartDateThumbnails(ENEStatement statement, HashMap<String, String[]> hmTopics, String strStartDate, boolean bFile) {
        // outpus JSON to system out
        String strOutput = "";
        Iterator<String> iterTopics = hmTopics.keySet().iterator();
        while (iterTopics.hasNext()) {
            // careers
            String strCategory = iterTopics.next();
            String[] astrTopics = hmTopics.get(strCategory);
            for (int j = 0; j < astrTopics.length; j++) {
                String strSubCategory = astrTopics[j];
                if (!strCategory.equals(strSubCategory)) {
                    strSubCategory = strSubCategory.replace(strCategory, "");
                }
                JSONArray array = CareerJSONFactory.generateRaceCategoryArray(statement, strCategory, strSubCategory, strStartDate, null, false);
                if (array.length() > 0) {
                    strOutput += (strCategory + "-" + strSubCategory + "\n");
                    if (bFile) {
                        CareerJSONFactory.updateCareerFile(statement, strCategory, strSubCategory, strStartDate, array);
                    } else {
                        strOutput += (CareerJSONFactory.formatCareerArray(array) + "\n");
                    }
                }
            }
        }
        return strOutput;
    }

    private static void loadYearThumbnails(ENEStatement statement, HashMap<String, String[]> hmTopics, int nYear) {
        // generates file
        Iterator<String> iterTopics = hmTopics.keySet().iterator();
        while (iterTopics.hasNext()) {
            // careers
            String strCategory = iterTopics.next();
            String[] astrTopics = hmTopics.get(strCategory);
            for (int j = 0; j < astrTopics.length; j++) {
                String strSubCategory = astrTopics[j];
                if (!strSubCategory.equals(strCategory)) {
                    strSubCategory = strSubCategory.replace(strCategory, "");
                }
                // ADD CONDITION IF DON'T WANT TO REBUILD
                //if ("nh_flat".equals(strSubCategory))
                //if ("france".equals(strSubCategory))
                //if ("chase".equals(strCategory) && (strSubCategory.indexOf("two_half_mile") == 0))
                CareerJSONFactory.generateCareerFile(statement, nYear, strCategory, strSubCategory);
            }
        }
    }

    public static String loadFlatThumbnails(ENEStatement statement) {
        // get latest date and write to file
        String strStartDate = JCEventsFactory.getEventDate(statement, "latest_flat_thumbnail");
        String strReturn = loadStartDateThumbnails(statement, sm_hmFlatTopics, strStartDate, true);
        JCEventsFactory.updateEventDate(statement, "latest_flat_thumbnail");
        return strReturn;
    }

    public static String loadFlatThumbnails(ENEStatement statement, String strStartDate, boolean bFile) {
        return loadStartDateThumbnails(statement, sm_hmFlatTopics, strStartDate, bFile);
    }

    public static void loadFlatThumbnails(ENEStatement statement, int nYear) {
        loadYearThumbnails(statement, sm_hmFlatTopics, nYear);
    }

    /*
    private void expandDescriptions(int nStart, int nEnd, int nMax)
    {
    ArrayList<String> lst = ENEColoursFactory.getJockeyColoursList(nStart, nEnd, nMax);
    Iterator<String> iter = lst.iterator();
    int nCount = 0;
    while(iter.hasNext())
    {
    String strDescription = iter.next();
    System.out.println(strDescription + "-" + ENEColoursParser.expandDescription(strDescription));
    }
    }
     */
    public static void loadNHThumbnails(ENEStatement statement, int nYear) {
        // generates file
        loadYearThumbnails(statement, sm_hmNHTopics, nYear);
    }

    public static String loadNHThumbnails(ENEStatement statement) {
        // get latest date and write to file
        String strStartDate = JCEventsFactory.getEventDate(statement, "latest_nh_thumbnail");
        String strReturn = loadStartDateThumbnails(statement, sm_hmNHTopics, strStartDate, true);
        JCEventsFactory.updateEventDate(statement, "latest_nh_thumbnail");
        return strReturn;
    }

    public static String loadNHThumbnails(ENEStatement statement, String strStartDate, boolean bFile) {
        //generates JSON
        return loadStartDateThumbnails(statement, sm_hmNHTopics, strStartDate, bFile);
    }
public static void generateARDRaceFiles(ENEStatement statement, int nYear, String strRaceType, boolean bOverwrite)
{
   JSONObject params = new JSONObject();
   params.put("YEAR", nYear);
   params.put("TYPE", strRaceType);  // NH or Flat
   JSONObject jsonObj = JSONUtils.reportJSONFile(statement, 
                    ENEColoursEnvironment.getInstance().getVariable("QUERY_DIRECTORY") + "select_colour_races.sql", 
                    params, 500, JSONUtils.JSON, null); 
   JSONArray array = (JSONArray) jsonObj.get("data");
   for(int i = 0; i < array.length(); i++)
   {
       int nRace = (int) ((JSONObject)array.get(i)).get("race_id");
       String strSource = (String) ((JSONObject)array.get(i)).get("arl_source");
       
       // check if file already exists
       String strFileName = String.valueOf(nRace) + ".json";
       if (!"SF".equals(strSource))
            strFileName = strSource + strFileName;

       JSONArray runners = CareerJSONFactory.generateRaceRunnerThumbnailsArray(statement, nRace, strSource, true, false);
   }
}
public static String outputMissingARDWikipediaOwners(ENEStatement statement, String strQuery, int nYear, String strRaceType) throws IOException
{
   JSONObject params = new JSONObject();
   params.put("YEAR", nYear);
   params.put("TYPE", strRaceType);  // NH or Flat
   JSONObject jsonObj = JSONUtils.reportJSONFile(statement, 
                    ENEColoursEnvironment.getInstance().getVariable("QUERY_DIRECTORY") + strQuery, 
                    params, 500, JSONUtils.JSON, null); 
   JSONArray array = (JSONArray) jsonObj.get("data");
   String strContent = "";
    for(int i = 0; i < array.length(); i++)
    {
        JSONObject obj = (JSONObject) array.get(i);
        strContent += (obj.get("meeting_date") + ": " + obj.get("race_id") + "-" + obj.get("ard_name") + "-" + obj.get("owner_name") + "-" + obj.get("jockey_colours") + '\n');
    }

   return strContent;
}
public static void generateARDWikipediaOwners(ENEStatement statement, String strQuery, int nYear, String strRaceType) throws IOException
{
   JSONObject params = new JSONObject();
   params.put("YEAR", nYear);
   params.put("TYPE", strRaceType);  // NH or Flat
   JSONObject jsonObj = JSONUtils.reportJSONFile(statement, 
                    ENEColoursEnvironment.getInstance().getVariable("QUERY_DIRECTORY") + strQuery, 
                    params, 500, JSONUtils.JSON, null); 
   JSONArray array = (JSONArray) jsonObj.get("data");
   System.out.println(array.toString());
 //List<AdditionalRaceInstance> lstRaces = SmartformRaceFactory.getRaceInstancesByCategory(getStatement(), "older", null, "2019-03-01", null, false);
int[] aRaces = {907290, 910507, 919440, 919972, 919975, 919976};
   for(int i = 0; i < array.length(); i++)
   //for(int i = 0; i < aRaces.length; i++)
   {
       int nRace = (int) ((JSONObject)array.get(i)).get("race_id");
       String strSource = (String) ((JSONObject)array.get(i)).get("arl_source");
       //int nRace =aRaces[i];
       //String strSource = "SF";
        AdditionalRaceInstance link = new AdditionalRaceInstance(strSource, nRace, null);
        ArrayList<SmartformColoursRunner> aRunners = ENEColoursRunnerFactory.getSmartformRaceRunners(statement, link, -1);
        Iterator<SmartformColoursRunner> iter = aRunners.iterator();
        while(iter.hasNext())
        {
            SmartformColoursRunner runner = iter.next();
            String strWIOwner = "Owner";    // Todo: Wikipedia.generateWikipediaOwner(statement, runner, "en");
            System.out.println(runner.getName() + "-" + strWIOwner);
        }
        String strOutput = generateRaceRunnerThumbnailsArray(statement, nRace, strSource, true, true).toString();
        System.out.println(strOutput);
   }             
}
public static void generateFlatMeetingFiles(ENEStatement statement, int nYear)
{
 /*      generateMeetingFile(statement, "flat", nYear, "guineas", "Newmarket", 5);
         generateMeetingFile(statement, "flat", nYear, "chester", "Chester", 5); 
        generateMeetingFile(statement, "flat", nYear, "lockinge", "Newbury", 5);  
     generateMeetingFile(statement, "flat", nYear, "irish_guineas", "Curragh", 5); 
        generateMeetingFile(statement, "flat", nYear, "dante", "York", 7);
        generateMeetingFile(statement, "flat", nYear, "derby", "Epsom_Downs", 7);  
    
        generateMeetingFile(statement, "flat", nYear, "royal_ascot", "Royal_Ascot", 6);
         generateMeetingFile(statement, "flat", nYear, "irish_derby", "Curragh", 6);  
        generateMeetingFile(statement, "flat", nYear, "irish_derby2", "Curragh", 7);   // 6+7 
        generateMeetingFile(statement, "flat", nYear, "july", "Newmarket", 7); 
      generateMeetingFile(statement, "flat", nYear, "eclipse", "Sandown", 7); 
        generateMeetingFile(statement, "flat", nYear, "king_george", "Ascot", 7); 
          generateMeetingFile(statement, "flat", nYear, "glorious", "Goodwood", 7);   
        generateMeetingFile(statement, "flat", nYear, "glorious2", "Goodwood", 8);   */ 
        generateMeetingFile(statement, "flat", nYear, "ebor", "York", 8); 
 /*        generateMeetingFile(statement, "flat", nYear, "st_leger", "Doncaster", 9);
        generateMeetingFile(statement, "flat", nYear, "irish_champion", "Leopardstown", 9);
       generateMeetingFile(statement, "flat", nYear, "irish_champion2", "Curragh", 9);  
          generateMeetingFile(statement, "flat", nYear, "western", "Ayr", 9); 
     generateMeetingFile(statement, "flat", nYear, "cambridgeshire", "Newmarket", 9); 
     generateMeetingFile(statement, "flat", nYear, "future_champions", "Newmarket", 10); 
         generateMeetingFile(statement, "flat", nYear, "arc", "Longchamp", 10); 
       generateMeetingFile(statement, "flat", nYear, "champions", "Ascot", 10);  */
    }
public static void generateNHMeetingFiles(ENEStatement statement, int nYear)
{
        generateMeetingFile(statement, "jump", nYear, "kempton", "Kempton", 12);
        generateMeetingFile(statement, "jump", nYear, "leopardstown", "Leopardstown", 12); 
        generateMeetingFile(statement, "jump", nYear, "dublin", "Leopardstown", 2); 
      generateMeetingFile(statement, "jump", nYear, "cheltenham", "Cheltenham", 3);
          generateMeetingFile(statement, "jump", nYear, "easter", "Fairyhouse", 4);
        generateMeetingFile(statement, "jump", nYear, "aintree", "Aintree", 4);
        generateMeetingFile(statement, "jump", nYear, "punchestown", "Punchestown", 4); 
        generateMeetingFile(statement, "jump", nYear, "punchestown2", "Punchestown", 5); 
}
}
