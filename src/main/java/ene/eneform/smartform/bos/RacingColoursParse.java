/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.smartform.bos;

/**
 *
 * @author Simon
 */
public class RacingColoursParse {
    
   private String m_strJacket;
    private String m_strSleeves;
    private String m_strCap;
    private String m_strUnresolved;
    public RacingColoursParse(String strJacket, String strSleeves, String strCap, String strUnresolved)
    {
        m_strJacket = strJacket;
        m_strSleeves = strSleeves;
        m_strCap=strCap;
        m_strUnresolved = strUnresolved;
    }

    public String getJacket() {
        return m_strJacket;
    }

    public void setJacket(String strJacket) {
        this.m_strJacket = strJacket;
    }

    public String getSleeves() {
        return m_strSleeves;
    }

    public void setSleeves(String strSleeves) {
        this.m_strSleeves = strSleeves;
    }

    public String getCap() {
        return m_strCap;
    }

    public void setCap(String strCap) {
        this.m_strCap = strCap;
    }
    public String getUnresolved() {
        return m_strUnresolved;
    }

    public void setUnresolved(String strUnresolved) {
        this.m_strJacket = strUnresolved;
    }
    public boolean equals(RacingColoursParse rcp)
    {
        return (m_strJacket.equals(rcp.getJacket())) && m_strSleeves.equals(rcp.getSleeves()) && m_strCap.equals(rcp.getCap()) && m_strUnresolved.equals(rcp.getUnresolved());
    }
}
