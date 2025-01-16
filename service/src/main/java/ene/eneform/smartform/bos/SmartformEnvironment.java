/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.smartform.bos;

import ene.eneform.smartform.bos.day.SmartformDay;
import ene.eneform.smartform.bos.meeting.SmartformMeeting;
import ene.eneform.smartform.factory.SmartformRaceFactory;
import ene.eneform.utils.ENEStatement;
import ene.eneform.utils.SmartformConnectionPool;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Simon
 */
public class SmartformEnvironment {

    private static SmartformEnvironment sm_environment = null;
    private static final SimpleDateFormat sm_gmtDate;
    private static final SimpleDateFormat sm_gmtShortDate;
    private static final SimpleDateFormat sm_gmtTime;
    private static final SimpleDateFormat sm_gmtSQLDate;
    private static final SimpleDateFormat sm_gmtYear;
    private static final SimpleDateFormat sm_gmtMonth;
    private static final SimpleDateFormat sm_gmtDay;

    static
    {
        TimeZone tz = TimeZone.getTimeZone("GMT+1:00");
        sm_gmtDate = new SimpleDateFormat( "EEEE, MMMM d yyyy" );
        sm_gmtDate.setTimeZone( tz );
        sm_gmtShortDate = new SimpleDateFormat( "MMM d yyyy" );
        sm_gmtShortDate.setTimeZone( tz );
        sm_gmtTime = new SimpleDateFormat( "HH:mm" );
        sm_gmtTime.setTimeZone( tz );
        sm_gmtSQLDate = new SimpleDateFormat( "yyyy-MM-dd" );
        sm_gmtSQLDate.setTimeZone( tz );
        sm_gmtYear = new SimpleDateFormat( "yyyy" );
        sm_gmtYear.setTimeZone( tz );
        sm_gmtMonth = new SimpleDateFormat( "MM" );
        sm_gmtMonth.setTimeZone( tz );
        sm_gmtDay = new SimpleDateFormat( "dd" );
        sm_gmtDay.setTimeZone( tz );
    }
    HashMap<Integer,SmartformMeeting> m_hmMeetings = new HashMap<Integer,SmartformMeeting>();
    HashMap<Integer, SmartformRace> m_hmRaces = new HashMap<Integer, SmartformRace>();  // referenced by Smartform Race Id
    HashMap<Integer, SmartformRace> m_hmBetfairRaces = new HashMap<Integer, SmartformRace>();  // referenced by Betfair Race Id
    HashMap<String, SmartformDay> m_hmDays = new HashMap<String, SmartformDay>();

    private HashMap<Integer,SmartformTrainer> m_hmTrainers = new HashMap<Integer,SmartformTrainer>();
    private HashMap<Integer,SmartformOwner> m_hmOwners = new HashMap<Integer,SmartformOwner>();
    private HashMap<Integer,SmartformJockey> m_hmJockeys = new HashMap<Integer,SmartformJockey>();

    public static SmartformEnvironment getInstance()
    {
        if (sm_environment == null)
            sm_environment = new SmartformEnvironment();

        return sm_environment;
    }

    private SmartformEnvironment()
    {
   }
    public DateFormat getDateFormat()
    {
        return sm_gmtDate;
    }
    public DateFormat getShortDateFormat()
    {
        return sm_gmtShortDate;
    }
    public DateFormat getSQLDateFormat()
    {
        return sm_gmtSQLDate;
    }
    public DateFormat getTimeFormat()
    {
        return sm_gmtTime;
    }
    public DateFormat getYearFormat()
    {
        return sm_gmtYear;
    }
    public DateFormat getMonthFormat()
    {
        return sm_gmtYear;
    }
    public DateFormat getDayFormat()
    {
        return sm_gmtYear;
    }

    public SmartformRace getSmartformRace(String strRace)
    {
        try
        {
            int nRace = Integer.valueOf(strRace);
            return getSmartformRace(nRace);
        }
        catch(NumberFormatException e)
        {

        }

        return null;
    }
    public SmartformRace getSmartformRace(int nRace)
    {
        Integer intRace = new Integer(nRace);
        if (m_hmRaces.containsKey(intRace))
            return m_hmRaces.get(intRace);
        else
        {
           ENEStatement statement = SmartformConnectionPool.getInstance().getENEStatement();
            SmartformRace race = SmartformRaceFactory.createSmartformRace(statement, nRace, "SF");
            statement.close();
            m_hmRaces.put(intRace, race);
            return race;
        }
    }

    public SmartformRace getBetfairSmartformRace(int nMarket)
    {
        Integer intMarket = Integer.valueOf(nMarket);
        if (m_hmBetfairRaces.containsKey(intMarket))
            return m_hmBetfairRaces.get(intMarket);
        else
        {
           ENEStatement statement = SmartformConnectionPool.getInstance().getENEStatement();
            SmartformDailyRace race = SmartformRaceFactory.createBetfairDailyRace(statement, nMarket);     // will be null, if Betfair Market has no Smartform equivalent
            statement.close();
            m_hmBetfairRaces.put(intMarket, race);
            m_hmRaces.put(Integer.valueOf(race.getRaceId()), race);
            return race;
        }
    }

    public SmartformMeeting getSmartformMeeting(String strMeeting)
    {
        try
        {
            int nMeeting = Integer.valueOf(strMeeting);
            return getSmartformMeeting(nMeeting);
        }
        catch(NumberFormatException e)
        {

        }

        return null;
    }

    public SmartformMeeting getSmartformMeeting(int nMeeting)
    {
        Integer intMeeting = Integer.valueOf(nMeeting);
        if (m_hmMeetings.containsKey(intMeeting))
            return m_hmMeetings.get(intMeeting);
        else
        {
            ENEStatement statement = SmartformConnectionPool.getInstance().getENEStatement();
            SmartformDay day = SmartformRaceFactory.createSmartformDayByMeeting(statement, nMeeting);
            statement.close();
            processSmartformDay(day);
            SmartformMeeting meeting = day.getMeeting(nMeeting);
            return meeting;
        }
    }

    public SmartformDay getTodaysData()
    {
        Date today = new Date();
        return getDateData(today);
    }

    public SmartformDay getDayData(int nDayOffset)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, nDayOffset);
        return getDateData(calendar.getTime());
    }

    public SmartformDay getDateData(Date date)
    {
        String strDate = SmartformEnvironment.getInstance().getDateFormat().format(date);

        if (m_hmDays.containsKey(strDate))
            return m_hmDays.get(strDate);
        else
        {
            ENEStatement statement = SmartformConnectionPool.getInstance().getENEStatement();
            SmartformDay objDate = SmartformRaceFactory.createSmartformDayByDate(statement, date);
            statement.close();
            processSmartformDay(objDate);

            return objDate;
        }
    }

    private void processSmartformDay(SmartformDay day)
    {
        String strDate = SmartformEnvironment.getInstance().getDateFormat().format(day.getDate());

        m_hmDays.put(strDate, day);
        Iterator<SmartformMeeting> iterMeeting = day.getMeetingIterator();
        while(iterMeeting.hasNext())
        {
            SmartformMeeting meeting = iterMeeting.next();
            m_hmMeetings.put(meeting.getMeetingId(), meeting);
            Iterator<SmartformRace> iterRace = meeting.getRaceIterator();
            while(iterRace.hasNext())
            {
                SmartformRace race = iterRace.next();
                m_hmRaces.put(Integer.valueOf(race.getRaceId()), race);
                m_hmBetfairRaces.put(Integer.valueOf(race.getBetfairId()), race);
          }
        }
    }

     public Iterator<SmartformJockey> getJockeyIterator()
    {
        return m_hmJockeys.values().iterator();
    }
    public Iterator<SmartformTrainer> getTrainerIterator()
    {
        return m_hmTrainers.values().iterator();
    }
    public Iterator<SmartformOwner> getOwnerIterator()
    {
        return m_hmOwners.values().iterator();
    }
    public SmartformJockey getJockey(int nJockey)
    {
        return m_hmJockeys.get(Integer.valueOf(nJockey));
    }
    public SmartformTrainer getTrainer(int nTrainer)
    {
        return m_hmTrainers.get(Integer.valueOf(nTrainer));
    }
    public SmartformOwner getOwner(int nOwner)
    {
        return m_hmOwners.get(Integer.valueOf(nOwner));
    }
    public void addTrainer(SmartformTrainer trainer)
    {
        m_hmTrainers.put(trainer.getTrainerId(), trainer);
    }
    public void addJockey(SmartformJockey jockey)
    {
        m_hmJockeys.put(jockey.getJockeyId(), jockey);
    }
    public void addOwner(SmartformOwner owner)
    {
        m_hmOwners.put(owner.getOwnerId(), owner);
    }

    public void reset()
    {
        m_hmMeetings = new HashMap<Integer,SmartformMeeting>();
        m_hmRaces = new HashMap<Integer, SmartformRace>();  
        m_hmBetfairRaces = new HashMap<Integer, SmartformRace>();  
        m_hmDays = new HashMap<String, SmartformDay>();

        m_hmTrainers = new HashMap<Integer,SmartformTrainer>();
        m_hmOwners = new HashMap<Integer,SmartformOwner>();
        m_hmJockeys = new HashMap<Integer,SmartformJockey>();
    }
}
