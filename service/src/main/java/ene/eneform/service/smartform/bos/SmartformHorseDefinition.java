/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.smartform.bos;

/**
 *
 * @author Simon
 */
public class SmartformHorseDefinition {
    
    protected String m_strName;
    protected int m_nYearBorn = -1;
    protected String m_strBred = "";

    protected String m_strColour = "";
    protected char m_cGender=' ';

    // not really the right place??
    protected String m_strChefDeRace = "";
    protected double m_dGRASP = -1.0;

    protected String m_strPQCode = "";
    
    protected String m_strSireName = "";        // for dam sire of historic_runners records
    
    private String m_strFullName;
    private String m_strLongColour="";
    private String m_strLongGender="";

    protected SmartformHorseDefinition(String strName)  // used by SmartformHorse
    {
        m_strName = strName;
        m_strFullName = strName;
     }
    public SmartformHorseDefinition(String strName, int nYearBorn, String strBred, char cGender)
    {
        m_strName = strName;
        m_strFullName = strName;
        m_nYearBorn = nYearBorn;
        if (strBred != null)
            setBred(strBred);
        setGender(cGender);
    }
    public SmartformHorseDefinition(String strName, int nYearBorn, String strBred)
    {
        this(strName, nYearBorn, strBred, ' ');
    }
   public void setName(String strName)
    {
        m_strName = strName;
    }
    public void setYearBorn(int nYearBorn)
    {
        m_nYearBorn = nYearBorn;
    }
    public final void setBred(String strBred) {
        this.m_strBred = strBred;
        if ((!"".equals(strBred)) && (!"GB".equals(strBred)))
            m_strFullName = m_strName + " (" + m_strBred + ")";
    }

    public void setPQCode(String strPQCode)
    {
        m_strPQCode = strPQCode;
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
    public String getPQCode()
    {
        return m_strPQCode;
    }
     public char getGender() {
        return m_cGender;
    }

    public String getColour() {
        return m_strColour;
    }

   public String getChefDeRace() {
        return m_strChefDeRace;
    }

    public void setChefDeRace(String strChefDeRace) {
        this.m_strChefDeRace = strChefDeRace;
    }
    public double getGRASP() {
        return m_dGRASP;
    }

    public void setGRASP(double dGRASP) {
        this.m_dGRASP = dGRASP;
    }
    
    public void setSireName(String strSireName) {
        this.m_strSireName = strSireName;
    }

   public String getSireName() {
        return m_strSireName;
    }
    @Override public String toString()
    {
        String strOutput = m_strName + " " + m_strBred;
        strOutput += " ";
        if (m_nYearBorn != -1)
            strOutput += (" " + m_nYearBorn);
        strOutput += (" " + m_strChefDeRace);
        strOutput += " ";
        if (m_dGRASP != -1.0)
            strOutput += (" " + m_dGRASP);
        
         return strOutput;
    }
    public String getKey()
    {
        String strKey = m_strName + "-" + m_strBred;
        if (m_nYearBorn > 0)
            strKey += ("-" + m_nYearBorn);
        
        return strKey;
    }
    public String getLongColour() {
        return m_strLongColour;
    }
    public final void setColour(String strColour) {
        this.m_strColour = strColour;

        if ("b".equals(strColour))
            m_strLongColour="Bay";
        else if("bl".equals(strColour))
            m_strLongColour="Black";
        else if("br".equals(strColour))
            m_strLongColour="Brown";
        else if("gr".equals(strColour) || ("g".equals(strColour)))
            m_strLongColour="Grey";
        else if("wh".equals(strColour))
            m_strLongColour="White";
        else if("ch".equals(strColour))
            m_strLongColour="Chestnut";
        else if(("ro".equals(strColour)) || ("rn".equals(strColour)))
            m_strLongColour="Roan";
         else if("sk".equals(strColour))
            m_strLongColour="Skuebald";
       else
            m_strLongColour = strColour;
    }
   public String getLongGender() {
        return m_strLongGender;
    }

    public final void setGender(char cGender) {
        m_cGender = cGender;

        if (cGender == 'g')
            m_strLongGender="Gelding";
        else if(cGender == 'c')
            m_strLongGender="Colt";
        else if(cGender == 'f')
            m_strLongGender="Filly";
        else if(cGender == 'm')
            m_strLongGender="Mare";
        else if(cGender == 'h')
            m_strLongGender="Horse";
        else if(cGender == 'r')
            m_strLongGender="Rig";
        else
            m_strLongGender = Character.toString(cGender);
    }
    public String getFullName()
    {
        return m_strFullName;
    }
    public String getSexString()
    {
         String strSex = m_strLongColour + " " + m_strLongGender;

         return strSex;
     }
}
