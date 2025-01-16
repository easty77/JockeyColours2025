package ene.eneform.service.mero.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

public class ENEColourItem extends ENEFillItem {

	private String m_strText;
	private Color m_colour;
	private String m_strHue;
	
/*	public ENEColourItem(String strText, Color colour)
	{
            this(strText, colour, "");
	} */
	public ENEColourItem(String strText, Color colour, String strHue)
	{
		m_strText = strText;
		m_colour = colour;
                m_strHue = strHue;
	}
	public String getText()
	{
		return m_strText;
	}
	public Color getColour()
	{
		return m_colour;
	}
	public void setColour(Graphics g)
	{
		g.setColor(getColour());
	}

	public String getHue()
	{
		return m_strHue;
	}
	public void fill(Graphics g, int nWidth, int nHeight)
	{
		g.setColor(getColour());
		g.fillRect(0, 0, nWidth, nHeight);
	}
	public void fillShape(Graphics g, Shape shape)
	{
		g.setColor(getColour());
		((Graphics2D)g).fill(shape);
	}
	public boolean needsOutline(Color pageColour)
	{
		return (m_colour.equals(pageColour));
	}
        public String getHexRGB()
        {
            return MeroUtils.getHexRGB(getColour());
        }
}