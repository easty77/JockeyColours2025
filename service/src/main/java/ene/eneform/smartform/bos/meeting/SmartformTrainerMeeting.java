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
public class SmartformTrainerMeeting extends SmartformRunnerContainer{

    private int m_nTrainer;
    private int m_nMeeting;

    public SmartformTrainerMeeting(int nTrainer, int nMeeting)
    {
        m_nTrainer = nTrainer;
        m_nMeeting = nMeeting;
    }
    public int getTrainerId()
    {
        return m_nTrainer;
    }
}
