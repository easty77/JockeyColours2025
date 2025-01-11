package ene.eneform.mero.colours;

import java.util.ArrayList;
import java.util.Iterator;

import ene.eneform.mero.config.*;
import ene.eneform.mero.parse.ENEColoursParserCompareAction;
import ene.eneform.mero.parse.ENEColoursParserExpand;
import ene.eneform.mero.parse.ENEColoursParserMatch;
import ene.eneform.mero.utils.ENEColourItem;
import ene.eneform.mero.utils.ENEFillItem;
import ene.eneform.mero.tartan.ENETartanItem;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class FullRacingColours {
    private final ENEColoursEnvironment environment;

    private String language;
    private String expanded;
    private String original;
    private String description;
    private String owner;
    private ENERacingColours colours;
    private String remainder = "";
    private String syntax = "";

    public FullRacingColours(ENEColoursEnvironment environment,
    String language, String description, String owner)
	{
        this.environment = environment;
    	this.original = description;
        this.owner = owner;
        this.language = language;
        colours = new ENERacingColours(language, original, owner,
                new ENEColoursElement(environment, language, ENEColoursElement.JACKET),
                new ENEColoursElement(environment, language, ENEColoursElement.SLEEVES),
                new ENEColoursElement(environment, language, ENEColoursElement.CAP)
                );
	this.description = environment.getAbbreviationsHandler().replaceAbbreviations(original, language);
        this.description += ".";    // end with full stop, because easier to detect than end of string
        log.info("NEW: " + this.description);

        this.expanded = expandDescription();

        log.info("EXPANDED: " + this.expanded);

        parse();
 	}

    private String expandDescription()
    {
        String expanded="";
        Iterator<ENEColoursParserExpand> iter = environment.getConfigExpands().getExpandList(language).iterator();
        while (iter.hasNext())
        {
            ENEColoursParserExpand expand = iter.next();
            try
            {
                int nCounter = 0;
                while(!description.equals(expanded = expand.expandString(description)))
                {
                    nCounter++;
                    if (nCounter > 100) // prevent endless iteration
                    {
                        System.out.println("expandDescription Max iteration level reached: "  + description);
                        return "";
                    }
                    if (!expanded.equals(description))
                    {
                        log.info("Expanded: " + expand.getExpandType() + "-" + expanded);
                        addSyntax(expand.getExpandType());
                    }
                }
            }
            catch(Exception e)
            {
                System.out.println("expandDescription ERROR: " + expand.getExpandType());
            }
        }

        return expanded;
    }

   private void parse()
    {
    	parse1(description);
        
        resolveImplications();

        resolveHues();

        log.info("Syntax: " + syntax);

        if (!"".equals(remainder))
            log.info("Remainder: " + remainder);
    }
    private void addSyntax(String add)
    {
        addSyntax(add, false);
    }
    private void addSyntax(String add, boolean bTrace)
    {
        if (bTrace)
            log.info(add);

       if (!"".equals(syntax))
                syntax += "-";
        syntax += add;
    }
     private void parse1(String strDescription)
     {
         String strOriginal = strDescription;

         //ENEColoursParserMatch jacketMatch = parseJacket(strDescription);
         ENEColoursParserMatch jacketMatch = parseElement("ENEJacket", colours.getJacket(), strDescription);

        if (jacketMatch != null)
        {
            log.info("Jacket match: " + jacketMatch.toString());
            strDescription = jacketMatch.extractFromString(strDescription);
            addSyntax(jacketMatch.getMatchType());
        }
        //ENEColoursParserMatch sleevesMatch = parseSleeves(strDescription);
         ENEColoursParserMatch sleevesMatch = parseElement("ENESleeves", colours.getSleeves(), strDescription);

        if (sleevesMatch != null)
        {
        	log.info("Sleeves match: " + sleevesMatch.toString());
            strDescription = sleevesMatch.extractFromString(strDescription);
            addSyntax(sleevesMatch.getMatchType());
        }
        //ENEColoursParserMatch capMatch = parseCap(strDescription);
        ENEColoursParserMatch capMatch = parseElement("ENECap", colours.getCap(), strDescription);

        if (capMatch != null)
        {
        	log.info("Cap match: " + capMatch.toString());
            strDescription = capMatch.extractFromString(strDescription);
            addSyntax(capMatch.getMatchType());
        }

        if ((!"".equals(strDescription)) && !strOriginal.equals(strDescription))    // still chance of more
        {
        	log.info("Non-empty final description: " + strDescription);
            parse1(strDescription);
        }
        else
        	this.remainder = strDescription;
      }

     private void resolveHues()
    {
         // the hue is sometimes used instead of repeating full name e.g. initially royal blue and then just blue imlying royal blue
         ArrayList<ENEFillItem> lstColours = colours.getColourList();

         Iterator<ENEFillItem> iter1 = lstColours.iterator();
         while(iter1.hasNext())
         {
             ENEFillItem fill1 = iter1.next();
             if (fill1 == null)
             {
         	log.info("NULL COLOUR IN 1st LIST");
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
                    	log.info("NULL COLOUR IN 2nd LIST");
                     }
                     else if("ENETartanItem".equals(fill2.getClass().getSimpleName()) && !"".equals(fill2.getText()))
                     {
                         ENETartanItem tartan2 = (ENETartanItem) fill2;
                         colours.updateColour(tartan1, tartan2);
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
                                     colours.updateColour(colour1, colour2);
                                     log.info(colour1.getText() + " converted to " + colour2.getText());
                                 }
                            }
                        }
                   }
             }
         }
     }
     private void resolveImplications()
     {
    	 ENEColoursElement jacket = colours.getJacket();
    	 ENEColoursElementPattern primaryJacket = jacket.getPrimaryPattern();
    	 ENEColoursElement sleeves = colours.getSleeves();
         ENEColoursElementPattern primarySleeves = sleeves.getPrimaryPattern();
         ENEColoursElement cap = colours.getCap();
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
         ConfigPatterns configPatterns = environment.getConfigPatterns();
        if ((!bSleevesExplicit) && (primaryJacket != null)  && primaryJacket.canPropagate()
                && (configPatterns.getPattern("ENESleeves", primaryJacket.getPattern(), language) != null)
                 && (configPatterns.getPattern("ENESleeves", primaryJacket.getPattern(), language).canDerive()))
        {
            addSyntax("Jacket primary pattern -> sleeves primary pattern", true);
            if ("halves".equals(primaryJacket.getPattern()))
            {
               if(jacket.getColourItem() != null)
                   sleeves.setColour(jacket.getColourItem());
               if (sleeves.getPrimaryPattern() == null)
               {
                   sleeves.setPrimaryPattern(new ENEColoursElementPattern(environment, language, "alternate", primaryJacket.getColour(1).getText()));
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
                         ENEColoursElementPattern pattern1 = new ENEColoursElementPattern(environment, language, "alternate");
                         sleeves.setColour(jacket.getColourItem());
                         pattern1.setColour(pattern.getColour(1));
                         sleeves.setPattern(pattern1);
                     }
                     else if(pattern.canPropagate() && configPatterns.isDerivePattern("ENESleeves", pattern.getPattern(), language))
                     {
                        addSyntax("Jacket pattern -> sleeves", true);
                             ENEColoursElementPattern pattern1 = new ENEColoursElementPattern(environment, pattern);
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
                (configPatterns.getPattern("ENECap", primaryJacket.getPattern(), language) != null)
                 && (configPatterns.getPattern("ENECap", primaryJacket.getPattern(), language).canDerive()))
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
                     if (pattern.canPropagate() &&  configPatterns.isDerivePattern("ENECap", pattern.getPattern(), language))
                     {
                        addSyntax("Jacket pattern -> cap", true);
                        ENEColoursElementPattern pattern1 = new ENEColoursElementPattern(environment, pattern);
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
    	ArrayList<ENEColoursParserCompareAction> list = environment.getConfigCompares().getCompareList(strType, language);
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
              if (colours.getCap().duplicateColours())
              {
                  String strCapColour1 = colours.getCap().getTextColour();
                  // problem, has probably come from jacket, but should have been overridden by sleeves
                  if ((colours.getSleeves().getColourItem() != null) && (!colours.getSleeves().getColourItem().equals(colours.getCap().getColourItem())))
                      colours.getCap().setColour(colours.getSleeves().getTextColour());
                  else if ((colours.getJacket().getColourItem() != null) && (!colours.getJacket().getColourItem().equals(colours.getCap().getColourItem())))
                      colours.getCap().setColour(colours.getJacket().getTextColour());

                  log.info("duplicateColourPatternCheck: " + strCapColour1 + "-" + colours.getSleeves().getTextColour());
             } 
       }
       @Override public String toString()
       {
    	   String strContent="";
    	   if (!"".equals(remainder))
    		   strContent += ("REM:" + remainder + "|");
    	   strContent += (original + "->" + colours.toString());
    	   
    	   return strContent;
       }
       
       // From ENEColoursEnvironment
       public ArrayList<ENEColoursParserExpand> getExpandList(String language)
       {
           return environment.getConfigExpands().getExpandList(language);
       }

}
