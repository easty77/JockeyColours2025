/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.service.smartform.bos;

/**
 *
 * @author Simon
 */
public class SmartformRunner {

    protected int m_nRace;
    protected int m_nRunner;
    protected SmartformHorse m_horse = null;
    protected SmartformTack m_tack = null;

    protected String m_strFormFigures="";
    protected int m_nAge = 0;
    protected int m_nCloth = 0;
    protected int m_nStall = 0;
    protected int m_nTrainer=0;
    protected int m_nJockey=0;
    protected int m_nLongHandicap=0;
    protected int m_nDaysSinceRan=0;
    private String m_strLastRunType="";
    protected int m_nWeightPounds=0;
    protected String m_strForecastPrice = "";
    protected double m_dForecastPriceDecimal=0;
    protected int m_nOfficialRating=0;

    protected int m_nPenaltyWeight=0;     // weight_penalty for daily and penalty_weight for historic!

    protected int m_nOwner=0;
    protected String m_strOwnerName="";
    protected String m_strJockeyName="";        // don't always have a SmartformEnvironemnt set up
    protected String m_strTrainerName="";
    protected int m_nJockeyClaim=0;

    protected boolean m_bNonRunner = false;

    protected UnregisteredColourSyntax m_ucs = null;    // Unregistered COlour Syntax - changes to officially published colours
    
    protected String m_strPreviousRace = null;      // title of previous race
    
    public SmartformRunner(int nRace, int nRunner)
    {
        m_nRace = nRace;
        m_nRunner = nRunner;
    }

    public boolean isHistoric()
    {
        return false;
    }

    public boolean isNonRunner()
    {
        return m_bNonRunner;
    }
    public void setNonRunner()
    {
        m_bNonRunner = true;
    }
    public void setHorse(SmartformHorse horse)
    {
        m_horse = horse;
    }
    public void setTack(SmartformTack tack)
    {
        m_tack = tack;
    }

    public int getRunnerId()
    {
        return m_nRunner;
    }
    public int getRaceId()
    {
        return m_nRace;
    }
    public SmartformHorse getHorse()
    {
        return m_horse;
    }
    public SmartformTack getTack()
    {
        return m_tack;
    }

    public String getName()
    {
        if (m_horse != null)
            return m_horse.getName();
        else
            return "";
    }
    public String getFullName()
    {
        if (m_horse != null)
            return m_horse.getFullName();
        else
            return "";
    }
     public int getAge() {
        return m_nAge;
    }

    public void setAge(int nAge) {
        this.m_nAge = nAge;
    }

    public int getDaysSinceRan() {
        return m_nDaysSinceRan;
    }

    public void setDaysSinceRan(int nDaysSinceRan) {
        this.m_nDaysSinceRan = nDaysSinceRan;
    }

    public int getJockeyId() {
        return m_nJockey;
    }

    public void setJockeyId(int nJockey) {
        this.m_nJockey = nJockey;
    }

    public int getLongHandicap() {
        return m_nLongHandicap;
    }

    public void setLongHandicap(int nLongHandicap) {
        this.m_nLongHandicap = nLongHandicap;
    }

    public int getOfficialRating() {
        return m_nOfficialRating;
    }

    public void setOfficialRating(int nOfficialRating) {
        this.m_nOfficialRating = nOfficialRating;
    }

    public String getOwnerName() {
        return m_strOwnerName;
    }

    public void setOwnerName(String strOwnerName) {
        this.m_strOwnerName = strOwnerName;
    }

    public int getStall() {
        return m_nStall;
    }

    public void setStall(int nStall) {
        this.m_nStall = nStall;
    }

    public int getTrainerId() {
        return m_nTrainer;
    }

    public void setTrainerId(int nTrainer) {
        this.m_nTrainer = nTrainer;
    }

    public String getForecastPrice() {
        return m_strForecastPrice;
    }

    public void setForecastPrice(String strForecastPrice) {
        this.m_strForecastPrice = strForecastPrice;
    }

   public double getForecastPriceDecimal() {
        return m_dForecastPriceDecimal;
    }

    public void setForecastPriceDecimal(double dForecastPriceDecimal) {
        this.m_dForecastPriceDecimal = dForecastPriceDecimal;
    }

    public String getFormFigures() {
        return m_strFormFigures;
    }

    public void setFormFigures(String strFormFigures) {
        this.m_strFormFigures = strFormFigures;
    }

    public int getClothNumber() {
        return m_nCloth;
    }

    public void setClothNumber(int nCloth) {
        this.m_nCloth = nCloth;
    }

    public int getPenaltyWeight() {
        return m_nPenaltyWeight;
    }

    public void setPenaltyWeight(int nPenaltyWeight) {
        this.m_nPenaltyWeight = nPenaltyWeight;
    }
    public int getWeightPounds() {
        return m_nWeightPounds;
    }

    public void setWeightPounds(int nWeightPounds) {
        this.m_nWeightPounds = nWeightPounds;
    }
    public String getLastRunType() {
        return m_strLastRunType;
    }

    public void setLastRunType(String strLastRunType) {
        this.m_strLastRunType = strLastRunType;
    }

    public String getJockeyName()
    {
        return m_strJockeyName;
        //return SmartformEnvironment.getInstance().getJockey(m_nJockey).getName();
    }
    public String getJockeyString()
    {
        String strJockey = m_strJockeyName;
        if (m_nJockeyClaim > 0)
            strJockey += ("(" + m_nJockeyClaim + ")");

        return strJockey;
    }
    public String getTrainerName()
    {
        return m_strTrainerName;
        //return SmartformEnvironment.getInstance().getTrainer(m_nTrainer).getName();
    }
    public static String getWeightString(int nWeightPounds)
    {
        return getWeightString(nWeightPounds, 0);
    }
    public static String getWeightString(int nWeightPounds, int nLongHandicap)
    {
        int nStones = nWeightPounds/14;
        int nPounds = nWeightPounds - (14 * nStones);
        String strWeight = nStones +"-" + nPounds;

        if (nLongHandicap > 0)
        {
            int nOutHandicap = nWeightPounds - nLongHandicap;
            strWeight += ("(" + nOutHandicap + "oh)");
        }

        return strWeight;
    }
    public String getBreedingString()
    {
        if (m_horse == null)
            return "";
        else
        {
            return m_horse.getBreedingString();
        }
    }
    public String getShortSexString()
    {
        if (m_horse == null)
            return "";
        else
        {
            return m_horse.getShortSexString();
        }
    }
    public String getSexString()
    {
        if (m_horse == null)
            return "";
        else
        {
            return m_horse.getSexString();
        }
    }

     public String getFoalingDateString()
    {
        if (m_horse == null)
            return "";
        else
        {
            if (m_horse.getFoalingDate() != null)
                return SmartformEnvironment.getInstance().getShortDateFormat().format(m_horse.getFoalingDate());
        }
        
        return "";
    }

   public String getAgeString()
    {
        String strAge = String.valueOf(m_nAge) + "-y-o";
 
        return strAge;
    }

   public String getFullAgeString()
    {
        String strAge = String.valueOf(m_nAge) + "-y-o";
        if (m_horse != null)
            strAge += (" (foaled " + SmartformEnvironment.getInstance().getShortDateFormat().format(m_horse.getFoalingDate()) + ")");

        return strAge;
    }
    public String getTackString()
    {
        String strTack = "";
        if (m_tack != null)
            strTack = m_tack.getTackString();

        return strTack;
    }
    public String getShortTackString()
    {
        String strTack = "";
        if (m_tack != null)
            strTack = m_tack.getShortTackString();

        return strTack;
    }
   public void setOwnerId(int nOwner) {
        this.m_nOwner = nOwner;
    }

   public int getOwnerId() {
        return m_nOwner;
    }
    public void setJockeyName(String strJockeyName)
    {
        m_strJockeyName = strJockeyName;
    }
    public void setTrainerName(String strTrainerName)
    {
        m_strTrainerName = strTrainerName;
    }
    public void setJockeyClaim(int nClaim)
    {
        m_nJockeyClaim = nClaim;
    }
    public int getJockeyClaim()
    {
        return m_nJockeyClaim;
    }
    
    public void setUnregisteredColourSyntax(UnregisteredColourSyntax ucs)
    {
        m_ucs = ucs;
    }
    public UnregisteredColourSyntax getUnregisteredColourSyntax()
    {
        return m_ucs;
    }
    public String getPreviousRace()
    {
        return m_strPreviousRace;
    }
    public void setPreviousRace(String strPreviousRace)
    {
        m_strPreviousRace = strPreviousRace;
    }
}
