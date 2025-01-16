/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.smartform.bos;

import ene.eneform.service.utils.RegExpUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Simon
 */
public class AdditionalRace extends SmartformBasicRace {
/*    private int m_nRaceId;
    private String m_strName;
    private String m_strTitle;
    private String m_strCourse;
    private Date m_date;
     private String m_strGoing;
    private int m_nRunners;
    private int m_nGroupRace = 0;
    private String m_strRaceType;

    private boolean m_bHandicap=false;
    private int m_nDistanceYards; */

    private String m_strDistance;
    private String m_strConditions="";
    private String m_strAgeRange;
    private String m_strClass;
    private String m_strScheduledTime = "";     // format = HH:MM

    private String m_strWinningTime = "";
    private double m_dWinningTimeSeconds = -1.0;
    private static Pattern sm_patternDistance1 = Pattern.compile("(\\d*)[m\\s]*(\\d*)[f\\s]*(\\d*)[y]*");
    private static Pattern sm_patternTimeSecs = Pattern.compile("([0-9]+)\\.([0-9][0-9])[s]");
    private static Pattern sm_patternTime = Pattern.compile("(\\d*)[m\\s]*\\s([0-9]+)\\.([0-9]+)[s]");
    private static Pattern sm_patternEuropeanTime = Pattern.compile("(\\d*)[\\:\\s]*([0-9]+)\\,([0-9]+)");
    private static Pattern sm_patternDistance2 = Pattern.compile("(\\d+m\\s*)?(\\d+f\\s*)?(\\d+y)?");
    private static double sm_dMetres2Yards = 1.0936133;
    private static Pattern sm_patternScheduledTime = Pattern.compile("(\\d*)[\\:\\s]*(\\d*)");
    protected String m_strSource;

    private static SimpleDateFormat sm_dtRace = new SimpleDateFormat("dd MMM yyyy");

    public AdditionalRace(String strSource, int nRace, String strName, String strTitle, String strCourse, Date meetingDate, String strRaceType, String strAgeRange, String strClass, int nRunners, String strGoing)
    {
        super(nRace, meetingDate, strCourse, strTitle, strGoing, strRaceType, nRunners);
        m_strSource = strSource; 
        m_strAgeRange = strAgeRange;
        m_strClass=strClass;
        setHandicap(strTitle);
    }
/*    public AdditionalRaceInstance(String strSource, int nRace, String strName, String strTitle, String strCourse, Date dtMeeting, String strGoing, int nRunners, String strAgeRange)
    {
        m_strSource = strSource; 
        m_nRaceId = nRace;
        m_strName = strName;
        m_strTitle = strTitle;
        m_strCourse = strCourse;
        m_date = dtMeeting;
        m_strGoing = strGoing;
        m_nRunners = nRunners;
        m_strAgeRange = strAgeRange;
        setHandicap(strTitle);
    } */
    
    public String getSource() {
        return m_strSource;
    }

    public String getRaceClass() {
        return m_strClass;
    }

    public String getAgeRange() {
        return m_strAgeRange;
    }

    public String getConditions() {
        return m_strConditions;
    }

   
    public String getWinningTime() {
        return m_strWinningTime;
    }
    public double getWinningTimeSeconds() {
        return m_dWinningTimeSeconds;
    }

    public void setWinningTime(String strWinningTime) {
        m_strWinningTime = strWinningTime;
        m_dWinningTimeSeconds = convertWinningTime(strWinningTime);
    }
    public void setDistance(String strDistance) {
        m_strDistance = strDistance;
        m_nDistanceYards = convertDistance(strDistance);
    }
   public void setDistanceMetres(int nMetres) {
        m_strDistance = nMetres + " metres";
        m_nDistanceYards = (int)(nMetres * sm_dMetres2Yards);
    }
    public void setConditions(String strConditions) {
        m_strConditions = strConditions;
    }

    public void setScheduledTime(String strScheduledTime, boolean bTwelve) {
          Matcher m1 = sm_patternScheduledTime.matcher(strScheduledTime);
            if (m1.matches())
            {
                int nHours = RegExpUtils.getMatcherGroupInt(m1, 1);
                 int nMinutes = RegExpUtils.getMatcherGroupInt(m1, 2);

                 if (bTwelve)
                 {
                     if (nHours < 12)
                        nHours += 12;      // racing always in afternoon/evening?  EXCEPT IN FAR EAST etc!
                     else
                         nHours -= 12;
                 }
                m_strScheduledTime = String.format("%02d:%02d", nHours, nMinutes);
            }
            else
                m_strScheduledTime = strScheduledTime;
    }
    public String getScheduledTime() {
        return m_strScheduledTime;
    }
private void setHandicap(String strTitle)
{
    if(strTitle.toLowerCase().indexOf("handicap") >= 0 || strTitle.toLowerCase().indexOf("h'cap") >= 0)
        m_bHandicap = true;
    else
        m_bHandicap = false;
}
private static int convertDistance(String strDistance)
{
           Matcher m1 = sm_patternDistance2.matcher(strDistance);
            if (m1.matches())
            {
                int nMiles = RegExpUtils.getMatcherGroupIntxLast(m1, 1);
                 int nFurlongs = RegExpUtils.getMatcherGroupIntxLast(m1, 2);
                 int nYards = RegExpUtils.getMatcherGroupIntxLast(m1, 3);

                return (nMiles * 1760) + (nFurlongs * 220) + nYards;
            }
            
            return 0;
}
private static int convertDistance2(String strDistance)
{
    int nYards = 0;
     String[] aDistances = strDistance.split(" ");
     for(int i = 0; i < aDistances.length; i++)
     {
         String strD = aDistances[i];
         if (strD.indexOf("m") > 0)
         {
             nYards += (Integer.parseInt(strD.replace("m", "").trim()) * 1760);
         }
         else if (strD.indexOf("f") > 0)
         {
             nYards += (Integer.parseInt(strD.replace("f", "").trim()) * 220);
         }
         else if (strD.indexOf("y") > 0)
         {
             nYards += Integer.parseInt(strD.replace("y", "").trim());
         }
     }
     
     return nYards;
}
private static double convertWinningTime(String strWinningTime)
{
          Matcher m1 = sm_patternTime.matcher(strWinningTime);
            if (m1.matches())
            {
                int nMinutes = RegExpUtils.getMatcherGroupInt(m1, 1);
                int nSeconds = RegExpUtils.getMatcherGroupInt(m1, 2);
                int nCentSeconds = RegExpUtils.getMatcherGroupInt(m1, 3);
                String strCentSeconds = m1.group(3);
                if (strCentSeconds.length() == 1)
                    nCentSeconds = nCentSeconds * 10;

                return (((double)nMinutes) * 60) + nSeconds + nCentSeconds/100.0;
            }
            else
            {
                Matcher m2 = sm_patternEuropeanTime.matcher(strWinningTime);
                if (m2.matches())
                {
                    int nMinutes = RegExpUtils.getMatcherGroupInt(m2, 1);
                    int nSeconds = RegExpUtils.getMatcherGroupInt(m2, 2);
                    int nCentSeconds = RegExpUtils.getMatcherGroupInt(m2, 3);

                    return (((double)nMinutes) * 60) + nSeconds + nCentSeconds/100.0;
                }
                else
                {
                    Matcher m3 = sm_patternTimeSecs.matcher(strWinningTime);
                    if (m3.matches())
                    {
                        int nSeconds = RegExpUtils.getMatcherGroupInt(m3, 1);
                        int nCentSeconds = RegExpUtils.getMatcherGroupInt(m3, 2);

                        return (nSeconds + nCentSeconds/100.0);
                    }
                }

            }
            
            return 0.0;
}
/*
20170401 - no longer need for new RP site
public void convertGoing2Conditions()
{
    String astrGoingConvert[] = {"OMS ", "July ", "Row ", "Jub ", "Grand National ", "Mildmay ", "Str ", "Rnd ", "New: ", "Old: ", "New ", "Old "};
    for(int i = 0; i < astrGoingConvert.length; i++)
    {
        if (m_strGoing.indexOf(astrGoingConvert[i]) >= 0)
        {
            m_strGoing = m_strGoing.replace(astrGoingConvert[i], "");
            m_strConditions += astrGoingConvert[i];
        }
    }
    Matcher matcher = Pattern.compile("\\d+").matcher(m_strGoing);
    boolean rc = matcher.find();
    if (rc)
    {
        int nIndex = matcher.start();    
        m_strConditions += m_strGoing.substring(nIndex);
        m_strGoing = m_strGoing.substring(0, nIndex-1);
    }
} */
}
