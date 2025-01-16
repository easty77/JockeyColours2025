/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.smartform.bos;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Simon
 */
public class SmartformBasicRace extends SmartformRunnerContainer{
    
    protected int m_nRace;
    protected Date m_dtRace;        // date and time
    protected String m_strCourse;
    protected String m_strTitle;
    protected int m_nDistanceYards;
    protected String m_strGoing;
    protected String m_strRaceType;
    protected int m_nGroupRace = 0;
    protected boolean m_bHandicap=false;
    protected int m_nRunners = 0;
    protected String m_strDirection;
    protected long m_lScheduledTime=-1;
    
    protected String m_strSex="";
    protected String m_strAgeRange="";

    protected String m_strCountry="";
    protected String m_strWinner="";

    public SmartformBasicRace(int nRace)
    {
        this(nRace, null);
    }
    public SmartformBasicRace(int nRace, Date dtRace)
    {
        m_nRace = nRace;
        m_dtRace = dtRace;
    }
    public SmartformBasicRace(int nRace, Date dtRace, String strCourse, String strTitle, String strGoing, String strRaceType, int nRunners)
    {
        m_nRace = nRace;
        m_dtRace = dtRace;
        m_strCourse = strCourse;
        m_strTitle = strTitle;
        m_strGoing = strGoing;
        m_strRaceType = strRaceType;
        m_nRunners = nRunners;
    }
    public void setDateTime(long lMilliseconds)
    {
        m_dtRace = new Date(lMilliseconds);
    }
    public int getRaceId()
    {
        return m_nRace;
    }

    public String getFormattedRaceTime()
    {
        if(m_dtRace != null)
            return SmartformEnvironment.getInstance().getTimeFormat().format(m_dtRace);
        else
            return "";
    }

    public int getYear()
    {
        int nYear = 0;
        try
        {
            nYear = Integer.parseInt(getYearString());
        }
        catch(NumberFormatException e)
        {
            
        }

        return nYear;
    }
    public String getYearString()
    {
        if(m_dtRace != null)
            return SmartformEnvironment.getInstance().getYearFormat().format(m_dtRace);
        else
            return "";
    }

    public String getMonthString()
    {
        if(m_dtRace != null)
            return SmartformEnvironment.getInstance().getMonthFormat().format(m_dtRace);
        else
            return "";
    }
    public String getDayString()
    {
        if(m_dtRace != null)
            return SmartformEnvironment.getInstance().getDayFormat().format(m_dtRace);
        else
            return "";
    }
   public Date getMeetingDate()
    {
        return m_dtRace;
    }
    public String getFormattedMeetingDate()
    {
       if(m_dtRace != null)
            return SmartformEnvironment.getInstance().getDateFormat().format(m_dtRace);
        else
            return "";
    }

    public String getFormattedMeetingDate(String strFormat)
    {
       if(m_dtRace != null)
            return new SimpleDateFormat( strFormat ).format(m_dtRace);
        else
            return "";
    }
   public String getSeasonString()
   {
       String strSeason;
       if ("Flat".equals(m_strRaceType) || "All Weather Flat".equals(m_strRaceType))
           strSeason = getYearString();
       else if (m_dtRace.getMonth() > 7)
       {
           strSeason = getYearString() + "-" + String.valueOf(m_dtRace.getYear() + 1901).substring(2);
       }
        else
       {
           strSeason =  String.valueOf(m_dtRace.getYear() + 1899) + "-" + getYearString().substring(2);
       }

       return strSeason;
    }

    public Date getDateTime()
    {
        return m_dtRace;
    }
    public void setDirection(String strDirection)
    {
        m_strDirection = strDirection;
    }
    public void setCourse(String strCourse)
    {
        m_strCourse = strCourse;
    }
    public void setTitle(String strTitle)
    {
        m_strTitle = strTitle;
    }
   public void setRaceType(String strRaceType)
    {
        m_strRaceType = strRaceType;
    }
    public void setGoing(String strGoing)
    {
        m_strGoing = strGoing;
    }
    public void setDistanceYards(int nDistanceYards)
    {
        m_nDistanceYards = nDistanceYards;
    }
    public String getCourse()
    {
        return m_strCourse;
    }
    public String getDirection()
    {
        return m_strDirection;
    }
    public String getTitle()
    {
        return m_strTitle;
    }
    public String getAbbreviatedTitle()
    {
        return getAbbreviatedTitle(m_strTitle);
    }
    public static String getAbbreviatedTitle(String strTitle) {
        // todo: moved to new BasicRace class
        return "Abbreviated Title";
    }

    private static String removeSponsor(String strRaceTitle)
    {
        String[] astrNoReplace ={"Betfair Chase", "Betfair Hurdle", "Betfred Sprint Cup", "Bet365 Hurdle", "Bet365 Chase", "Racing Post Trophy", "Racing Post Chase", "Racing Post Novice Chase", "Ryanair Hurdle", "Ryanair Chase", "Royal Sunalliance Novices' Hurdle"};
        for (int i = 0; i < astrNoReplace.length; i++)
        {
            if (strRaceTitle.equalsIgnoreCase(astrNoReplace[i]))
                return strRaceTitle;
        }
        
        if ("Dobbins & Madigans At Punchestown Hurdle".equalsIgnoreCase(strRaceTitle))
            strRaceTitle =  "Dobbins & Madigans At Punchestown Morgiana Hurdle";;
        
        strRaceTitle = replaceSponsorString(strRaceTitle);
        
        return strRaceTitle.replace("Vi ", "VI ").replace("Rsa ", "RSA ");
    }
    public static String replaceSponsorString(String strRaceTitle)
    {
        String[] astrReplace ={"William Hill", "John Smith's", "Keith Prowse Hospitality", "Coral-", "Crabbie's", "Racing Uk On Virgin 536", "Cathay Pacific",
        "A.P. Wins Sports Personality", "Rabobank", "Rewards4Racing", "Www.punchestown.com", "JLT", "888sport", "Duty Free", "Foster's", "Crowson",  "Garrard",
        "paddypower.com iPhone App", "Paddypower.com", "Paddy Power", "Evening Herald", "Dobbins & Madigans At Punchestown", "Juddmonte", "Qipco", "Market Slide", "QNB", "Willmott Dixon",
        "Martell", "Deloitte And Touche", "Duggan Brothers", "Doom Bar", "Qatar", "Investec", "williamhill.com", "Matalan", "JCB", "Racing Post", "Longines", "Coolmore", "Pearl Bloodstock",
        "Merewood Homes", "Merewood Group", "HBLB", "Quantel", "Grangewood", "JPMorgan Private Bank", "JPMorgan", "J. P. Morgan", "Weatherbys Insurance",
        "GNER", "Great North Eastern Railway", "Tooheys New", "Champagne Lanson", "Vodafone", "Jefferson Smurfit Memorial", "Kingspin", "Darley",
        "Commercial First", "Cantor Fitzgerald", "MBNA", "Dubai Duty Free Finest", "Vision.ae", "188Bet", "bet365", "32Red"};
        for (int i = 0; i < astrReplace.length; i++)
        {
            int nIndex = strRaceTitle.toLowerCase().indexOf(astrReplace[i].toLowerCase());
            if (nIndex == 0)
            {
                return strRaceTitle.substring(astrReplace[i].length()).trim();
            }
        }
        astrReplace = new String[]{"Avonmore Waterford", "Avonmore", "AIG Europe", "A.I.B. Agri-Business", "ABN Amro", "Andrex"};
        for (int i = 0; i < astrReplace.length; i++)
        {
            int nIndex = strRaceTitle.toLowerCase().indexOf(astrReplace[i].toLowerCase());
            if (nIndex == 0)
            {
                return strRaceTitle.substring(astrReplace[i].length()).trim();
            }
        }

        astrReplace = new String[]{"Betfred TV", "Betfair Price Rush", "betfred.com", "Bonusprint.com", "Betfred", "Betfair", "Bet365.com", "Bet365", "Bibby Financial Services Ireland", "Bathwick Tyres",
        "Byrne Group", "BGC Partners", "Bar One Racing", "BHP Insurances", "BHP Insurance", "BetVictor", "Betway", "Bovis Homes", "Bonusprint"};
        for (int i = 0; i < astrReplace.length; i++)
        {
            int nIndex = strRaceTitle.toLowerCase().indexOf(astrReplace[i].toLowerCase());
            if (nIndex == 0)
            {
                return strRaceTitle.substring(astrReplace[i].length()).trim();
            }
        }
        astrReplace = new String[]{"Ladbrokes", "Lough Derg"};
        for (int i = 0; i < astrReplace.length; i++)
        {
            int nIndex = strRaceTitle.toLowerCase().indexOf(astrReplace[i].toLowerCase());
            if (nIndex == 0)
            {
                return strRaceTitle.substring(astrReplace[i].length()).trim();
            }
        }
        astrReplace = new String[]{"StanJames.com", "Stan James", "Seasons Holidays", "Sportinglife.com", "Sportingbet.com", "Sportingbet", "Smurfit",
        "Stanley Cooker", "Shell", "Sodexho", "Sagitta", "Sky Bet", "Stella Artois"};
        for (int i = 0; i < astrReplace.length; i++)
        {
            int nIndex = strRaceTitle.toLowerCase().indexOf(astrReplace[i].toLowerCase());
            if (nIndex == 0)
            {
                return strRaceTitle.substring(astrReplace[i].length()).trim();
            }
        }
        astrReplace = new String[]{"Totesport.com", "Totesport", "Totescoop6", "Totepool.com", "Totepool", "Tattersalls Millions", "Bet Online With TheTote.com", "Thebettingsite.com"};
        for (int i = 0; i < astrReplace.length; i++)
        {
            int nIndex = strRaceTitle.toLowerCase().indexOf(astrReplace[i].toLowerCase());
            if (nIndex == 0)
            {
                return strRaceTitle.substring(astrReplace[i].length()).trim();
            }
        }
        return strRaceTitle;
    }
    public String getRaceType()
    {
        if ("N_H_Flat".equals(m_strRaceType))
            return "National Hunt Flat";
        else if ("A_W_Flat".equals(m_strRaceType))
            return "All Weather Flat";
        else
            return m_strRaceType;
    }
    public String getGoing()
    {
        return m_strGoing;
    }
    public String getShortGoing()
    {
        if (m_strGoing != null)
            return m_strGoing.replace(" to ", "/").replace(" To ", "/").replace(" - ", "/");
        
        return "";
    }
    public int getDistanceYards()
    {
        return m_nDistanceYards;
    }
    public int getNrRunners()
    {
        return m_nRunners;
    }
    public int getDistanceFurlongs()
    {
        return (int) (((double)m_nDistanceYards)/220.0 + 0.5);
    }
    public double getDecimalDistance()
    {
        int nDistance10 = (int)((m_nDistanceYards * 10.0)/220.0);
        return nDistance10/10.0; // scale it
    }
    public String getFormattedDistance()
    {
        return getShortFormattedDistance(false);
/*         int nMiles = m_nDistanceYards/(220 * 8);
       int nRemainder = (m_nDistanceYards - (nMiles * 220 * 8));
        int nFurlongs = nRemainder/220;
        nRemainder = (m_nDistanceYards - (nMiles * 220 * 8) - (nFurlongs * 220));
        int nYards = nRemainder;

        String strDistance = "";
        if (nMiles > 0)
            strDistance += (nMiles + "m ");

        if (nFurlongs > 0)
            strDistance += (nFurlongs + "f ");

        if (nYards >  0)
            strDistance += (nYards + "y");

        return strDistance.trim(); */
    }
    public String getLongFormattedDistance(boolean bHTML)
    {
        return getFormattedDistance(m_nDistanceYards, false, false, bHTML);
    }
    public String getShortFormattedDistance(boolean bHTML)
    {
        return getFormattedDistance(m_nDistanceYards, true, false, bHTML);
    }
    
    public static String getFormattedDistance(int nDistanceYards, boolean bAbbrev, boolean bYards, boolean bHTML)
    {
        String strMileLabel = bAbbrev ? "m" : " mile";
        String strFurlongLabel = bAbbrev ? "f" : " furlong";
        String strYardsLabel = bAbbrev ? "y" : " yds";
        
        int nMiles = nDistanceYards/(220 * 8);
        int nRemainder = (nDistanceYards - (nMiles * 220 * 8));
        int nFurlongs = nRemainder/220;
        nRemainder = (nDistanceYards - (nMiles * 220 * 8) - (nFurlongs * 220));
        int nYards = nRemainder;
        if ((!bYards) && (nFurlongs == 7) && (nYards > 165))
        {
            nFurlongs = 0;
            nYards = 0;
            nMiles++;
        }
        String strDistance = "";
        if (nMiles > 0)
            strDistance += (nMiles + strMileLabel);
        if ((nMiles > 1) && (!bAbbrev))
            strDistance += "s";
        strDistance += " ";

        String strFurlongs = "";
        if (nFurlongs > 0)
        {
            strFurlongs = String.valueOf(nFurlongs);
            if (!bYards)
            {
                if (nYards > 165)
                {
                    strFurlongs = String.valueOf(nFurlongs + 1);
                }
                else if (nYards > 55)
                    strFurlongs += (bHTML ? "&frac12;" : "�");
            }
            
            strFurlongs += strFurlongLabel;
        
            if ((nFurlongs > 1) && (!bAbbrev))
                strFurlongs += "s";
        }
        else if (!bYards)
        {
            if (nYards > 165)
            {
                strFurlongs = "1" + strFurlongLabel;
            }
            else if (nYards > 55)
            {
                strFurlongs = (bHTML ? "&frac12;" : "�") + strFurlongLabel;
            }
        }
        if (!"".equals(strFurlongs))
        {
            strFurlongs += " ";
            strDistance += strFurlongs;
        }

        if (bYards && (nYards > 0))
        {
            strDistance += (nYards + strYardsLabel);
        }

        return strDistance.trim();
    }
    public void setNrRunners(int nRunners)
    {
        m_nRunners = nRunners;
    }
    public void setGroupRace(int nGroupRace)
    {
        m_nGroupRace = nGroupRace;
    }
    public int getGroupRace()
    {
        int nGroupRace = m_nGroupRace;
        if ((m_nGroupRace <= 0) && ((m_strTitle.indexOf(" Listed") >= 0) || (m_strTitle.indexOf("(Listed") >= 0)))
            nGroupRace = 4;

       return nGroupRace;
    }
    public boolean isHandicap()
    {
        return m_bHandicap;
    }       
    public void setHandicap(boolean bHandicap)
    {
        m_bHandicap = bHandicap;
    }
    public String getScheduledTime()
    {
        if (m_lScheduledTime > 0)
            return new SimpleDateFormat("HH.mm").format(m_lScheduledTime);
        else
            return null;
    }       
    public void setScheduledTime(long lScheduledTime)
    {
        
        m_lScheduledTime = lScheduledTime;
    }
    public String getSex() {
        return m_strSex;
    }

    public void setSex(String strSex) {
        m_strSex = strSex;
    }

    public String getCountry() {
        return m_strCountry;
    }

    public void setCountry(String strCountry) {
        m_strCountry = strCountry;
    }
    public String getWinner() {
        return m_strWinner;
    }

    public void setWinner(String strWinner) {
        m_strWinner = strWinner;
    }
    public String getAgeRange() {
        return m_strAgeRange;
    }

    public void setAgeRange(String strAgeRange) {
        m_strAgeRange = strAgeRange;
    }

}
