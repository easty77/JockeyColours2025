/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.jockeycolours.controllers;


import ene.eneform.colours.bos.ENEColoursDBEnvironment;
import ene.eneform.colours.service.WikipediaService;
import ene.eneform.mero.config.ENEColoursEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileNotFoundException;

/**
 *
 * @author Simon
 */
@Controller
@RequestMapping("/colours")
@RequiredArgsConstructor
public class ColoursController {

    private final WikipediaService wikipediaService;
    @GetMapping("/resetEnvironment")
    public String resetEnvironment(ModelMap model) {
        ENEColoursEnvironment.getInstance().reset();
        ENEColoursDBEnvironment.getInstance().reset();
        model.put("message", "resetEnvironment");
        return "message";
    }
    @GetMapping("/createWikipediaOwner")
    public String createWikipediaOwner(@RequestParam String ownerName,
                                       @RequestParam String jockeyColours,
                                       @RequestParam String comment,
                                       @RequestParam String compress,
                                       ModelMap model) {
        boolean bCompress = !compress.equals("No");
        try {
            wikipediaService.generateWikipediaOwner(ownerName, jockeyColours, comment, "en", bCompress, true);
        }
        catch(Exception e) {
            model.put("error", "generateWikipediaOwner");
            return "error";
        }
        model.put("message", "generateWikipediaOwner");
        return "message";

    }

    /*

            if ("create_colour_description".equalsIgnoreCase(strAction))
            {
                String strLanguage = request.getParameter("language");
                if ((strLanguage == null) || "".equals(strLanguage))
                    strLanguage = ENEColoursEnvironment.DEFAULT_LANGUAGE;
                String strDescription = request.getParameter("description");
                // ignore label & format - all Mero
                String strLabel = request.getParameter("label");
                String strFormat = request.getParameter("format");
                String strOutput = Wikipedia.createImageContent(statement, strDescription, strLanguage, true);
                out.print(strOutput);
            }
            else if("create_svg_sample".equalsIgnoreCase(strAction))
            {
                out.print("<?xml version='1.0' encoding='UTF-8'?><!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN' 'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'><svg xmlns:xlink='http://www.w3.org/1999/xlink' xmlns='http://www.w3.org/2000/svg'><rect x='25' y='25' id='myRect' rx='0.6' ry='0.6' width='150' height='150' fill='green' stroke='yellow' stroke-width='8' /></svg>");
            }
            else if("generate_top_race_svg".equalsIgnoreCase(strAction))
            {
                String strRace = request.getParameter("race");
                try
                {
                    int nRace = 0;
                    nRace = Integer.parseInt(strRace);
                    
                    int nWinners = ENETopRacesFactory.generateSVGTopRaceWinners(statement, nRace, "");
                    out.print("Top Race SVG Generated: " + strRace + "-" + nWinners);
                }
                catch(NumberFormatException e)
                {
                    out.print("Invalid Top Race: " + strRace);
                }
              }
            else if("generate_owners_svg".equalsIgnoreCase(strAction))
            {
                /* commented out when repackaging 20160408
                String strOrganisation = request.getParameter("organisation");
                String strOrgType = request.getParameter("orgtype");
                String strYear = request.getParameter("year");
                String strFormat = request.getParameter("format");
                if ((strFormat == null) || "".equals(strFormat))
                    strFormat = "standard";
                try
                {
                    int nYear = 0;
                    nYear = Integer.parseInt(strYear);
                    // hard-code standard for now
                    int nWinners = ENEColoursFactory.generateSVGRegisteredOwnerFiles(statement, strOrganisation, strOrgType, nYear, strFormat);
                    out.print("Owners SVG Generated: " + strOrganisation + "-" + nYear + ", " + nWinners);
                }
                catch(NumberFormatException e)
                {
                    out.print("Invalid Year: " + strYear);
                }
              }
            else if("generate_race_sequence_svg".equalsIgnoreCase(strAction))
            {
                String[] astrRaces = request.getParameterValues("race");
                Integer[] anRaces = new Integer[astrRaces.length];
                for(int i = 0; i < astrRaces.length; i++)
                {
                    try
                    {
                        anRaces[i] = Integer.parseInt(astrRaces[i]);
                    }
                    catch(NumberFormatException e)
                    {

                    }
                }
                ENEColoursFactory.processSVGRaceSequence(statement, ENEColoursEnvironment.DEFAULT_LANGUAGE, Arrays.asList(anRaces), "r");  // 1st October
                out.print("processSVGRaceSequence done");
            }
            else if("generate_all_racecards".equalsIgnoreCase(strAction))
            {
                /* commented out when repackaging 20160408
 
                String strDate = request.getParameter("date");
                int nDay = 0;
                try
                {
                    nDay = Integer.parseInt(strDate);
                }
                catch(NumberFormatException e)
                {

                }
                
                // all courses
                SmartformRacecardDefinition racecard = new SmartformRacecardDefinition("day", nDay, new ArrayList<CourseTimeList>());
                List<Integer> alRaces = ENEColoursRaceFactory.generateRacecard(statement, ENEColoursEnvironment.DEFAULT_LANGUAGE, racecard);
                out.println(alRaces.toString());
                
                List<String> lstCourses = SmartformRaceFactory.getCourseList(statement, nDay);
                for(int i = 0; i < lstCourses.size(); i++)
                {
                    // each course
                    ArrayList<CourseTimeList> alCTL = new ArrayList<CourseTimeList>();
                    CourseTimeList ctl = new CourseTimeList(lstCourses.get(i), new String[]{});
                    alCTL.add(ctl);
                    racecard = new SmartformRacecardDefinition("course", nDay, alCTL);
                    alRaces = ENEColoursRaceFactory.generateRacecard(statement, ENEColoursEnvironment.DEFAULT_LANGUAGE, racecard);
                    out.println(alRaces.toString());
                }
            }
           else if("generate_racecard1".equalsIgnoreCase(strAction))
            {

                
                String strType = request.getParameter("type");
                String strTitle = request.getParameter("title");

                // generate where clause from course, date, hour/minute values
                String strDate = request.getParameter("date");
                int nDay = 0;
                try
                {
                    nDay = Integer.parseInt(strDate);
                }
                catch(NumberFormatException e)
                {

                }
                String[] astrRaces = request.getParameterValues("race");
                List<Integer> alRaces = SmartformRaceFactory.getRaceIds(statement, astrRaces);

                ENEColoursRaceFactory.generateRacecard(statement, ENEColoursEnvironment.DEFAULT_LANGUAGE, alRaces, strType, strTitle, nDay);
                out.print(alRaces.toString());
            }
            else if("generate_racecard".equalsIgnoreCase(strAction))
            {
                /* commented out when repackaging 20160408
                
                String strType = request.getParameter("type");
                String strTitle = request.getParameter("title");

                // generate where clause from course, date, hour/minute values
                String strDate = request.getParameter("date");
                int nDay = 0;
                    try
                    {
                        nDay = Integer.parseInt(strDate);
                    }
                    catch(NumberFormatException e)
                    {

                    }

                ArrayList<CourseTimeList> alCTL = new ArrayList<CourseTimeList>();
                for (int i = 1; i <= 3; i++)
                {
                    String strCourse = request.getParameter("course" + i);
                    if (!"".equals(strCourse))
                    {
                        ArrayList<String> alTimes = new ArrayList<String>();
                        String strHourParam = "hour" + i;
                        String strMinuteParam = "minute" + i;
                        for(int j = 1; j <= 4; j++)
                        {
                            String strHour = request.getParameter(strHourParam + j);
                            if (!"".equals(strHour))
                            {
                                String strMinute = request.getParameter(strMinuteParam + j);
                                alTimes.add(strHour + ":" + strMinute);
                            }
                        }
                        CourseTimeList ctl = new CourseTimeList(strCourse, alTimes.toArray(new String[alTimes.size()]));
                        alCTL.add(ctl);
                    }
                }
                SmartformRacecardDefinition racecard = new SmartformRacecardDefinition(strType, nDay, alCTL);
                racecard.setTitle(strTitle);
                List<Integer> alRaces = ENEColoursRaceFactory.generateRacecard(statement, ENEColoursEnvironment.DEFAULT_LANGUAGE, racecard);
                out.print(alRaces.toString());
            }
            else if("wikipedia_race".equalsIgnoreCase(strAction))
            {
                  String strRaceName = request.getParameter("race_name");
                 int nRace = getNumericParameterValue(request, "race_id");
                 String strRaceContent="";
                 if (nRace >= 0)
                 {
                       strRaceContent = Wikipedia.generateRace(statement, nRace, strRaceName, ENEColoursEnvironment.DEFAULT_LANGUAGE, "<br />");
                 }
                 else
                 {
                    int nYear = getNumericParameterValue(request, "year");
                    if (nYear > 0)
                    {
                       strRaceContent = Wikipedia.generateRace(statement, strRaceName, nYear, ENEColoursEnvironment.DEFAULT_LANGUAGE, "<br />");
                        
                    }
                    else
                    {
                        // assume latest
                       strRaceContent = Wikipedia.generateRace(statement, strRaceName, ENEColoursEnvironment.DEFAULT_LANGUAGE, "<br />");
                        
                    }
                 }
                out.print(strRaceContent);
              }
            else if("wikipedia_race_sequence".equalsIgnoreCase(strAction))
            {
                String strRaceName = request.getParameter("race_name");
                String strRaceContent = Wikipedia.generateRaceSequence(statement, strRaceName, ENEColoursEnvironment.DEFAULT_LANGUAGE, "<br />");
                out.print(strRaceContent);
             }
            else if("create_wikipedia_owner".equalsIgnoreCase(strAction))
            {
                String strOwnerName = request.getParameter("owner_name");
                String strColours = request.getParameter("jockey_colours");
                String strComment = request.getParameter("comment");
                if (strComment == null)
                    strComment = "";
                boolean bCompress = (request.getParameter("compress") != "No");
                Wikipedia.generateWikipediaOwner(statement, strOwnerName, strColours, strComment, "en", bCompress, true);
            }
            else if("unregistered_colour_syntax".equalsIgnoreCase(strAction))
            {
                String strOwnerName = request.getParameter("owner_name");
                String strColours = request.getParameter("jockey_colours");
                String strCountry = request.getParameter("country");
                WikipediaImagesFactory.insertUnregisteredColourSyntax(statement, strCountry, strColours, strOwnerName);
            }
            
            else if("wikipedia_image".equalsIgnoreCase(strAction))
            {
                String strOwnerName = request.getParameter("owner_name");
                boolean bOwner = Wikipedia.generateOwnerColours(statement, strOwnerName, ENEColoursEnvironment.DEFAULT_LANGUAGE, true, false);    // assume that not a Back View, and don't want to reparse
                if (bOwner)
                {
                    out.print("<a href=\"/images/colours/svg/wikipedia/owners/owner_" + strOwnerName.trim() + ".svg\">" +  strOwnerName + " image generated</a>");
                    WikipediaImagesFactory.updateWikipediaImageTimestamp(statement, strOwnerName);
                }
                else
                    out.print("Not found: " + strOwnerName);
             }
           else if("upload_wikimedia_image".equalsIgnoreCase(strAction))
            {
                String strOwnerName = request.getParameter("owner_name");
                WikipediaUpdate.uploadWikimediaImage(strOwnerName);
                out.print("Image uploaded: " + strOwnerName);
             }
           else if("update_wikipedia_race".equalsIgnoreCase(strAction))
            {
                String strRaceName = request.getParameter("race_name");
                String strLanguage = request.getParameter("language");
                String strGenerateOnly = request.getParameter("generate_only");
                if ((strLanguage == null) || "".equals(strLanguage))
                    strLanguage = "en";
                WikipediaUpdate.updateWikipediaRace(statement, ENEColoursDBEnvironment.getInstance().getAdditionalRaceData(strRaceName), strLanguage, !"yes".equals(strGenerateOnly));
                out.print("Race updated: " + strRaceName);
             }
             else if ("previous_race_year".equalsIgnoreCase(strAction))
            {
                int nRace = getNumericParameterValue(request, "race_id");
                int nPreviousRace = getNumericParameterValue(request, "previous_race_id");
          
                boolean bInsert = SmartformRaceFactory.insertPreviousRaceYear(statement, nRace, nPreviousRace);
                out.print(bInsert ? "Inserted" : "Failed");
             }
            else if ("owner_unique_colours".equalsIgnoreCase(strAction))
            {
                String strOwnerName = request.getParameter("owner_name");
                String strColours = request.getParameter("colours");
          
                boolean bInsert = WikipediaFactory.insertOwnerUniqueColours(statement, strOwnerName, strColours);
                out.print(bInsert ? "Inserted" : "Failed");
            }
            else if ("update_owner_colours".equalsIgnoreCase(strAction))
            {
                strDataType = request.getParameter("data_type");
                String strOwner =  request.getParameter("owner_name");
                String strColours  = request.getParameter("jockey_colours");
                String strName = request.getParameter("name");
                String strRace = request.getParameter("race_id");
                try
                {
                    int nRaceId = Integer.parseInt(strRace);
                    int nCount = AdditionalRunnersFactory.updateOwnerColours(statement, strDataType, strOwner, strColours.trim(), strName, nRaceId);              
                    out.print(strOwner + "-" + strColours + "-" + nCount);
                }
                catch(NumberFormatException e)
                {
                    out.print("update_owner_colours: Invalid race_id: " + strRace);
                }
            }
            else if ("update_owner_name".equalsIgnoreCase(strAction))
            {
                strDataType = request.getParameter("data_type");
                String strOwner =  request.getParameter("owner_name");
                String strName = request.getParameter("name");
                String strRace = request.getParameter("race_id");
                try
                {
                    int nRaceId = Integer.parseInt(strRace);
                    int nCount = AdditionalRunnersFactory.updateOwnerName(statement, strDataType, strOwner, strName, nRaceId);              
                    out.print(strOwner + "-" + nCount);
                }
                catch(NumberFormatException e)
                {
                    out.print("update_owner_colours: Invalid race_id: " + strRace);
                }
            }
            else if ("update_runner_name".equalsIgnoreCase(strAction))
            {
                strDataType = request.getParameter("data_type");
                String strPosition = request.getParameter("position");
                String strRace = request.getParameter("race_id");
                String strName = request.getParameter("name");
                try
                {
                    int nRaceId = Integer.parseInt(strRace);
                    int nPosition = Integer.parseInt(strPosition);
                    int nCount = AdditionalRunnersFactory.updateRunnerName(statement, strDataType, strName, nPosition, nRaceId);              
                    out.print(strName + "-" + nCount);
                }
                catch(NumberFormatException e)
                {
                    out.print("update_owner_colours: Invalid race_id: " + strRace);
                }
            }
            else if ("update_paris_turf_race".equalsIgnoreCase(strAction))
            {
                 String strARDName = request.getParameter("ard_name");
                String strTitle = request.getParameter("title");
                String strCourse = request.getParameter("course");
                int nMonth = getNumericParameterValue(request, "month", -1);
                int nYear = getNumericParameterValue(request, "year", -1);
                String strUnassigned = request.getParameter("unassigned");
                int nCount = ParisTurfFactory.updateParisTurfRaces(statement, strARDName, strCourse, strTitle, nYear, nMonth, "Yes".equals(strUnassigned));
                out.print(nCount);
            }
           else if("career_update_days".equalsIgnoreCase(strAction))
            {
                int nDay = getNumericParameterValue(request, "date");
                if (nDay > 0)
                {
                    JSONArray array = CareerEnvironment.getInstance().getCareerUpdates(statement, nDay);
                    out.print(array.toString());
                }
                else
                {
                    JSONArray array = CareerEnvironment.getInstance().getLatestCareerUpdates(statement);
                    out.print(array.toString());
                }
           }
           else if("generate_career_days".equalsIgnoreCase(strAction))
            {
                int nDay = getNumericParameterValue(request, "date");
                if (nDay > 0)
                {
                    // to do: this needs to return JSON containing the URLs of all the updated careers
                     JSONObject obj = CareerEnvironment.getInstance().generateCareerUpdates(statement, nDay);
                     out.print(obj.toString());
                }
                else
                {
                    JSONObject obj = CareerEnvironment.getInstance().generateLatestCareerUpdates(statement);
                    out.print(obj.toString());
                }
           }
           else if("generate_career_horse".equalsIgnoreCase(strAction))
            {
                String strHorse = request.getParameter("name");
                String strReturnURL = CareerEnvironment.getInstance().generateCareer(statement, strHorse);
                JSONObject obj = new JSONObject();
                obj.put("name", strHorse);
                obj.put("url", strReturnURL);
                out.print(obj.toString());
           }
           else if ("upload_wikipedia_race".equalsIgnoreCase(strAction))
           {
               //String strJSON = IOUtils.toString(request.getReader());
               String strJSON = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
               System.out.println("upload_wikipedia_race: " + request.getMethod() + "-" + strJSON);
               try
               {
                JSONObject obj = JSONObject.parse(strJSON);  // body of post
                boolean bZipped = false;
                String strZipped = request.getParameter("zipped");
                if ("yes".equals(strZipped))
                    bZipped = true;
                obj = WikipediaUpdate.updateWikipediaRaceURL(statement, obj, bZipped);
                out.print(obj.toString());
               }
               catch(IOException e)
               {
                   out.print("upload_wikipedia_race: " + e.getMessage());
               }
           }
           else if("upload_wikipedia_images_by_date".equalsIgnoreCase(strAction))
           {
                 String strContent;
                 int nDay = getNumericParameterValue(request, "date");
                 if (nDay >= 0)
                 {
                    String strStartTime = request.getParameter("time");
                    strContent = WikipediaUpdate.uploadWikimediaImagesDate(statement, nDay, ("".equals(strStartTime) ? null : strStartTime));      // nDay is integer GTEQ 0
                }
                else
                {
                    strContent = WikipediaUpdate.uploadWikimediaImagesLatest(statement);  
                 }
                 out.print(strContent);
           }
           else if("upload_wikipedia_races_by_date".equalsIgnoreCase(strAction))
           {
               // No Longer Used - retrieve_wikipedia_races_by_date to get races and then call one at a time
                int nDay = getNumericParameterValue(request, "date");
                String strGenerateOnly = request.getParameter("generate_only");
                if (nDay > 0)
                {
                    // to do: this needs to return a list of all Wikipedia pages updated - so browser can open in new window
                     JSONObject obj = WikipediaUpdate.updateWikipediaRacesDate(statement, nDay, "", !"yes".equals(strGenerateOnly));      // nDay is integer GTEQ 1
                     out.print(obj.toString());
                }
           }
           else if("retrieve_wikipedia_races_by_date".equalsIgnoreCase(strAction))
            {
                int nDay = getNumericParameterValue(request, "date");
                if (nDay > 0)
                {
                    JSONArray array = WikipediaFactory.selectAllWikipediaRacesByDate(statement, nDay);
                     out.print(array.toString());
                 }
                else
                {
                    JSONArray array = WikipediaFactory.selectAllWikipediaRacesLatest(statement);
                    out.print(array.toString());
                }
            }
           else if ("output_paris_turf".equalsIgnoreCase(strAction))
            {
                String strARDName = request.getParameter("ard_name");
                String strOutput = ParisTurfFactory.selectParisTurfReferences(statement, strARDName);
                out.print(strOutput);
            }
            else if ("output_racing_post".equalsIgnoreCase(strAction))
            {
                String strARDName = request.getParameter("ard_name");
                String strLanguage = request.getParameter("language");
                if ((strLanguage == null) || "".equals(strLanguage))
                    strLanguage = "en";
                int nStartYear = getNumericParameterValue(request, "start_year", 1988);
                int nEndYear = getNumericParameterValue(request, "end_year", 2002);
                String strOutput = RacingPostRacecards.getWikipediaReferences(statement, strLanguage, ENEColoursDBEnvironment.getInstance().getAdditionalRaceData(strARDName), nStartYear, nEndYear, null);
                out.print(strOutput);
            }
            else if ("output_career_defn".equalsIgnoreCase(strAction))
            {
                String strName = request.getParameter("name");
                String strBred = getStringParameterValue(request, "where");
                String strOutput = (new CareerHTMLFactory(statement, strName, strBred)).generateCareer();
                out.print(strOutput);
            }
            else if ("generate_career".equalsIgnoreCase(strAction))
            {
                String strName = request.getParameter("name");
                String strReturnURL = CareerEnvironment.getInstance().generateCareer(statement, strName);
                if (strReturnURL != null)
                {
                    JSONObject obj = new JSONObject();
                    JSONArray  pages = new JSONArray();
                    pages.add(strReturnURL);
                    obj.put("pages", pages);
                    out.print(obj.toString());
                }   
                else
                    out.print((new JSONObject()).toString());
            }
            
            else if ("racing_post_horse".equalsIgnoreCase(strAction))
            {
                String strName = request.getParameter("name");
                String strBred = request.getParameter("bred");
                boolean bDebug = false;
                String strDebug = request.getParameter("debug");
                if ("yes".equals(strDebug))
                    bDebug = true;
                String strOutput = "";
                RacingPostRacecards.insertHorseRaces(statement, strName, RacingPostHorse.getHorseId(strName, strBred), 0, bDebug, true, true);  // nMaxYear = 0, so retrieve all races
                out.print(strOutput);
            }
            else if ("racing_post_url".equalsIgnoreCase(strAction))
            {
                String strARDName = request.getParameter("ard_name");
                String strRaceURL = request.getParameter("race_url");
                RacingPostRaceSummary summary = new RacingPostRaceSummary(strRaceURL);
                if (!"".equals(strARDName))
                {
                    AdditionalRaceData ard = ENEColoursDBEnvironment.getInstance().getAdditionalRaceData(strARDName);
                    if (ard != null)
                    {
                          RacingPostRacecards.insertFullRaceResult(statement, summary, ard, true, false, false);    // don't overwrite with ATR data
                        out.print("Processed URL for: " + strARDName);
                    }
                    else
                    {
                        RacingPostRacecards.insertFullRaceResult(statement, summary, true, false);   // REPLACE ENTIRE RP record, but don't overwrite with ATR data
                        out.print("ARD not found: " + strARDName + ".  Processed URL.");
                    }
                }
                else
                {
                    RacingPostRacecards.insertFullRaceResult(statement, summary, true, false);  // REPLACE ENTIRE RP record, but don't overwrite with ATR data
                    out.print("Processed URL.");
                }
            }
            else if ("racing_post_race".equalsIgnoreCase(strAction))
            {
                String strARDName = request.getParameter("ard_name");
                String strLanguage = request.getParameter("language");
                if (strLanguage == null || "".equals(strLanguage))
                    strLanguage = "en";
                int nStartYear = getNumericParameterValue(request, "start_year", 1988);
                int nEndYear = getNumericParameterValue(request, "end_year", 2002);
                String strOutput = "Invalid";
                if (nStartYear >= 1987 && nEndYear <= 2020 && nStartYear <= nEndYear)
                {
                    AdditionalRaceData ard = ENEColoursDBEnvironment.getInstance().getAdditionalRaceData(strARDName);
                    if (ard != null)
                    {
                        RacingPostRacecards.loadRacingPostRacesByReference(statement, ard, strLanguage, nStartYear, nEndYear);
                        strOutput="Processed";
                    }
                    else
                    {
                        strOutput="Invalid race name";
                    }
                }
                out.print(strOutput);
            }
            else if ("racing_post_load_race".equalsIgnoreCase(strAction))
            {
                String strARDName = request.getParameter("ard_name");
                int nStartYear = getNumericParameterValue(request, "start_year", 1988);
                int nEndYear = getNumericParameterValue(request, "end_year", 2002);
                String strOutput = "Invalid";
                RacingPostFactory.loadArdRaceUrls(statement, strARDName, nStartYear, nEndYear, false);  // do not replace whjole record, as colours may have been corrected
            }

            else if ("racing_post_race_id".equalsIgnoreCase(strAction))
            {
                String strARDName = request.getParameter("ard_name");
                if ("".equals(strARDName))
                    strARDName = null;
                int nRace = getNumericParameterValue(request, "race", 0);
                
                String strMeeting = request.getParameter("meeting");
                if ("yes".equals(strMeeting))
                {
                    while(nRace > 0)
                    {
                        nRace = RacingPostRacecards.getFullRaceResults(statement, strARDName, nRace);
                    }
                }
                else if (nRace > 0)
                {
                    RacingPostRacecards.getFullRaceResults(statement, strARDName, nRace);                
                }
            }
            else if ("sporting_life_race".equalsIgnoreCase(strAction))
            {
                String strARDName = request.getParameter("ard_name");
                String strKeywords = request.getParameter("keywords");
                int nStartYear = getNumericParameterValue(request, "start_year", 1994);
                int nEndYear = getNumericParameterValue(request, "end_year", 2002);
                String strOutput = "Invalid";
                if (nStartYear >= 1988 && nEndYear <= 2015 && nStartYear <= nEndYear)
                {
                    strOutput="Processed";
                    SportingLifeRacecards.loadSLRaceList(statement, strARDName, strKeywords, nStartYear, nEndYear);
                }
                out.print(strOutput);
            }
            else if ("insert_sf_ar_links".equalsIgnoreCase(strAction))
            {
                    AdditionalRaceLinkFactory.insertSFAdditionalRaceLinks(statement);                
            }
            else if ("update_ebay_racecard".equalsIgnoreCase(strAction))
            {
                out.print("update_ebay_racecard");
                String strArticleId = request.getParameter("ebay_article_id");
                String strArticleType = request.getParameter("ebay_article_type");
                String strCourse = request.getParameter("ebay_course");
                String strMeeting = request.getParameter("ebay_meeting");
                String strMeetingDate = request.getParameter("ebay_meeting_date");
                String strYear = request.getParameter("ebay_year");
                int nRacecards = getNumericParameterValue(request, "ebay_nr_racecards", 1);
                
                int nRecordsUpdated = EBayRacecards.updateRacecard(statement, strArticleId, strArticleType, strCourse, strMeeting, strMeetingDate, strYear, nRacecards);
                out.print(nRecordsUpdated + " records updated");
            }
            else if ("update_atr_colours".equalsIgnoreCase(strAction))
            {
                int nDayOffset = getNumericParameterValue(request, "day_offset", 0);
                int nRecordsUpdated = AtTheRacesFactory.updateToday(statement, nDayOffset);
                out.print(nRecordsUpdated + " records updated");
            }
            else if ("update_atr_colours_date".equalsIgnoreCase(strAction))
            {
                DateFormat dtFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                String strDate = request.getParameter("date");
                int nRecordsUpdated = -1;
                try
                {
                    nRecordsUpdated = AtTheRacesFactory.updateDate(statement, dtFormat.parse(strDate));
                }
                catch(ParseException e)
                {
                    nRecordsUpdated = -1;
                }
                out.print(nRecordsUpdated + " records updated");
            }
            else if ("notable_races_download".equalsIgnoreCase(strAction))
            {
                String strRaceName = request.getParameter("race_name");
                String strRaceType = request.getParameter("race_type"); // historic or future
                if ("historic".equals(strRaceType))
                {
                    RacingPostRacecards.loadRacingPostHorses(statement, strRaceName, "SF", true);   // previous races
                    RacingPostRacecards.loadRacingPostHorses(statement, strRaceName, "RP", true);   // previous races
                    RacingPostRacecards.loadRacingPostHorses(statement, strRaceName, "RP", false);   //damsire etc
                }
                else
                {
                    RacingPostRacecards.loadRacingPostDailyHorses(statement, strRaceName);
                }
                
            }
           else if ("notable_races_export".equalsIgnoreCase(strAction))
            {
                String strRaceName = request.getParameter("race_name");
                String strRaceType = request.getParameter("race_type"); // historic or future
                ReportJSON.selectJSONRaceNameFile(statement, "big_race_" + strRaceType, strRaceType, strRaceName);                
            }
            else if ("race_thumbnail_json".equalsIgnoreCase(strAction))
            {
                int nRace = getNumericParameterValue(request, "race", 0);
                String strSource = request.getParameter("source"); 
                String strWriteFile = request.getParameter("write_file"); 
                String strOutput = CareerJSONFactory.generateRaceRunnerThumbnailsArray(statement, nRace, strSource, "yes".equals(strWriteFile), true).toString();
                out.print(strOutput);
            }
            else if ("group_thumbnail".equalsIgnoreCase(strAction))
            {
                int nGroup = getNumericParameterValue(request, "group", 1);
                String strRaceType = request.getParameter("race_type");  // Flat, NH
                String strCountry = request.getParameter("country");  // Eire, UK, France
                String strStartDate = request.getParameter("start_date"); 
                String strSource = "SF";
                if ((!"UK".equals(strCountry)) && (!"Eire".equals(strCountry)))
                    strSource = "RP";
                String strOutput = CareerHTMLFactory.generateGroupRaceRunnerThumbnails(statement, strStartDate, strRaceType, strCountry, nGroup, "");
                out.print(strOutput);
            }
            else if ("season_thumbnail".equalsIgnoreCase(strAction))
            {
                int nSeason = getNumericParameterValue(request, "season", 2017);
                int nAge = getNumericParameterValue(request, "age", 3);
                String strRaceType = request.getParameter("race_type");  // Flat, NH
                String strGender = request.getParameter("gender");  // FM or CGH or empty (all)
                if (strGender == null)
                    strGender = "";
                String strCountry = request.getParameter("country");  // France, UK, Eire
                if (strCountry == null)
                    strCountry = "";
                String strOutput = CareerHTMLFactory.generateSeasonRunnerThumbnails(statement, nSeason, strRaceType, strCountry, nAge, strGender, false);
                 out.print(strOutput);
            }
            else if ("latest_thumbnails".equalsIgnoreCase(strAction))
            {
                String strRaceType = request.getParameter("race_type");  // Flat, NH
                String strOutput="";
                if ("flat".equals(strRaceType))
                {
                    strOutput = CareerJSONFactory.loadFlatThumbnails(statement);
                }
                else
                {
                    strOutput = CareerJSONFactory.loadNHThumbnails(statement);
                }
                
                // convert to HTML
                strOutput = "<p>" + strOutput.replace("\n", "<br />") + "</p>";
                out.print(strOutput);
            }
            
            else if ("ebay_article_redirect".equalsIgnoreCase(strAction))
            {
                String strURL = null;
                int nArticle = getNumericParameterValue(request, "article_id", 0);
                if (nArticle > 0)
                    strURL = EBayRacecards.getActiveArticleURL(statement, nArticle);
                if (strURL != null)
                    response.sendRedirect(strURL);
                else
                    out.print("No Active Article");
            }
            else if ("ebay_mad_process".equalsIgnoreCase(strAction))
            {
                // two forms
                // 1) get param challenge_code
                // 2) post metadata.topic="MARKETPLACE_ACCOUNT_DELETION" 
                String strChallenge = request.getParameter("challenge_code");
                if (strChallenge != null)
                {
                    String strVerify="9IjC3wv3g4nyDzuUFgu8QQytTPUIugTs";
                    String strEndpointURL = "https://www.jockeycolours.com/JockeyColours/servlet/ENEColoursServlet?action=ebay_mad_process&output=json";
                    try
                    {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        digest.update(strChallenge.getBytes(StandardCharsets.UTF_8));
                        digest.update(strVerify.getBytes(StandardCharsets.UTF_8));
                        byte[] bytes = digest.digest(strEndpointURL.getBytes(StandardCharsets.UTF_8));
                        JSONObject obj = new JSONObject();
                        obj.put("challengeResponse", org.apache.commons.codec.binary.Hex.encodeHexString(bytes));
                        out.print(obj.toString());
                    }
                    catch(NoSuchAlgorithmException e)
                    {
                        
                    }
                 }
                else
                {
                    String strJSON = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
                   try
                   {
                    JSONObject obj = JSONObject.parse(strJSON);  // body of post
                    JSONObject metadata = (JSONObject) obj.get("metadata");
                    if (metadata != null)
                    {
                        String strTopic = (String) metadata.get("topic");
                        if ("MARKETPLACE_ACCOUNT_DELETION".equals(strTopic))
                        {
                            JSONObject notification = (JSONObject) obj.get("notification");
                            if (notification != null)
                            {
                                JSONObject data = (JSONObject) notification.get("data");
                                if (data != null)
                                {
                                    String strUserName = (String) data.get("username");
                                    String strUserId = (String) data.get("userId");
                                    System.out.println("eBay MAD received for: " + strUserName + "-" + strUserId);
                                    data.serialize(out);
                                }       
                        }
                        else
                        {
                            metadata.put("error", "Unknown action");
                            out.println(metadata.toString());
                        }
                    }
                   }
                   }
                   catch(IOException e)
                   {
                       
                   }
                }
            }
            else if ("update_racecard_year_status".equalsIgnoreCase(strAction))
            {
                String strARDName = request.getParameter("ard_name");
                int nYear = getNumericParameterValue(request, "year", 0);
                int nStatus = getNumericParameterValue(request, "status", 0);
                int nRecords = EBayRacecards.updateRacecardYearStatus(statement, strARDName, nYear, nStatus);
                out.print(nRecords + " records updated");
            }
            else if ("owner_search".equalsIgnoreCase(strAction))
            {
                // return JSON to replace owner_search.jsp/top_races_owner_search.jsp/top_race_winner_search.jsp
                String strOwnerAttribute = request.getParameter("owner_attrib"); // e.g. ro_display_name
                String strOwnerSearch = request.getParameter("search_string");
                String ss_op = request.getParameter("ss_op");
                String strColoursSearch = request.getParameter("colours_string");
                String cs_op = request.getParameter("cs_op");
                String strOrganisation = request.getParameter("organisation");
                String strOrgType = request.getParameter("orgtype");
                int nYear = getNumericParameterValue(request, "year", 0);
                int nFirst = getNumericParameterValue(request, "first", 1);
                int nPageSize = getNumericParameterValue(request, "pagesize", 100);
                int nMatches = getNumericParameterValue(request, "matches", -1);
                
                // for top_races only
                int nRace = getNumericParameterValue(request, "race_id", 0);
                boolean bAscending = true;
                String strAscending = request.getParameter("ascending");
                if ("no".equalsIgnoreCase(strAscending))
                    bAscending = false;

                String strOutput = ColoursSearch.getColourSearchJSON(statement, strOwnerAttribute, strOwnerSearch, ss_op, strColoursSearch, cs_op, strOrganisation, strOrgType, nYear, nPageSize, nMatches, nFirst, nRace, bAscending).toString();                
                out.print(strOutput);
    
            }
            else if ("additional_races_month_datatable".equalsIgnoreCase(strAction))
            {
                int nMaxRecords = getNumericParameterValue(request, "max_records", -1);    // default to no max records (retrieve all)
                int nMonth = getNumericParameterValue(request, "month", 0);
                JSONObject params = new JSONObject();
                params.put("month_offset",  nMonth);
                JSONObject  datatable = JSONUtils.reportJSONFile(statement, ENEColoursEnvironment.getInstance().getVariable("QUERY_DIRECTORY") + "additional_races_month.sql", params, nMaxRecords, JSONUtils.DATATABLE, null);    // use default if null, as DataTable does not like NULL values
                out.print(datatable.serialize(true));
            }
            else if ("combined_colours_datatable".equalsIgnoreCase(strAction))
            {
                int nMaxRecords = getNumericParameterValue(request, "max_records", 50);    // default to max of 50 records
                String strQuery = request.getParameter("query");;
                String strName = request.getParameter("name");;
                String strColours = request.getParameter("colours");
                JSONObject params = new JSONObject();
                params.put("owner_name",  strName.replace("'", "''"));
                params.put("jockey_colours", strColours);
                JSONObject  datatable = JSONUtils.reportJSONFile(statement, ENEColoursEnvironment.getInstance().getVariable("QUERY_DIRECTORY") + "combined_colours/" + strQuery + ".sql", params, nMaxRecords, JSONUtils.DATATABLE, null);    // use default if null, as DataTable does not like NULL values
                out.print(datatable.serialize(true));
            }
            else if ("insert_rp_owner_colours".equalsIgnoreCase(strAction))
            {
                int nOwner = getNumericParameterValue(request, "owner", 0);    // default to max of 50 records
                String strColours = request.getParameter("colours");
                int nRecords = 0;
                if (nOwner > 0)
                {
                    nRecords = RacingPostOwnerFactory.insertRPOwnerColours(statement, nOwner, strColours);
                }
                out.print(nRecords);
            }
            else if ("select_datatable".equalsIgnoreCase(strAction))
            {
                JSONObject  jsonObj = processQuery(request, statement, JSONUtils.DATATABLE);
                out.print(jsonObj.serialize(true));
            }
            else if ("select_json".equalsIgnoreCase(strAction))
            {
               JSONObject  jsonObj = processQuery(request, statement, JSONUtils.JSON);
               out.print(jsonObj.serialize(true));
             }
            else if ("select_array".equalsIgnoreCase(strAction))
            {
               JSONObject  jsonObj = processQuery(request, statement, JSONUtils.ARRAY);
                out.print(jsonObj.serialize(true));
            }
          }
        catch (Exception e)
        {
            System.out.println("ENEColoursServlet: action=" + strAction + ", message=" + e.getMessage());
            e.printStackTrace();
        }

        finally
        {
            if (statement != null)
                statement.close();
            out.flush();
            out.close();
        }
    }

private static JSONObject processQuery(HttpServletRequest request, ENEStatement statement, int nOutputType)
{
    int nMaxRecords = getNumericParameterValue(request, "max_records", 500);    // default to max of 50 records
    String strQuery = request.getParameter("query");
    JSONObject params = processQueryParams(request);
    JSONObject  jsonObj = JSONUtils.reportJSONFile(statement, ENEColoursEnvironment.getInstance().getVariable("QUERY_DIRECTORY") + strQuery + ".sql", 
             params, nMaxRecords, nOutputType, null);    // use default if null, as DataTable does not like NULL values

    return jsonObj;
}
private static JSONObject processQueryParams(HttpServletRequest request)
{
     String[] aParams = request.getParameterValues("params");
     JSONObject params = new JSONObject();
     for (int i=0; i < aParams.length; i++)
     {
         params.put(aParams[i],  request.getParameter(aParams[i]));
     }
     return params;
}
public static String getStringParameterValue(HttpServletRequest request, String strParameter)
{
    String strValue = request.getParameter(strParameter);
    if (strValue == null)
        strValue = "";
    
    return strValue;
}
public static int getNumericParameterValue(HttpServletRequest request, String strParameter)
{
    return  getNumericParameterValue(request, strParameter, -1);
}
public static int getNumericParameterValue(HttpServletRequest request, String strParameter, int nDefault)
{
    String strValue = request.getParameter(strParameter);
    int nValue = nDefault;
    if ((strValue != null) && !"".equals(strValue))
    {
        try 
        {
            nValue = Integer.parseInt(strValue);
        }
        catch(NumberFormatException e)
        {
        }
    }

    return nValue;
}
*/
}
