/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.smartform.bos;

/**
 *
 * @author Simon
 */
public class SmartformDailyRunner extends SmartformRunner implements JockeyColoursBO, SmartformColoursRunner {

    // adjusted_rating, jockey_colours, days_since_ran_type, course_winner, distance_winner, candd_winner, beaten_favourite, weight_penalty
    private int m_nAdjustedRating=0;
    private int m_nCourseWinnerCount=0;
    private int m_nDistanceWinnerCount=0;
    private int m_nCourseDistanceWinnerCount=0;
    private int m_nBeatenFavouriteCount=0;

    private String m_strJockeyColours="";   // desription
    private String m_strPrimaryOwner="";    // primary owner based on jockey_colours
    
    public SmartformDailyRunner(int nRace, int nRunner)
    {
        super(nRace, nRunner);
    }

    public int getAdjustedRating() {
        return m_nAdjustedRating + m_nPenaltyWeight;
    }

    public void setAdjustedRating(int nAdjustedRating) {
        this.m_nAdjustedRating = nAdjustedRating;
    }

    public int getBeatenFavouriteCount() {
        return m_nBeatenFavouriteCount;
    }

    public void setBeatenFavouriteCount(int nBeatenFavouriteCount) {
        this.m_nBeatenFavouriteCount = nBeatenFavouriteCount;
    }

    public int getCourseDistanceWinnerCount() {
        return m_nCourseDistanceWinnerCount;
    }

    public void setCourseDistanceWinnerCount(int nCourseDistanceWinnerCount) {
        this.m_nCourseDistanceWinnerCount = nCourseDistanceWinnerCount;
    }

    public int getCourseWinnerCount() {
        return m_nCourseWinnerCount;
    }

    public void setCourseWinnerCount(int nCourseWinnerCount) {
        this.m_nCourseWinnerCount = nCourseWinnerCount;
    }

    public int getDistanceWinnerCount() {
        return m_nDistanceWinnerCount;
    }

    public void setDistanceWinnerCount(int nDistanceWinnerCount) {
        this.m_nDistanceWinnerCount = nDistanceWinnerCount;
    }

    public String getJockeyColours() {
        return m_strJockeyColours;
    }

    public void setJockeyColours(String strJockeyColours) {
        this.m_strJockeyColours = strJockeyColours;
    }

   public String getPrimaryOwner() {
        return m_strPrimaryOwner;
    }

    public void setPrimaryOwner(String strPrimaryOwner) {
        this.m_strPrimaryOwner = strPrimaryOwner;
    }
    public String getCourseDistanceString()
    {
        String strCourseDistance = "";

        if (m_nCourseWinnerCount > 0)
            strCourseDistance += ("C-" + m_nCourseWinnerCount + " ");
        if (m_nDistanceWinnerCount > 0)
            strCourseDistance += ("D-" + m_nDistanceWinnerCount + " ");
        if (m_nCourseDistanceWinnerCount > 0)
            strCourseDistance += ("CD-" + m_nCourseDistanceWinnerCount + " ");
        if (m_nBeatenFavouriteCount > 0)
            strCourseDistance += ("BF-" + m_nBeatenFavouriteCount + " ");

        return strCourseDistance.trim();
    }
    // ENColoursRunner interface
    public String getStartingPrice()
    {
        return "";  // unknown
    }
    public String getInRaceComment()
    {
        return "";  // unknown
    }
    public int getFinishPosition()
    {
        return 0;
    }
    public String getFinishPositionString()
    {
        return "";
    }
    public String getShortFinishPositionString()
    {
        return "";
    }
    public String getDistanceBeatenString()
    {
        return "";
    }
    public double getDistanceBehindWinner()
    {
        return 0.0;
    }
    public String getDistanceBehindWinnerString()
    {
        // to do: implement
        return "";
    }
    public String getShortDistanceBeatenString()
    {
        // to do: implement
        return "";
    }
    public String getFullStartingPrice()
    {
        // to do: implement
        return "";
    }
   public int getDistanceTravelled()
    {
        return 0;
    }
    public double getDistanceWon(){return 0;}
    public double getStartingPriceDecimal(){return 0.0;}
    public int getPositionInBetting(){return 0;}
}
