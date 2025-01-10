/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.career;

import ene.eneform.smartform.bos.SmartformHorse;
import ene.eneform.utils.ENEStatement;

/**
 *
 * @author Simon
 */
public class HorseCareerDefinition extends CareerDefinition{

    private SmartformHorse m_horse = null;
    
    public HorseCareerDefinition(String strId, String strName)
    {
        super(strId, strName, "horse");
       
    }
    @Override public void expand(ENEStatement statement)
    {
        
    }
    public SmartformHorse getHorse()
    {
        return m_horse;
    }
    public void setHorse(SmartformHorse horse)
    {
        m_horse = horse;
    }
    @Override public boolean isMeeting(){return false;}
    @Override public String getFileName(){return getName();}
}
