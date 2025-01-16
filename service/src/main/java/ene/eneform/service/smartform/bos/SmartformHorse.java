/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.service.smartform.bos;

import java.text.DateFormat;
import java.util.Calendar;

/**
 *
 * @author Simon
 */
public class SmartformHorse extends SmartformHorseDefinition {

    private int m_nRunner;

        // foaling_date, colour, gender, bred, dam_name, dam_year_born, sire_name, sire_year_born, dam_sire_name, dam_sire_year_born
    private Calendar m_calFoalingDate;
    SmartformHorseDefinition m_sire = null;
    SmartformHorseDefinition m_dam = null;
    SmartformHorseDefinition m_damsire = null;
    

    private SmartformTrainer m_trainer = null;
    private SmartformOwner m_owner = null;
    
    private String m_strBreeder = "";   // career_horses table only
    private Calendar m_calFinalRun = null;
    
    public SmartformHorse(int nRunner, String strName)
    {
        super(strName);
        m_nRunner = nRunner;
    }

   public SmartformHorse(int nRunner, String strName, Calendar calFoalingDate, String strBred, char cGender)
    {
        super(strName, calFoalingDate != null ? calFoalingDate.get(Calendar.YEAR) : 0, strBred, cGender);
        m_calFoalingDate = calFoalingDate;
        m_nRunner = nRunner;
    }

   public int getId()
    {
        return m_nRunner;
    }

    public Calendar getFoalingDate() {
        return m_calFoalingDate;
    }

    public void setFoalingDate(Calendar calFoalingDate) {
        this.m_calFoalingDate = calFoalingDate;
    }

    public void setSire(SmartformHorseDefinition sire)
    {
        m_sire = sire;
    }
    public SmartformHorseDefinition getSire()
    {
        return m_sire;
    }
    public void setDam(SmartformHorseDefinition dam)
    {
        m_dam = dam;
    }
    public SmartformHorseDefinition getDam()
    {
        return m_dam;
    }
    public void setDamSire(SmartformHorseDefinition damsire)
    {
        m_damsire = damsire;
    }
    public SmartformHorseDefinition getDamSire()
    {
        return m_damsire;
    }
    public String getDamSireName()
    {
        if (m_damsire != null)
            return m_damsire.getFullName();
        else if (m_dam != null)
            return m_dam.getSireName();
        
        return null;
    }
    public void setTrainer(SmartformTrainer trainer) {
        m_trainer = trainer;
    }
    public void setOwner(SmartformOwner owner) {
        m_owner = owner;
    }

    public SmartformTrainer getTrainer() {
        return m_trainer;
    }

   public SmartformOwner getOwner() {
        return m_owner;
    }
 

    public String getBreedingString()
    {
        String strBreeding = m_sire.getName() + " - " + m_dam.getName();

        return strBreeding;
    }
    public String getFullBreedingString()
    {
        String strBreeding = getBreedingString();
        if (!"".equals(m_damsire.getName()))
            strBreeding += (" (" + m_damsire.getName() + ")");

        return strBreeding;
    }
    public String getShortSexString()
    {
         String strSex = m_strColour + " " + m_cGender;

         return strSex;
     }
   @Override public String toString()
    {
        String strOutput = super.toString();
        if (m_sire != null)
            strOutput += ("Sire: " + m_sire.toString());
        if (m_dam != null)
            strOutput += ("Dam: " + m_dam.toString());
        
        return strOutput;
     }
   
    public String getFormattedFoalingDate()
    {
       return getFormattedFoalingDate(SmartformEnvironment.getInstance().getShortDateFormat());
    }
    public String getFormattedFoalingDate(DateFormat fmtDate)
    {
       if(m_calFoalingDate != null)
            return fmtDate.format(m_calFoalingDate.getTime());
        else
            return "";
    }
    public void setBreeder(String strBreeder)
    {
        m_strBreeder = strBreeder;
    }
    public String getBreeder()
    {
        return m_strBreeder;
    }
    public Calendar getFinalRun() {
        return m_calFinalRun;
    }

    public void setFinalRun(Calendar calFinalRun) {
        this.m_calFinalRun = calFinalRun;
    }
}
