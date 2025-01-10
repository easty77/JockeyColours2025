package ene.eneform.mero.parse;

import java.util.ArrayList;
import java.util.Iterator;

import ene.eneform.mero.colours.ENEColoursElement;
import ene.eneform.mero.colours.ENERacingColours;
import ene.eneform.mero.colours.ENEColoursElementPattern;
import ene.eneform.mero.config.ENEAbbreviations;
import ene.eneform.mero.parse.ENEColoursParserCompareAction;
import ene.eneform.mero.parse.ENEColoursParserExpand;
import ene.eneform.mero.parse.ENEColoursParserMatch;
import ene.eneform.mero.utils.ENEColourItem;
import ene.eneform.mero.config.ENEColoursEnvironment;
import ene.eneform.mero.utils.ENEFillItem;
import ene.eneform.mero.tartan.ENETartanItem;

public class ENEColoursParser {

	private String m_strLanguage;
	private String m_strOriginal;
	private String m_strDescription;
	private ENERacingColours m_colours = null;
	private String m_strRemainder = "";
	private String m_strSyntax = "";
        private int m_nMaxExpandIterations = 50;    // default in case not defined in enecoloursconfig.xml
 

    public ENEColoursParser(String strLanguage, String strDescription, String strOwner)
	{
            m_strLanguage = strLanguage;
    	m_strOriginal = strDescription;
	m_strDescription = ENEAbbreviations.replaceAbbreviations(ENEAbbreviations.preparse(convertToLower(m_strOriginal), m_strLanguage), m_strLanguage);
        m_colours = ENEColoursEnvironment.getInstance().createRacingColours(m_strLanguage, m_strOriginal, strOwner);
        m_strDescription += ".";    // end with full stop, because easier to detect than end of string
        ENEColoursEnvironment.getInstance().debug("NEW: " + m_strDescription);

        m_strDescription = expandDescription(m_strDescription);
        int nMaxExpandIterations = ENEColoursEnvironment.getInstance().getMaxNrExpandIterations();
        if (nMaxExpandIterations > 0)
            m_nMaxExpandIterations = nMaxExpandIterations;
        
        ENEColoursEnvironment.getInstance().debug("EXPANDED: " + m_strDescription);
 	}

    public String getExpanded()
    {
        return m_strDescription;
    }

    public String expandDescription(String strDescription)
    {
        Iterator<ENEColoursParserExpand> iter = ENEColoursEnvironment.getInstance().getExpandList(m_strLanguage).iterator();
        while (iter.hasNext())
        {
            ENEColoursParserExpand expand = iter.next();
            try
            {
                String strExpanded;
                int nCounter = 0;
                while(!strDescription.equals(strExpanded = expand.expandString(strDescription)))
                {
                    nCounter++;
                    if (nCounter > 100) // prevent endless iteration
                    {
                        System.out.println("expandDescription Max iteration level reached: "  + strDescription);
                        return "";
                    }
                    if (!strExpanded.equals(strDescription))
                    {
                        ENEColoursEnvironment.getInstance().trace("Expanded: " + expand.getExpandType() + "-" + strExpanded);
                        addSyntax(expand.getExpandType());
                        strDescription = strExpanded;
                    }
                }
            }
            catch(Exception e)
            {
                System.out.println("expandDescription ERROR: " + expand.getExpandType());
            }
        }

        return strDescription;
    }

   public ENERacingColours parse()
    {
    	parse1(m_strDescription);
        
        resolveImplications();

        resolveHues();

        ENEColoursEnvironment.getInstance().debug("Syntax: " + m_strSyntax);

        if (!"".equals(m_strRemainder))
            ENEColoursEnvironment.getInstance().debug("Remainder: " + m_strRemainder);

        //ENEColoursEnvironment.getInstance().trace("parse: " + m_colours.toString());
        return m_colours;
    }
    public String getRemainder()
    {
    	return m_strRemainder;
    }
    public String getSyntax()
    {
    	return m_strSyntax;
    }
    private void addSyntax(String strSyntax)
    {
        addSyntax(strSyntax, false);
    }
    private void addSyntax(String strSyntax, boolean bTrace)
    {
        if (bTrace)
            ENEColoursEnvironment.getInstance().trace(strSyntax);

       if (!"".equals(m_strSyntax))
                m_strSyntax += "-";
        m_strSyntax += strSyntax;
    }
     private void parse1(String strDescription)
     {
         String strOriginal = strDescription;

         //ENEColoursParserMatch jacketMatch = parseJacket(strDescription);
         ENEColoursParserMatch jacketMatch = parseElement("ENEJacket", m_colours.getJacket(), strDescription);

        if (jacketMatch != null)
        {
            ENEColoursEnvironment.getInstance().trace("Jacket match: " + jacketMatch.toString());
            strDescription = jacketMatch.extractFromString(strDescription);
            addSyntax(jacketMatch.getMatchType());
        }
        //ENEColoursParserMatch sleevesMatch = parseSleeves(strDescription);
         ENEColoursParserMatch sleevesMatch = parseElement("ENESleeves", m_colours.getSleeves(), strDescription);

        if (sleevesMatch != null)
        {
        	ENEColoursEnvironment.getInstance().trace("Sleeves match: " + sleevesMatch.toString());
            strDescription = sleevesMatch.extractFromString(strDescription);
            addSyntax(sleevesMatch.getMatchType());
        }
        //ENEColoursParserMatch capMatch = parseCap(strDescription);
        ENEColoursParserMatch capMatch = parseElement("ENECap", m_colours.getCap(), strDescription);

        if (capMatch != null)
        {
        	ENEColoursEnvironment.getInstance().trace("Cap match: " + capMatch.toString());
            strDescription = capMatch.extractFromString(strDescription);
            addSyntax(capMatch.getMatchType());
        }

        if ((!"".equals(strDescription)) && !strOriginal.equals(strDescription))    // still chance of more
        {
        	ENEColoursEnvironment.getInstance().debug("Non-empty final description: " + strDescription);
            parse1(strDescription);
        }
        else
        	m_strRemainder = strDescription;
      }

     private void resolveHues()
    {
         // the hue is sometimes used instead of repeating full name e.g. initially royal blue and then just blue imlying royal blue
         ArrayList<ENEFillItem> lstColours = m_colours.getColourList();

         Iterator<ENEFillItem> iter1 = lstColours.iterator();
         while(iter1.hasNext())
         {
             ENEFillItem fill1 = iter1.next();
             if (fill1 == null)
             {
         	ENEColoursEnvironment.getInstance().debug("NULL COLOUR IN 1st LIST");
             }
             else if("ENETartanItem".equals(fill1.getClass().getSimpleName()))
             {
                 // has specific tartan already been used
                ENETartanItem tartan1 = (ENETartanItem) fill1;
                Iterator<ENEFillItem> iter2 = lstColours.iterator();
                while(iter2.hasNext())
                {
                     ENEFillItem fill2 = iter2.next();
                     if (fill2 == null)
                     {
                    	ENEColoursEnvironment.getInstance().debug("NULL COLOUR IN 2nd LIST");
                     }
                     else if("ENETartanItem".equals(fill2.getClass().getSimpleName()) && !"".equals(fill2.getText()))
                     {
                         ENETartanItem tartan2 = (ENETartanItem) fill2;
                         m_colours.updateColour(tartan1, tartan2);
                     }
                }
             }
             else if((fill1.getText() != null) && "ENEColourItem".equals(fill1.getClass().getSimpleName()))
             {
                ENEColourItem colour1 = (ENEColourItem) fill1;
                if (colour1.getHue().equals(colour1.getText()))
                 {
                        Iterator<ENEFillItem> iter2 = lstColours.iterator();
                        while(iter2.hasNext())
                        {
                             ENEFillItem fill2 = iter2.next();
                             if ((fill2 != null) && (fill2.getText() != null) && "ENEColourItem".equals(fill2.getClass().getSimpleName()))
                             {
                                 ENEColourItem colour2 = (ENEColourItem) fill2;
                                 if (colour2.getHue().equals(colour1.getHue()) && (!colour2.getText().equals(colour1.getText())) && (colour2.getText().indexOf(colour1.getText()) > -1))
                                 {
                                     m_colours.updateColour(colour1, colour2);
                                     ENEColoursEnvironment.getInstance().trace(colour1.getText() + " converted to " + colour2.getText());
                                 }
                            }
                        }
                   }
             }
         }
     }
     private void resolveImplications()
     {
    	 ENEColoursElement jacket = m_colours.getJacket();
    	 ENEColoursElementPattern primaryJacket = jacket.getPrimaryPattern();
    	 ENEColoursElement sleeves = m_colours.getSleeves();
         ENEColoursElementPattern primarySleeves = sleeves.getPrimaryPattern();
         ENEColoursElement cap = m_colours.getCap();
         ENEColoursElementPattern primaryCap = cap.getPrimaryPattern();
         boolean bSleevesExplicit = ((primarySleeves != null) || (sleeves.getColourItem() != null));
         
// look for the same pattern elsewhere
         Iterator<ENEColoursElementPattern> iter1 = sleeves.getPatternIterator();
         while(iter1.hasNext())
         {
             ENEColoursElementPattern matchpattern;
             ENEColoursElementPattern pattern = iter1.next();
             if (pattern.getColourCount() == 0)
             {
                 if ((matchpattern = jacket.getPatternMatch(pattern.getPattern())) != null)
                 {
                     addSyntax("Sleeves pattern matches jacket", true);

                     int nCount = matchpattern.getColourCount();
                     int i = 1;
                     while(i <= nCount)
                     {
                         pattern.setColour(matchpattern.getColour(i));
                         i++;
                    }
                 }
             }
         }
        Iterator<ENEColoursElementPattern> iter2 = cap.getPatternIterator();
         while(iter2.hasNext())
         {
             ENEColoursElementPattern matchpattern;
             ENEColoursElementPattern pattern = iter2.next();
             if (pattern.getColourCount() == 0)
             {
                 if ((matchpattern = jacket.getPatternMatch(pattern.getPattern())) != null)
                 {
                     addSyntax("Cap pattern matches jacket", true);
                     int nCount = matchpattern.getColourCount();
                     int i = 1;
                     while(i <= nCount)
                     {
                         pattern.setColour(matchpattern.getColour(i));
                         i++;
                     }
                 }
                 else if((matchpattern = sleeves.getPatternMatch(pattern.getPattern())) != null)
                 {
                     addSyntax("Cap pattern matches sleeves", true);
                     int nCount = matchpattern.getColourCount();
                     int i = 1;
                     while(i <= nCount)
                     {
                         pattern.setColour(matchpattern.getColour(i));
                         i++;
                    }
                 }
             }
         }

    	 // jacket -> sleeves
         // 20140314 - can derive Cap from Jacket if 1) Sleeve colour has been specified ...
        boolean bPrimaryJacketCapDerive = (sleeves.getColourItem() != null);
        if ((!bSleevesExplicit) && (primaryJacket != null)  && primaryJacket.canPropagate()
                && (ENEColoursEnvironment.getInstance().getPattern("ENESleeves", primaryJacket.getPattern(), m_strLanguage) != null)
                 && (ENEColoursEnvironment.getInstance().getPattern("ENESleeves", primaryJacket.getPattern(), m_strLanguage).canDerive()))
        {
            addSyntax("Jacket primary pattern -> sleeves primary pattern", true);
            if ("halves".equals(primaryJacket.getPattern()))
            {
               if(jacket.getColourItem() != null)
                   sleeves.setColour(jacket.getColourItem());
               if (sleeves.getPrimaryPattern() == null)
               {
                   sleeves.setPrimaryPattern(new ENEColoursElementPattern(m_strLanguage, "alternate", primaryJacket.getColour(1).getText()));
                    bPrimaryJacketCapDerive = true;                     // 2) Sleeve pattern is also derived from jacket
               }
            }
            else
            {
                sleeves.setColour(jacket.getColourItem());
                if (sleeves.getPrimaryPattern() == null)
                {
                    sleeves.setPrimaryPattern(primaryJacket);
                    bPrimaryJacketCapDerive = true;                     // 2) Sleeve pattern is also derived from jacket
                }
            }
        }
        else if ((sleeves.getColourItem() == null) && (jacket.getColourItem() != null))
    	 {
             addSyntax("Jacket colour -> sleeves", true);
             sleeves.setColour(jacket.getColourItem());
             if (sleeves.getPatternCount() == 0)
             {
                 Iterator<ENEColoursElementPattern> iter = jacket.getPatternIterator();
                 while(iter.hasNext())
                 {
                     ENEColoursElementPattern pattern = iter.next();
                     if ("halved".equals(pattern.getPattern()) || "halves".equals(pattern.getPattern()))
                     {
                         ENEColoursElementPattern pattern1 = new ENEColoursElementPattern(m_strLanguage, "alternate");
                         sleeves.setColour(jacket.getColourItem());
                         pattern1.setColour(pattern.getColour(1));
                         sleeves.setPattern(pattern1);
                     }
                     else if(pattern.canPropagate() && ENEColoursEnvironment.getInstance().isDerivePattern("ENESleeves", pattern.getPattern(), m_strLanguage))
                     {
                        addSyntax("Jacket pattern -> sleeves", true);
                             ENEColoursElementPattern pattern1 = new ENEColoursElementPattern(pattern);
                             sleeves.setPattern(pattern1);
                     }
                 }
            }
             else if (primaryJacket != null)
             {
                 Iterator<ENEColoursElementPattern> iter = sleeves.getPatternIterator();
                 while(iter.hasNext())
                 {
                    ENEColoursElementPattern pattern = iter.next();
                    if (pattern.getColourCount() == 0)
                    {
                            addSyntax("Jacket colour pattern -> sleeves", true);
                            pattern.setColour(primaryJacket.getColour(1));
                    }
                 }
             }
    	 }
    	 // jacket -> cap
       if (bPrimaryJacketCapDerive && (cap.getColourItem() == null) && (primaryCap == null) && (primaryJacket != null) &&
                (ENEColoursEnvironment.getInstance().getPattern("ENECap", primaryJacket.getPattern(), m_strLanguage) != null)
                 && (ENEColoursEnvironment.getInstance().getPattern("ENECap", primaryJacket.getPattern(), m_strLanguage).canDerive()))
        {
            addSyntax("Jacket primary pattern -> cap primary pattern", true);
            cap.setColour(jacket.getColourItem());
            cap.setPrimaryPattern(primaryJacket);
        }
         else if((cap.getColourItem() == null) && (jacket.getColourItem() != null))
    	 {
             addSyntax("Jacket colour -> cap", true);
             if ((primaryCap != null) && (primaryCap.getColour(1) != null) && (jacket.getColourItem().getText().equals(primaryCap.getColour(1).getText())))
             {
                 // about to set main colour to that of primary pattern , so pattern won't show
                 // clearly an error in expanding e.g. Maroon, Yellow Chevron, Maroon sleeves and Striped cap -> Maroon striped cap
                 // so reset pattern colour ro that of primary jacket or sleeves
                 addSyntax("Cap pattern colour reset as matches derived main colour", true);
                 if (primaryJacket != null)
                     primaryCap.replaceColour(1, primaryJacket.getColour(1));
                 else if(primarySleeves != null)
                     primaryCap.replaceColour(1, primarySleeves.getColour(1));
             }

             cap.setColour(jacket.getColourItem());
             
             if (bPrimaryJacketCapDerive && (cap.getPatternCount() == 0))     
             {
                 Iterator<ENEColoursElementPattern> iter = jacket.getPatternIterator();
                 while(iter.hasNext())
                 {
                     ENEColoursElementPattern pattern = iter.next();
                     if (pattern.canPropagate() &&  ENEColoursEnvironment.getInstance().isDerivePattern("ENECap", pattern.getPattern(), m_strLanguage))
                     {
                        addSyntax("Jacket pattern -> cap", true);
                        ENEColoursElementPattern pattern1 = new ENEColoursElementPattern(pattern);
                        cap.setPattern(pattern1);
                     }
                 }
             }
             else if (primaryJacket != null)
             {
                 Iterator<ENEColoursElementPattern> iter = cap.getPatternIterator();
                 while(iter.hasNext())
                 {
                    ENEColoursElementPattern pattern = iter.next();
                    if ((pattern.getColourCount() == 0) && (primaryJacket.getColour(1) != null) && (primaryJacket.getColour(1) != cap.getColourItem()))
                    {
                        addSyntax("Jacket colour pattern -> cap", true);
                        pattern.setColour(primaryJacket.getColour(1));
                    }
                 }
             }
    	 }
    	 // sleeves -> cap
         if ((cap.getPatternCount() > 0) && (cap.getPrimaryPattern() != null) && (cap.getPrimaryPattern().getColour(1) == null))
         {
            addSyntax("Sleeves colour -> cap", true);
            if ((primarySleeves != null) && (primarySleeves.getColour(1) != null) && (primarySleeves.getColour(1) != cap.getColourItem()))
                cap.getPrimaryPattern().setColour(primarySleeves.getColour(1));
            else if (sleeves.getColourItem() != null)
                cap.getPrimaryPattern().setColour(sleeves.getColourItem());
         }
     }
    public static String convertToLower(String strDescription)
    {
         int nQuote = strDescription.indexOf(" '");
         if (nQuote < 0)
            return strDescription.toLowerCase();
         else
         {
             String strLower = strDescription.substring(0, nQuote).toLowerCase();
             int nEndQuote = strDescription.lastIndexOf("'");
             strLower += strDescription.substring(nQuote, nEndQuote);
             strLower += strDescription.substring(nEndQuote).toLowerCase();
             
             return strLower;
         }
     }

    private ENEColoursParserMatch parseElement(String strType, ENEColoursElement element, String strDescription)
    {
    	ArrayList<ENEColoursParserCompareAction> list = ENEColoursEnvironment.getInstance().getCompareList(strType, m_strLanguage);
    	Iterator<ENEColoursParserCompareAction> iter = list.listIterator();
    	while(iter.hasNext())
    	{
    		ENEColoursParserCompareAction compare = iter.next();
    		ENEColoursParserMatch match = compare.match(element, strDescription);
    		if (match != null)
    		{
    			return match;
    		}
    	}
    	return null;
    }
 
       private void duplicateColourPatternCheck()
       {
              // colour of cap is implied, but if colour1 = colour2 then this is clearly wrong because there is definitely a pattern
              if (m_colours.getCap().duplicateColours())
              {
                  String strCapColour1 = m_colours.getCap().getTextColour();
                  // problem, has probably come from jacket, but should have been overridden by sleeves
                  if ((m_colours.getSleeves().getColourItem() != null) && (!m_colours.getSleeves().getColourItem().equals(m_colours.getCap().getColourItem())))
                      m_colours.getCap().setColour(m_colours.getSleeves().getTextColour());
                  else if ((m_colours.getJacket().getColourItem() != null) && (!m_colours.getJacket().getColourItem().equals(m_colours.getCap().getColourItem())))
                      m_colours.getCap().setColour(m_colours.getJacket().getTextColour());

                  ENEColoursEnvironment.getInstance().debug("duplicateColourPatternCheck: " + strCapColour1 + "-" + m_colours.getSleeves().getTextColour());
             } 
       }
       @Override public String toString()
       {
    	   String strContent="";
    	   if (!"".equals(m_strRemainder))
    		   strContent += ("REM:" + m_strRemainder + "|");
    	   strContent += (m_strOriginal + "->" + m_colours.toString());
    	   
    	   return strContent;
       }
 }
