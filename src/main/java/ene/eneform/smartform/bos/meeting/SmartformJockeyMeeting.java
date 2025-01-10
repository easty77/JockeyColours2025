/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.smartform.bos.meeting;

import ene.eneform.smartform.bos.SmartformRunnerContainer;

/**
 *
 * @author Simon
 */
public class SmartformJockeyMeeting extends SmartformRunnerContainer{

    private int m_nJockey;
    private int m_nMeeting;

    public SmartformJockeyMeeting(int nJockey, int nMeeting)
    {
        m_nJockey = nJockey;
        m_nMeeting = nMeeting;
    }
    public int getJockeyId()
    {
        return m_nJockey;
    }
}
