/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.service.smartform.bos.day;

import ene.eneform.service.smartform.bos.SmartformEnvironment;
import ene.eneform.service.smartform.bos.meeting.SmartformMeeting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Simon
 */
public class SmartformDay {

    private Date m_dtDaily;
    private ArrayList<SmartformMeeting> m_lstMeetings = new ArrayList<SmartformMeeting>();
    private HashMap<Integer,SmartformTrainerDay> m_hmTrainers = new HashMap<Integer,SmartformTrainerDay>();
    private HashMap<Integer,SmartformOwnerDay> m_hmOwners = new HashMap<Integer,SmartformOwnerDay>();
    private HashMap<Integer,SmartformJockeyDay> m_hmJockeys = new HashMap<Integer,SmartformJockeyDay>();

    public SmartformDay(Date dtDaily)
    {
        m_dtDaily = dtDaily;
    }

    public Date getDate()
    {
        return m_dtDaily;
    }

    public String getFormattedDate()
    {
        return SmartformEnvironment.getInstance().getDateFormat().format(m_dtDaily);
    }

    public int addMeeting(SmartformMeeting meeting)
    {
        m_lstMeetings.add(meeting);

        return m_lstMeetings.size();
    }

    public Iterator<SmartformMeeting> getMeetingIterator()
    {
        return m_lstMeetings.iterator();
    }
    public SmartformMeeting getMeeting(int nMeeting)
    {
        Iterator<SmartformMeeting> iter = m_lstMeetings.iterator();
        while(iter.hasNext())
        {
            SmartformMeeting meeting = iter.next();
            if (meeting.getMeetingId() == nMeeting)
                return meeting;
        }

        return null;
    }
    public Iterator<SmartformJockeyDay> getJockeyDayIterator()
    {
        return m_hmJockeys.values().iterator();
    }
    public Iterator<SmartformTrainerDay> getTrainerDayIterator()
    {
        return m_hmTrainers.values().iterator();
    }
    public Iterator<SmartformOwnerDay> getOwnerDayIterator()
    {
        return m_hmOwners.values().iterator();
    }
    public SmartformJockeyDay getJockeyDay(int nJockey)
    {
        return m_hmJockeys.get(Integer.valueOf(nJockey));
    }
    public SmartformTrainerDay getTrainerDay(int nTrainer)
    {
        return m_hmTrainers.get(Integer.valueOf(nTrainer));
    }
    public SmartformOwnerDay getOwnerDay(int nOwner)
    {
        return m_hmOwners.get(Integer.valueOf(nOwner));
    }
    public void addTrainerDay(SmartformTrainerDay trainer)
    {
        m_hmTrainers.put(trainer.getTrainerId(), trainer);
    }
    public void addJockeyDay(SmartformJockeyDay jockey)
    {
        m_hmJockeys.put(jockey.getJockeyId(), jockey);
    }
    public void addOwnerDay(SmartformOwnerDay owner)
    {
        m_hmOwners.put(owner.getOwnerId(), owner);
    }
}
