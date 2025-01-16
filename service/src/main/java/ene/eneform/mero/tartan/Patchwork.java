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
public class Patchwork extends ENEFabricItem {
    
    int m_nPatchHeight = 16;
    Color m_aColours[] = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW}; 
    int m_nColours = m_aColours.length;
    int m_aColourDisplay[][] = {
        {0, 1, 2, 3, 4},
        {4, 3, 1, 2, 0},
        {1, 2, 3, 0, 4},
        {2, 0, 4, 1, 3},
        {4, 2, 3, 0, 1}
    };
    int m_nRows = m_nPatchHeight * m_nColours;
    
	public void setData(String strData)
        {
            // no additional information required
        }
       protected void initialise()
       {
           // multi-coloured squares 
                 
           m_pixels = new Color[(m_nPatchHeight * m_nPatchHeight) * (m_nColours * m_nColours)];
           
            for(int i = 0; i < m_aColourDisplay.length; i++)
            {
                for(int j = 0; j < m_aColourDisplay[i].length; j++)
                {
                    int nStartingPoint = (i * m_nRows * m_nPatchHeight) + (j * m_nPatchHeight);
                    for(int k = 0; k < m_nPatchHeight; k++)
                    {
                        for(int l = 0; l < m_nPatchHeight; l++)
                        {
                            m_pixels[nStartingPoint + (k * m_nRows) + l] = m_aColours[m_aColourDisplay[i][j]];
                        }
                    }
                }
            }

                m_bInitialised = true;

                return;
            }
        public int getWidth()
        {
            return m_nPatchHeight * m_nColours;
        }
        public int getHeight()
        {
            return m_nPatchHeight * m_nColours;
        }
 	public @Override void setName(String strName)
	{
	        m_strName = strName;
                m_strResourceName = strName;
	}
}
