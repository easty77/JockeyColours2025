/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.smartform.bos;

import ene.eneform.utils.HorseRacingUtils;
import ene.eneform.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ene.eneform.smartform.bos.SmartformHistoricRunner.convertFinishPosition;

/**
 *
 * @author Simon
 */
public class AdditionalRunner implements SmartformColoursRunner {
    
 protected int m_nRaceId;
 protected int m_nClothNumber = -1;
 protected int m_nStallNumber = -1;      // Flat only

 protected String m_strName;
 protected String m_strBred = "";
 
 protected String m_strSireName = "";
 protected String m_strSireBred = "";

 protected String m_strDamName = "";
 protected String m_strDamBred = "";

 protected String m_strDamSireName = "";
 protected String m_strDamSireBred = "";

 
 protected String m_strColour = "";
 protected String m_strGender = "";
 protected char m_cGender = ' ';

 protected SmartformTack m_tack = null;

 // historic data
 private int m_nFinishPosition = -1;
 private String m_strDistanceBeaten = "";
 private String m_strShortDistanceBeaten = "";
 private double m_dDistanceBeaten = -1;
 private String m_strDistanceBehindWinner = "";
 private double m_dDistanceBehindWinner = -1;
 private String m_strSP;
 private double m_dSP = -1;
 private boolean m_bFavourite = false;

 private double m_dDistanceWon = -1;
 private int m_nPositionInBetting = 0;
 
 protected String m_strTrainer = "";
 protected String m_strJockey = "";
 protected int m_nJockeyClaim = 0;
 protected String m_strOwner = "";
 
 protected String m_strPrimaryOwner = null;
 
 protected int m_nWeightPounds = 1;
 protected String m_strForecastSP;
 private double m_dForecastSP = -1;
 
 protected String m_strFormFigures;
 protected int m_nAge = -1;

 protected String m_strTack = "";

 protected int m_nDaysSinceRan = -1;
 protected int m_nDistanceTravelled = -1;
 
 protected int m_nPenaltyWeight = -1;
 protected int m_nOfficialRating = 0;
 
 private static Pattern sm_patternFullName1 = Pattern.compile("([\\w'\\s\\-]+)\\s([A-Z]+)");   // bred in capitals
 private static Pattern sm_patternFullName2 = Pattern.compile("([\\w'\\s\\-]+)\\s*\\(*([\\w]*)\\)*");   // optional bred in brackets
 private static Pattern sm_patternStonesPounds = Pattern.compile("([\\d]+)\\-([\\d]+)");
 private static Pattern sm_patternToteSP = Pattern.compile("([0-9]+\\.[0-9])");
 
 protected String m_strJockeyColours="";
 
 protected UnregisteredColourSyntax m_ucs = null;
 
 protected String m_strInRunning="";
 protected String m_strBetting="";
 protected String m_strUnfinished=null;

 protected String m_strSource;


public AdditionalRunner(String strSource, int nRaceId, int nClothNumber, String strName)
 {
     m_strSource = strSource; 
     m_nRaceId = nRaceId;
     m_nClothNumber = nClothNumber;
     setFullName(strName);
 }
 
public AdditionalRunner(String strSource, int nRaceId, String strName)
 {
     // From Timeform Results - no Cloth Number, name includes bred
      m_strSource = strSource; 
     m_nRaceId = nRaceId;
     setFullName(strName);
 }
    public String getSource() {
        return m_strSource;
    }
    public void setForecastSP(String strForecastSP) {
        this.m_strForecastSP = strForecastSP;
        m_dForecastSP = HorseRacingUtils.convertSP(strForecastSP);
    }

    public int getRunnerId()
    {
        return 0;   // not supported
    }
    public boolean isNonRunner()
    {
        return false;
    }
    public int getRaceId() {
        return m_nRaceId;
    }

    public int getClothNumber() {
        return m_nClothNumber;
    }

    public int getStallNumber() {
        return m_nStallNumber;
    }

    public String getName() {
        return m_strName;
    }

    public String getBred() {
        return m_strBred;
    }

    public String getSireName() {
        return m_strSireName;
    }

    public String getSireBred() {
        return m_strSireBred;
    }

    public String getDamName() {
        return m_strDamName;
    }

    public String getDamBred() {
        return m_strDamBred;
    }

    public String getDamSireName() {
        return m_strDamSireName;
    }

    public String getDamSireBred() {
        return m_strDamSireBred;
    }
    public String getColour() {
        return m_strColour;
    }

    public String getGender() {
        return m_strGender;
    }
    public char getShortGender() {
        return m_cGender;
    }

    public int getFinishPosition() {
        return m_nFinishPosition;
    }

    public String getTrainer() {
        return m_strTrainer;
    }

    public String getJockey() {
        return m_strJockey;
    }

    public String getOwner() {
        return m_strOwner;
    }

    public int getWeightPounds() {
        return m_nWeightPounds;
    }
   public int getJockeyClaim() {
        return m_nJockeyClaim;
    }

    public String getDistanceBeatenString() {
        return m_strDistanceBeaten;
    }
    public String getShortDistanceBeatenString() {
        return m_strShortDistanceBeaten;
    }
    public double getDistanceBeaten() {
        return m_dDistanceBeaten;
    }

    public double getDistanceBehindWinner()
    {
         return m_dDistanceBehindWinner;
    }
    public String getDistanceBehindWinnerString()
    {
         return m_strDistanceBehindWinner;
    }
    public String getForecastSPString() {
        return m_strForecastSP;
    }
    public double getForecastSP() {
        return m_dForecastSP;
    }

    public void setFullName(String strFullName)
{
          Matcher m1 = sm_patternFullName1.matcher(strFullName);
            if (m1.matches())
            {
                m_strName = m1.group(1);
                m_strBred = m1.group(2);
            } 
            else
            {
                m1 = sm_patternFullName2.matcher(strFullName);
                if (m1.matches())
                {
                    m_strName = m1.group(1);
                    m_strBred = m1.group(2);
                } 
                else
                {
                   m_strName = strFullName;     
                }
            }
 }
public void setSireName(String strSireName)
{
           Matcher m1 = sm_patternFullName1.matcher(strSireName);
            if (m1.matches())
            {
                m_strSireName = m1.group(1);
                m_strSireBred = m1.group(2);
            } 
            else
            {
                m1 = sm_patternFullName2.matcher(strSireName);
                if (m1.matches())
                {
                    m_strSireName = m1.group(1);
                    m_strSireBred = m1.group(2);
                } 
            }
            
 }
public void setDamName(String strDamName)
{
           Matcher m1 = sm_patternFullName1.matcher(strDamName);
            if (m1.matches())
            {
                m_strDamName = m1.group(1);
                m_strDamBred = m1.group(2);
            } 
            else
            {
                m1 = sm_patternFullName2.matcher(strDamName);
                if (m1.matches())
                {
                    m_strDamName = m1.group(1);
                    m_strDamBred = m1.group(2);
                } 
            }
            
 }
public void setDamSireName(String strDamSireName)
{
          Matcher m1 = sm_patternFullName1.matcher(strDamSireName);
            if (m1.matches())
            {
                m_strDamSireName = m1.group(1);
                m_strDamSireBred = m1.group(2);
            } 
            else
            {
                m1 = sm_patternFullName2.matcher(strDamSireName);
                if (m1.matches())
                {
                    m_strDamSireName = m1.group(1);
                    m_strDamSireBred = m1.group(2);
                } 
            }
            
 }
    public void setStallNumber(int m_nStallNumber) {
        this.m_nStallNumber = m_nStallNumber;
    }

    public void setBred(String m_strBred) {
        this.m_strBred = m_strBred;
    }

    public void setColour(String m_strColour) {
        this.m_strColour = m_strColour;
    }

    public void setGender(String strGender) {
        if (strGender == null)
            strGender="";
        this.m_strGender = strGender;
        if (strGender.length() > 0)
            m_cGender = strGender.charAt(0);
    }

    public void setTrainer(String strTrainer) {
        this.m_strTrainer = strTrainer.replace(". ", " ");
    }

    public void setJockey(String strJockey) {
        strJockey = strJockey.replace(". ", " ");
        if (strJockey == null)
            strJockey ="";
        String[] astrJockey = strJockey.split("\\(");
        if (astrJockey.length > 1)
        {
            m_strJockey = astrJockey[0].trim();
            try
            {
                m_nJockeyClaim = Integer.parseInt(astrJockey[1].replace(")", ""));
            }
            catch(Exception e)
            {
                System.out.println("Invalid jockey claim: " + astrJockey[1].replace(")", ""));
                // probably overweight
            }
        }
        else
            this.m_strJockey = strJockey;
    }

    public void setOwner(String strOwner) {
        this.m_strOwner = strOwner.replace(". ", " ");
    }

    public void setPrimaryOwner(String strPrimaryOwner) {
        this.m_strPrimaryOwner = strPrimaryOwner;
    }
    public void setWeightKilos(String strKilos)
    {
        if (strKilos.indexOf(",") > 0)
        {
            strKilos=strKilos.replace(",", ".");    // decimal point
            double dKilos = Double.parseDouble(strKilos);
            m_nWeightPounds = (int) (dKilos * 2.2046);
        }
        else
        {
            int nKilos = 0;
            try
            {
                nKilos = Integer.parseInt(strKilos);
            }
            catch(Exception e)
            {
                System.out.println("Invalid weight: " + strKilos);
            }
            m_nWeightPounds = (int) (nKilos * 2.2046);
        }
    }
    public void setWeightPounds(int nWeightPounds) {
        this.m_nWeightPounds = nWeightPounds;
    }
    public void setWeightStonesPounds(String strStonesPounds) {
          Matcher m1 = sm_patternStonesPounds.matcher(strStonesPounds);
            if (m1.matches())
            {
                int nStones = Integer.parseInt(m1.group(1));
                int nPounds = Integer.parseInt(m1.group(2));
                m_nWeightPounds = (14 * nStones) + nPounds;
            } 
            else
            {
                System.out.println("Invalid StonesPounds: " + strStonesPounds);
            }
    }
 
    public String getFullStartingPrice() {
        String strSP = m_strSP;
        if ("1/1".equalsIgnoreCase(strSP))
            strSP="Evens";
      if (isFavourite() && (strSP.indexOf("F") < 0)) // and Co-Favs ??
          strSP += (" F");

        return strSP;
    }
    public String getStartingPrice() {
        return m_strSP;
    }
   public boolean isFavourite() {
        return m_bFavourite;
    }

    public void setHistoricData(int nFinishPosition, String strDistanceBeaten, String strSP) {
        m_nFinishPosition = nFinishPosition;
        // to do: convert to decimal
        setDistanceBeaten(strDistanceBeaten);
       setStartingPrice(strSP);
    }
    public void setFavourite()
    {
        m_bFavourite = true;
        m_nPositionInBetting = 1;
    }
    public void setStartingPrice(String strSP) {
        m_strSP = strSP;
        if ((strSP.toLowerCase().indexOf("f") > 0) || (strSP.toLowerCase().indexOf("j") > 0) || (strSP.toLowerCase().indexOf("c") > 0))
        {
            m_bFavourite = true;
            m_nPositionInBetting = 1;
        }
        m_dSP = HorseRacingUtils.convertSP(strSP);
    }

    public void setToteStartingPrice(String strToteSP) 
    {
          Matcher m1 = sm_patternToteSP.matcher(strToteSP);
          if (m1.matches())
          {
                m_dSP = Double.parseDouble(strToteSP) - 1.0;
          } 
          if (m_dSP < 0)
          {
              // do nothing - no SP
          }
          else 
          {
              m_strSP = HorseRacingUtils.convertToteToSP(m_dSP);
          }
    }
    public String getFormFigures() {
        return m_strFormFigures;
    }

    public void setFormFigures(String strFormFigures) {
        this.m_strFormFigures = strFormFigures;
    }

    public int getAge() {
        return m_nAge;
    }

    public void setAge(int nAge) {
        this.m_nAge = nAge;
    }

   public void setDistanceBeaten(double dDistanceBeaten) {
        m_strDistanceBeaten = SmartformHistoricRunner.convertDistance(dDistanceBeaten);
        m_strShortDistanceBeaten = SmartformHistoricRunner.convertShortDistance(dDistanceBeaten);
        m_dDistanceBeaten = dDistanceBeaten;
    }
   public void setDistanceBeaten(String strDistanceBeaten) {
        m_strDistanceBeaten = strDistanceBeaten;
        m_dDistanceBeaten = HorseRacingUtils.convertDistanceBeaten(strDistanceBeaten);
    }
   public void setDistanceBehindWinner(double dDistanceBehindWinner) {
        m_strDistanceBehindWinner = SmartformHistoricRunner.convertDistance(dDistanceBehindWinner);
        m_dDistanceBehindWinner = dDistanceBehindWinner;
   }
    public void setDistanceBehindWinner(String strDistanceBehindWinner) {
        m_strDistanceBehindWinner = strDistanceBehindWinner;
        m_dDistanceBehindWinner = HorseRacingUtils.convertDistanceBeaten(strDistanceBehindWinner);
    }
    public int getDaysSinceRan() {
        return m_nDaysSinceRan;
    }

    public void setDaysSinceRan(int nDaysSinceRan) {
        this.m_nDaysSinceRan = nDaysSinceRan;
    }
       public int getPenaltyWeight() {
        return m_nPenaltyWeight;
    }

    public void setPenaltyWeight(int nPenaltyWeight) {
        m_nPenaltyWeight = nPenaltyWeight;
    }
      public int getOfficialRating() {
        return m_nOfficialRating;
    }

    public void setOfficialRating(int nOfficialRating) {
        m_nOfficialRating = nOfficialRating;
    }

   
public @Override String toString()
 {
       String strOutput =  "Name: " + m_strName;
       if (m_nClothNumber!= -1)
           strOutput += " Cloth Number: " + m_nClothNumber;
       if (m_nStallNumber!= -1)
           strOutput += " Stall: " + m_nStallNumber;
       if (!"".equals(m_strBred))
            strOutput += " Bred: " + m_strBred;
       strOutput += " Owner: " + m_strOwner;
       strOutput += " Trainer: " + m_strTrainer;
       strOutput += " Jockey: " + m_strJockey;
       if (!"".equals(m_strColour))
            strOutput += " Colour: " + m_strColour;
       if (!"".equals(m_strGender))
            strOutput += " Gender: " + m_strGender; 
       if (!"".equals(m_strSireName))
           strOutput += " Sire: " + m_strSireName; 
       if (!"".equals(m_strDamName))
            strOutput += " Dam: " + m_strDamName; 
      if (!"".equals(m_strDamSireName))
           strOutput += " DamSire: " + m_strDamSireName; 
     if (m_strFormFigures != null)
           strOutput += " Form Figures: " + m_strFormFigures; 
     if (m_strForecastSP != null)
           strOutput += " Forecast SP: " + m_strForecastSP; 
     if (!"".equals(m_strTack))
           strOutput += " Tack: " + m_strTack; 
       if (m_nWeightPounds > 1)
           strOutput += " Weight: " + m_nWeightPounds;
       if (m_nDaysSinceRan > 0)
           strOutput += " Days Since Ran: " + m_nDaysSinceRan;
       if (m_nAge > 1)
           strOutput += " Age: " + m_nAge;
       if (m_nFinishPosition != -1)
       {
            strOutput += (" Position: " + m_nFinishPosition + " Distance: " + m_strDistanceBeaten + ((m_strSP != null) ? (" SP: " + m_strSP) : ""));           
       }
       
       return strOutput;
}
    // SmartformColoursRunner interface
    public String getTrainerName() {
        return getTrainer();
    }

    public String getJockeyName() {
        return getJockey();
    }
    public String getOwnerName() {
        return getOwner();
    }
    public String getPrimaryOwner() {
        return m_strPrimaryOwner;
    }
    public int getStall()
    {
        return getStallNumber();
    }
    public String getFinishPositionString()
    {
        if (m_nFinishPosition <= 0)
            return m_strUnfinished;
        else
             return String.valueOf(m_nFinishPosition);
//           return StringUtils.getOrdinalString(m_nFinishPosition);
    }

    public String getShortFinishPositionString()
    {
        if (m_nFinishPosition <= 0)
            return convertFinishPosition(m_strUnfinished, false);
        else
           return StringUtils.getOrdinalString(m_nFinishPosition);
    }

    public String getJockeyColours() {
        return m_strJockeyColours;
    }

    public void setJockeyColours(String strJockeyColours) {
        this.m_strJockeyColours = strJockeyColours;
    }
public UnregisteredColourSyntax getUnregisteredColourSyntax()
{
    return m_ucs;
}
public void setUnregisteredColourSyntax(UnregisteredColourSyntax ucs)
{
    m_ucs = ucs;;
}
    public String getInRaceComment() {
        return m_strInRunning;
    }

    public void setInRunning(String strInRunning) {
        if (strInRunning != null)
            this.m_strInRunning = strInRunning;
    }

    public String getBetting() {
        return m_strBetting;
    }

    public String getUnfinished() {
        return m_strUnfinished;
    }
    
    public void setBetting(String m_strBetting) {
        this.m_strBetting = m_strBetting;
    }
    public void setFinishPosition(String strPosition) 
    {
        String strOrdinal = strPosition.toLowerCase();
    
        // unfinished
        if ("PU".equalsIgnoreCase(strOrdinal) || "arr.".equalsIgnoreCase(strOrdinal))   // Paris Turf
            m_strUnfinished = "Pulled Up";
        else if ("F".equalsIgnoreCase(strOrdinal))
            m_strUnfinished="Fell";
        else if ("UR".equalsIgnoreCase(strOrdinal) || "tbé".equalsIgnoreCase(strOrdinal))   // Paris Turf
            m_strUnfinished="Unseated Rider";
        else if ("SU".equalsIgnoreCase(strOrdinal))
            m_strUnfinished="Slipped Up";
        else if ("RO".equalsIgnoreCase(strOrdinal))
            m_strUnfinished="Ran Out";
        else if ("BD".equalsIgnoreCase(strOrdinal))
            m_strUnfinished="Brought Down";
        else if ("REF".equalsIgnoreCase(strOrdinal))
            m_strUnfinished="Refused";
        else if ("rp".equalsIgnoreCase(strOrdinal)) // Paris Turf
            m_strUnfinished="Refused To Race";
        else if ("R".equalsIgnoreCase(strOrdinal))
            m_strUnfinished="Refused";
        else if ("CO".equalsIgnoreCase(strOrdinal))
            m_strUnfinished="Carried Out";
        else if ("Npl.".equalsIgnoreCase(strOrdinal) || "NP".equalsIgnoreCase(strOrdinal))  // Paris Turf
        {
            // finished, but no info on position 
        }
        else
        {
            // ParisTurf
            if (strOrdinal.indexOf("dist.") >= 0)
            {
                m_strUnfinished="Disqualified";
                strOrdinal = strOrdinal.replace("dist.", "");
            }
            
            // to do convert to cardinal
            strOrdinal=strOrdinal.replace("rd", "").replace("nd", "").replace("st", "").replace("th", "");
            // French (Paris Turf)
            strOrdinal=strOrdinal.replace("er", "").replace("e", "");
            
            try
            {
                m_nFinishPosition = Integer.valueOf(strOrdinal.replace(" ", ""));
            }
            catch(NumberFormatException e)
            {
                System.out.println("Invalid position: " + m_strName + "-" + m_nRaceId + ": " + strPosition);
                m_strUnfinished = strPosition;
            }
        }
    }

    public SmartformTack getTack()
    {
        return m_tack;
    }

    public void setTack(SmartformTack tack)
    {
        m_tack = tack;
    }

    public int getDistanceTravelled(){return m_nDistanceTravelled;}
    public void setDistanceTravelled(int nDistanceTravelled){ m_nDistanceTravelled=nDistanceTravelled;}

    public void setDistanceWon(double dDistanceWon){m_dDistanceWon=dDistanceWon;}
    public void setStartingPriceDecimal(double dSP){m_dSP=dSP;}
    public void setPositionInBetting(int nPositionInBetting){m_nPositionInBetting=nPositionInBetting;}

    public double getDistanceWon(){return m_dDistanceWon;}
    public double getStartingPriceDecimal(){return m_dSP;}
    public int getPositionInBetting(){return m_nPositionInBetting;}
    
}
