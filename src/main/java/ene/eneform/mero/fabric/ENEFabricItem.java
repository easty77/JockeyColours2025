/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.mero.fabric;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

import ene.eneform.mero.utils.ENEFillItem;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 *
 * @author Simon
 */
public abstract class ENEFabricItem extends ENEFillItem {

    protected String m_strName = "";
    protected String m_strResourceName = "";
    protected String m_strData = "";
    protected MemoryImageSource m_image = null;
    protected double m_dShrinkFactor = 1.0; // show as is
    protected Color m_pixels[];
    protected boolean m_bInitialised = false;

    public ENEFabricItem()
    {
    }

 	public String getName()
	{
	        return m_strName;
	}

	public String getResourceName()
	{
	        return m_strResourceName;
	}

 	public void setName(String strName)
	{
	        m_strName = strName;
	}

	public void setResourceName(String strResourceName)
	{
	        m_strResourceName = strResourceName;
	}

        abstract public void setData(String strData);

        abstract protected void initialise();

 	abstract public int getWidth();

  	abstract public int getHeight();

 	public double getShrinkFactor()
	{
	        return m_dShrinkFactor;
	}

        public MemoryImageSource getMemoryImageSource()
	{
	    if (m_image == null)
	    {
                if (!m_bInitialised)
                    initialise();

	        int[] pix = new int[getHeight() * getWidth()];
	        for (int y = 0; y < getHeight(); y++)
	        {
	            for (int x = 0; x < getWidth(); x++)
	            {
	                int nColour = m_pixels[(y * getWidth()) + x].getRGB();
	                        pix[(getWidth() * y) + x] = nColour;
	            }
	        }
	        m_image = new MemoryImageSource(getWidth(), getHeight(), pix, 0, getWidth());
	    }

	    return m_image;
	}

//    abstract public void paint(Graphics2D g);

	public void paint(Graphics g)
	{
                if (!m_bInitialised)
                    initialise();
	        for (int x=0; x<getWidth(); x++)
	        {
	          for (int y=0; y<getHeight(); y++)
	          {
	              g.setColor(m_pixels[(x*getHeight()) + y]);
	              //g2.fill(new Rectangle(x, y, 1, 1));
	              //g2.setStroke(new BasicStroke(1));
	              g.drawLine(x,  y,  x,  y);
	          }
            }
	}

        public void fill(Graphics g, int nWidth, int nHeight)
        {
            fillShape(g, new Rectangle(0, 0, nWidth, nHeight));
        }
	public void fillShape(Graphics g, Shape shape)
	{
            if (!m_bInitialised)
              initialise();

            Graphics2D g2 = (Graphics2D) g;
	    // now expanding all patterns to approx 250 threads by repetition
	    MemoryImageSource mis = getMemoryImageSource();
	    Toolkit toolkit = Toolkit.getDefaultToolkit();
	    java.awt.Image image = toolkit.createImage(mis);

	    //BufferedImage bi = new BufferedImage((int)(image.getWidth(null)/m_dShrinkFactor), (int)(image.getHeight(null)/m_dShrinkFactor), BufferedImage.TYPE_INT_RGB);
	    BufferedImage bi = new BufferedImage((int)(image.getWidth(null)), (int)(image.getHeight(null)), BufferedImage.TYPE_INT_RGB);
	    Graphics2D big = bi.createGraphics();
	    //AffineTransform xform = AffineTransform.getScaleInstance(1.0/m_dShrinkFactor, 1.0/m_dShrinkFactor);
	    big.drawImage(image,null,null);

	    // Create the rectangle to fill
	    Rectangle2D r = new Rectangle2D.Double((int)shape.getBounds().getMinX(), (int)shape.getBounds().getMinY(), (int)shape.getBounds().getWidth(), (int)shape.getBounds().getHeight());
	    // Create a texture rectangle the same size as the texture image.
	    Rectangle2D tr = new Rectangle2D.Double(0, 0, bi.getWidth()/m_dShrinkFactor, bi.getHeight()/m_dShrinkFactor);
	    // Create the TexturePaint.
	    TexturePaint tp = new TexturePaint(bi, tr);

	    g2.setPaint(tp);

            g2.fill(shape);
	    //paint(g2);
	 }
	public boolean needsOutline(Color pageColour)
	{
		// tartan never needs outline
		return false;
	}
	public void setColour(Graphics g)
	{
		// do nothing
	}
    public String getText()
    {
            return m_strName;
    }
}
