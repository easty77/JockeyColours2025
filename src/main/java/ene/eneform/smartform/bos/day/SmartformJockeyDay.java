/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.smartform.bos.day;

import ene.eneform.smartform.bos.SmartformRunnerContainer;

import java.util.Date;

/**
 *
 * @author Simon
 */
public class SmartformJockeyDay extends SmartformRunnerContainer{

    private int m_nJockey;
    private Date m_dtJockey;

    public SmartformJockeyDay(int nJockey, Date dtJockey)
    {
        m_nJockey = nJockey;
        m_dtJockey = dtJockey;
    }
    public int getJockeyId()
    {
        return m_nJockey;
    }
}
