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
public abstract class SmartformRunnerContainer {

    protected ArrayList<SmartformColoursRunner> m_lstRunners = new ArrayList<SmartformColoursRunner>();

    public void addRunner(SmartformColoursRunner runner)
    {
        m_lstRunners.add(runner);
    }
    public Iterator<SmartformColoursRunner> getRunnerIterator()
    {
        return m_lstRunners.iterator();
    }
   public SmartformColoursRunner getRunner(int nRunner)
    {
        Iterator<SmartformColoursRunner> iter = m_lstRunners.iterator();
        while(iter.hasNext())
        {
            SmartformColoursRunner runner = iter.next();
            if (runner.getRunnerId() == nRunner)
                return runner;
        }

        return null;
    } 
   public SmartformColoursRunner getRunnerByName(String strName)
    {
        Iterator<SmartformColoursRunner> iter = m_lstRunners.iterator();
        while(iter.hasNext())
        {
            SmartformColoursRunner runner = iter.next();
            if (runner.getName().equalsIgnoreCase(strName))
                return runner;
        }

        return null;
    }
   public int getNrRunners()
   {
       return m_lstRunners.size();
   }
}
