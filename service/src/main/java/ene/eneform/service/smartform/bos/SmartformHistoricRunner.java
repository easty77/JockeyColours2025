/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.service.smartform.bos;

import ene.eneform.service.utils.StringUtils;

import java.text.DecimalFormat;

/**
 *
 * @author Simon
 */
public class SmartformHistoricRunner extends SmartformDailyRunner implements Comparable<SmartformHistoricRunner>, SmartformColoursRunner {

    private static final DecimalFormat sm_df = new DecimalFormat("#.##");
    protected int m_nDistanceTravelled=0;
    protected int m_nFencesJumped=0;
    protected int m_nHowEasyWon=0;
    protected String m_strInRaceComment="";
    protected String m_strOfficialRatingType="";
    protected int m_nSpeedRating=0;
    protected String m_strSpeedRatingType="";
    protected int m_nPrivateHandicap=0;
    protected String m_strPrivateHandicapType="";
    protected String m_strStartingPrice="";
    protected double m_dStartingPriceDecimal=0;
    protected String m_strBettingText="";
    protected int m_nPositionInBetting=0;
    protected int m_nFinishPosition=0;
    protected int m_nAmendedPosition=0;
    protected String m_strUnfinished="";
    protected double m_dDistanceBeaten=0;
    protected double m_dDistanceWon=0;
    protected double m_dDistanceBehindWinner=0;
    protected double m_dPrizeMoney=0;
    protected double m_dToteWin=0;
    protected double m_dTotePlace=0;
    protected int m_nLastRaceTypeId=0;
    protected int m_nLastRaceBeatenFav=0;
    protected int m_nOverWeight=0;

    public SmartformHistoricRunner(int nRace, int nRunner)
    {
        super(nRace, nRunner);
    }

    public @Override boolean isHistoric()
    {
        return true;
    }
     public double getDistanceBeaten() {
        return m_dDistanceBeaten;
    }
public static String convertDistance(double dDistance)
{   
       if (dDistance == 0)
            return "deadheat";
        else if(dDistance == 0.02)
            return "nose";    // Timeform use ns, SL use nse
        else if(dDistance == 0.05 || dDistance == 0.06)
            return "short head";    // Timeform use ns, SL use nse
         else if(dDistance == 0.12)
            return "head";
        else if(dDistance == 0.2)
            return "short neck";
        else if(dDistance == 0.25)
            return "neck";
        else if(dDistance == 0.5)
            return "� length";
        else if(dDistance == 0.75)
            return "� length";
        else if (((int)dDistance) == dDistance)
        {
            int nDistance = (int)dDistance;
            return String.valueOf(nDistance) + " length" + ((nDistance > 1) ? "s" : "");
        }
        else if ((dDistance - (int)dDistance) == 0.25)
            return (String.valueOf((int)dDistance) + "� lengths");
        else if ((dDistance - (int)dDistance) == 0.5)
            return (String.valueOf((int)dDistance) + "� lengths");
        else if ((dDistance - (int)dDistance) == 0.75)
            return (String.valueOf((int)dDistance) + "� lengths");
       else
            return sm_df.format(dDistance);
}
public static String convertShortDistance(double dDistance)
{   
       if (dDistance == 0)
            return "dh";
        else if(dDistance == 0.02)
            return "ns";    // Timeform use ns, SL use nse
         else if(dDistance == 0.05 || dDistance == 0.06)
            return "shd";
         else if(dDistance == 0.12)
            return "hd";
        else if(dDistance == 0.2)
            return "snk";
        else if(dDistance == 0.25)
            return "nk";
        else if(dDistance == 0.5)
            return "�";
        else if(dDistance == 0.75)
            return "�";
        else if (((int)dDistance) == dDistance)
            return String.valueOf((int)dDistance);
        else if ((dDistance - (int)dDistance) == 0.25)
            return (String.valueOf((int)dDistance) + "�");
        else if ((dDistance - (int)dDistance) == 0.5)
            return (String.valueOf((int)dDistance) + "�");
        else if ((dDistance - (int)dDistance) == 0.75)
            return (String.valueOf((int)dDistance) + "�");
       else
            return String.valueOf(dDistance);
}
public String getShortDistanceBeatenString() 
     {
         return convertShortDistance(m_dDistanceBeaten);
     }
public @Override String getDistanceBeatenString() 
     {
         if ((m_strUnfinished == null) || "".equals(m_strUnfinished))
              return convertDistance(m_dDistanceBeaten);
         else
             return "";
     }

   public String getShortDistanceWonString() 
     {
         return convertShortDistance(m_dDistanceWon);
     }
    public String getDistanceWonString() 
     {
         return convertDistance(m_dDistanceWon);
     }

    public void setDistanceBeaten(double dDistanceBeaten) {
        this.m_dDistanceBeaten = dDistanceBeaten;
    }

    public double getDistanceBehindWinner() {
        return m_dDistanceBehindWinner;
    }

   public String getDistanceBehindWinnerString() {
         if ((m_strUnfinished == null) || "".equals(m_strUnfinished))
              return convertDistance(m_dDistanceBehindWinner);
         else
             return "";
    }

   public void setDistanceBehindWinner(double dDistanceBehindWinner) {
        this.m_dDistanceBehindWinner = dDistanceBehindWinner;
    }

    public double getDistanceWon() {
        return m_dDistanceWon;
    }

    public void setDistanceWon(double dDistanceWon) {
        this.m_dDistanceWon = dDistanceWon;
    }

    public double getPrizeMoney() {
        return m_dPrizeMoney;
    }

    public void setPrizeMoney(double dPrizeMoney) {
        this.m_dPrizeMoney = dPrizeMoney;
    }

    @Override public double getStartingPriceDecimal() {
        return m_dStartingPriceDecimal;
    }

    public void setStartingPriceDecimal(double dStartingPriceDecimal) {
        this.m_dStartingPriceDecimal = dStartingPriceDecimal;
    }

    public double getTotePlace() {
        return m_dTotePlace;
    }

    public void setTotePlace(double dTotePlace) {
        this.m_dTotePlace = dTotePlace;
    }

    public double getToteWin() {
        return m_dToteWin;
    }

    public void setToteWin(double dToteWin) {
        this.m_dToteWin = dToteWin;
    }

    public int getAmendedPosition() {
        return m_nAmendedPosition;
    }

    public void setAmendedPosition(int nAmendedPosition) {
        this.m_nAmendedPosition = nAmendedPosition;
    }

    @Override public int getDistanceTravelled() {
        return m_nDistanceTravelled;
    }

    public void setDistanceTravelled(int nDistanceTravelled) {
        this.m_nDistanceTravelled = nDistanceTravelled;
    }

    public int getFencesJumped() {
        return m_nFencesJumped;
    }

    public void setFencesJumped(int nFencesJumped) {
        this.m_nFencesJumped = nFencesJumped;
    }

    @Override public int getFinishPosition() {
        return m_nFinishPosition;
    }

    public void setFinishPosition(int nFinishPosition) {
        this.m_nFinishPosition = nFinishPosition;
    }

    public int getHowEasyWon() {
        return m_nHowEasyWon;
    }

    public void setHowEasyWon(int nHowEasyWon) {
        this.m_nHowEasyWon = nHowEasyWon;
    }

    public int getLastRaceBeatenFav() {
        return m_nLastRaceBeatenFav;
    }

    public void setLastRaceBeatenFav(int nLastRaceBeatenFav) {
        this.m_nLastRaceBeatenFav = nLastRaceBeatenFav;
    }

    public int getLastRaceTypeId() {
        return m_nLastRaceTypeId;
    }

    public void setLastRaceTypeId(int nLastRaceTypeId) {
        this.m_nLastRaceTypeId = nLastRaceTypeId;
    }

    public int getOverWeight() {
        return m_nOverWeight;
    }

    public void setOverWeight(int nOverWeight) {
        this.m_nOverWeight = nOverWeight;
    }

    @Override public int getPositionInBetting() {
        return m_nPositionInBetting;
    }

    public void setPositionInBetting(int nPositionInBetting) {
        this.m_nPositionInBetting = nPositionInBetting;
    }

    public int getPrivateHandicap() {
        return m_nPrivateHandicap;
    }

    public void setPrivateHandicap(int nPrivateHandicap) {
        this.m_nPrivateHandicap = nPrivateHandicap;
    }

    public String getPrivateHandicapType() {
        return m_strPrivateHandicapType;
    }

    public void setPrivateHandicapType(String strPrivateHandicapType) {
        this.m_strPrivateHandicapType = strPrivateHandicapType;
    }

    public int getSpeedRating() {
        return m_nSpeedRating;
    }

    public void setSpeedRating(int nSpeedRating) {
        this.m_nSpeedRating = nSpeedRating;
    }

    public String getSpeedRatingType() {
        return m_strSpeedRatingType;
    }

    public void setSpeedRatingType(String strSpeedRatingType) {
        this.m_strSpeedRatingType = strSpeedRatingType;
    }

    public String getBettingText() {
        return m_strBettingText;
    }

    public void setBettingText(String strBettingText) {
        this.m_strBettingText = strBettingText;
    }

    @Override public String getInRaceComment() {
        return m_strInRaceComment;
    }

    public void setInRaceComment(String strInRaceComment) {
        if (strInRaceComment != null)
            this.m_strInRaceComment = strInRaceComment;
    }

    public String getOfficialRatingType() {
        return m_strOfficialRatingType;
    }

    public void setOfficialRatingType(String strOfficialRatingType) {
        this.m_strOfficialRatingType = strOfficialRatingType;
    }

    @Override public String getStartingPrice() {
             return m_strStartingPrice;
    }

    @Override public String getFullStartingPrice() {
        String strSP = m_strStartingPrice;
        if ("1/1".equalsIgnoreCase(strSP))
            strSP="Evens";
      if (getPositionInBetting() == 1)  // and Co-Favs ??
          strSP += (" F");

        return strSP;
    }

    public void setStartingPrice(String strStartingPrice) {
        this.m_strStartingPrice = strStartingPrice;
    }

    public String getUnfinished() {
        return m_strUnfinished;
    }

    public void setUnfinished(String strUnfinished) {
        this.m_strUnfinished = strUnfinished;
    }

    @Override public String getFinishPositionString()
    {
        if (m_nFinishPosition == 0)
            return m_strUnfinished;
        else
            //return StringUtils.getOrdinalString(m_nFinishPosition);
            return String.valueOf(m_nFinishPosition);
    }

    public String getShortFinishPositionString()
    {
        if (m_nFinishPosition == 0)
        {
            return convertFinishPosition(m_strUnfinished, false);
        }
       else
            return StringUtils.getOrdinalString(m_nFinishPosition);
    }
    public static String convertFinishPosition(String strUnfinished, boolean bSingle)
    {
        String strShort = null;
         if ("Unseated Rider".equalsIgnoreCase(strUnfinished))
             strShort= "UR";
         else if (strUnfinished.toLowerCase().contains("disqualified") || "DSQ".equalsIgnoreCase(strUnfinished))
             strShort= "DIS";
         else if ("Fell".equalsIgnoreCase(strUnfinished))
             strShort= "F";
         else if ("Pulled Up".equalsIgnoreCase(strUnfinished))
             strShort= "PU";
         else if ("Slipped Up".equalsIgnoreCase(strUnfinished))
             strShort= "SU";
         else if ("Ran Out".equalsIgnoreCase(strUnfinished))
             strShort= "RO";
         else if ("Carried Out".equalsIgnoreCase(strUnfinished))
             strShort= "CO";
         else if ("Brought Down".equalsIgnoreCase(strUnfinished))
             strShort= "BD";
         else if (("Refused".equalsIgnoreCase(strUnfinished)) || ("Refused to Race".equalsIgnoreCase(strUnfinished)))
             strShort= "R";
         else if ("Withdrawn".equalsIgnoreCase(strUnfinished))  // didn't run, but included for special circumstances
             strShort= "-";
         
         if (strShort != null)
         {
             if ((strShort.length() > 1) && bSingle)
                 return strShort.substring(0, 1);
             else
                 return strShort;
         }
         else    
             return (strUnfinished == null) ? "" : strUnfinished;
    }
    public static String convertFinishPositionSingle(String strUnfinished)
    {
        try
        {
            Integer.valueOf(strUnfinished);
        }
        catch(NumberFormatException e)
        {
            strUnfinished = convertFinishPosition(strUnfinished, true);
        }
        
        return strUnfinished;
    }
    public int compareTo(SmartformHistoricRunner runner)
    {
        if (m_nFinishPosition == 0)
        {
            if ((m_nFinishPosition == runner.getFinishPosition()))
            {
                if (m_nFencesJumped > runner.getFencesJumped())
                    return -1;
                else if (m_nFencesJumped < runner.getFencesJumped())
                    return 1;
                else if (m_strUnfinished != null)
                {
                    if (m_strUnfinished.equals(runner.getUnfinished()))
                        return 0;
                    else if("Non-Runner".equals(m_strUnfinished))
                        return 1;
                   else if("Non-Runner".equals(runner.getUnfinished()))
                        return -1;
                    else
                        return m_strUnfinished.compareTo(runner.getUnfinished());
                }
                else
                    return 0;
            }
            else
                return 1;
        }
        else if (runner.getFinishPosition() == 0)
            return -1;
        else if (m_nFinishPosition < runner.getFinishPosition())
                return -1;
        else if (m_nFinishPosition > runner.getFinishPosition())
                return 1;
        else
            return 0;
    }
}
