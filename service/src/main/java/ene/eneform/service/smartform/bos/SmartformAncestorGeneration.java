/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.smartform.bos;

/**
 *
 * @author Simon
 */
public class SmartformAncestorGeneration {
    
   protected String m_strName;
    protected int m_nYearBorn = -1;
    protected String m_strBred = "";
    
    protected int m_nGeneration;
    
    protected int m_nBrilliance = 0;
    protected int m_nIntermediate = 0;
    protected int m_nClassic = 0;
    protected int m_nSolid = 0;
    protected int m_nProfessional = 0;
    
    public SmartformAncestorGeneration(String strName, int nYearBorn, String strBred, int nGeneration)
    {
        m_strName = strName;
        m_nYearBorn = nYearBorn;
        m_strBred = strBred;
        m_nGeneration = nGeneration;
    }
    public void setName(String strName)
    {
        m_strName = strName;
    }
    public void setYearBorn(int nYearBorn)
    {
        m_nYearBorn = nYearBorn;
    }
    public void setBred(String strBred)
    {
        m_strBred = strBred;
    }
    
    public String getName()
    {
        return m_strName;
    }
    public int getYearBorn()
    {
        return m_nYearBorn;
    }
    public String getBred()
    {
        return m_strBred;
    }

    public int getGeneration() {
        return m_nGeneration;
    }

    public int getBrilliance() {
        return m_nBrilliance;
    }

    public void setBrilliance(int nBrilliance) {
        this.m_nBrilliance = nBrilliance;
    }

    public int getIntermediate() {
        return m_nIntermediate;
    }

    public void setIntermediate(int nIntermediate) {
        this.m_nIntermediate = nIntermediate;
    }

    public int getClassic() {
        return m_nClassic;
    }

    public void setClassic(int nClassic) {
        this.m_nClassic = nClassic;
    }

    public int getSolid() {
        return m_nSolid;
    }

    public void setSolid(int nSolid) {
        this.m_nSolid = nSolid;
    }

    public int getProfessional() {
        return m_nProfessional;
    }

    public void setProfessional(int nProfessional) {
        this.m_nProfessional = nProfessional;
    }
}
