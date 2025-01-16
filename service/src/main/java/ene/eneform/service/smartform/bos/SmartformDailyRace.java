/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.service.smartform.bos;

import java.util.StringTokenizer;
/**
 *
 * @author Simon
 */
public class SmartformDailyRace extends SmartformRace {

    protected String m_strAgeRange;
    protected SmartformLastWinner m_lastWinner= null;

    public SmartformDailyRace(int nRace)
    {
        super(nRace);
    }

    public void setSmartformLastWinner(SmartformLastWinner lastWinner)
    {
        m_lastWinner = lastWinner;
    }
   public SmartformLastWinner getSmartformLastWinner()
    {
        return m_lastWinner;
    }
    public void setAgeRange(String strAgeRange)
    {
        m_strAgeRange = strAgeRange;

        StringTokenizer st = new StringTokenizer(strAgeRange);
        int nTokens = st.countTokens();

        // derive values for m_nMinAge, m_nMaxAge
        if (nTokens == 1)
        {
            // NULL
        }
        else if (nTokens == 2)
        {
            // nYO only, nYO plus
            String strMinAge = st.nextToken();
            String strOperator = st.nextToken();
            if (strMinAge.indexOf("YO") > 0)
                m_nMinAge = Integer.parseInt(strMinAge.substring(0, strMinAge.indexOf("YO")));

            if ("only".equalsIgnoreCase(strOperator))
                m_nMaxAge = m_nMinAge;
        }
        else if (nTokens == 3)
        {
            // mYO & nYO, mYO to nYO
            String strMinAge = st.nextToken();
            String strOperator = st.nextToken();
            String strMaxAge = st.nextToken();

            if (strMinAge.indexOf("YO") > 0)
                m_nMinAge = Integer.parseInt(strMinAge.substring(0, strMaxAge.indexOf("YO")));
            if (strMaxAge.indexOf("YO") > 0)
                m_nMaxAge = Integer.parseInt(strMaxAge.substring(0, strMaxAge.indexOf("YO")));
        }

    }

    @Override public  void setTitle(String strTitle)
    {
        super.setTitle(strTitle);

        String strLowerTitle = strTitle.toLowerCase();
        // always derive values for m_bClaimer, m_bApprentice, m_bAmateur, m_bSeller, m_bMaiden
        if ((strLowerTitle.indexOf("claimer") >= 0) || (strLowerTitle.indexOf("claiming") >= 0))
            setClaimer(true);
        if (strLowerTitle.indexOf("maiden") >= 0)
            setMaiden(true);
        if ((strLowerTitle.indexOf("seller") >= 0) || (strLowerTitle.indexOf("selling") >= 0))
           setSeller(true);
        if (strLowerTitle.indexOf("amateur") >= 0)
            setAmateur(true);
        if (strLowerTitle.indexOf("apprentice") >= 0)
            setApprentice(true);

        if (strLowerTitle.indexOf("grade") >= 0)
        {
            if (strLowerTitle.indexOf("grade 1") >= 0)
            {
                setGroupRace(1);
            }
            else if(strLowerTitle.indexOf("grade 2") >= 0)
            {
                setGroupRace(2);
            }
            else if(strLowerTitle.indexOf("grade 3") >= 0)
            {
                setGroupRace(3);
            }
        }
        else if(strLowerTitle.indexOf("group") >= 0)
        {
           if (strLowerTitle.indexOf("group 1") >= 0)
            {
                 setGroupRace(1);
            }
            else if(strLowerTitle.indexOf("group 2") >= 0)
            {
                 setGroupRace(2);
            }
            else if(strLowerTitle.indexOf("group 3") >= 0)
            {
                setGroupRace(3);
            }
        }
    }
    
    @Override  public void setTrackType(String strTrackType)
    {
        super.setTrackType(strTrackType);
        // Turf, Fibresand, AllWeather, Dirt, Polytrack
        if (!"Turf".equals(strTrackType))
            m_bAllWeather = true;
    }

    public String getAgeRange()
    {
        return m_strAgeRange;
    }
}
