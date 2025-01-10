/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.smartform.bos;

/**
 *
 * @author Simon
 */
public class SmartformOwner  extends SmartformRunnerContainer {

    private int m_nOwner;
    private String m_strName;

    public SmartformOwner(int nOwner)
    {
        m_nOwner = nOwner;
    }

    public void setName(String strName)
    {
        m_strName = strName;
    }
    public int getOwnerId()
    {
        return m_nOwner;
    }
    public String getName()
    {
        return m_strName;
    }
}
