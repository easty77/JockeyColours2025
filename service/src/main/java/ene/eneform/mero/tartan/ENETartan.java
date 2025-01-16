/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.mero.tartan;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 *
 * @author Simon
 */
public class ENETartan implements Serializable{

    public String extendSett() {
        String strSett = m_strSett;
        if (m_nPivots > 0)
        {
            StringTokenizer st = new StringTokenizer(m_strSett);
            String[] bandColors = new String[st.countTokens() / 2];
            int[] bandThreads = new int[st.countTokens() / 2];
            int bandCount = 0;
            while (st.countTokens() > 1) {
                String strColour = st.nextToken();
                int nThreads = Integer.valueOf(st.nextToken()).intValue();
                bandColors[bandCount] = strColour;
                bandThreads[bandCount] = nThreads;
                bandCount++;
            }
            for (int i = bandColors.length - 1; i > 0; i--) {
                strSett += " " + bandColors[i] + " " + bandThreads[i];
            }
        }
        
        return strSett;
    }

    private String m_strId;
    private int m_nPivots;
    private String m_strSett;
    private double m_dScaleFactor;

    public ENETartan(String strId, String strSett, int nPivots, double dScaleFactor)
    {
        m_strId = strId;
        m_strSett = strSett;
        m_nPivots = nPivots;
        m_dScaleFactor = dScaleFactor;
    }

	public String getId()
	{
	        return m_strId;
	}

        public String getSett()
	{
	        return m_strSett;
	}

        public int getNrPivots()
        {
            return m_nPivots;
        }

        public double getScaleFactor()
        {
            return m_dScaleFactor;
        }
}
