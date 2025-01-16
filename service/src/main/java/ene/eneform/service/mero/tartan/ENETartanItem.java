package ene.eneform.service.mero.tartan;

import ene.eneform.service.mero.fabric.ENEFabricItem;
import ene.eneform.service.mero.config.ENEColoursEnvironment;

import java.awt.Color;
import java.util.StringTokenizer;

/**
 *
 * @author Simon
 */

public class ENETartanItem extends ENEFabricItem {
    private final ENEColoursEnvironment environment;

	private boolean m_bDarkened = true;
	private int m_nPivots;
	private String m_strSett;
        private int m_nThreadCount = 0;

        public ENETartanItem(ENEColoursEnvironment environment, ENETartan tartan)
        {
            this.environment = environment;
            m_strSett = tartan.getSett();
            m_nPivots = tartan.getNrPivots();
            m_strResourceName = tartan.getId();
            m_nThreadCount = ENETartanUtils.calculateThreadCount(m_strSett, m_nPivots);
        }

        public ENETartanItem(ENEColoursEnvironment environment, String strName, String strSett)
        {
            this(environment, strName, strSett, null,1, true);
        }

        public ENETartanItem(ENEColoursEnvironment environment, String strName, String strSett, int nPivots)
        {
            this(environment, strName, strSett, null, nPivots, true);
        }

        public ENETartanItem(ENEColoursEnvironment environment, String strName, String strSett, Double dShrinkFactor, int nPivots, boolean bDarkened)
        {
            this.environment = environment;
            m_strName = strName;
            m_strResourceName = strName;
            m_strSett = strSett;
            m_nPivots = nPivots;
            if (dShrinkFactor == null) {
                dShrinkFactor = environment.getTartanShrinkFactor();
            }
            m_dShrinkFactor = dShrinkFactor;
            m_bDarkened = bDarkened;
            m_nThreadCount = ENETartanUtils.calculateThreadCount(m_strSett, m_nPivots);
        }

        public void setData(String strTartan)
        {
            ENETartan tartan = environment.getTartan(strTartan);
            if (tartan != null)
            {
                m_strResourceName = tartan.getId();
                m_strSett = tartan.getSett();
                m_nPivots = tartan.getNrPivots();
                m_dShrinkFactor = tartan.getScaleFactor();
                m_nThreadCount = ENETartanUtils.calculateThreadCount(m_strSett, m_nPivots);
            }
            else
                System.out.println("Tartan not found: " + strTartan);
            
        }
        protected void initialise()
        {
            // Store these for future reference.
            StringTokenizer st = new StringTokenizer(m_strSett);
            Color bandColors[] = new Color[st.countTokens()];
            int bandThreads[] = new int[st.countTokens()]; // we leave room for symmetry
            int bandCount=0;

            // Convert string to bands (a list of colors and color counts).
            while (st.countTokens() > 1) {
                    Color colour = ENETartanUtils.string2Color(st.nextToken());    // string2RGB(st.nextToken());
                    if (m_bDarkened)
                        colour = colour.darker();

                    int nColour = colour.getRGB();
                    int count = Integer.valueOf(st.nextToken()).intValue();
                    if (count > 0)
                    {
                            bandColors[bandCount] = colour;
                            bandThreads[bandCount] = count;
                            bandCount++;
                            //m_nThreadCount += bandThreads[bandCount];
                    }
            }
            // Adjust the bands for symmetry.
            if ((m_nPivots > 0) && (bandCount >= 4))        // TO DO: Handle Double Pivots!
            {
                // SE apply pattern in reverse - excluding first and last bands
                for(int i = 0; i < bandCount - 2; i++)
                {
                    bandColors[bandCount+i] = bandColors[bandCount-(i+2)];
                    bandThreads[bandCount+i] = bandThreads[bandCount-(i+2)];
                    //m_nThreadCount += bandThreads[bandCount-(i+2)];
                }
                bandCount = (2 * bandCount) - 2;
            }

            // Expand bands to threads.
            Color threadColors[] = new Color[m_nThreadCount];
            int index = 0;
            for (int i=0; i<bandCount; i++)
                    for (int j=0; j<bandThreads[i]; j++)
                            threadColors[index++] = bandColors[i];

            // "Weave" with the threads.  This is the twill pattern we       VVHH
            // use, where H is the horizontal thread and V is the            HVVH
            // vertical thread. ------------------------------------------>  HHVV
            //                                                               VHHV
            m_pixels = new Color[m_nThreadCount * m_nThreadCount];
            index = 0;
            for (int y = 0; y < m_nThreadCount; y++)
                    for (int x = 0; x < m_nThreadCount; x++)
                            m_pixels[index++] = threadColors[ (((x%4) - (y%4) + 4) %4 > 1) ? x : y ];


            m_bInitialised = true;
        }
	public @Override String getText()
	{
	        return getName();   // + " tartan";
	}

	public String getSett()
	{
	        return m_strSett;
	}

        public int getNrPivots()
        {
            return m_nPivots;
        }
        public int getWidth()
        {
            return m_nThreadCount;
        }
        public int getHeight()
        {
            return m_nThreadCount;
        }
        public int getThreadCount()
        {
            return m_nThreadCount;
        }

}
