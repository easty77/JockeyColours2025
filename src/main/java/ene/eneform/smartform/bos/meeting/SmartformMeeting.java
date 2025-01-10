/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.smartform.bos.meeting;

import ene.eneform.smartform.bos.SmartformEnvironment;
import ene.eneform.smartform.bos.SmartformRace;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Simon
 */
public class SmartformMeeting {

    protected int m_nMeeting;
    protected String m_strCourse;

    private ArrayList<SmartformRace> m_lstRaces = new ArrayList<SmartformRace>();

    private HashMap<Integer,SmartformTrainerMeeting> m_hmTrainers = new HashMap<Integer,SmartformTrainerMeeting>();
    private HashMap<Integer,SmartformOwnerMeeting> m_hmOwners = new HashMap<Integer,SmartformOwnerMeeting>();
    private HashMap<Integer,SmartformJockeyMeeting> m_hmJockeys = new HashMap<Integer,SmartformJockeyMeeting>();

    protected Date m_dtMeeting;
    private String m_strMeetingStatus;
    private String m_strDrawAdvantage;
    private String m_strMeetingAbandonedReason;
    private String m_strCountry;
    private String m_strWeather;

    public SmartformMeeting(int nMeeting)
    {
        m_nMeeting = nMeeting;
    }

    public void setCourse(String strCourse)
    {
        m_strCourse = strCourse;
    }

    public int addRace(SmartformRace race)
    {
        m_lstRaces.add(race);

        return m_lstRaces.size();
    }

    public int getMeetingId()
    {
        return m_nMeeting;
    }

    public String getCourse()
    {
        return m_strCourse;
    }

    public Iterator<SmartformRace> getRaceIterator()
    {
        return m_lstRaces.iterator();
    }
    public Iterator<SmartformJockeyMeeting> getJockeyMeetingIterator()
    {
        return m_hmJockeys.values().iterator();
    }
    public Iterator<SmartformTrainerMeeting> getTrainerMeetingIterator()
    {
        return m_hmTrainers.values().iterator();
    }
    public Iterator<SmartformOwnerMeeting> getOwnerMeetingIterator()
    {
        return m_hmOwners.values().iterator();
    }
    public SmartformRace getRace(int nRace)
    {
        Iterator<SmartformRace> iter = m_lstRaces.iterator();
        while(iter.hasNext())
        {
            SmartformRace race = iter.next();
            if (race.getRaceId() == nRace)
                return race;
        }

        return null;
    }
    public SmartformJockeyMeeting getJockeyMeeting(int nJockey)
    {
        return m_hmJockeys.get(Integer.valueOf(nJockey));
    }
    public SmartformTrainerMeeting getTrainerMeeting(int nTrainer)
    {
        return m_hmTrainers.get(Integer.valueOf(nTrainer));
    }
    public SmartformOwnerMeeting getOwnerMeeting(int nOwner)
    {
        return m_hmOwners.get(Integer.valueOf(nOwner));
    }
    public void addTrainerMeeting(SmartformTrainerMeeting trainer)
    {
        m_hmTrainers.put(trainer.getTrainerId(), trainer);
    }
    public void addJockeyMeeting(SmartformJockeyMeeting jockey)
    {
        m_hmJockeys.put(jockey.getJockeyId(), jockey);
    }
    public void addOwnerMeeting(SmartformOwnerMeeting owner)
    {
        m_hmOwners.put(owner.getOwnerId(), owner);
    }

    public void setMeetingDate(long lMilliseconds)
    {
        m_dtMeeting = new Date(lMilliseconds);
    }
    public void setMeetingStatus(String strMeetingStatus)
    {
        m_strMeetingStatus = strMeetingStatus;
    }
    public void setDrawAdvantage(String strDrawAdvantage)
    {
        m_strDrawAdvantage = strDrawAdvantage;
    }
    public void setMeetingAbandonedReason(String strMeetingAbandonedReason)
    {
        m_strMeetingAbandonedReason = strMeetingAbandonedReason;
    }
    public void setCountry(String strCountry)
    {
        m_strCountry = strCountry;
    }
    public void setWeather(String strWeather)
    {
        m_strWeather = strWeather;
    }

    public Date getMeetingDate()
    {
        return m_dtMeeting;
    }
    public String getFormattedMeetingDate()
    {
        return SmartformEnvironment.getInstance().getDateFormat().format(m_dtMeeting);
    }
    public String getMeetingStatus()
    {
        return m_strMeetingStatus;
    }
    public String getDrawAdvantage()
    {
        return m_strDrawAdvantage;
    }
    public String getMeetingAbandonedReason()
    {
        return m_strMeetingAbandonedReason;
    }
    public String getCountry()
    {
        return m_strCountry;
    }
    public String getWeather()
    {
        return m_strWeather;
    }
}
