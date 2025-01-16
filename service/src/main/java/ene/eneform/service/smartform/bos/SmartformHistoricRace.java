/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.service.smartform.bos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Simon
 */
public class SmartformHistoricRace extends SmartformDailyRace
{
    private int m_nRaceNumber = 0;
    private int m_nFences = 0;
    private int m_nRunners = 0;
    private int m_nFinishers = 0;

    private String m_strShortTitle;
    private String m_strConditions;
    private int m_nTopRating = 0;

    private double m_dWinningTime = 0;
    private double m_dStandardTime = 0;
    private String m_strWinningTime;
    private String m_strStandardTime;

    private String m_strDirection;  // Left Handed, Right Handed, Straight, NULL to do: derive for Daily Race

    private List<SmartformHistoricRunner> m_lstSortedRunners = null;

    public SmartformHistoricRace(int nRace)
    {
        super(nRace);
    }

    public @Override boolean isHistoric()
    {
        return true;
    }

    public void setRaceNumber(int nRaceNumber)
    {
        m_nRaceNumber = nRaceNumber;   // number of race on the card
    }
    public void setDirection(String strDirection)
    {
        m_strDirection = strDirection;
    }
    public void setNrRunners(int nRunners)
    {
        m_nRunners = nRunners;
    }
    public void setNrFinishers(int nFinishers)
    {
        m_nFinishers = nFinishers;
    }
    public void setNrFences(int nFences)
    {
        m_nFences = nFences;
    }
    public void setMinimumAge(int nMinAge)
    {
        m_nMinAge = nMinAge;
    }
    public void setMaximumAge(int nMaxAge)
    {
        m_nMaxAge = nMaxAge;
    }
    public void setAllWeather(boolean bAllWeather)
    {
        m_bAllWeather = bAllWeather;
    }

    public void setShortTitle(String strShortTitle)
    {
        m_strShortTitle = strShortTitle;
    }
    public void setConditions(String strConditions)
    {
        m_strConditions = strConditions;
    }
     public void setTopRating(int nTopRating)
    {
        m_nTopRating = nTopRating;
    }
    public void setWinningTime(String strWinningTime)
    {
        m_strWinningTime = strWinningTime;
    }
     public void setStandardTime(String strStandardTime)
    {
        m_strStandardTime = strStandardTime;
    }
     public void setWinningTimeSeconds(double dWinningTime)
    {
        m_dWinningTime = dWinningTime;
    }
     public void setStandardTimeSeconds(double dStandardTime)
    {
        m_dStandardTime = dStandardTime;
    }
    public String getWinningTime()
    {
        return m_strWinningTime;
    }
    public String getStandardTime()
    {
        return m_strStandardTime;
    }
    public double getWinningTimeSeconds()
    {
        return m_dWinningTime;
    }
    public double getStandardTimeSeconds()
    {
        return m_dStandardTime;
    }
    public double getStandardTimeDifference()
    {
        return m_dWinningTime - m_dStandardTime;
    }
    
    public int getRaceNumber()
    {
        return m_nRaceNumber;   // number of race on the card
    }
    public @Override int getNrRunners()
    {
        return m_nRunners;
    }
    public int getNrFinishers()
    {
        return m_nFinishers;
    }
    public int getNrFences()
    {
        return m_nFences;
    }
    public String getDirection()
    {
        return m_strDirection;
    }
   public String getConditions()
    {
        return m_strConditions;
    }

    public String getShortTitle()
    {
        return m_strShortTitle;
    }

    public Iterator<SmartformHistoricRunner> getSortedRunnerIterator()
    {
        if (m_lstSortedRunners == null)
        {
            // copy and then sort
            m_lstSortedRunners = new ArrayList<SmartformHistoricRunner>();
            Iterator<SmartformColoursRunner> iter = m_lstRunners.iterator();
            while(iter.hasNext())
            {
                SmartformColoursRunner runner = (SmartformColoursRunner)iter.next();
                if (!runner.isNonRunner())
                    m_lstSortedRunners.add((SmartformHistoricRunner) runner);
            }

            Collections.sort(m_lstSortedRunners);
        }

        return m_lstSortedRunners.iterator();
    }

}
