/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.smartform.bos;

/**
 *
 * @author Simon
 */
public class SmartformPedigree {

    private String m_strName;
    private String m_strBred;
    private int m_nYearBorn;
    private char m_cSex;          // F(emale) or M(ale)
    
    private char m_cAncestorType;
 
    private String m_strAncestorName;
    private String m_strAncestorBred;
    private int m_nAncestorYearBorn;

 
    public SmartformPedigree(String strName, String strBred, int nYearBorn, char cSex, char cAncestorType, String strAncestorName, String strAncestorBred, int nAncestorYearBorn)
    {
        m_strName = strName;
        m_strBred = strBred;
        m_nYearBorn = nYearBorn;
        m_cSex = cSex;
        m_cAncestorType = cAncestorType;
        m_strAncestorName = strAncestorName;
        m_strAncestorBred = strAncestorBred;
        m_nAncestorYearBorn = m_nAncestorYearBorn;
    }
    
    public String getName() {
        return m_strName;
    }

    public void setName(String strName) {
        m_strName = strName;
    }

    public String getBred() {
        return m_strBred;
    }

    public void setBred(String strBred) {
        m_strBred = strBred;
    }

    public int getYearBorn() {
        return m_nYearBorn;
    }

    public void setYearBorn(int nYearBorn) {
        m_nYearBorn = nYearBorn;
    }

    public char getSex() {
        return m_cSex;
    }

    public void setSex(char cSex) {
        m_cSex = cSex;
    }

    public String getAncestorName() {
        return m_strAncestorName;
    }

    public void setAncestorName(String strAncestorName) {
        m_strAncestorName = strAncestorName;
    }

    public String getAncestorBred() {
        return m_strAncestorBred;
    }

    public void setAncestorBred(String strAncestorBred) {
        m_strAncestorBred = strAncestorBred;
    }

    public int getAncestorYearBorn() {
        return m_nAncestorYearBorn;
    }

    public void setAncestorYearBorn(int nAncestorYearBorn) {
        m_nAncestorYearBorn = nAncestorYearBorn;
    }

    public char getAncestorType() {
        return m_cAncestorType;
    }

    public void setAncestorType(char cAncestorType) {
        m_cAncestorType = cAncestorType;
    }
 
    
}
