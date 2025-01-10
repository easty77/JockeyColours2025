/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.smartform.factory;

import ene.eneform.smartform.bos.*;
import ene.eneform.smartform.bos.day.SmartformDay;
import ene.eneform.smartform.bos.day.SmartformJockeyDay;
import ene.eneform.smartform.bos.day.SmartformOwnerDay;
import ene.eneform.smartform.bos.day.SmartformTrainerDay;
import ene.eneform.smartform.bos.meeting.SmartformJockeyMeeting;
import ene.eneform.smartform.bos.meeting.SmartformMeeting;
import ene.eneform.smartform.bos.meeting.SmartformOwnerMeeting;
import ene.eneform.smartform.bos.meeting.SmartformTrainerMeeting;
import ene.eneform.utils.DbUtils;
import ene.eneform.utils.ENEStatement;
import ene.eneform.utils.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author Simon
 */
public class SmartformRunnerFactory {


    private static String sm_strDailyRunnerAllFields = "runner_id, race_id, name, foaling_date, age, colour, form_figures, form_type, gender, bred, cloth_number, stall_number, long_handicap, official_rating, adjusted_rating, trainer_name, trainer_id, owner_name, jockey_name, jockey_id, jockey_claim, jockey_colours, dam_name, dam_year_born, sire_name, sire_year_born, dam_sire_name, dam_sire_year_born, forecast_price, forecast_price_decimal, days_since_ran, days_since_ran_type, weight_pounds, weight_penalty, tack_hood, tack_visor, tack_blinkers, tack_eye_shield, tack_eye_cover, tack_cheek_piece, tack_pacifiers, tack_tongue_strap, course_winner, distance_winner, candd_winner, beaten_favourite";
    private static String sm_strHistoricRunnerAllFields = "runner_id, race_id, name, foaling_date, colour, distance_travelled, form_figures, gender, age, bred, cloth_number, stall_number, num_fences_jumped, long_handicap, how_easy_won, in_race_comment, official_rating, official_rating_type, speed_rating, speed_rating_type, private_handicap, private_handicap_type, trainer_name, trainer_id, owner_name, owner_id, jockey_name, jockey_id, jockey_claim, dam_name, dam_id, sire_name, sire_id, dam_sire_name, dam_sire_id, forecast_price, forecast_price_decimal, starting_price, starting_price_decimal, betting_text, position_in_betting, finish_position, amended_position, unfinished, distance_beaten, distance_won, distance_behind_winner, prize_money, tote_win, tote_place, days_since_ran, last_race_type_id, last_race_type, last_race_beaten_fav, weight_pounds, penalty_weight, over_weight, tack_hood, tack_visor, tack_blinkers, tack_eye_shield, tack_eye_cover, tack_cheek_piece, tack_pacifiers, tack_tongue_strap";

    private static String sm_strDailyRunnerFields = "adjusted_rating, jockey_colours, days_since_ran_type, course_winner, distance_winner, candd_winner, beaten_favourite, weight_penalty, dam_year_born, sire_year_born, dam_sire_year_born";
    private static String sm_strHistoricRunnerFields = "h.distance_travelled, h.num_fences_jumped, h.how_easy_won, h.in_race_comment, h.official_rating_type, h.speed_rating, h.speed_rating_type, h.private_handicap, h.private_handicap_type, h.starting_price, h.starting_price_decimal, h.betting_text, h.position_in_betting, h.finish_position, h.amended_position, h.unfinished, h.distance_beaten, h.distance_won, h.distance_behind_winner, h.prize_money, h.tote_win, h.tote_place, h.last_race_type_id, h.last_race_type, h.last_race_beaten_fav, h.penalty_weight, h.over_weight";

    // 20121207 historic must be best jockey name - what about trainer/owner?
    private static String sm_strBestRunnerFields = "coalesce(u.form_figures, h.form_figures) as form_figures, coalesce(u.age, h.age) as age, coalesce(u.cloth_number, h.cloth_number) as cloth_number, coalesce(u.stall_number, h.stall_number) as stall_number, coalesce(u.trainer_name, h.trainer_name) as trainer_name,  coalesce(u.trainer_id, h.trainer_id) as trainer_id, coalesce(u.owner_name, h.owner_name) as owner_name, coalesce(h.jockey_name, u.jockey_name) as jockey_name, coalesce(u.jockey_id, h.jockey_id) as jockey_id, coalesce(u.jockey_claim, h.jockey_claim) as jockey_claim, coalesce(u.long_handicap, h.long_handicap) as long_handicap, coalesce(u.days_since_ran, h.days_since_ran) as days_since_ran, coalesce(u.weight_pounds, h.weight_pounds) as weight_pounds, coalesce(u.forecast_price, h.forecast_price) as forecast_price,  coalesce(u.forecast_price_decimal, h.forecast_price_decimal) as forecast_price_decimal, coalesce(u.official_rating, h.official_rating) as official_rating";
    private static String sm_strBestHorseFields = "coalesce(u.foaling_date, h.foaling_date) as foaling_date, coalesce(u.colour, h.colour) as colour, coalesce(u.gender, h.gender) as gender, coalesce(u.bred, h.bred) as bred, coalesce(u.dam_name, h.dam_name) as dam_name, coalesce(u.sire_name, h.sire_name) as sire_name, coalesce(u.dam_sire_name, h.dam_sire_name) as dam_sire_name";
    private static String sm_strBestTackFields = "coalesce(u.tack_hood, h.tack_hood) as tack_hood, coalesce(u.tack_visor, h.tack_visor) as tack_visor, coalesce(u.tack_blinkers, h.tack_blinkers) as tack_blinkers, coalesce(u.tack_eye_shield, h.tack_eye_shield) as tack_eye_shield, coalesce(u.tack_eye_cover, h.tack_eye_cover) as tack_eye_cover, coalesce(u.tack_cheek_piece, h.tack_cheek_piece) as tack_cheek_piece, coalesce(u.tack_pacifiers, h.tack_pacifiers) as tack_pacifiers, coalesce(u.tack_tongue_strap, h.tack_tongue_strap) as tack_tongue_strap";

    private static String sm_strRunnerFields = "u.form_figures, u.age, u.cloth_number, u.stall_number, u.trainer_name, u.trainer_id, u.owner_name, u.jockey_name, u.jockey_id, u.jockey_claim, u.long_handicap, u.days_since_ran, u.weight_pounds, u.forecast_price, u.forecast_price_decimal, u.official_rating";
    private static String sm_strHorseFields = "u.foaling_date, u.colour, u.gender, u.bred, u.dam_name, u.sire_name, u.dam_sire_name";
    private static String sm_strTackFields = "u.tack_hood, u.tack_visor, u.tack_blinkers, u.tack_eye_shield, u.tack_eye_cover, u.tack_cheek_piece, u.tack_pacifiers, u.tack_tongue_strap";
    public static final String sm_RCPVersion = "Summer2015";

public static ArrayList<SmartformHistoricRunner> createMeetingWinners(ENEStatement stmt, int nMeeting)
{
    ArrayList<SmartformHistoricRunner> alRunners = new ArrayList<SmartformHistoricRunner>();

    String strQuery = "select h.race_id, h.runner_id, h.name, u.runner_id as daily_runner_id, coalesce(h.owner_id, o.owner_id) as owner_id, " + sm_strBestRunnerFields +"," + sm_strDailyRunnerFields + "," + sm_strBestHorseFields + "," + sm_strBestTackFields + "," + sm_strHistoricRunnerFields + " from (((((historic_races ha left outer join daily_races a on ha.race_id=a.race_id) inner join historic_runners h on ha.race_id=h.race_id and (finish_position=1 or amended_position=1)) left outer join runner_id_diff on h.runner_id=historic_runner_id) left outer join daily_runners u on ha.race_id=u.race_id and coalesce(daily_runner_id,h.runner_id)=u.runner_id) left outer join unique_owner o on u.owner_name=o.owner_name) where ha.meeting_id=" + nMeeting + " order by ha.meeting_date, ha.scheduled_time";

    ResultSet rs = stmt.executeQuery(strQuery);
    if (rs != null)
    {
        try
        {
    
            while (rs.next())
            {
                 SmartformHistoricRunner runner = createSmartformHistoricRunnerObject(rs);
                 runner = (SmartformHistoricRunner) updateSmartformRunnerObject(rs, runner);
                 SmartformHorse horse = createSmartformHorseObject(rs);
                 runner.setHorse(horse);
                 SmartformTack tack = createSmartformTackObject(rs);
                 runner.setTack(tack);
                 alRunners.add(runner);
            }
            rs.close();
        }
        catch(SQLException e)
        {
            
        }
    }
    return alRunners;
}
public static ArrayList<SmartformHistoricRunner> createRaceRunners(ENEStatement stmt, int nRace, boolean bFinished)
{
    // TO DO: unregistered_colour_syntax match can return 2 records if country = Eire and match on Eire and UK - removed for now - should check that NO exact match before looking for aggregated match
    ArrayList<SmartformHistoricRunner> alRunners = new ArrayList<SmartformHistoricRunner>();
    String strQuery = "select h.race_id, h.runner_id, h.name, u.runner_id as daily_runner_id, " + sm_strBestRunnerFields +"," + sm_strDailyRunnerFields + "," + sm_strBestHorseFields + "," + sm_strBestTackFields + "," + sm_strHistoricRunnerFields;
    strQuery += ", coalesce(wi2.wi_jacket, ucs_jacket) as ucs_jacket, coalesce(wi2.wi_sleeves, ucs_sleeves) as ucs_sleeves, coalesce(wi2.wi_cap, ucs_cap) as ucs_cap, coalesce(wi1.wi_owner, wi2.wi_owner, '') as spc_primary_owner";
    strQuery += ", 0 as owner_id, '' as previous_race_name";      // non-essential fields with default values
    strQuery += " from (((( ( (( historic_races ha left outer join daily_races a on ha.race_id=a.race_id)";
    strQuery += " inner join historic_runners h on ha.race_id=h.race_id";
    if (bFinished)
        strQuery +=  " and (h.unfinished is null or h.amended_position is not null))";
    else
        strQuery +=  " and coalesce(h.unfinished, '') != 'Non-Runner')";
    strQuery += " left outer join daily_runners u on ha.race_id=u.race_id and h.name=u.name)";
    strQuery += " left outer join unregistered_colour_syntax on replace(u.jockey_colours, ' & ', ' and ')=ucs_colours and (country=ucs_organisation or (country in ('England', 'Wales', 'Scotland', 'Northern Ireland', 'EireXX') and ucs_organisation = 'UK')) and (year(a.meeting_date) = ucs_year or ucs_year = 0))";
    strQuery += " left outer join racing_colours_parse rcp on rcp_description=u.jockey_colours and rcp_version='" + sm_RCPVersion + "')";
    strQuery += " left outer join wikipedia_images wi1 on coalesce(ucs_jacket,rcp_jacket)=wi1.wi_jacket and coalesce(ucs_sleeves,rcp_sleeves)=wi1.wi_sleeves and coalesce(ucs_cap,rcp_cap)=wi1.wi_cap and coalesce(rcp_unresolved, '') = '') ";
    strQuery += " left outer join wikipedia_images wi2 on replace(u.jockey_colours, ' & ', ' and ')=wi2.wi_description)";
    strQuery += (" where ha.race_id=" + nRace);
    strQuery += " order by coalesce(h.amended_position, h.finish_position, 99), h.num_fences_jumped desc";
    
    ResultSet rs = stmt.executeQuery(strQuery);
    if (rs != null)
    {
       SmartformHistoricRunner runner = null;
       try 
       {
    
            while (rs.next())
            {
                 runner = createSmartformHistoricRunnerObject(rs);
                 runner = (SmartformHistoricRunner) updateSmartformRunnerObject(rs, runner);
                 runner = (SmartformHistoricRunner) updateSmartformDailyRunnerObject(rs, runner);
                 runner.setUnregisteredColourSyntax(createUnregisteredColourSyntax(rs));
                 SmartformHorse horse = createSmartformHorseObject(rs);
                 runner.setHorse(horse);
                 SmartformTack tack = createSmartformTackObject(rs);
                 runner.setTack(tack);
                 alRunners.add(runner);

            }
            rs.close();
        }
        catch(SQLException e)
        {
           System.out.println("createRaceRunners exception: " + runner.getName() + "-" + runner.getRaceId() + "-" + e.getMessage());
            e.printStackTrace();
        }
    }
    return alRunners;
}
public static UnregisteredColourSyntax createUnregisteredColourSyntax(ResultSet rs) throws SQLException
{
    String strJacket = DbUtils.getDBString(rs, "ucs_jacket");
    if ((strJacket != null) && !"".equals(strJacket))
    {
          return new UnregisteredColourSyntax(strJacket, DbUtils.getDBString(rs, "ucs_sleeves"), DbUtils.getDBString(rs, "ucs_cap"));    
    }
    
    return null;
}
public static ArrayList<SmartformHistoricRunner> getRacesWinnerList(ENEStatement stmt, ArrayList<SmartformRace> alRaces)
 {
    ArrayList<SmartformHistoricRunner> alRunners = new ArrayList<SmartformHistoricRunner>();
    String strRaceList = "";
    for (int i = 0; i < alRaces.size(); i++)
    {
        if (!"".equals(strRaceList))
            strRaceList += ", ";
        strRaceList+= alRaces.get(i).getRaceId();
    }
    String strQuery = "select h.race_id, h.runner_id, h.name, u.runner_id as daily_runner_id, coalesce(h.owner_id, o.owner_id) as owner_id, " + sm_strBestRunnerFields +"," + sm_strDailyRunnerFields + "," + sm_strBestHorseFields + "," + sm_strBestTackFields + "," + sm_strHistoricRunnerFields + " from (((((historic_races ha left outer join daily_races a on ha.race_id=a.race_id) inner join historic_runners h on ha.race_id=h.race_id and (finish_position=1 or amended_position=1)) left outer join runner_id_diff on h.runner_id=historic_runner_id) left outer join daily_runners u on ha.race_id=u.race_id and coalesce(daily_runner_id,h.runner_id)=u.runner_id) left outer join unique_owner o on u.owner_name=o.owner_name) where ha.race_id in (" + strRaceList + ") order by ha.meeting_date";

    ResultSet rs = stmt.executeQuery(strQuery);
    if (rs != null)
    {
        try
        {
            while (rs.next())
            {
                 SmartformHistoricRunner runner = createSmartformHistoricRunnerObject(rs);
                 runner = (SmartformHistoricRunner) updateSmartformRunnerObject(rs, runner);
                 SmartformHorse horse = createSmartformHorseObject(rs);
                 runner.setHorse(horse);
                 SmartformTack tack = createSmartformTackObject(rs);
                 runner.setTack(tack);
                 alRunners.add(runner);
            }
            rs.close();
        }
        catch(SQLException e)
        {
            System.out.println("getRacesWinnerList exception: " + e.getMessage());
        }
        
    }

    return alRunners;
}
public static SmartformHistoricRunner getRaceWinner(ENEStatement stmt, int nRace)
 {
    SmartformHistoricRunner runner = null;
    String strQuery = "select h.race_id, h.runner_id, h.name, u.runner_id as daily_runner_id, coalesce(h.owner_id, o.owner_id) as owner_id, " + sm_strBestRunnerFields +"," + sm_strDailyRunnerFields + "," + sm_strBestHorseFields + "," + sm_strBestTackFields + "," + sm_strHistoricRunnerFields + " from (((((historic_races ha left outer join daily_races a on ha.race_id=a.race_id) inner join historic_runners h on ha.race_id=h.race_id and (finish_position=1 or amended_position=1)) left outer join runner_id_diff on h.runner_id=historic_runner_id) left outer join daily_runners u on ha.race_id=u.race_id and coalesce(daily_runner_id,h.runner_id)=u.runner_id) left outer join unique_owner o on u.owner_name=o.owner_name) where ha.race_id = " + nRace + " order by ha.meeting_date";

    ResultSet rs = stmt.executeQuery(strQuery);
    if (rs != null)
    {
        try
        {
    
            while (rs.next())   // 1 record
            {
                 runner = createSmartformHistoricRunnerObject(rs);
                 runner = (SmartformHistoricRunner) updateSmartformRunnerObject(rs, runner);
                 SmartformHorse horse = createSmartformHorseObject(rs);
                 runner.setHorse(horse);
                 SmartformTack tack = createSmartformTackObject(rs);
                 runner.setTack(tack);
            }
            rs.close();
        }
        catch(SQLException e)
        {
            
        }
    }

    return runner;
}
public static List<Pair<String,String>> getRunnerNames(ENEStatement stmt, String strWhere)
 {
     ArrayList<Pair<String,String>> astrNames = new ArrayList<Pair<String,String>>();
   SmartformHistoricRunner runner = null;
   // use daily_races so can filter by country
    String strQuery = "select name, jockey_colours from (select distinct h.name, jockey_colours, meeting_date from historic_runners h inner join daily_races a using (race_id) inner join daily_runners d using (race_id, runner_id) where coalesce(h.unfinished, '') != 'Non-Runner' and jockey_colours != '' and jockey_colours != 'Not Available'" + strWhere + "";
    strQuery += "union select distinct name, jockey_colours, meeting_date from additional_runners h inner join additional_races a using (race_id) where jockey_colours != '' and jockey_colours != 'Not Available'" + strWhere + ") d1 group by name order by name, meeting_date desc";

    ResultSet rs = stmt.executeQuery(strQuery);
    if (rs != null)
    {
        try
        {
    
            while (rs.next())   // 1 record
            {
                 astrNames.add(new Pair<String,String>(rs.getString(1), rs.getString(2)));
            }
            rs.close();
        }
        catch(SQLException e)
        {
            
        }
    }

    return astrNames;
     
 }
public static SmartformHistoricRunner getRaceRunner(ENEStatement stmt, int nRace, String strHorse)
 {
    SmartformHistoricRunner runner = null;
    String strQuery = "select h.race_id, h.runner_id, h.name, u.runner_id as daily_runner_id, coalesce(h.owner_id, o.owner_id) as owner_id, " + sm_strBestRunnerFields +"," + sm_strDailyRunnerFields + "," + sm_strBestHorseFields + "," + sm_strBestTackFields + "," + sm_strHistoricRunnerFields + " from (((((historic_races ha left outer join daily_races a on ha.race_id=a.race_id) inner join historic_runners h on ha.race_id=h.race_id) left outer join runner_id_diff on h.runner_id=historic_runner_id) left outer join daily_runners u on ha.race_id=u.race_id and coalesce(daily_runner_id,h.runner_id)=u.runner_id) left outer join unique_owner o on u.owner_name=o.owner_name) where ha.race_id = " + nRace + " and h.name='" + strHorse.replace("'", "''") + "' order by ha.meeting_date";

    ResultSet rs = stmt.executeQuery(strQuery);
    if (rs != null)
    {
        try
        {
    
            while (rs.next())   // 1 record
            {
                 runner = createSmartformHistoricRunnerObject(rs);
                 runner = (SmartformHistoricRunner) updateSmartformRunnerObject(rs, runner);
                 SmartformHorse horse = createSmartformHorseObject(rs);
                 runner.setHorse(horse);
                 SmartformTack tack = createSmartformTackObject(rs);
                 runner.setTack(tack);
            }
            rs.close();
        }
        catch(SQLException e)
        {
            
        }
    }

    return runner;
}
public static ArrayList<SmartformHistoricRunner> getRacesFirstNList(ENEStatement stmt, ArrayList<SmartformRace> alRaces, int nPlaces)
 {
    ArrayList<SmartformHistoricRunner> alRunners = new ArrayList<SmartformHistoricRunner>();
    String strRaceList = "";
    for (int i = 0; i < alRaces.size(); i++)
    {
        if (!"".equals(strRaceList))
            strRaceList += ", ";
        strRaceList+= alRaces.get(i).getRaceId();
    }
    String strQuery = "select h.race_id, h.runner_id, h.name, u.runner_id as daily_runner_id, coalesce(h.owner_id, o.owner_id) as owner_id, " + sm_strBestRunnerFields +"," + sm_strDailyRunnerFields + "," + sm_strBestHorseFields + "," + sm_strBestTackFields + "," + sm_strHistoricRunnerFields + " from (((((historic_races ha left outer join daily_races a on ha.race_id=a.race_id) inner join historic_runners h on ha.race_id=h.race_id and (finish_position <= " + nPlaces + " or amended_position <= " + nPlaces + ")) left outer join runner_id_diff on h.runner_id=historic_runner_id) left outer join daily_runners u on ha.race_id=u.race_id and coalesce(daily_runner_id,h.runner_id)=u.runner_id) left outer join unique_owner o on u.owner_name=o.owner_name) where ha.race_id in (" + strRaceList + ") order by ha.meeting_date, ha.scheduled_time, coalesce(amended_position, finish_position)";

    ResultSet rs = stmt.executeQuery(strQuery);
    if (rs != null)
    {
        try
        {
    
            while (rs.next())
            {
                 SmartformHistoricRunner runner = createSmartformHistoricRunnerObject(rs);
                 runner = (SmartformHistoricRunner) updateSmartformRunnerObject(rs, runner);
                 SmartformHorse horse = createSmartformHorseObject(rs);
                 runner.setHorse(horse);
                 SmartformTack tack = createSmartformTackObject(rs);
                 runner.setTack(tack);
                 alRunners.add(runner);
            }
            rs.close();
       }
        catch(SQLException e)
        {
            
        }
    }
    return alRunners;
}
public static SmartformDay createSmartformDayRunners(ENEStatement statement, SmartformDay day)
{
    Date dtDaily = day.getDate();

    int nCount = 0;
    String strDate = SmartformEnvironment.getInstance().getSQLDateFormat().format(dtDaily);
    String strQuery = "select d.meeting_id, d.meeting_date, d.race_id, coalesce(u.runner_id, h.runner_id) as runner_id, coalesce(u.name, h.name) as name, u.runner_id as daily_runner_id, h.runner_id as historic_runner_id, ha.race_id as historic_race_id, coalesce(h.owner_id, o.owner_id) as owner_id, " + sm_strBestRunnerFields +"," + sm_strDailyRunnerFields + "," + sm_strBestHorseFields + "," + sm_strBestTackFields + "," + sm_strHistoricRunnerFields + " from ((((daily_races d inner join daily_runners u on d.race_id=u.race_id) left outer join historic_races ha on d.race_id=ha.race_id) left outer join historic_runners h on d.race_id=h.race_id and u.runner_id=h.runner_id) left outer join unique_owner o on u.owner_name=o.owner_name) where d.meeting_date='" + strDate + "' order by d.meeting_id, u.race_id, u.cloth_number, h.race_id, h.cloth_number";

    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        int nPreviousRace = 0;
        int nPreviousMeeting = 0;
        SmartformRace race = null;
        SmartformMeeting meeting = null;
        if (rs != null)
        {
            while (rs.next())
            {
                int nMeeting = rs.getInt("meeting_id");

                int nHistoricRace = rs.getInt("historic_race_id");
                boolean bHistoricRaceCheck = !rs.wasNull();
                int nHistoricRunner = rs.getInt("historic_runner_id");
                boolean bHistoricRunnerCheck = !rs.wasNull();

                SmartformRunner runner = null;
                if (bHistoricRunnerCheck)
                {
                    runner = (SmartformRunner) createSmartformHistoricRunnerObject(rs);
                }
                else if (bHistoricRaceCheck)
                {
                    runner = (SmartformRunner) createSmartformNonRunnerObject(rs);
                }
                else
                {
                     runner = (SmartformRunner) createSmartformDailyRunnerObject(rs);
                }

                int nRace = runner.getRaceId();

                if (nMeeting != nPreviousMeeting)
                {
                    meeting = day.getMeeting(nMeeting);
                    nPreviousMeeting = nMeeting;
                }
                if (nRace != nPreviousRace)
                {
                    race = meeting.getRace(nRace);
                    nPreviousRace = nRace;
                }
                if (race != null)
                {
                    race.addRunner((SmartformColoursRunner)runner);
                }
                else
                {
                    System.out.println("Null race for: " + nRace);
                }
                SmartformHorse horse = createSmartformHorseObject(rs);
                runner.setHorse(horse);
                SmartformTack tack = createSmartformTackObject(rs);
                runner.setTack(tack);

                createEnvironmentObjects(rs, runner);
                createDayEnvironmentObjects(rs, day, runner, dtDaily);
                createMeetingEnvironmentObjects(rs, meeting, runner);
            }
            rs.close();
        }
    }
    catch(SQLException e)
    {
    }

    return day;
}

    public static void createDayEnvironmentObjects(ResultSet rs, SmartformDay day, SmartformRunner runner, Date dtDaily) throws SQLException
    {
            int nTrainer = runner.getTrainerId();
            SmartformTrainerDay dayTrainer = day.getTrainerDay(nTrainer);
            if (dayTrainer == null)
            {
                dayTrainer = createSmartformTrainerDayObject(rs, dtDaily);
                day.addTrainerDay(dayTrainer);
            }
            dayTrainer.addRunner((SmartformColoursRunner)runner);

            int nJockey = runner.getJockeyId();
            SmartformJockeyDay dayJockey = day.getJockeyDay(nJockey);
            if (dayJockey == null)
            {
                dayJockey = createSmartformJockeyDayObject(rs, dtDaily);
                day.addJockeyDay(dayJockey);
            }
            dayJockey.addRunner((SmartformColoursRunner)runner);
          int nOwner = runner.getOwnerId();
          if (nOwner != 0)
          {
                SmartformOwnerDay dayOwner = day.getOwnerDay(nOwner);
                if (dayOwner == null)
                {
                    dayOwner = createSmartformOwnerDayObject(rs, dtDaily);
                    day.addOwnerDay(dayOwner);
                }
                dayOwner.addRunner((SmartformColoursRunner)runner);
           }
    }
    public static void createMeetingEnvironmentObjects(ResultSet rs, SmartformMeeting  meeting, SmartformRunner runner) throws SQLException
    {
            int nTrainer = runner.getTrainerId();
             SmartformTrainerMeeting mtgTrainer = meeting.getTrainerMeeting(nTrainer);
            if (mtgTrainer == null)
            {
                mtgTrainer = createSmartformTrainerMeetingObject(rs);
                meeting.addTrainerMeeting(mtgTrainer);
            }
            mtgTrainer.addRunner((SmartformColoursRunner)runner);

            int nJockey = runner.getJockeyId();
            SmartformJockeyMeeting mtgJockey = meeting.getJockeyMeeting(nJockey);
            if (mtgJockey == null)
            {
                mtgJockey = createSmartformJockeyMeetingObject(rs);
                meeting.addJockeyMeeting(mtgJockey);
            }
            mtgJockey.addRunner((SmartformColoursRunner)runner);

          int nOwner = runner.getOwnerId();
          if (nOwner != 0)
          {
               SmartformOwnerMeeting mtgOwner = meeting.getOwnerMeeting(nOwner);
                if (mtgOwner == null)
                {
                    mtgOwner = createSmartformOwnerMeetingObject(rs);
                    meeting.addOwnerMeeting(mtgOwner);
                }
                mtgOwner.addRunner((SmartformColoursRunner)runner);
           }
    }
    public static void createEnvironmentObjects(ResultSet rs, SmartformRunner runner) throws SQLException
    {
            // trainer
            int nTrainer = runner.getTrainerId();
            SmartformTrainer trainer = SmartformEnvironment.getInstance().getTrainer(nTrainer);
            if (trainer == null)
            {
                trainer = createSmartformTrainerObject(rs);
                SmartformEnvironment.getInstance().addTrainer(trainer);
            }
            trainer.addRunner((SmartformColoursRunner)runner);



            // jockey
            int nJockey = runner.getJockeyId();
            SmartformJockey jockey = SmartformEnvironment.getInstance().getJockey(nJockey);
            if (jockey == null)
            {
                jockey = createSmartformJockeyObject(rs);
                SmartformEnvironment.getInstance().addJockey(jockey);
            }
            jockey.addRunner((SmartformColoursRunner)runner);



            // owner
          int nOwner = runner.getOwnerId();
          if (nOwner != 0)
          {
                SmartformOwner owner = SmartformEnvironment.getInstance().getOwner(nOwner);
                if (owner == null)
                {
                    owner = createSmartformOwnerObject(rs);
                    SmartformEnvironment.getInstance().addOwner(owner);
                }
                owner.addRunner((SmartformColoursRunner)runner);

            }

    }
public static ArrayList<SmartformDailyRunner> createDailyRaceRunners(ENEStatement stmt, int nRace)
{
    return createDailyRaceRunners(stmt, nRace, false);
}
public static ArrayList<SmartformHistoricRunner> createDailyHistoricRunners(ENEStatement stmt, int nRace)
{
    String strQuery = "select u.race_id, u.race_id as daily_race_id, u.runner_id as runner_id, u.runner_id as daily_runner_id, -1 as owner_id, u.name, " + sm_strRunnerFields +"," + sm_strHistoricRunnerFields +"," + sm_strDailyRunnerFields + "," + sm_strHorseFields + "," + sm_strTackFields + ", coalesce(wi_jacket,ucs_jacket, '') as ucs_jacket, coalesce(wi_sleeves, ucs_sleeves) as ucs_sleeves, coalesce(wi_cap,ucs_cap) as ucs_cap, coalesce(wi_owner, spcs_primary_owner) as spc_primary_owner from ";
    strQuery += "((((((historic_runners h inner join daily_runners u on h.race_id=u.race_id and h.runner_id=u.runner_id)";
    strQuery += " inner join daily_races a on u.race_id = a.race_id)";
    strQuery += " left outer join unregistered_colour_syntax on jockey_colours=ucs_colours and (country=ucs_organisation or (country in ('England', 'Wales', 'Scotland', 'Northern Ireland', 'Eire') and ucs_organisation = 'UK')) and (year(a.meeting_date) = ucs_year or ucs_year = 0))";
    strQuery += " left outer join racing_colours_parse rcp on rcp_description=u.jockey_colours and rcp_version='" + sm_RCPVersion + "')";
    strQuery += " left outer join wikipedia_images wi on coalesce(ucs_jacket,rcp_jacket)=wi_jacket and coalesce(ucs_sleeves,rcp_sleeves)=wi_sleeves and coalesce(ucs_cap,rcp_cap)=wi_cap and coalesce(rcp_unresolved, '') = '') ";
    strQuery += " left outer join smartform_primary_colours_syntax spc on coalesce(ucs_jacket,rcp_jacket)=spcs_jacket and coalesce(ucs_sleeves,rcp_sleeves)=spcs_sleeves and coalesce(ucs_cap,rcp_cap)=spcs_cap and (country=spcs_organisation or (country in ('England', 'Wales', 'Scotland', 'Northern Ireland', 'Eire') and spcs_organisation = 'UK'))) ";
    strQuery += " where h.race_id = " + nRace + " and coalesce(h.unfinished, '') != 'Non-Runner'";
    strQuery += " order by u.stall_number";

    ArrayList<SmartformHistoricRunner> alRunners = new ArrayList<SmartformHistoricRunner>();
        ResultSet rs = stmt.executeQuery(strQuery);
        if (rs != null)
        {
            try
            {
                while (rs.next())
                {
                    SmartformHistoricRunner runner = createSmartformHistoricRunnerObject(rs);
                    SmartformHorse horse = createSmartformHorseObject(rs);
                    runner.setHorse(horse);
                    SmartformTack tack = createSmartformTackObject(rs);
                    runner.setTack(tack);
                    String strJacketSyntax = DbUtils.getDBString(rs, "ucs_jacket");
                    if (!"".equals(strJacketSyntax))
                        runner.setUnregisteredColourSyntax(new UnregisteredColourSyntax(strJacketSyntax, DbUtils.getDBString(rs, "ucs_sleeves"), DbUtils.getDBString(rs, "ucs_cap")));

                    createEnvironmentObjects(rs, runner);
                    alRunners.add(runner);
                }
                rs.close();
            }
            catch(SQLException e)
            {
                
            }
        }

    return alRunners;
}
public static ArrayList<SmartformDailyRunner> createDailyRaceRunners(ENEStatement stmt, int nRace, boolean bParseFlag)
{
    String strQuery = "select u.race_id, u.race_id as daily_race_id, u.runner_id as runner_id, u.runner_id as daily_runner_id, -1 as owner_id, u.name, " + sm_strRunnerFields + "," + sm_strDailyRunnerFields + ", " + sm_strHorseFields + "," + sm_strTackFields + ", ucs_jacket, ucs_sleeves, ucs_cap, coalesce(wi_owner, spcs_primary_owner) as spc_primary_owner from ";
    strQuery += "((((((daily_runners u inner join daily_races a on u.race_id = a.race_id) left outer join nonrunner n on u.race_id = n.race_id and u.runner_id = n.runner_id)";
    strQuery += " left outer join unregistered_colour_syntax on jockey_colours=ucs_colours and (country=ucs_organisation or (country in ('England', 'Wales', 'Scotland', 'Northern Ireland', 'Eire') and ucs_organisation = 'UK')) and (year(a.meeting_date) = ucs_year or ucs_year = 0))";
    strQuery += " left outer join racing_colours_parse rcp on rcp_description=u.jockey_colours and rcp_version='" + sm_RCPVersion + "')";
    strQuery += " left outer join wikipedia_images wi on coalesce(ucs_jacket,rcp_jacket)=wi_jacket and coalesce(ucs_sleeves,rcp_sleeves)=wi_sleeves and coalesce(ucs_cap,rcp_cap)=wi_cap and coalesce(rcp_unresolved, '') = '') ";
    strQuery += " left outer join smartform_primary_colours_syntax spc on coalesce(ucs_jacket,rcp_jacket)=spcs_jacket and coalesce(ucs_sleeves,rcp_sleeves)=spcs_sleeves and coalesce(ucs_cap,rcp_cap)=spcs_cap and (country=spcs_organisation or (country in ('England', 'Wales', 'Scotland', 'Northern Ireland', 'Eire') and spcs_organisation = 'UK'))) ";
    strQuery += " where u.race_id = " + nRace + " and n.runner_id is null and u.cloth_number <= 40";
    if (bParseFlag)
        strQuery += " and rcp_description is null";
    strQuery += " order by u.cloth_number";

    ArrayList<SmartformDailyRunner> alRunners = new ArrayList<SmartformDailyRunner>();
        ResultSet rs = stmt.executeQuery(strQuery);
        if (rs != null)
        {
            try
            {
                while (rs.next())
                {
                    SmartformDailyRunner runner = createSmartformDailyRunnerObject(rs);
                    SmartformHorse horse = createSmartformHorseObject(rs);
                    runner.setHorse(horse);
                    SmartformTack tack = createSmartformTackObject(rs);
                    runner.setTack(tack);
                    String strJacketSyntax = DbUtils.getDBString(rs, "ucs_jacket");
                    if (!"".equals(strJacketSyntax))
                        runner.setUnregisteredColourSyntax(new UnregisteredColourSyntax(strJacketSyntax, DbUtils.getDBString(rs, "ucs_sleeves"), DbUtils.getDBString(rs, "ucs_cap")));

                    createEnvironmentObjects(rs, runner);
                    alRunners.add(runner);
                }
                rs.close();
            }
            catch(SQLException e)
            {
                
            }
        }

    return alRunners;
}
public static ArrayList<SmartformDailyRunner> createDailyDayRunners(ENEStatement stmt, int nDayDiff)
{
    String strQuery = "select u.race_id, u.race_id as daily_race_id, u.runner_id as runner_id, u.runner_id as daily_runner_id, -1 as owner_id, u.name, " + sm_strRunnerFields +"," + sm_strDailyRunnerFields + "," + sm_strHorseFields + "," + sm_strTackFields + " from ((daily_races a inner join daily_runners u on a.race_id = u.race_id) left outer join nonrunner n on u.race_id = n.race_id and u.runner_id = n.runner_id) where a.meeting_date = date_sub(current_date, interval " + nDayDiff + " day) and n.runner_id is null";

    ArrayList<SmartformDailyRunner> alRunners = new ArrayList<SmartformDailyRunner>();
        ResultSet rs = stmt.executeQuery(strQuery);
        if (rs != null)
        {
        try
        {
            while (rs.next())
            {
                SmartformDailyRunner runner = createSmartformDailyRunnerObject(rs);
                SmartformHorse horse = createSmartformHorseObject(rs);
                runner.setHorse(horse);
                SmartformTack tack = createSmartformTackObject(rs);
                runner.setTack(tack);
                createEnvironmentObjects(rs, runner);
                alRunners.add(runner);
            }
            rs.close();
        }
            catch(SQLException e)
            {
                
            }
        }

    return alRunners;
}

public static int insertNonRunner(ENEStatement statement, int nRace, int nRunner, String strReason)
{
    int nReturn = 0;
    String strUpdate = "replace into nonrunner (race_id, runner_id, reason) values (" + nRace + "," + nRunner + ",'" + strReason + "')";

        nReturn  = statement.executeUpdate(strUpdate);
 
    SmartformRace race = SmartformEnvironment.getInstance().getSmartformRace(nRace);
    if (race != null)
    {
        SmartformRunner runner = (SmartformRunner) race.getRunner(nRunner);
        if (runner != null)
            runner.setNonRunner();
    }

    return nReturn;
}
// create Smartform Objects
private static SmartformDailyRunner createSmartformDailyRunnerObject(ResultSet rs) throws SQLException
{
    // adjusted_rating, jockey_colours, days_since_ran_type, course_winner, distance_winner, candd_winner, beaten_favourite, weight_penalty
    SmartformDailyRunner runner = new SmartformDailyRunner(rs.getInt("race_id"), rs.getInt("runner_id"));

    updateSmartformDailyRunnerObject(rs, runner);

    return runner;
}
private static SmartformDailyRunner createSmartformNonRunnerObject(ResultSet rs) throws SQLException
{
    // adjusted_rating, jockey_colours, days_since_ran_type, course_winner, distance_winner, candd_winner, beaten_favourite, weight_penalty
    SmartformDailyRunner runner = new SmartformNonRunner(rs.getInt("race_id"), rs.getInt("runner_id"));

    updateSmartformDailyRunnerObject(rs, runner);

    return runner;
}

private static SmartformDailyRunner updateSmartformDailyRunnerObject(ResultSet rs, SmartformDailyRunner runner) throws SQLException
{
    runner = (SmartformDailyRunner)updateSmartformRunnerObject(rs, runner);
    runner.setPenaltyWeight(rs.getInt("weight_penalty"));
    runner.setAdjustedRating(rs.getInt("adjusted_rating"));
    runner.setJockeyColours(DbUtils.getDBString(rs, "jockey_colours"));
    runner.setLastRunType(DbUtils.getDBString(rs, "days_since_ran_type"));
    runner.setCourseWinnerCount(rs.getInt("course_winner"));
    runner.setDistanceWinnerCount(rs.getInt("distance_winner"));
    runner.setCourseDistanceWinnerCount(rs.getInt("candd_winner"));
    runner.setBeatenFavouriteCount(rs.getInt("beaten_favourite"));
    runner.setPrimaryOwner(DbUtils.getDBString(rs, "spc_primary_owner"));

    // 20121114
    UnregisteredColourSyntax ucs = createUnregisteredColourSyntax(rs);
    if (ucs != null)
        runner.setUnregisteredColourSyntax(ucs);

    runner.setPreviousRace(DbUtils.getDBString(rs, "previous_race_name"));

    return runner;
}

private static SmartformHistoricRunner createSmartformHistoricRunnerObject(ResultSet rs) throws SQLException
{
    // owner_id, distance_travelled, num_fences_jumped, how_easy_won, in_race_comment, official_rating_type, speed_rating, speed_rating_type, private_handicap,
    // private_handicap_type, starting_price, starting_price_decimal, betting_text, position_in_betting, finish_position, amended_position, unfinished,
    // distance_beaten, distance_won, distance_behind_winner, prize_money, tote_win, tote_place, last_race_type_id, last_race_type, last_race_beaten_fav, penalty_weight, over_weight
    SmartformHistoricRunner runner = new SmartformHistoricRunner(rs.getInt("race_id"), rs.getInt("runner_id"));

    runner = (SmartformHistoricRunner)updateSmartformRunnerObject(rs, runner);

    runner = (SmartformHistoricRunner) updateSmartformDailyRunnerObject(rs, runner);

    runner.setDistanceTravelled(rs.getInt("distance_travelled"));
    runner.setFencesJumped(rs.getInt("num_fences_jumped"));
    runner.setHowEasyWon(rs.getInt("how_easy_won"));
    runner.setInRaceComment(rs.getString("in_race_comment"));
    runner.setOfficialRatingType(rs.getString("official_rating_type"));
    runner.setSpeedRating(rs.getInt("speed_rating"));
    runner.setSpeedRatingType(rs.getString("speed_rating_type"));
    runner.setPrivateHandicap(rs.getInt("private_handicap"));
    runner.setPrivateHandicapType(rs.getString("private_handicap_type"));
    runner.setStartingPrice(rs.getString("starting_price"));
    runner.setStartingPriceDecimal(rs.getDouble("starting_price_decimal"));
    runner.setBettingText(rs.getString("betting_text"));
    runner.setPositionInBetting(rs.getInt("position_in_betting"));
    runner.setFinishPosition(rs.getInt("finish_position"));
    runner.setAmendedPosition(rs.getInt("amended_position"));
    runner.setUnfinished(rs.getString("unfinished"));
    runner.setDistanceBeaten(rs.getDouble("distance_beaten"));
    runner.setDistanceWon(rs.getDouble("distance_won"));
    runner.setDistanceBehindWinner(rs.getDouble("distance_behind_winner"));
    runner.setPrizeMoney(rs.getDouble("prize_money"));
    runner.setToteWin(rs.getDouble("tote_win"));
    runner.setTotePlace(rs.getDouble("tote_place"));
    runner.setLastRaceTypeId(rs.getInt("last_race_type_id"));
    runner.setOverWeight(rs.getInt("over_weight"));
    runner.setLastRaceBeatenFav(rs.getInt("last_race_beaten_fav"));

    runner.setPenaltyWeight(rs.getInt("penalty_weight"));
    runner.setLastRunType(DbUtils.getDBString(rs, "last_race_type"));

    return runner;
}

private static SmartformRunner updateSmartformRunnerObject(ResultSet rs, SmartformRunner runner) throws SQLException
{
    // form_figures, age, cloth_number, stall_number, trainer_id, owner_id, jockey_id, long_handicap, days_since_ran, weight_pounds, forecast_price, forecast_price_decimal, official_rating
    runner.setFormFigures(DbUtils.getDBString(rs, "form_figures"));
    runner.setAge(rs.getInt("age"));
    runner.setClothNumber(rs.getInt("cloth_number"));
    runner.setStall(rs.getInt("stall_number"));
    runner.setTrainerId(rs.getInt("trainer_id"));
    runner.setOwnerId(rs.getInt("owner_id"));
    runner.setOwnerName(DbUtils.getDBString(rs, "owner_name"));    // why is owner id not in daily_runners ??
    runner.setJockeyId(rs.getInt("jockey_id"));
    runner.setJockeyClaim(rs.getInt("jockey_claim"));
    runner.setLongHandicap(rs.getInt("long_handicap"));
    runner.setDaysSinceRan(rs.getInt("days_since_ran"));
    runner.setWeightPounds(rs.getInt("weight_pounds"));
    runner.setForecastPriceDecimal(rs.getDouble("forecast_price_decimal"));
    runner.setForecastPrice(DbUtils.getDBString(rs, "forecast_price"));
    runner.setOfficialRating(rs.getInt("official_rating"));

    // 20110719
    runner.setTrainerName(DbUtils.getDBString(rs, "trainer_name")); // not always an environment
    runner.setJockeyName(DbUtils.getDBString(rs, "jockey_name"));

    return runner;
}

private static SmartformHorse createSmartformHorseObject(ResultSet rs) throws SQLException
{
    // foaling_date, colour, gender, bred, dam_name, dam_year_born, sire_name, sire_year_born, dam_sire_name, dam_sire_year_born
    SmartformHorse horse = new SmartformHorse(rs.getInt("runner_id"), DbUtils.getDBString(rs, "name"));
    try
    {
        String strDate = rs.getString("foaling_date");
        if (!"0000-00-00".equals(strDate))  // try to avoid exception when null  DOESN'T WORK, so must set foaling_date to something
        {        
            Calendar cal = new GregorianCalendar(); 
            cal.setTimeInMillis(rs.getDate("foaling_date").getTime());
            horse.setFoalingDate(cal);
        }
        else
            System.out.println("Runner has no foaling date: " + horse.getId() + "-" + horse.getName() );
    }
    catch(Exception e)
    {
           System.out.println("Foaling date exception: " + horse.getId() + "-" + horse.getName() + "-" + e.getMessage() );
    }
    horse.setColour(DbUtils.getDBString(rs, "colour"));
    String strGender = DbUtils.getDBString(rs, "gender");
    if (strGender.length() > 0)
        horse.setGender(strGender.charAt(0));
    horse.setBred(DbUtils.getDBString(rs, "bred"));
    SmartformHorseDefinition sire = new SmartformHorseDefinition(DbUtils.getDBString(rs, "sire_name"), 0, "");
    SmartformHorseDefinition dam = new SmartformHorseDefinition(DbUtils.getDBString(rs, "dam_name"), 0, "");
    SmartformHorseDefinition damsire = new SmartformHorseDefinition(DbUtils.getDBString(rs, "dam_sire_name"), 0, "");

    int nDailyRunner = rs.getInt("daily_runner_id");
    if (nDailyRunner > 0)
    {
        sire.setYearBorn(rs.getInt("sire_year_born"));
        dam.setYearBorn(rs.getInt("dam_year_born"));
        damsire.setYearBorn(rs.getInt("dam_sire_year_born"));
    }

    horse.setSire(sire);
    horse.setDam(dam);
    horse.setDamSire(damsire);
    
    return horse;
}

public static SmartformTack createSmartformTackObject(ResultSet rs) throws SQLException
{
    // tack_hood, tack_visor, tack_blinkers, tack_eye_shield, tack_eye_cover, tack_cheek_piece, tack_pacifiers, tack_tongue_strap
    SmartformTack tack = new SmartformTack();   // rs.getInt("race_id"), rs.getInt("runner_id"));
    tack.setHood(rs.getInt("tack_hood"));
    tack.setVisor(rs.getInt("tack_visor"));
    tack.setBlinkers(rs.getInt("tack_blinkers"));
    tack.setEyeShield(rs.getInt("tack_eye_shield"));
    tack.setEyeCover(rs.getInt("tack_eye_cover"));
    tack.setCheekPieces(rs.getInt("tack_cheek_piece"));
    tack.setPacifiers(rs.getInt("tack_pacifiers"));
    tack.setTongueStrap(rs.getInt("tack_tongue_strap"));

    return tack;
}
public static SmartformTrainer createSmartformTrainerObject(ResultSet rs) throws SQLException
{
     SmartformTrainer trainer = new SmartformTrainer(rs.getInt("trainer_id"), DbUtils.getDBString(rs, "trainer_name"));
    return trainer;
}
private static SmartformJockey createSmartformJockeyObject(ResultSet rs) throws SQLException
{
    SmartformJockey jockey = new SmartformJockey(rs.getInt("jockey_id"), DbUtils.getDBString(rs, "jockey_name"));
    jockey.setClaim(rs.getInt("jockey_claim"));

    return jockey;
}
public static SmartformOwner createSmartformOwnerObject(ResultSet rs) throws SQLException
{
    SmartformOwner owner = new SmartformOwner(rs.getInt("owner_id"));
    owner.setName(DbUtils.getDBString(rs, "owner_name"));

    return owner;
}
private static SmartformTrainerDay createSmartformTrainerDayObject(ResultSet rs, Date dtDay) throws SQLException
{
    SmartformTrainerDay trainer = new SmartformTrainerDay(rs.getInt("trainer_id"), dtDay);

    return trainer;
}
private static SmartformJockeyDay createSmartformJockeyDayObject(ResultSet rs, Date dtDay) throws SQLException
{
    SmartformJockeyDay jockey = new SmartformJockeyDay(rs.getInt("jockey_id"), dtDay);

    return jockey;
}
private static SmartformOwnerDay createSmartformOwnerDayObject(ResultSet rs, Date dtDay) throws SQLException
{
    SmartformOwnerDay owner = new SmartformOwnerDay(rs.getInt("owner_id"), dtDay);

    return owner;
}
private static SmartformTrainerMeeting createSmartformTrainerMeetingObject(ResultSet rs) throws SQLException
{
     SmartformTrainerMeeting trainer = new SmartformTrainerMeeting(rs.getInt("trainer_id"), rs.getInt("meeting_id"));

    return trainer;
}
private static SmartformJockeyMeeting createSmartformJockeyMeetingObject(ResultSet rs) throws SQLException
{
    SmartformJockeyMeeting jockey = new SmartformJockeyMeeting(rs.getInt("jockey_id"), rs.getInt("meeting_id"));

    return jockey;
}
private static SmartformOwnerMeeting createSmartformOwnerMeetingObject(ResultSet rs) throws SQLException
{
    SmartformOwnerMeeting owner = new SmartformOwnerMeeting(rs.getInt("owner_id"), rs.getInt("meeting_id"));

    return owner;
}
public static ArrayList<ArrayList<SmartformHistoricRunner>> createMeeting123(ENEStatement stmt, int nMeeting, int nPlaces)
{
    ArrayList<ArrayList<SmartformHistoricRunner>> alRaceRunners = new ArrayList<ArrayList<SmartformHistoricRunner>>();

    String strQuery = "select h.race_id, h.runner_id, h.name, u.runner_id as daily_runner_id, coalesce(h.owner_id, o.owner_id) as owner_id, " + sm_strBestRunnerFields +"," + sm_strDailyRunnerFields + "," + sm_strBestHorseFields + "," + sm_strBestTackFields + "," + sm_strHistoricRunnerFields + ", ucs_jacket, ucs_sleeves, ucs_cap from ((((((historic_races ha left outer join daily_races a on ha.race_id=a.race_id) inner join historic_runners h on ha.race_id=h.race_id";
    strQuery += " and (finish_position<=" + nPlaces + " or amended_position<=" + nPlaces + ")) left outer join runner_id_diff on h.runner_id=historic_runner_id) left outer join daily_runners u on ha.race_id=u.race_id and coalesce(daily_runner_id,h.runner_id)=u.runner_id) left outer join unique_owner o on u.owner_name=o.owner_name)";
    strQuery += " left outer join unregistered_colour_syntax on jockey_colours=ucs_colours and (country=ucs_organisation or (country in ('England', 'Wales', 'Scotland', 'Northern Ireland', 'Eire') and ucs_organisation = 'UK')) and (year(a.meeting_date) = ucs_year or ucs_year = 0))";
    strQuery += (" where ha.meeting_id=" + nMeeting);
    strQuery += " order by ha.meeting_date, ha.off_time, finish_position, amended_position";

    ResultSet rs = stmt.executeQuery(strQuery);
    if (rs != null)
    {
        ArrayList<SmartformHistoricRunner> alRunners = null;
        try
        {
    
             int nCurrentRace = 0;
            while (rs.next())
           {
                int nRace = rs.getInt("race_id");
                if (nRace != nCurrentRace)
                {
                    if (alRunners != null)
                        alRaceRunners.add(alRunners);
                   alRunners = new ArrayList<SmartformHistoricRunner>();
                }
                nCurrentRace = nRace;
                SmartformHistoricRunner runner = createSmartformHistoricRunnerObject(rs);
                runner = (SmartformHistoricRunner) updateSmartformRunnerObject(rs, runner);
                SmartformHorse horse = createSmartformHorseObject(rs);
                runner.setHorse(horse);
                SmartformTack tack = createSmartformTackObject(rs);
                runner.setTack(tack);
                String strJacketSyntax = DbUtils.getDBString(rs, "ucs_jacket");
                if (!"".equals(strJacketSyntax))
                    runner.setUnregisteredColourSyntax(new UnregisteredColourSyntax(strJacketSyntax, DbUtils.getDBString(rs, "ucs_sleeves"), DbUtils.getDBString(rs, "ucs_cap")));
                alRunners.add(runner);
           }
            rs.close();
        }
        catch(SQLException e)
        {
            
        }

        alRaceRunners.add(alRunners);
    }
    return alRaceRunners;
}

}
