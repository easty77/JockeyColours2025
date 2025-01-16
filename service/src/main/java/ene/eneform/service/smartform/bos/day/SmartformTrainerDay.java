/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.service.smartform.bos.day;

import ene.eneform.service.smartform.bos.SmartformRunnerContainer;

import java.util.Date;
/**
 *
 * @author Simon
 */
public class SmartformTrainerDay extends SmartformRunnerContainer{

    private int m_nTrainer;
    private Date m_dtTrainer;

    public SmartformTrainerDay(int nTrainer, Date dtTrainer)
    {
        m_nTrainer = nTrainer;
        m_dtTrainer = dtTrainer;
    }
    public int getTrainerId()
    {
        return m_nTrainer;
    }
}
