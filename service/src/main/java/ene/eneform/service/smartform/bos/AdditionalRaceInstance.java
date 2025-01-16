/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.smartform.bos;

import org.json.JSONObject;

import java.util.Date;

/**
 *
 * @author Simon
 */
public class AdditionalRaceInstance extends SmartformBasicRace {

    protected String m_strSource; // SL (Sporting Life)  or SF (SmartForm)
     
     
    public AdditionalRaceInstance(String strSource, int nRace, Date dtRace)
    {
        super(nRace, dtRace);
        m_strSource = strSource;
     }
   public String getSource() {
        return m_strSource;
    }
    public JSONObject toJSON(String strDateFormat) {
        // ari contains race_id, source, meeting_date, title
        JSONObject obj = new JSONObject();
        obj.put("id", getRaceId());
        obj.put("source", getSource());
        obj.put("date", getFormattedMeetingDate(strDateFormat));
        obj.put("title", getTitle());
        obj.put("grade", (getGroupRace() == 0) ? "ungraded" : "group" + getGroupRace());
        obj.put("course", getCourse().replace("_", " "));
        obj.put("distance", getLongFormattedDistance(true).trim());
        obj.put("winner", getWinner());
        if (getRaceId() > 0) {
            obj.put("going", getGoing());
            obj.put("nr_runners", getNrRunners());
            obj.put("age_range", getAgeRange());
            obj.put("sex", getSex());
        }
        return obj;
    }
}
