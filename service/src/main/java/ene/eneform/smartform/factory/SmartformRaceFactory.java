/*
 * To change this template, choose Tools | Templates
 * and open theD template in the editor.
 */

package ene.eneform.smartform.factory;

import ene.eneform.smartform.bos.*;
import ene.eneform.smartform.bos.day.SmartformDay;
import ene.eneform.smartform.bos.meeting.SmartformMeeting;
import ene.eneform.smartform.utils.CourseTimeList;
import ene.eneform.utils.DbUtils;
import ene.eneform.utils.ENEStatement;
import ene.eneform.utils.Pair;
import ene.eneform.utils.SetUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 *
 * @author Simon
 */
public class SmartformRaceFactory {

    private static String sm_strBestRaceFields = "coalesce(d.meeting_date, h.meeting_date) as meeting_date, coalesce(d.race_type, h.race_type) as race_type, coalesce(d.distance_yards, h.distance_yards) as distance_yards, coalesce(d.added_money, h.added_money) as added_money, coalesce(d.scheduled_time, h.scheduled_time) as scheduled_time, coalesce(d.class, h.class) as class, coalesce(d.handicap, h.handicap) as handicap, coalesce(d.draw_advantage, h.draw_advantage) as draw_advantage";
    private static String sm_strRaceFields = "d.meeting_date, d.race_type, d.distance_yards, d.added_money, d.scheduled_time, d.class, d.handicap, d.draw_advantage";
    private static String sm_strDailyRaceFields = "race_title, track_type, advanced_going, age_range, penalty_value";
    private static String sm_strDailyRaceMeetingFields = "weather, meeting_status, meeting_abandoned_reason, country";

    private static String sm_strHistoricRaceFields = "race_name, race_abbrev_name, race_num, conditions, direction, going, min_age, max_age, num_fences, coalesce(nr_runners, num_runners) as num_runners, num_finishers, all_weather, rating, group_race, winning_time_disp, winning_time_secs, standard_time_disp, standard_time_secs";

    private static String sm_strDailyLastWinnerFields = "last_winner_no_race,last_winner_year,last_winner_runners,last_winner_runner_id,last_winner_name,last_winner_age,last_winner_bred,last_winner_weight,last_winner_trainer,last_winner_trainer_id,last_winner_jockey,last_winner_jockey_id,last_winner_sp,last_winner_sp_decimal,last_winner_betting_ranking,last_winner_course_winner,last_winner_distance_winner,last_winner_candd_winner,last_winner_beaten_favourite";
    private static String sm_strDailyPrizeMoneyFields = "prize_pos_1,prize_pos_2,prize_pos_3,prize_pos_4,prize_pos_5,prize_pos_6,prize_pos_7,prize_pos_8,prize_pos_9";


public static int setRaceGoing(ENEStatement statement, int nRace, String strGoing)
{
    int nReturn = 0;
    String strUpdate = "update daily_races set advanced_going='" + strGoing + "', loaded_at=loaded_at where race_id = " + nRace;


    nReturn  = statement.executeUpdate(strUpdate);
 
    SmartformRace race = SmartformEnvironment.getInstance().getSmartformRace(nRace);
    if (race != null)
    {
        race.setGoing(strGoing);
    }

    return nReturn;
}

public static int setMeetingGoing(ENEStatement statement, int nMeeting, String strGoing)
{
    int nReturn = 0;
    String strUpdate = "update daily_races set advanced_going='" + strGoing + "', loaded_at=loaded_at where meeting_id = " + nMeeting;

        nReturn  = statement.executeUpdate(strUpdate);
 
    SmartformMeeting meeting = SmartformEnvironment.getInstance().getSmartformMeeting(nMeeting);
    if (meeting != null)
    {
        Iterator<SmartformRace> iter = meeting.getRaceIterator();
        while(iter.hasNext())
        {
            SmartformRace race = iter.next();

            race.setGoing(strGoing);
        }
    }

    return nReturn;
}

public static SmartformDay createSmartformDayByDate(ENEStatement statement, Date dtDaily)
{

    String strDate = SmartformEnvironment.getInstance().getSQLDateFormat().format(dtDaily);
    
    String strQuery = "select x.meeting_id, coalesce(d.course, h.course) as course, x.race_id, bf_race_id, h.race_id as historic_race_id, " + sm_strDailyRaceMeetingFields + ", " + sm_strBestRaceFields + ", " + sm_strDailyRaceFields + "," + sm_strHistoricRaceFields + "," + sm_strDailyLastWinnerFields + " from ((((all_races x left outer join daily_races d on x.race_id=d.race_id) left outer join historic_races h on x.race_id = h.race_id) left outer join ene_historic_races e on d.race_id=e.race_id) left outer join betfair_races b on d.race_id = b.race_id) where x.meeting_date = '" + strDate + "' order by x.meeting_id, d.course, d.scheduled_time, h.course, h.scheduled_time";

    return createSmartformDay(statement, strQuery, dtDaily);
}
public static SmartformDay createSmartformToday(ENEStatement statement)
{
    return createSmartformToday(statement, 0);
}
public static SmartformDay createSmartformToday(ENEStatement statement, int nDayOffset)
{
    String strQuery = "select x.meeting_id, coalesce(d.course, h.course) as course, x.race_id, bf_race_id, h.race_id as historic_race_id, " + sm_strDailyRaceMeetingFields + ", " + sm_strBestRaceFields + ", " + sm_strDailyRaceFields + "," + sm_strHistoricRaceFields + "," + sm_strDailyLastWinnerFields + " from ((((all_races x left outer join daily_races d on x.race_id=d.race_id) left outer join historic_races h on x.race_id = h.race_id) left outer join ene_historic_races e on d.race_id=e.race_id) left outer join betfair_races b on d.race_id = b.race_id) where x.meeting_date = date_sub(current_date, interval " + nDayOffset + " day) order by x.meeting_id, d.course, d.scheduled_time, h.course, h.scheduled_time";

    final Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, - nDayOffset);
    
    return createSmartformDay(statement, strQuery, cal.getTime());
}

public static SmartformDay createSmartformDayByMeeting(ENEStatement statement, int nMeeting)
{
    String strQuery = "select x.meeting_date, x.meeting_id, coalesce(d.course, h.course) as course, x.race_id, bf_race_id, h.race_id as historic_race_id, " + sm_strDailyRaceMeetingFields + ", " + sm_strBestRaceFields + ", " + sm_strDailyRaceFields + "," + sm_strHistoricRaceFields + "," + sm_strDailyLastWinnerFields + " from ((((all_races x left outer join daily_races d on x.race_id=d.race_id) left outer join historic_races h on x.race_id = h.race_id) left outer join ene_historic_races e on d.race_id=e.race_id) left outer join betfair_races b on d.race_id = b.race_id) where x.meeting_date = (select min(meeting_date) from all_races where meeting_id = " + nMeeting + ") order by x.meeting_id, d.course, d.scheduled_time";

    return createSmartformDay(statement, strQuery, null);
}

public static SmartformDay createSmartformDay(ENEStatement statement, String strQuery, Date dtDaily)
{
    SmartformDay daily = null;
    if (dtDaily != null)
        daily = new SmartformDay(dtDaily);
    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        int nLatestMeeting = 0;
        SmartformMeeting meeting = null;
        if (rs != null)
        {
            while (rs.next())
            {
                if ((nLatestMeeting == 0) && (daily == null))
                {
                    long lDate = rs.getDate("meeting_date").getTime();
                    Date d1 = new Date(lDate);
                    TimeZone localTimezone = TimeZone.getDefault();   
                    //Date d2 = new Date(lDate + localTimezone.getRawOffset() + localTimezone.getDSTSavings());   // add time difference compared with GMT
                    long lRaw = localTimezone.getRawOffset();
                    long lDST = localTimezone.getDSTSavings();
                    long lOffset = localTimezone.getOffset(lDate);
                    Date d2 = new Date(lDate + lOffset);   // add time difference compared with GMT

                    String strTime1 = SmartformEnvironment.getInstance().getTimeFormat().format(d1);
                    String strTime2 = SmartformEnvironment.getInstance().getTimeFormat().format(d2);
                    String strDate2 = SmartformEnvironment.getInstance().getDateFormat().format(d2);

                     daily = new SmartformDay(d2);
                }
                int nMeeting = rs.getInt("meeting_id");
                if (nMeeting != nLatestMeeting)
                {
                    meeting = createSmartformMeetingObject(rs);
                    daily.addMeeting(meeting);
                    nLatestMeeting = nMeeting;
                }

                int nHistoricId = rs.getInt("historic_race_id");
                boolean bHistoricCheck = !rs.wasNull();

                SmartformRace race = null;
                if (bHistoricCheck)
                {
                    race = createSmartformHistoricRaceObject(rs);
                }
                else
                {
                    race = createSmartformDailyRaceObject(rs);
                }
                meeting.addRace(race);
            }
            rs.close();
        }
    }
    catch(SQLException e)
    {
    }

    daily = SmartformRunnerFactory.createSmartformDayRunners(statement, daily);
    
    return daily;
}

public static SmartformMeeting createDailyMeeting(ENEStatement statement, int nMeeting)
{
    String strMeetingQuery = "select distinct meeting_id, course, " + sm_strRaceFields + "," + sm_strDailyRaceMeetingFields + "," + sm_strDailyLastWinnerFields + " from daily_races d where meeting_id = " + nMeeting;

    return createDailyMeetingList(statement, strMeetingQuery).get(0);
}

public static ArrayList<SmartformMeeting> createDailyMeetingDate(ENEStatement statement, Date dtMeeting)
{
    String strMeetingQuery = "select distinct meeting_id, course, " + sm_strRaceFields + "," + sm_strDailyRaceMeetingFields + "," + sm_strDailyLastWinnerFields + " from daily_races d where meeting_date = " + dtMeeting.toString();

    return createDailyMeetingList(statement, strMeetingQuery);
}

public static ArrayList<SmartformMeeting> createDailyMeetingList(ENEStatement statement, String strQuery)
{
    ArrayList<SmartformMeeting> list = new ArrayList<SmartformMeeting>();

    try
    {
         ResultSet rs = statement.executeQuery(strQuery);
        while (rs.next())
        {
            SmartformMeeting meeting = createSmartformMeetingObject(rs);

            list.add(meeting);
         }
        rs.close();
    }
    catch(SQLException e)
    {
    }

    return list;
}

public static ArrayList<SmartformRace> createSmartformDailyRaces(ENEStatement statement, int nMeeting)
{
    return createSmartformDailyRaces(statement, nMeeting, false);
}

public static ArrayList<SmartformRace> createSmartformDailyRaces(ENEStatement statement, int nMeeting, boolean bHistoric)
{
    ArrayList<SmartformRace> alRaces = null;
    String strMeetingQuery = "select x.race_id, b.bf_race_id, x.meeting_id, coalesce(d.course, h.course) as course, h.race_id as historic_race_id, "  + sm_strBestRaceFields + "," + sm_strDailyRaceFields + "," + sm_strHistoricRaceFields + "," + sm_strDailyLastWinnerFields + " from ((((all_races x left outer join daily_races d on x.race_id=d.race_id) ";
    strMeetingQuery += bHistoric ? " inner" : " left outer"; 
    strMeetingQuery += " join historic_races h on x.race_id=h.race_id) left outer join ene_historic_races e on d.race_id=e.race_id) left outer join betfair_races b on d.race_id=b.race_id) where x.meeting_id = " + nMeeting + " order by x.meeting_date, x.scheduled_time, x.race_id";
    alRaces = createRaceList(statement, strMeetingQuery);

    return alRaces;
}

public static ArrayList<SmartformRace> createSmartformARDYearRaces(ENEStatement statement, int nYear, String strStartDate)
{
    ArrayList<SmartformRace> alRaces = null;
    String strARDYearQuery = "select x.race_id, b.bf_race_id, x.meeting_id, coalesce(d.course, h.course) as course, h.race_id as historic_race_id, "  + sm_strBestRaceFields + "," + sm_strDailyRaceFields + "," + sm_strHistoricRaceFields + "," + sm_strDailyLastWinnerFields + " from (((((all_races x left outer join daily_races d on x.race_id=d.race_id) ";
    strARDYearQuery += " left outer join historic_races h on x.race_id=h.race_id) inner join additional_race_link on arl_race_id=x.race_id and arl_source='SF') left outer join betfair_races b on d.race_id=b.race_id) left outer join ene_historic_races e on d.race_id=e.race_id)";
    strARDYearQuery += " where year(x.meeting_date) = " + nYear; 
    if (strStartDate != null)
        strARDYearQuery += " and x.meeting_date >= '" + strStartDate + "'";
    strARDYearQuery += " order by x.meeting_date, x.scheduled_time, x.race_id";
    alRaces = createRaceList(statement, strARDYearQuery);

    return alRaces;
}

public static ArrayList<SmartformRace> createSmartformCourseNameClassRaces(ENEStatement stmt, String strCourse, String strName, String strClass, int nStart, int nEnd)
{
    ArrayList<SmartformRace> alRaces = null;
    String strMeetingQuery = "select x.race_id, b.bf_race_id, x.meeting_id, coalesce(d.course, h.course) as course, h.race_id as historic_race_id, "  + sm_strBestRaceFields + "," + sm_strDailyRaceFields + "," + sm_strHistoricRaceFields + "," + sm_strDailyLastWinnerFields + " from ((((all_races x left outer join daily_races d on x.race_id=d.race_id) left outer join historic_races h on x.race_id=h.race_id) left outer join ene_historic_races e on x.race_id=e.race_id) left outer join betfair_races b on x.race_id=b.race_id) where h.course = '" + strCourse + "' and h.race_name like '%" + strName + "%' and h.class = '" + strClass + "' and year(x.meeting_date) >= " + nStart + " and year(x.meeting_date) <= " + nEnd + " order by x.scheduled_time, x.race_id";
    alRaces = createRaceList(stmt, strMeetingQuery);

    return alRaces;
}


public static SmartformRace createSmartformRace(ENEStatement statement, int nRace, String strSource)
{
    SmartformRace race = null;
     String strRaceQuery = "select x.race_id, b.bf_race_id, x.meeting_id, coalesce(d.course, h.course) as course, h.race_id as historic_race_id, "  + sm_strBestRaceFields + "," + sm_strDailyRaceFields + "," + sm_strHistoricRaceFields + "," + sm_strDailyLastWinnerFields + " from ((((all_races x left outer join daily_races d on x.race_id=d.race_id) left outer join historic_races h on x.race_id=h.race_id) left outer join ene_historic_races e on x.race_id=e.race_id) left outer join betfair_races b on x.race_id=b.race_id) where x.race_id = " + nRace;
    ArrayList<SmartformRace> alRaces  = createRaceList(statement, strRaceQuery);
    if (alRaces.size() == 1)
    {
        race = alRaces.get(0); 
        ArrayList<SmartformHistoricRunner> aRunners = SmartformRunnerFactory.createDailyHistoricRunners(statement, nRace);
        Iterator<SmartformHistoricRunner> iter = aRunners.iterator();
        while(iter.hasNext())
        {
            race.addRunner(iter.next());
        }
        return race;
    }
    else
        return null;
}

public static String getRacePosition(ENEStatement statement, int nRace, String strHorse)
{
     String strQuery = "select case when coalesce(amended_position, finish_position, 0) = 0 then unfinished else cast(coalesce(amended_position, finish_position) as char) end from historic_races inner join historic_runners using (race_id) where race_id=" + nRace + " and name='" + strHorse.replace("'", "''") +"'";
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            try
            {
                while (rs.next())
                {
                    String strPosition = rs.getString(1);
 
                    return strPosition;
                }
            
                rs.close();
            }
            catch(SQLException e)
            {
            }
        }
        
        return null;
}

public static SmartformDailyRace createBetfairDailyRace(ENEStatement statement, int nMarket)
{
    SmartformDailyRace race = null;
 
    String strRaceQuery = "select b.race_id, b.bf_race_id, d.meeting_id, d.course, h.race_id as historic_race_id, "  + sm_strBestRaceFields + "," + sm_strDailyRaceFields + "," + sm_strHistoricRaceFields + "," + sm_strDailyLastWinnerFields + " from (((betfair_races b left outer join daily_races d on b.race_id=d.race_id) left outer join historic_races h on b.race_id=h.race_id) left outer join ene_historic_races e on b.race_id=e.race_id) where b.bf_race_id = " + nMarket;    // CONVERT_TZ(at_time,'+01:00','+00:00')
    race = (SmartformDailyRace) createRaceList(statement, strRaceQuery).get(0);
 
    return race;
}

public static ArrayList<SmartformRace> createCourseMonthYearRaces(ENEStatement stmt, String strCourse, int nMonth, int nYear)
{
    String strRaceQuery = "select x.race_id, b.bf_race_id, x.meeting_id, coalesce(d.course, h.course) as course, h.race_id as historic_race_id, "  + sm_strBestRaceFields + "," + sm_strDailyRaceFields + "," + sm_strHistoricRaceFields + "," + sm_strDailyLastWinnerFields + " from ((((all_races x left outer join daily_races d on x.race_id=d.race_id) left outer join historic_races h on x.race_id=h.race_id) left outer join ene_historic_races e on x.race_id=e.race_id) left outer join betfair_races b on x.race_id=b.race_id) where year(d.meeting_date) = " + nYear + " and d.course='" + strCourse + "' and month(d.meeting_date) = " + nMonth + " order by d.meeting_date, d.scheduled_time";
    return createRaceList(stmt, strRaceQuery);
}
public static ArrayList<SmartformRace> createSponsorRaces(ENEStatement stmt, String strCourse, int nYear, String strSponsor)
{
    String strRaceQuery = "select x.race_id, b.bf_race_id, x.meeting_id, coalesce(d.course, h.course) as course, h.race_id as historic_race_id, "  + sm_strBestRaceFields + "," + sm_strDailyRaceFields + "," + sm_strHistoricRaceFields + "," + sm_strDailyLastWinnerFields + " from ((((all_races x left outer join daily_races d on x.race_id=d.race_id) left outer join historic_races h on x.race_id=h.race_id) left outer join ene_historic_races e on x.race_id=e.race_id) left outer join betfair_races b on x.race_id=b.race_id) where year(d.meeting_date) = " + nYear + " and d.course='" + strCourse + "' and d.race_title like '%" + strSponsor + "%' order by d.meeting_date, d.scheduled_time";
    return createRaceList(stmt, strRaceQuery);
}
private static ArrayList<SmartformRace> createRaceList(ENEStatement stmt, String strQuery)
{
    ArrayList<SmartformRace> list = new ArrayList<SmartformRace>();
        ResultSet rs = stmt.executeQuery(strQuery);
        if (rs != null)
        {
            try
            {
                while (rs.next())
                {
                    int nHistoricId = rs.getInt("historic_race_id");
                    boolean bHistoricCheck = !rs.wasNull();

                    SmartformRace race = null;
                    if (bHistoricCheck)
                    {
                        race = createSmartformHistoricRaceObject(rs);
                    }
                    else
                    {
                        race = createSmartformDailyRaceObject(rs);
                    }

                    list.add(race);
                }
            
                rs.close();
            }
            catch(SQLException e)
            {
            }
        }
    return list;
}

public static ArrayList<Integer> createRaceIdList(ENEStatement stmt, String strQuery)
{
    ArrayList<Integer> list = new ArrayList<Integer>();
        ResultSet rs = stmt.executeQuery(strQuery);
        if (rs != null)
        {
            try
            {
                while (rs.next())
                {
                    int nRaceId = rs.getInt("race_id");
 
                    list.add(nRaceId);
                }
            
                rs.close();
            }
            catch(SQLException e)
            {
            }
        }
    return list;
}


public static SmartformHistoricRace createBetfairHistoricRace(ENEStatement statement, int nMarket)
{
    // direction, course
    String strRaceQuery = "select b.race_id, b.bf_race_id, meeting_id, course, " + sm_strHistoricRaceFields + " from (betfair_races b left outer join historic_races h on b.race_id=h.race_id) where b.bf_race_id = " + nMarket;
    return createHistoricRaceList(statement, strRaceQuery).get(0);
}

private static ArrayList<SmartformHistoricRace> createHistoricRaceList(ENEStatement statement, String strQuery)
{
    ArrayList<SmartformHistoricRace> list = new ArrayList<SmartformHistoricRace>();
    try
    {
         ResultSet rs = statement.executeQuery(strQuery);
        while (rs.next())
        {
            SmartformHistoricRace race = createSmartformHistoricRaceObject(rs);

            list.add(race);
        }
        rs.close();
    }
    catch(SQLException e)
    {
    }
 
    return list;
}







// create Smartform Objects
private static SmartformDailyRace createSmartformDailyRaceObject(ResultSet rs) throws SQLException
{
    SmartformDailyRace race = new SmartformDailyRace(rs.getInt("race_id"));

    race = (SmartformDailyRace) updateSmartformRaceObject(rs, race);

    updateSmartformDailyRaceObject(race, rs);

    return race;
}

private static SmartformDailyRace updateSmartformDailyRaceObject(SmartformDailyRace race, ResultSet rs) throws SQLException
{
    race.setTitle(DbUtils.getDBString(rs, "race_title"));
    race.setTrackType(DbUtils.getDBString(rs, "track_type"));
    race.setGoing(DbUtils.getDBString(rs, "advanced_going"));
    race.setAgeRange(DbUtils.getDBString(rs, "age_range"));
    race.setPenaltyValue(rs.getFloat("penalty_value"));

    race.setSmartformLastWinner(createSmartformLastWinnerObject(race.getRaceId(), rs));

    return race;
}

private static SmartformLastWinner createSmartformLastWinnerObject(int nRace, ResultSet rs) throws SQLException
{
    SmartformLastWinner winner = new SmartformLastWinner(nRace);

    winner.setHorse(DbUtils.getDBString(rs, "last_winner_name"));
    winner.setJockey(DbUtils.getDBString(rs, "last_winner_jockey"));
    winner.setTrainer(DbUtils.getDBString(rs, "last_winner_trainer"));
    winner.setYear(rs.getInt("last_winner_year"));
    winner.setStartingPrice(DbUtils.getDBString(rs, "last_winner_sp"));
    winner.setNoRaceReason(DbUtils.getDBString(rs, "last_winner_no_race"));

    return winner;
}

private static SmartformRace updateSmartformRaceObject(ResultSet rs, SmartformRace race) throws SQLException
{

    race.setBetfairId(rs.getInt("bf_race_id"));
    race.setMeetingId(rs.getInt("meeting_id"));
    Timestamp timestamp = rs.getTimestamp("scheduled_time");
    TimeZone localTimezone = TimeZone.getDefault();   
    if (timestamp != null)
        race.setDateTime(timestamp.getTime() + localTimezone.getOffset(timestamp.getTime()));   // add 1 hour time difference for  BST
    else
    {
        Date date = rs.getDate("meeting_date");

        race.setDateTime(date.getTime() + localTimezone.getOffset(date.getTime()));   // add 1 hour time difference for GMT (change to 2 for BST)
    }
    race.setCourse(DbUtils.getDBString(rs, "course"));
    race.setRaceType(DbUtils.getDBString(rs, "race_type"));
     race.setRaceClass(DbUtils.getDBString(rs, "class"));
    race.setHandicap(rs.getInt("handicap") == 1);
    race.setDistanceYards(rs.getInt("distance_yards"));
    race.setAddedMoney(rs.getFloat("added_money"));

    return race;
}


private static SmartformHistoricRace createSmartformHistoricRaceObject(ResultSet rs) throws SQLException
{
    SmartformHistoricRace race = new SmartformHistoricRace(rs.getInt("race_id"));

    race = (SmartformHistoricRace) updateSmartformRaceObject(rs, race);

    updateSmartformDailyRaceObject(race, rs);

    race.setRaceNumber(rs.getInt("race_num"));
    race.setDirection(DbUtils.getDBString(rs, "direction"));
    race.setTitle(DbUtils.getDBString(rs, "race_name"));
    race.setShortTitle(DbUtils.getDBString(rs, "race_abbrev_name"));
    race.setConditions(DbUtils.getDBString(rs, "conditions"));
    race.setGoing(DbUtils.getDBString(rs, "going"));
    race.setMinimumAge(rs.getInt("min_age"));
    race.setMaximumAge(rs.getInt("max_age"));
    race.setNrRunners(rs.getInt("num_runners"));
    race.setNrFinishers(rs.getInt("num_finishers"));
    race.setNrFences(rs.getInt("num_fences"));
    race.setAllWeather(rs.getInt("all_weather") == 1);
    race.setTopRating(rs.getInt("rating"));
    race.setGroupRace(rs.getInt("group_race"));
    race.setWinningTime(DbUtils.getDBString(rs, "winning_time_disp"));
    race.setWinningTimeSeconds(rs.getDouble("winning_time_secs"));
    race.setStandardTime(DbUtils.getDBString(rs, "standard_time_disp"));
    race.setStandardTimeSeconds(rs.getDouble("standard_time_secs"));

    return race;
}




private static SmartformMeeting createSmartformMeetingObject(ResultSet rs) throws SQLException
{
    SmartformMeeting meeting = new SmartformMeeting(rs.getInt("meeting_id"));
    meeting.setCourse(DbUtils.getDBString(rs, "course"));
    TimeZone localTimezone = TimeZone.getDefault();   
    meeting.setMeetingDate(rs.getDate("meeting_date").getTime() + localTimezone.getOffset(rs.getDate("meeting_date").getTime()));   // add 1 hour time difference for GMT (change to 2 for BST)
    meeting.setCountry(DbUtils.getDBString(rs, "country"));
    meeting.setMeetingStatus(DbUtils.getDBString(rs, "meeting_status"));
    meeting.setMeetingAbandonedReason(DbUtils.getDBString(rs, "meeting_abandoned_reason"));
    meeting.setDrawAdvantage(DbUtils.getDBString(rs, "draw_advantage"));
    meeting.setWeather(DbUtils.getDBString(rs, "weather"));
    return meeting;
}

// no longer in use
public static SmartformDay createShortSmartformDay(ENEStatement statement, Date dtDaily)
{
    SmartformDay daily = new SmartformDay(dtDaily);

    String strMeetingQuery = "select meeting_id, course, d.race_id, bf_race_id, scheduled_time from daily_races d left outer join betfair_races using (race_id) where meeting_date = " + dtDaily.toString() + " order by course, scheduled_time";

    try
    {
         ResultSet rs = statement.executeQuery(strMeetingQuery);
        int nLatestMeeting = 0;
        SmartformMeeting meeting = null;
        while (rs.next())
        {
            int nMeeting = rs.getInt("meeting_id");
            if (nMeeting != nLatestMeeting)
            {
                meeting = new SmartformMeeting(nMeeting);
                meeting.setCourse(DbUtils.getDBString(rs, "course"));
                daily.addMeeting(meeting);
                nLatestMeeting = nMeeting;
            }

            SmartformDailyRace race = new SmartformDailyRace(rs.getInt("race_id"));
            TimeZone localTimezone = TimeZone.getDefault();   
            race.setDateTime(rs.getTimestamp("scheduled_time").getTime() + localTimezone.getOffset(rs.getTimestamp("scheduled_time").getTime()));   // add 1 hour time difference for GMT (change to 2 for BST)
            race.setBetfairId(rs.getInt("bf_race_id"));
            meeting.addRace(race);
         }
        rs.close();
    }
    catch(SQLException e)
    {

    }

    return daily;
}
public static List<String> getCourseList(ENEStatement statement, int nDayDiff)
{
    ArrayList<String> alCourses = new ArrayList<String>();
    String strQuery = "select distinct course from daily_races where meeting_status != \"Abandoned\" and meeting_date = date_add(current_date, interval " + nDayDiff + " day)";
    
        try
        {
            ResultSet rs = statement.executeQuery(strQuery);
            if (rs != null)
            {
                while (rs.next())
                {
                    String strCourse = rs.getString(1);
                    alCourses.add(strCourse);
                }
                rs.close();
            }
        }
        catch(SQLException e)
        {

        }
        
        return alCourses;
}
public static String getRacecardTitle(ENEStatement statement, SmartformRacecardDefinition racecard)
{
    String strQuery = null;
    String strTitle = null;
    if ("day".equals(racecard.getType()))
    {
        strQuery = "select distinct date_format(meeting_date, \"%D %M %Y\") from daily_races where meeting_date = date_add(current_date, interval " + racecard.getDayDiff() + " day)";
    }
    else if ("tv".equals(racecard.getType()))
    {
        strQuery = "select distinct concat(date_format(meeting_date, \"%D %M %Y\"), \" - TV\") from daily_races where meeting_date = date_add(current_date, interval " + racecard.getDayDiff() + " day)";
    }
    else if ("course".equals(racecard.getType()))
    {
        ArrayList<CourseTimeList> alCTL = racecard.getCourseTimeList();
        String strCourse = alCTL.get(0).getCourse();
        strQuery = "select distinct concat(date_format(meeting_date, \"%D %M %Y\"), \" - \", course) from daily_races where meeting_date = date_add(current_date, interval " + racecard.getDayDiff() + " day) and course=\"" + strCourse + "\"";
    }
    
    if (strQuery != null)
    {
        try
        {
            ResultSet rs = statement.executeQuery(strQuery);
            if (rs != null)
            {
                while (rs.next())
                {
                    strTitle = rs.getString(1);
                }
                rs.close();
            }
        }
        catch(SQLException e)
        {

        }
    }
    
    return strTitle;
}
public static List<Integer> getRaceIds(ENEStatement statement, String[] astrRaces)
{
    String strWhere = (" race_id in (");
    for(int i = 0; i < astrRaces.length; i++)
    {
        if ("".equals(astrRaces[i]))
            break;
        
        if (i > 0)
            strWhere += ", ";
        strWhere += astrRaces[i];
    }
    strWhere += ")";

    return getRaceIds(statement, strWhere);
}
public static List<Integer> getRaceIds(ENEStatement statement, SmartformRacecardDefinition dctl)
{
    String strWhere = (" meeting_date=date_add(current_date, interval " + dctl.getDayDiff() + " day)");
    Iterator<CourseTimeList> iter = dctl.getCourseTimeList().iterator();
    int nCount = 0;
    while(iter.hasNext())
    {
        CourseTimeList cdtl = iter.next();
        if (nCount == 0)
            strWhere += " and (";
        else
            strWhere += " or";
        strWhere += "(";
        strWhere += (" course=\"" + cdtl.getCourse() + "\"");
        if (cdtl.getTimes().length > 0)
        {
            strWhere+= " and time(scheduled_time) in (";
            String[] arTimes = cdtl.getTimes();
            for(int i = 0; i < arTimes.length; i++)
            {
                if (i > 0)
                    strWhere += ",";
                strWhere += ("\"" + arTimes[i] + ":00\"");
            }
            strWhere += ")";    // in
        }
        
        strWhere += ")";    // or
        nCount++;
    }
    if (nCount > 0)
        strWhere += ")";    // and

    return getRaceIds(statement, strWhere);
}
public static List<Integer> getAdditionalRaceIds(ENEStatement statement, String strSource, String strWhere, boolean bCountry)
{
    String strQuery;
    if (bCountry)
        strQuery = "select race_id from additional_races d inner join additional_race_link on race_id=arl_race_id and ara_source=arl_source inner join additional_race_data on arl_name=ard_name where ara_source='" + strSource + "' and " + strWhere + " order by meeting_date";
    else
        strQuery = "select race_id from additional_races where ara_source='" + strSource + "' and " + strWhere + " order by meeting_date";
    
    List<Integer> alRaces = new ArrayList<Integer>();
    
    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            while (rs.next())
            {
                int nRace = rs.getInt("race_id");
                alRaces.add(nRace);
             }
            rs.close();
        }
    }
    catch(SQLException e)
    {

    }

    return alRaces;
}
private static List<Integer> getRaceIds(ENEStatement statement, String strWhere)
{
    // backwards compatability - daily_races
    return getRaceIds(statement, strWhere, false, false);
}
public static List<AdditionalRaceInstance> getRaceInstancesByCategory(ENEStatement statement, String strCategory, String strSubCategory, String strStartDate, String strEndDate, boolean bFixtures)
{
    String strQuery = "select arc_name as ard_name, race_id, arl_source as source, meeting_date, race_title, ard_group_race, ard_course, ard_distance_yards, winner from";
    strQuery += " (select arc_name, race_id, arl_source, meeting_date, race_name as race_title, ard_group_race, ard_course, ard_distance_yards, '' as winner from additional_race_category arc, additional_race_data, additional_race_link, additional_races where arc_name=arl_name and ard_name=arl_name and arl_race_id=race_id and arl_source=ara_source and meeting_date >= '" + strStartDate + "' and arc_category='" + strCategory + "'";
    if (strSubCategory != null)
        strQuery += " and arc_subcategory='" + strSubCategory + "'";
    if (strEndDate != null)
        strQuery += " and meeting_date <= '" + strEndDate + "'";
    strQuery += " union";
    strQuery += " select arc_name, race_id, arl_source, meeting_date, race_name, ard_group_race, ard_course, ard_distance_yards, '' from additional_race_category arc, additional_race_data, additional_race_link, historic_races where arc_name=arl_name and ard_name=arl_name and arl_race_id=race_id and arl_source='SF' and meeting_date >= '" + strStartDate + "' and arc_category='" + strCategory + "'";
    if (strSubCategory != null)
        strQuery += " and arc_subcategory='" + strSubCategory + "'";
    if (strEndDate != null)
        strQuery += " and meeting_date <= '" + strEndDate + "'";
    if (bFixtures)
    {
        strQuery += " union";
        strQuery += " select arf_name, arf_previous_race_id, concat('FI-', arf_previous_race_source), arf_meeting_date, ard_name, ard_group_race, ard_course, ard_distance_yards, arf_previous_winner from additional_race_category arc, additional_race_data, additional_race_fixture arf where arc_name=arf_name and ard_name=arf_name and arf_meeting_date >= '" + strStartDate + "' and arc_category='" + strCategory + "' and arc_subcategory='" + strSubCategory + "'";
        if (strEndDate != null)
            strQuery += " and arf_meeting_date <= '" + strEndDate + "'";
    }
    strQuery += " ) d1";
    strQuery += " order by meeting_date";
   System.out.println(strQuery);
    List<AdditionalRaceInstance> alRaces = new ArrayList<AdditionalRaceInstance>();
    
    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            while (rs.next())
            {
                int nRace = rs.getInt("race_id");
                String strSource = rs.getString("source");
                Date dtMeeting = rs.getDate("meeting_date");
                AdditionalRaceInstance ari = new AdditionalRaceInstance(strSource, nRace, dtMeeting);
                ari.setTitle(rs.getString("race_title"));
                ari.setGroupRace(rs.getInt("ard_group_race"));
                ari.setDistanceYards(rs.getInt("ard_distance_yards"));
                ari.setCourse(rs.getString("ard_course"));
                ari.setWinner(rs.getString("winner"));    // will only be set for previous winner of fixture
                alRaces.add(ari);
             }
            rs.close();
        }
    }
    catch(SQLException e)
    {

    }

    return alRaces;
}
public static List<AdditionalRaceInstance> getRaceInstances(ENEStatement statement, String strWhere)
{
    String strQuery = "select ard_name, race_id, source, meeting_date, scheduled_time from (select ard_name, d.race_id, 'SF' as source, d.meeting_date, time(h.scheduled_time) as scheduled_time from daily_races d, historic_races h, additional_race_link arl, additional_race_data ard " + " where " + strWhere + " and h.race_id=d.race_id and arl_source='SF' and arl_race_id=d.race_id and arl_name=ard_name";
    strQuery += " union select ard_name, race_id, 'RP', meeting_date, scheduled_time from additional_races d inner join additional_race_link on race_id=arl_race_id and ara_source=arl_source inner join additional_race_data on arl_name=ard_name where ara_source='RP' and " + strWhere + ") d1";
    strQuery += " order by meeting_date, scheduled_time";
    
    System.out.println(strQuery);
    List<AdditionalRaceInstance> alRaces = new ArrayList<AdditionalRaceInstance>();
    
    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            while (rs.next())
            {
                int nRace = rs.getInt("race_id");
                String strSource = rs.getString("source");
                Date dtMeeting = rs.getDate("meeting_date");
                AdditionalRaceInstance ari = new AdditionalRaceInstance(strSource, nRace, dtMeeting);
                ari.setTitle(rs.getString("ard_name"));
                alRaces.add(ari);
             }
            rs.close();
        }
    }
    catch(SQLException e)
    {

    }

    return alRaces;
}
public static List<Integer> getRaceIds(ENEStatement statement, String strWhere, boolean bHistoric, boolean bARD)
{
    String strQuery = "select d.race_id from daily_races d" + (bHistoric ? ", historic_races h" : "") + (bARD ? ", additional_race_link arl" : "") + " where " + strWhere + (bHistoric ? " and h.race_id=d.race_id" : "meeting_status != \"Abandoned\"") + (bARD ? " and arl_race_id=d.race_id" : "");
    strQuery += " order by d.meeting_date, d.scheduled_time, d.course";
    
    List<Integer> alRaces = new ArrayList<Integer>();
    
    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            while (rs.next())
            {
                int nRace = rs.getInt("race_id");
                alRaces.add(nRace);
             }
            rs.close();
        }
    }
    catch(SQLException e)
    {

    }

    return alRaces;
}
public static List<Integer> getViewRaceIds(ENEStatement statement, String strView)
{
    String strQuery = "select race_id from " + strView;
    strQuery += " order by race_id";
    
    List<Integer> alRaces = new ArrayList<Integer>();
    
    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            while (rs.next())
            {
                int nRace = rs.getInt("race_id");
                alRaces.add(nRace);
             }
            rs.close();
        }
    }
    catch(SQLException e)
    {

    }

    return alRaces;
}
public static List<Integer> getPreviousRaceIds(ENEStatement statement, int nParentRace, boolean bDaily)
{
    String strQuery = "select pr.child_id as race_id from";
    if (bDaily)
        strQuery += " previous_race pr inner join daily_races d on child_id=race_id";
    else
        strQuery += " previous_race pr";
    strQuery += (" where parent_id = " + nParentRace);
    strQuery += " order by child_year desc";
    
    List<Integer> alRaces = new ArrayList<Integer>();
    
    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            while (rs.next())
            {
                int nRace = rs.getInt("race_id");
                alRaces.add(nRace);
             }
            rs.close();
        }
    }
    catch(SQLException e)
    {

    }

    return alRaces;
}
public static boolean insertPreviousRaceYear(ENEStatement statement, int nRace, int nPreviousRace)
{
    String strInsert = "insert into previous_race_year (race_id, previous_race_id, year) (select ";
    strInsert += (nRace + ", race_id, year(meeting_date) from historic_races where race_id=" + nPreviousRace + ")");

    int nRows = statement.executeUpdate(strInsert);
    
    return (nRows == 1);
}

public static int getRPRaceId(ENEStatement statement, String strCourse, String strScheduledTime)
{
    String strQuery = "select race_id from daily_races, racing_post_course where rp_course_name=course and rp_course_code='" + strCourse + "' and scheduled_time='" + strScheduledTime+ "'";
    
    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            while (rs.next())
            {
                int nRace = rs.getInt("race_id");
                return nRace;
             }
            rs.close();
        }
    }
    catch(SQLException e)
    {

    }

    return 0;
}
public static String getCurrentDateInterval(ENEStatement statement, int nInterval)
{
   String strQuery = "select cast(date_add(current_date, interval " + nInterval + " day) as char) from sporting_life_course where sl_course_name='Salisbury'";
    
    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            while (rs.next())
            {
                String strDate = rs.getString(1);
                return strDate;
             }
            rs.close();
        }
    }
    catch(SQLException e)
    {

    }

    return null;
}
public static ArrayList<String> getDateRunnerNames(ENEStatement statement, int nInterval)
{
    ArrayList<String> alDateNames = new ArrayList<String>();
   String strQuery = "select name, race_id, race_name, ara_source from (select name, a.race_id, race_name, 'SF' as ara_source, meeting_date from historic_runners u inner join historic_races a on u.race_id=a.race_id inner join career_horses on name=horse_name where meeting_date >= date_sub(current_date, interval " + nInterval + " day) and (unfinished is null or unfinished != 'Non-Runner')";    
   strQuery += " union ";
   strQuery += "select name, a.race_id, race_name, ara_source, meeting_date from additional_runners u inner join additional_races a on u.race_id=a.race_id inner join career_horses on name=horse_name where meeting_date >= date_sub(current_date, interval " + nInterval + " day)";
   strQuery += ") d1 order by name";
   try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            while (rs.next())
            {
                String strName = rs.getString(1);
                alDateNames.add(strName);
             }
            rs.close();
        }
    }
    catch(SQLException e)
    {

    }

    return alDateNames;
}
public static JSONArray getDateRunners(ENEStatement statement, ArrayList<String> alNames, int nInterval)
{
    JSONArray array = new JSONArray();
   String strQuery = "select name, race_id, race_name, ara_source from (select name, race_id, race_name, 'SF' as ara_source, meeting_date from historic_runners inner join historic_races using (race_id) where meeting_date >= date_sub(current_date, interval " + nInterval + " day) and (unfinished is null or unfinished != 'Non-Runner') and name in (" + SetUtils.toQuotedList(alNames) + ")";    
   strQuery += "union ";
   strQuery += "select name, race_id, race_name, ara_source, meeting_date from additional_runners inner join additional_races using (race_id) where meeting_date >= date_sub(current_date, interval " + nInterval + " day) and ara_source=aru_source and name in (" + SetUtils.toQuotedList(alNames) + ")";    
   strQuery += ") d1 order by meeting_date, race_id, name";
    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            while (rs.next())
            {
                String strName = rs.getString(1);
                int nRace = rs.getInt(2);
                String strRaceName = rs.getString(3);
                String strSource = rs.getString(4);
                JSONObject obj = new JSONObject();
                obj.put("name", strName);
                obj.put("xml", "<cell><race " + ("SF".equals(strSource) ? "" : ("source=\"" + strSource + "\" ")) + "title=\"" + SmartformBasicRace.getAbbreviatedTitle(strRaceName) + "\">" + nRace + "</race></cell>");
                array.put(obj);
             }
            rs.close();
        }
    }
    catch(SQLException e)
    {

    }

    return array;
}
public static List<Pair<String, Integer>> getMeetingRaceIds(ENEStatement statement, String strCourse, int nMonth, int nYear)
{
    
    ArrayList<Pair<String, Integer>> alRaces = new ArrayList<Pair<String, Integer>>();
    String strQuery = "select distinct concat(concat(date_format(meeting_date, '%W|%D %M'), '|'), min(coalesce(going, ''))) as date, race_id from historic_races inner join historic_runners using (race_id) where course='" + strCourse + "' and month(meeting_date)=" + nMonth + " and year(meeting_date)=" + nYear + " group by meeting_date, race_id order by meeting_date, scheduled_time";    
    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            while (rs.next())
            {
                String strDate = rs.getString(1);
                int nRace = rs.getInt(2);
                alRaces.add(new Pair<String,Integer>(strDate, nRace));
            }
            rs.close();
        }
    }
    catch(SQLException e)
    {

    }

    return alRaces;
}
public static List<Pair<String, Integer>> getMeetingRaceIds(ENEStatement statement, int nMeeting)
{
    
    ArrayList<Pair<String, Integer>> alRaces = new ArrayList<Pair<String, Integer>>();
    String strQuery = "select substr(scheduled_time, 12, 5) as time, race_id from historic_races where meeting_id=" + nMeeting + " order by scheduled_time";    
    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            while (rs.next())
            {
                String strDate = rs.getString(1);
                int nRace = rs.getInt(2);
                alRaces.add(new Pair<String,Integer>(strDate, nRace));
            }
            rs.close();
        }
    }
    catch(SQLException e)
    {

    }

    return alRaces;
}
public static String getMeetingTitle(ENEStatement statement, int nMeeting)
{
    String strQuery = "select distinct concat(course, ' - ', date_format(meeting_date, '%D %M'), ', ', going) as title from historic_races where meeting_id=" + nMeeting;    
    try
    {
        ResultSet rs = statement.executeQuery(strQuery);
        if (rs != null)
        {
            while (rs.next())
            {
                return rs.getString(1);
            }
            rs.close();
        }
    }
    catch(SQLException e)
    {

    }

    return null;
}
}