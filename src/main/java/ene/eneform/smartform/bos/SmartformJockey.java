/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.smartform.bos;


/**
 *
 * @author Simon
 */
public class SmartformJockey extends SmartformRunnerContainer {

    private int m_nJockey;
    private String m_strName;
    private int m_nClaim=0;

    public SmartformJockey(int nJockey, String strName)
    {
        m_nJockey = nJockey;
        m_strName = strName;
    }

    public void setClaim(int nClaim)
    {
        m_nClaim = nClaim;
    }
    public int getJockeyId()
    {
        return m_nJockey;
    }
    public String getName()
    {
        return m_strName;
    }
    public int getClaim()
    {
        return m_nClaim;
    }
    public String getJockeyString()
    {
        String strJockey = m_strName;
        if (m_nClaim > 0)
            strJockey += ("(" + m_nClaim + ")");
        
        return strJockey;
    }
}

