/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.tartan;

import ene.eneform.mero.fabric.ENEFabricItem;
import java.awt.Color;

/**
 *
 * @author Simon
 */
public class Jazz extends ENEFabricItem {
    
    int m_nWidth = 10;
    Color m_aColours[] = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW}; 
    int m_nColours = m_aColours.length;
    int m_nRows = 1;
    
	public void setData(String strData)
        {
            // no additional information required
        }
       protected void initialise()
       {
           // multi-coloured squares 
                 
           m_pixels = new Color[m_nWidth * m_nColours];
           
            for(int i = 0; i < m_nColours; i++)
            {
                    int nStartingPoint = (i * m_nWidth);
                         for(int l = 0; l < m_nWidth; l++)
                        {
                            m_pixels[nStartingPoint + l] = m_aColours[i];
                        }
             }

                m_bInitialised = true;

                return;
            }
        public int getWidth()
        {
            return m_nWidth * m_nColours;
        }
        public int getHeight()
        {
            return m_nRows;
        }
 	public @Override void setName(String strName)
	{
	        m_strName = strName;
                m_strResourceName = strName;
	}
}
