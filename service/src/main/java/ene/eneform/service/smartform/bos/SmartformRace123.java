/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.smartform.bos;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Simon
 */
public class SmartformRace123 {
    
    private SmartformHistoricRace m_race;
    private ArrayList<SmartformHistoricRunner> m_alRunners = new ArrayList<SmartformHistoricRunner>();
    
    public SmartformRace123(SmartformHistoricRace race, ArrayList<SmartformHistoricRunner> alRunners)
    {
        m_race = race;
        for(int i = 0; i < alRunners.size() && i < 3; i++)
        {
            m_alRunners.add(alRunners.get(i));
        }
    }
    public SmartformHistoricRace getRace()
    {
        return m_race;
    }
   public Iterator<SmartformHistoricRunner> getRunnerIterator()
    {
        return m_alRunners.iterator();
    }
}
