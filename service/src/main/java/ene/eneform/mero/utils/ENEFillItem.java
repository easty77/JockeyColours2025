package ene.eneform.mero.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Area;
import java.io.Serializable;


public abstract class ENEFillItem implements Serializable {

	public abstract void setColour(Graphics g);
	public abstract void fill(Graphics g, int nWidth, int nHeight);
	//public abstract void fillRectangle(Graphics g, Rectangle rect);
	public abstract void fillShape(Graphics g, Shape shape);
	public abstract String getText();
	public abstract boolean needsOutline(Color pageColour);
	
    public void colourAll(Graphics g, int nWidth, int nHeight)
    {
    	fill(g, nWidth, nHeight);
    }

    public void colourClipArea(Graphics g, Area a, int nWidth, int nHeight)
    {
        Shape shape = g.getClip();
        if (shape != null)
            a.intersect(new Area(shape));

        g.setClip(a);
        colourAll(g, nWidth, nHeight);

     // 20111207 comment out for now - this is outlines on patterns!  Is it ever necessa&ry
    /*  if (needsOutline())
        {
            g.setClip(null);
            Color color = g.getColor();
            g.setColor(Color.black);
            ((Graphics2D) g).draw(a);
            g.setColor(color);
        }  */
        g.setClip(shape);
    }
	
}
