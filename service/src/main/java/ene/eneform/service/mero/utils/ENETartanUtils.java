package ene.eneform.service.mero.utils;

import ene.eneform.service.mero.model.tartan.ENETartan;
import ene.eneform.service.mero.model.tartan.ENETartanColour;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 *
 * @author Simon
 */
public class ENETartanUtils {

         public static Color string2Color(String s) {
		// I have copied these color abbreviations from XTartan, and the RGB values
		// from /usr/X11/lib/X11/rgb.txt.  I think that they're too bright as is, but
		// look pretty good when darkened.

		if      (s.equalsIgnoreCase("r"))  return new Color(255,0,0);    // Red
		else if (s.equalsIgnoreCase("cr")) return new Color(178,34,34);  // Crimson (Firebrick)
		else if (s.equalsIgnoreCase("mn")) return new Color(176,48,96);  // Maroon
		else if (s.equalsIgnoreCase("or")) return new Color(255,165,0);  // Orange
		else if (s.equalsIgnoreCase("y"))  return new Color(255,255,0);  // Yellow
		else if (s.equalsIgnoreCase("g"))  return new Color(34,139,34);  // Green (Forest Green)
		else if (s.equalsIgnoreCase("lg")) return new Color(152,251,152);// Light Green (Pale Green)
		else if (s.equalsIgnoreCase("db")) return new Color(0,0,128);    // Dark Blue (Navy)
		else if (s.equalsIgnoreCase("b"))  return new Color(0,0,205);    // Medium Blue
		else if (s.equalsIgnoreCase("az")) return new Color(135,206,235);// Azure (Sky Blue)
		else if (s.equalsIgnoreCase("pu")) return new Color(221,160,221);// Purple (Plum)
		else if (s.equalsIgnoreCase("lil"))return new Color(218,112,214);// Lilac (Orchid)
		else if (s.equalsIgnoreCase("lv")) return new Color(230,230,250);// Lavendar
		else if (s.equalsIgnoreCase("ma")) return new Color(255,0,255);  // Magenta
		else if (s.equalsIgnoreCase("br")) return new Color(165,42,42);  // Brown
		else if (s.equalsIgnoreCase("w"))  return new Color(255,255,255);// White
		else if (s.equalsIgnoreCase("gy")) return new Color(190,190,190);// Gray
		else return new Color(0,0,0); // K, BK for black
		// Cy: Cyan  Cor: Coral  ForGr: ForestGreen  SlB: Slate Blue
		// Glr: Goldenrod Mar: Maroon  Trq: Turquoise  Wh: Wheat
	}
	// ----- String Utilities -----

	/**
	 * Gets rid of garbage and extra white space, and spaces the letter and number sections.
	 * @param s the string
	 * @returns the cleaned-up string
	 */
	public static String spaceify(String s) {
		int LETTER = 1;
		int NUMBER = 2;
		int OTHER  = 3;

		int kind, lastKind=OTHER;

		StringBuffer t = new StringBuffer(2 * s.length());

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".indexOf(c) != -1)
				kind = LETTER;
			else if ("0123456789".indexOf(c) != -1)
				kind = NUMBER;
			else
				kind = OTHER;

			if ( kind != OTHER && (kind == lastKind || lastKind == OTHER))
				t.append(c);
			else if (kind == OTHER) {
				if (lastKind != OTHER) t.append(' ');
			} else
				t.append(' ').append(c);

			lastKind = kind;
		}

		return t.toString();
	}

        public static HashMap<String,ArrayList<ENETartanColour>> getColourBands(String strSett)
        {
            HashMap<String,ArrayList<ENETartanColour>> hmColourBands = new HashMap<String,ArrayList<ENETartanColour>>();
            
            int nThreadCount = 0;
            StringTokenizer st = new StringTokenizer(strSett);
            int bandCount=0;

            // Convert string to bands (a list of colors and color counts).
            while (st.countTokens() > 1)
            {
                String strColour = st.nextToken();  // ignore
                String strCount = st.nextToken();
                    int count = Integer.valueOf(strCount).intValue();
                    if (count > 0)
                    {
                        ArrayList<ENETartanColour> alBands = hmColourBands.get(strColour);
                        if (alBands == null)
                        {
                            alBands = new ArrayList<ENETartanColour>();
                            hmColourBands.put(strColour, alBands);
                        }
                        alBands.add(new ENETartanColour(strColour, nThreadCount, count));
                        nThreadCount += count;
                     }
            }
            
            return hmColourBands;
        }
        public static String generateSVGTartan(String strSett)
        {
            // sreSett is already extended to include pivot
            String strPattern = "";
            
            HashMap<String,ArrayList<ENETartanColour>> hmColourBands = getColourBands(strSett);
            
            int nThreads = getNrThreads(hmColourBands);
            String strContent = String.format("<pattern id=\"weave\" patternUnits=\"userSpaceOnUse\" patternTransform=\"translate(36 36)\" width=\"%d\" height=\"%d\">\n", nThreads, nThreads);
            String strRectangle="";
            //strRectangle=String.format("<rect width=\"%d\" height=\"%d\" fill=\"url(#weave)\"/>", nThreads, nThreads);
            Iterator<String> iter = hmColourBands.keySet().iterator();
            while(iter.hasNext())
            {
                String strColour = iter.next();
                strPattern += generatePatternDefinition(strColour);
                ArrayList<ENETartanColour> aBands = hmColourBands.get(strColour);
                strContent += generateSVGTartanColour(strColour, aBands, nThreads);        
            }
            
            strContent += "</pattern>";
            
            return "<defs>\n" + strPattern +"\n" + strContent + "\n</defs>\n" + strRectangle; 
        }
        public static void generateSVGTartanElement(Document document, Element defs, ENETartan tartan)
        {
           Element weave = document.createElementNS("http://www.w3.org/2000/svg", "pattern");
           generateSVGTartanElement(document, weave, defs, tartan);
        }
        public static void generateSVGTartanElement(SVGGraphics2D svgGenerator, Element defs, ENETartan tartan)
        {
           Element weave = svgGenerator.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "pattern");
           generateSVGTartanElement(svgGenerator.getDOMFactory(), weave, defs, tartan);
        }
        public static void generateSVGTartanElement(Document document, Element weave, Element defs, ENETartan tartan)
        {
            String strSett = tartan.extendSett();
            String strName = tartan.getId();
            double dScale = tartan.getScaleFactor();
            // strSett is already extended to include pivot
          HashMap<String,ArrayList<ENETartanColour>> hmColourBands = getColourBands(strSett);
            
           int nThreads = getNrThreads(hmColourBands);
           weave.setAttributeNS(null, "id", strName);   // "weave"
           weave.setAttributeNS(null, "patternUnits", "userSpaceOnUse");
           String strPatternTransform = "translate(36 36)";
           if (dScale != 1.0)
           {
               strPatternTransform += " scale(" + dScale + " " + dScale + ")";
           }
           weave.setAttributeNS(null, "patternTransform", strPatternTransform);
           weave.setAttributeNS(null, "width", String.valueOf(nThreads));
           weave.setAttributeNS(null, "height", String.valueOf(nThreads));
            Iterator<String> iter = hmColourBands.keySet().iterator();
            while(iter.hasNext())
            {
                String strColour = iter.next();
                generatePatternDefinitionElement(document, defs, strColour);
                ArrayList<ENETartanColour> aBands = hmColourBands.get(strColour);
                generateSVGTartanColourElement(document, weave, strColour, aBands, nThreads);        
            }
            defs.appendChild(weave);
        }
        private static String generateSVGTartanColour(String strColour, ArrayList<ENETartanColour> aBands, int nThreads)
        {
            // <path fill="url(#blue2)" d="M 0 56 h 216 v 2 h -216 z  M 0 60 h 216 v 2 h -216 z  M 0 64 h 216 v 16 h -216 z  M 0 82 h 216 v 2 h -216 z  M 0 86 h 216 v 2 h -216 z  M 0 160 h 216 v 16 h -216 z  M 0 178 h 216 v 4 h -216 z  M 0 184 h 216 v 16 h -216 z"/>
            String strHorizontal = "<path stroke=\"none\" fill=\"url(#" + strColour + "h)\" d=\"";
            String strHorizontalBlock = " M 0 %d h %d v %d h -%d z";
            // <path fill="url(#blue1)" d="  M 56 0 v 216 h 2 v -216 z  M 60 0 v 216 h 2 v -216 z  M 64 0 v 216 h 16 v -216 z  M 82 0 v 216 h 2 v -216 z  M 86 0 v 216 h 2 v -216 z  M 160 0 v 216 h 16 v -216 z  M 178 0 v 216 h 4 v -216 z  M 184 0 v 216 h 16 v -216 z"/>
            String strVertical = "<path stroke=\"none\" fill=\"url(#" + strColour + "v)\" d=\"";
            String strVerticalBlock = " M %d 0 v %d h %d v -%d z";
           
            Iterator<ENETartanColour> iter1 = aBands.iterator();
            while(iter1.hasNext())
            {
                ENETartanColour band = iter1.next();
                strHorizontal += String.format(strHorizontalBlock, band.getStart(), nThreads, band.getWidth(), nThreads);
                strVertical += String.format(strVerticalBlock, band.getStart(), nThreads, band.getWidth(), nThreads);
            }

            strHorizontal += "\" />\n";
            strVertical += "\" />\n";

            return strHorizontal + strVertical;
        }
        private static void generateSVGTartanColourElement(Document document, Element content, String strColour, ArrayList<ENETartanColour> aBands, int nThreads)
        {
            Element horizontal = document.createElementNS("http://www.w3.org/2000/svg", "path");
            horizontal.setAttributeNS(null, "fill", "url(#" + strColour + "h)");
            horizontal.setAttributeNS(null, "stroke", "none");
            Element vertical = document.createElementNS("http://www.w3.org/2000/svg", "path");
            vertical.setAttributeNS(null, "fill", "url(#" + strColour + "v)");
            vertical.setAttributeNS(null, "stroke", "none");
 
            String strHorizontal = "";
            String strHorizontalBlock = " M 0 %d h %d v %d h -%d z";
            String strVertical = "";
            String strVerticalBlock = " M %d 0 v %d h %d v -%d z";
           
            Iterator<ENETartanColour> iter1 = aBands.iterator();
            while(iter1.hasNext())
            {
                ENETartanColour band = iter1.next();
                strHorizontal += String.format(strHorizontalBlock, band.getStart(), nThreads, band.getWidth(), nThreads);
                strVertical += String.format(strVerticalBlock, band.getStart(), nThreads, band.getWidth(), nThreads);
            }

            horizontal.setAttributeNS(null, "d", strHorizontal);
            vertical.setAttributeNS(null, "d", strVertical);

            content.appendChild(horizontal);
            content.appendChild(vertical);
        }
        
        private static int getNrThreads(HashMap<String,ArrayList<ENETartanColour>> hmColourBands)
        {
            int nThreadCount = 0;
            Iterator<String> iter = hmColourBands.keySet().iterator();
            while(iter.hasNext())
            {
                String strColour = iter.next();
                ArrayList<ENETartanColour> aBands = hmColourBands.get(strColour);
                Iterator<ENETartanColour> iter1= aBands.iterator();
                while(iter1.hasNext())
                {
                    ENETartanColour colour = iter1.next();
                    nThreadCount += colour.getWidth();
                }
            }
            
            return nThreadCount;
        }
        private static String generatePatternDefinition(String strColour)
        {
            Color colour = string2Color(strColour);
            String strPattern = "<pattern id=\"%s%s\" width=\"4\" height=\"4\" patternUnits=\"userSpaceOnUse\" patternTransform=\"translate(0 %.1f)\">\n<path d=\"M 0 4 L 4 0 M -2 2 L 2 -2 M 2 6 L 6 2\" style=\"stroke:#%02x%02x%02x;stroke-width:1.4142px;\"/>\n</pattern>\n";
            
            String strVerticalPattern = String.format(strPattern, strColour, "v", 2.5, colour.getRed(), colour.getGreen(), colour.getBlue());
            String strHorizontaPattern = String.format(strPattern, strColour, "h", 0.5, colour.getRed(), colour.getGreen(), colour.getBlue());

            return strVerticalPattern + "\n" + strHorizontaPattern;
        }
        private static void generatePatternDefinitionElement(Document document, Element content, String strColour)
        {
           Color colour = string2Color(strColour);
           Element horizontal = document.createElementNS("http://www.w3.org/2000/svg", "pattern");
           horizontal.setAttributeNS(null, "id", strColour + "h");
           horizontal.setAttributeNS(null, "width", "8");
           horizontal.setAttributeNS(null, "height", "8");
           horizontal.setAttributeNS(null, "patternUnits", "userSpaceOnUse");
           horizontal.setAttributeNS(null, "patternTransform", "translate(0 2.5)");
           Element hpath = document.createElementNS("http://www.w3.org/2000/svg", "path");
           hpath.setAttributeNS(null, "d", "M -4 8 L 8 -4 M -6 6 L 4 -4 M -2 10 L 10 -2 M 12 0 L 0 12 M 14 2 L 2 14");
           hpath.setAttributeNS(null, "style", String.format("stroke:#%02x%02x%02x;stroke-width:1.4142px;", colour.getRed(), colour.getGreen(), colour.getBlue()));
           horizontal.appendChild(hpath);

           Element vertical = document.createElementNS("http://www.w3.org/2000/svg", "pattern");
           vertical.setAttributeNS(null, "id", strColour + "v");
           vertical.setAttributeNS(null, "width", "8");
           vertical.setAttributeNS(null, "height", "8");
           vertical.setAttributeNS(null, "patternUnits", "userSpaceOnUse");
           vertical.setAttributeNS(null, "patternTransform", "translate(0 0.5)");
           Element vpath = document.createElementNS("http://www.w3.org/2000/svg", "path");
           vpath.setAttributeNS(null, "d", "M -4 8 L 8 -4 M -6 6 L 4 -4 M -2 10 L 10 -2 M 12 0 L 0 12 M 14 2 L 2 14");
           vpath.setAttributeNS(null, "style", String.format("stroke:#%02x%02x%02x;stroke-width:1.4142px;", colour.getRed(), colour.getGreen(), colour.getBlue()));
           vertical.appendChild(vpath);

           content.appendChild(horizontal);
           content.appendChild(vertical);
        }
}
