/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.smartform.bos;

/**
 *
 * @author Simon
 */
public interface SmartformColoursRunner {
    
    public  UnregisteredColourSyntax getUnregisteredColourSyntax();
    public  String getJockeyColours();
    public  String getOwnerName();
    public  String getPrimaryOwner();
    public  String getName();
    public  String getJockeyName();
    public  String getTrainerName();
    public  String getInRaceComment();
    public  String getFullStartingPrice();
    public  String getStartingPrice();
    public  int getFinishPosition();
    public  String getFinishPositionString();
    public String getShortFinishPositionString();
    public String getDistanceBeatenString();
    public String getShortDistanceBeatenString();
    public double getDistanceBehindWinner();
    public String getDistanceBehindWinnerString();
    public int getOfficialRating();
    public int getWeightPounds();
    public int getStall();
    public int getClothNumber();
    public int getDaysSinceRan();
    public int getDistanceTravelled();
    public SmartformTack getTack();
    public int getPositionInBetting();
    public double getStartingPriceDecimal();
    public double getDistanceWon();
    public int getAge();
    public int getRunnerId();   // not supported by AdditionalRUnner, returns 0 for all
    public boolean isNonRunner();   // always flse for AdditionalRunner
    public int getRaceId();
}
