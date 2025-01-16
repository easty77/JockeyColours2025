package ene.eneform.mero.action;

import ene.eneform.mero.colours.ENEColoursElementPattern;
import ene.eneform.mero.utils.ENEFillItem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public abstract class ENEPatternAction implements Serializable {

	public abstract void drawPattern(Graphics g, ENEFillItem colour, ENEColoursElementPattern pattern, Color pageColour);
        public boolean isSymmetric(){return false;} // to do - move to patterns.xml

   protected BufferedImage imageToBufferedImage(Image image) {

        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return bufferedImage;

    }

 
protected BufferedImage getImage(String strImage)
{
    try
    {
       return ImageIO.read(new File("c:/Users/Simon/Documents/NetBeansProjects/RacingColours/images/" + strImage + ".png"));      // Toolkit.getDefaultToolkit().getImage();
    }
    catch(IOException e)
    {
        return null;
    }

}

public boolean hasImage()
    {
    return false;
}
protected void drawJacketText(Graphics2D g, String strText)
{
    // count letters to determine start point AND font
    int nLetters = strText.length();
    int nY = 82;
    int nX = (100/(nLetters+1)) - 8;
    Font font = g.getFont();
    g.setFont(new Font("Courier", Font.BOLD, 25));
    if (nLetters == 1)
    {
        g.setFont(new Font("Courier", Font.BOLD, 60));
        nX = 28;
        nY = 80;
    }
    else if(nLetters == 2)
    {
        g.setFont(new Font("Courier", Font.BOLD, 40));
        nX = 20;
    }
    System.out.println(nLetters + ":" + g.getFont().getFamily() + "-" + g.getFont().getName());
    g.drawString(strText, nX, nY);
    g.setFont(font);
}
protected void drawText(Graphics2D g, String strText, Font f, int nX, int nY)
{
    Font font = g.getFont();
    g.setFont(f);
    System.out.println(g.getFont().getFamily() + "-" + g.getFont().getName());
    g.drawString(strText, nX, nY);
    g.setFont(font);
}
}
