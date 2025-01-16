package ene.eneform.service.colours.database;

import ene.eneform.service.mero.colours.ENERacingColours;
import ene.eneform.service.smartform.bos.SmartformColoursRunner;
import ene.eneform.service.smartform.bos.UnregisteredColourSyntax;
import ene.eneform.service.smartform.factory.SmartformRunnerFactory;
import ene.eneform.service.utils.ENEStatement;

public class ENERacingColoursFactory
{

     
    public static ENERacingColours createRunnerColours(String strLanguage, SmartformColoursRunner runner)
    {
        return createRunnerColours(strLanguage, runner, SmartformRunnerFactory.sm_RCPVersion);
    }
    public static ENERacingColours createRunnerColours(String strLanguage, SmartformColoursRunner runner, String strVersion)
    {
        ENERacingColours colours = null;
        if (runner != null)
        {
            colours = createRacingColours(null, runner.getUnregisteredColourSyntax(), strLanguage, runner.getJockeyColours(), strVersion);
         }   
        
        return colours;
    }
    public static ENERacingColours createColours(ENEStatement statement, String strLanguage, String strJockeyColours)
    {
        return createColours(statement, strLanguage, strJockeyColours, SmartformRunnerFactory.sm_RCPVersion);
    }
    public static ENERacingColours createColours(ENEStatement statement, String strLanguage, String strJockeyColours, String strVersion)
    {
        return createRacingColours(statement, WikipediaFactory.createUnregisteredColourSyntax(statement, strJockeyColours), strLanguage, strJockeyColours, strVersion);
    }

    public static ENERacingColours createRacingColours(ENEStatement statement, UnregisteredColourSyntax ucs, String strLanguage, String strJockeyColours, String strVersion)
    {
        ENERacingColours colours = null;
        // now use MeroService
         
        return colours;
       
    }
private static int insertJockeyColoursParse(ENEStatement statement, String strVersion, ENERacingColours colours, String strUnresolved, String strExpanded, String strSyntax)
{
    int nReturn = 0;

    String strJacket = colours.getJacket().toString();
    String strSleeves = colours.getSleeves().toString();
    String strCap = colours.getCap().toString();
    String strDescription = colours.getDescription();

    String strInsert = "replace into racing_colours_parse (rcp_description, rcp_expanded, rcp_version, rcp_jacket, rcp_sleeves, rcp_cap, rcp_unresolved, rcp_syntax) values (\"";
    strInsert += (strDescription + "\",\"");
    strInsert += (strExpanded + "\",\"");
    strInsert += (strVersion + "\",\"");
    strInsert += (strJacket + "\",\"");
    strInsert += (strSleeves + "\",\"");
    strInsert += (strCap + "\",\"");
    strInsert += (strUnresolved + "\",\"");
    strInsert += (strSyntax + "\")");

    nReturn  = statement.executeUpdate(strInsert);
 
    return nReturn;
  }
}
