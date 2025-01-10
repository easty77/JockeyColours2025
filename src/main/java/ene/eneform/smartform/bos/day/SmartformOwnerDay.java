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
public class SmartformOwnerDay extends SmartformRunnerContainer{
    private int m_nOwner;
    private Date m_dtOwner;

    public SmartformOwnerDay(int nOwner, Date dtOwner)
    {
        m_nOwner = nOwner;
        m_dtOwner = dtOwner;
    }
    public int getOwnerId()
    {
        return m_nOwner;
    }
}
