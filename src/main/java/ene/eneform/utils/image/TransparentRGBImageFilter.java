/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.utils.image;

import java.awt.*;
import java.awt.image.RGBImageFilter;

/**
 *
 * @author Simon
 */
public class TransparentRGBImageFilter extends RGBImageFilter
{

    private int m_markerRGB;

    public TransparentRGBImageFilter(Color color)
    {
        m_markerRGB = color.getRGB() | 0xFF000000;
        canFilterIndexColorModel = true;
    }

    public final int filterRGB(int x, int y, int rgb)
    {
        int red = (rgb >> 16) & 0x0ff;
        int green = (rgb >> 8) & 0x0ff;
        int blue = (rgb) & 0x0ff;
        // the color we are looking for... Alpha bits are set to opaque
        if ((red < 200) || (green < 200) || (blue < 200))       // if all > 200 then approximating white
        //if ((rgb | 0xFF000000) == m_markerRGB)
        {
            // Mark the alpha bits as zero - transparent
            return rgb;
        } else
        {
            // nothing to do
            return 0x00FFFFFF & rgb;
        }
    }
}
