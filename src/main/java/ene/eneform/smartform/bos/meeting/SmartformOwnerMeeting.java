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
public class SmartformOwnerMeeting extends SmartformRunnerContainer{

    private int m_nOwner;
    private int m_nMeeting;

    public SmartformOwnerMeeting(int nOwner, int nMeeting)
    {
        m_nOwner = nOwner;
        m_nMeeting = nMeeting;
    }
    public int getOwnerId()
    {
        return m_nOwner;
    }
}
