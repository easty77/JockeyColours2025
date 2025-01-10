/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.smartform.bos;

import ene.eneform.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Simon
 */
public abstract class SmartformRace extends SmartformRunnerContainer {
    protected SmartformBasicRace m_race;
    protected int m_nBetfair = 0;

    protected boolean m_bValid = false;
    protected int m_nMeeting = 0;
    protected Calendar m_calRace;

    protected String m_strRaceClass;
    protected String m_strTrackType;
    protected double m_dAddedMoney;
    protected double m_dPenaltyValue;

    protected boolean m_bHandicap;
    protected boolean m_bSeller = false;
    protected boolean m_bMaiden = false;
    protected boolean m_bClaimer = false;
    protected boolean m_bApprentice = false;
    protected boolean m_bAmateur = false;
    protected boolean m_bAllWeather = false;

    protected boolean m_bNovice = false;
    protected boolean m_bBeginners = false;
    protected boolean m_bConditional = false;

    protected int m_nMinAge = 0;
    protected int m_nMaxAge = 0;

    protected String m_strSex="";

    protected ArrayList<String> m_arCategories = new ArrayList<String>();
 
    static protected String RACE_HANDICAP = "Handicap";
    static protected String RACE_NURSERY = "Nursery";
    static protected String RACE_SELLER = "Seller";
    static protected String RACE_MAIDEN = "Maiden";
    static protected String RACE_CLAIMER = "Claimer";
    static protected String RACE_APPRENTICE = "Apprentice";
    static protected String RACE_AMATEUR = "Amateur";
    static protected String RACE_GROUP = "Group";
    static protected String RACE_NOVICE = "Novice";
    static protected String RACE_BEGINNERS = "Beginners";
    static protected String RACE_CONDITIONAL = "Conditional";

    public SmartformRace(int nRace)
    {
        m_race = new SmartformBasicRace(nRace);
    }

    public void setDateTime(long lMilliseconds)
    {
        m_race.setDateTime(lMilliseconds);
    }

    public void setBetfairId(int nBetfair)
    {
        m_nBetfair = nBetfair;
    }

   public int getRaceId()
    {
        return m_race.getRaceId();
    }

    public String getFormattedRaceTime()
    {
        return m_race.getFormattedRaceTime();
    }

    public String getYearString()
    {
        return m_race.getYearString();
    }

    public String getMonthString()
    {
        return m_race.getMonthString();
    }
    public String getDayString()
    {
        return m_race.getDayString();
    }
    public String getFormattedMeetingDate()
    {
       return m_race.getFormattedMeetingDate();
    }

    public String getFormattedMeetingDate(String strFormat)
    {
       return m_race.getFormattedMeetingDate(strFormat);
    }

    public Date getDateTime()
    {
        return m_race.getDateTime();
    }

    public int getBetfairId()
    {
        return m_nBetfair;
    }

    public boolean isHistoric()
    {
        return false;
    }
    public void setMeetingId(int nMeeting)
    {
        m_nMeeting = nMeeting;
    }
    public void setCalendar(long lMilliseconds)
    {
        m_calRace = new GregorianCalendar();
        m_calRace.setTimeInMillis(lMilliseconds);
    }
    public void setCourse(String strCourse)
    {
        m_race.setCourse(strCourse);
    }
    public void setTitle(String strTitle)
    {
        m_race.setTitle(strTitle);

        String strLowerTitle = strTitle.toLowerCase();
        if (strLowerTitle.indexOf("fillies") >= 0)
        {
            if (strLowerTitle.indexOf("mares") >= 0)
                m_strSex = "FM";
            else
                m_strSex = "F";
        }
        else if(strLowerTitle.indexOf("colts") >= 0)
        {
            if (strLowerTitle.indexOf("geldings") >= 0)
                m_strSex = "CG";
            else
                m_strSex = "C";
        }
        else if (strLowerTitle.indexOf("mares") >= 0)
        {
            m_strSex = "M";
        }

        if (!"Flat".equals(m_race.getRaceType()))
        {
            if (strLowerTitle.indexOf("beginners'") >= 0)
            {
                setBeginners(true);
            }
            else if(strLowerTitle.indexOf("novice") >= 0)
            {
                setNovice(true);
            }

            if (strLowerTitle.indexOf("conditional") >= 0)
            {
                setConditional(true);
            }
       }
    }
    public void setRaceClass(String strRaceClass)
    {
        m_strRaceClass = strRaceClass;
    }
    public void setRaceType(String strRaceType)
    {
        m_race.setRaceType(strRaceType);
    }
    public void setTrackType(String strTrackType)
    {
        m_strTrackType = strTrackType;
    }
    public void setGoing(String strGoing)
    {
        m_race.setGoing(strGoing);
    }
    public void setDistanceYards(int nDistanceYards)
    {
        m_race.setDistanceYards(nDistanceYards);
    }
    public void setAddedMoney(double dAddedMoney)
    {
        m_dAddedMoney = dAddedMoney;
    }
    public void setPenaltyValue(double dPenaltyValue)
    {
        m_dPenaltyValue = dPenaltyValue;
    }

    public void setHandicap(boolean bHandicap)
    {
        m_bHandicap = bHandicap;
        editCategories(m_nMaxAge == 2 ? RACE_NURSERY : RACE_HANDICAP, bHandicap);
   }
    public void setNovice(boolean bNovice)
    {
        m_bNovice = bNovice;
        editCategories(RACE_NOVICE, bNovice);
    }
    public void setBeginners(boolean bBeginners)
    {
        m_bBeginners = bBeginners;
        editCategories(RACE_BEGINNERS, bBeginners);
    }
    public void setSeller(boolean bSeller)
    {
        m_bSeller = bSeller;
        editCategories(RACE_SELLER, bSeller);
    }
    public void setClaimer(boolean bClaimer)
    {
        m_bClaimer= bClaimer;
        editCategories(RACE_CLAIMER, bClaimer);
    }
    public void setConditional(boolean bConditional)
    {
        m_bConditional = bConditional;
        editCategories(RACE_CONDITIONAL, bConditional);
    }
    public void setMaiden(boolean bMaiden)
    {
        m_bMaiden = bMaiden;
        editCategories(RACE_MAIDEN, bMaiden);
    }
    public void setApprentice(boolean bApprentice)
    {
        m_bApprentice = bApprentice;
        editCategories(RACE_APPRENTICE, bApprentice);
    }
    public void setAmateur(boolean bAmateur)
    {
        m_bAmateur = bAmateur;
        editCategories(RACE_AMATEUR, bAmateur);
    }

    private void editCategories(String strCategory, boolean bExists)
    {
         boolean bCategory = m_arCategories.contains(strCategory);
        if (bExists && !bCategory)
            m_arCategories.add(strCategory);
        else if (bCategory && !bExists)
            m_arCategories.remove(strCategory);
    }
    public int getMeetingId()
    {
        return m_nMeeting;
    }
    public Calendar getCalendar()
    {
        return m_calRace;
    }
    public String getCourse()
    {
        return m_race.getCourse();
    }
    public String getTitle()
    {
        return m_race.getTitle();
    }
    public String getAbbreviatedTitle()
    {
        return m_race.getAbbreviatedTitle();
    }
    public boolean isValid()
    {
        return m_bValid;
    }
    public String getRaceClass()
    {
        return m_strRaceClass;
    }
    public String getRaceType()
    {
        return m_race.getRaceType();
    }
    public String getGoing()
    {
        return m_race.getGoing();
    }
   public boolean isHandicap()
   {
        return m_bHandicap;
    }
   public boolean isSeller()
   {
        return m_bSeller;
    }
   public boolean isMaiden()
   {
        return m_bMaiden;
    }
   public boolean isClaimer()
   {
        return m_bClaimer;
    }
   public boolean isAmateur()
   {
        return m_bAmateur;
    }
   public boolean isApprentice()
   {
        return m_bApprentice;
    }
   public boolean isAllWeather()
   {
        return m_bAllWeather;
    }
    public int getDistanceYards()
    {
        return m_race.getDistanceYards();
    }
    public int getDistanceFurlongs()
    {
        return m_race.getDistanceFurlongs();
    }
    public double getDecimalDistance()
    {
        return m_race.getDecimalDistance();
    }
    public String getFormattedDistance(boolean bHTML)
    {
        return m_race.getShortFormattedDistance(bHTML);
    }
    public String getLongFormattedDistance(boolean bHTML)
    {
        return m_race.getLongFormattedDistance(bHTML);
    }
    public double getAddedMoney()
    {
        return m_dAddedMoney;
    }
    public double getPenaltyValue()
    {
        return m_dPenaltyValue;
    }
    public void setGroupRace(int nGroupRace)
    {
        m_race.setGroupRace(nGroupRace);
        if (nGroupRace > 0)
            editCategories(RACE_GROUP + nGroupRace, true);
    }
    public int getGroupRace()
    {
        return m_race.getGroupRace();
    }
    public String getSeasonString()
    {
        return m_race.getSeasonString();
    }
    public int getMinAge()
    {
        return m_nMinAge;
    }
    public int getMaxAge()
    {
        return m_nMaxAge;
    }
    public String getCategory()
    {
       return StringUtils.join(m_arCategories, ",");
    }
   public String getTrackType()
    {
        return m_strTrackType;
    }
   public static int getGroupFromTitle(String strTitle)
   {
       int nGroup = 0;
       if ((strTitle.indexOf("(Group 1)") > 0) || (strTitle.indexOf("(Grade 1)") > 0) || (strTitle.indexOf("(Group 1 Handicap)") > 0))
           nGroup = 1;
       else if ((strTitle.indexOf("(Group 2)") > 0) || (strTitle.indexOf("(Grade 2)") > 0))
           nGroup = 2;
       else if ((strTitle.indexOf("(Group 3)") > 0) || (strTitle.indexOf("(Grade 3)") > 0))
           nGroup = 3;
       else if ((strTitle.indexOf("(Listed)") > 0) || (strTitle.indexOf("Listed Race") > 0))
           nGroup = 4;
        
       return nGroup;
  }
}
