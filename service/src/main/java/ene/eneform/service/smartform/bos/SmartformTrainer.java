/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.service.smartform.bos;

/**
 *
 * @author Simon
 */
public class SmartformTrainer extends SmartformRunnerContainer {

    private int m_nTrainer;
    private String m_strName;

    public SmartformTrainer(int nTrainer, String strName)
    {
        m_nTrainer = nTrainer;
        m_strName = strName;
    }

    public int getTrainerId()
    {
        return m_nTrainer;
    }
    public String getName()
    {
        return m_strName;
    }
}
