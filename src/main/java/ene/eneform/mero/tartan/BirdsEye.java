/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.mero.tartan;

import ene.eneform.mero.config.ENEColoursEnvironment;
import ene.eneform.mero.fabric.ENEFabricItem;
import java.awt.Color;

/**
 *
 * @author Simon
 */
public class BirdsEye extends ENEFabricItem
{
    private Color m_colour1 = Color.WHITE;
    private Color m_colour2 = Color.BLUE;;

    private int m_nHeight = 14;
    private int m_nWidth = 16;

	public BirdsEye()
        {
        }

	public void setData(String strData)
        {
            String strColour1 = strData.split(",")[0].trim();
            String strColour2 = strData.split(",")[1].trim();

            if (ENEColoursEnvironment.getInstance().getColourItem(strColour1, ENEColoursEnvironment.DEFAULT_LANGUAGE) != null)
                m_colour1 = ENEColoursEnvironment.getInstance().getColourItem(strColour1, ENEColoursEnvironment.DEFAULT_LANGUAGE).getColour();
            if (ENEColoursEnvironment.getInstance().getColourItem(strColour2, ENEColoursEnvironment.DEFAULT_LANGUAGE) != null)
                m_colour2 = ENEColoursEnvironment.getInstance().getColourItem(strColour2, ENEColoursEnvironment.DEFAULT_LANGUAGE).getColour();
        }
  	public @Override void setName(String strName)
	{
	        m_strName = strName;
                m_strResourceName = strName;
	}
       protected void initialise()
        {
            // create m_pixels
                m_pixels = new Color[16 * 14];
                for(int i = 0; i < (16 * 14); i++)
                {
                    m_pixels[i] = m_colour1;
                }
       /*         int anPositions[][] = {
                    {1, 2, 3, 4, 11, 12, 13, 14},
                    {1, 2, 3, 4, 7, 8, 11, 12, 13, 14},
                    {0, 1, 2, 7, 8, 13, 14, 15},
                    {0, 1, 2, 5, 6, 7, 8, 9, 10, 13, 14, 15},
                    {0, 5, 6, 7, 8, 9, 10, 15},
                    {0, 3, 4, 5, 6, 9, 10, 11, 12, 15},
                    {3, 4, 5, 6, 9, 10, 11, 12}
                }; */
               int anPositions[][] = {
                    {2, 3, 4, 5, 8, 9, 10, 11},
                    {0, 1, 2, 3, 10, 11, 12, 13},
                    {0, 1, 2, 3, 10, 11, 12, 13},
                    {0, 1, 5, 6, 7, 8, 12, 13},
                    {0, 1, 5, 6, 7, 8, 12, 13},
                    {3, 4, 5, 6, 7, 8, 9, 10},
                    {3, 4, 5, 6, 7, 8, 9, 10},
                    {1, 2, 3, 4, 9, 10, 11, 12}
                };  // array works its way down column 1, 2 etc.
                Color[] arColors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};
                for(int i = 0; i < 16; i++)
                {
                    int nOffset = (i * 14);
                    int nRow = i;
                    if (i > 7)
                        nRow = (15 - i);
                    for (int j = 0; j < anPositions[nRow].length; j++)
                    {
                        m_pixels[nOffset + anPositions[nRow][j]] = m_colour2; // arColors[i%4];
                    }
                }

                m_bInitialised = true;

                return;
            }
        public int getWidth()
        {
            return m_nWidth;
        }
        public int getHeight()
        {
            return m_nHeight;
        }
}
