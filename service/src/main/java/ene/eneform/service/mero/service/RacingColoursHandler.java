package ene.eneform.service.mero.service;

import ene.eneform.port.out.mero.model.ParseInfo;
import ene.eneform.service.mero.config.ConfigPatterns;
import ene.eneform.service.mero.config.ENEColoursEnvironment;
import ene.eneform.service.mero.model.*;
import ene.eneform.service.mero.model.colours.ENERacingColours;
import ene.eneform.service.mero.model.tartan.ENETartanItem;
import ene.eneform.service.mero.parse.ENEColoursParserCompareAction;
import ene.eneform.service.mero.parse.ENEColoursParserExpand;
import ene.eneform.service.mero.parse.ENEColoursParserMatch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;

@Slf4j
@Getter
@RequiredArgsConstructor
@Service
public class RacingColoursHandler {
    private final ENEColoursEnvironment environment;

    public ENEParsedRacingColours createParsedRacingColours(
                                String language, String description, String owner)
	{
        ENEParsedRacingColours racingColours = new ENEParsedRacingColours(language, description, owner);
       ENERacingColours colours = createRacingColours(language,
               racingColours.getDescription(), owner,
                null, null, null
                );
       String updated = environment.getAbbreviationsHandler().replaceAbbreviations(racingColours.getDescription(), language).toLowerCase() + ".";
        ParseInfo parseInfo = new ParseInfo(updated);

        expandDescription(parseInfo, language);

        parseInfo = parse(parseInfo, colours, language);

        parseInfo = resolveImplications(parseInfo, colours, language);

        resolveHues(colours);
        racingColours.setParseInfo(parseInfo);
       racingColours.setColours(colours);

        return racingColours;
    }

    private ParseInfo expandDescription(ParseInfo parseInfo, String language)
    {
        String description = parseInfo.getDescription();
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
                        return parseInfo;
                    }
                    if (!expanded.equals(description))
                    {
                        log.info("Expanded: " + expand.getExpandType() + "-" + expanded);
                        parseInfo.addSyntax(expand.getExpandType());
                        description = expanded;
                    }
                }
            }
            catch(Exception e)
            {
                System.out.println("expandDescription ERROR: " + expand.getExpandType());
            }
        }
        parseInfo.setExpanded(expanded);
        return parseInfo;
    }

     private ParseInfo parse(ParseInfo parseInfo, ENERacingColours colours, String language) {
        return parse1(parseInfo, parseInfo.getExpanded(), colours, language);
     }
    private ParseInfo parse1(ParseInfo parseInfo, String strDescription, ENERacingColours colours, String language) {

String strOriginal = strDescription;
         //ENEColoursParserMatch jacketMatch = parseJacket(strDescription);
         ENEColoursParserMatch jacketMatch = parseElement("ENEJacket", colours.getJacket(), strDescription, language);

        if (jacketMatch != null)
        {
            log.info("Jacket match: " + jacketMatch.toString());
            strDescription = jacketMatch.extractFromString(strDescription);
            parseInfo.addSyntax(jacketMatch.getMatchType());
        }
        //ENEColoursParserMatch sleevesMatch = parseSleeves(strDescription);
         ENEColoursParserMatch sleevesMatch = parseElement("ENESleeves", colours.getSleeves(), strDescription, language);

        if (sleevesMatch != null)
        {
        	log.info("Sleeves match: " + sleevesMatch.toString());
            strDescription = sleevesMatch.extractFromString(strDescription);
            parseInfo.addSyntax(sleevesMatch.getMatchType());
        }
        //ENEColoursParserMatch capMatch = parseCap(strDescription);
        ENEColoursParserMatch capMatch = parseElement("ENECap", colours.getCap(), strDescription, language);

        if (capMatch != null)
        {
        	log.info("Cap match: " + capMatch.toString());
            strDescription = capMatch.extractFromString(strDescription);
            parseInfo.addSyntax(capMatch.getMatchType());
        }

        if ((!"".equals(strDescription)) && !strOriginal.equals(strDescription))    // still chance of more
        {
        	log.info("Non-empty final description: " + strDescription);
            parseInfo = parse1(parseInfo, strDescription, colours, language);
        }
        else {
            parseInfo.setRemainder(strDescription);
        }

        return parseInfo;
      }

     private void resolveHues(ENERacingColours colours)
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
     private ParseInfo resolveImplications(ParseInfo parseInfo, ENERacingColours colours, String language)
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
                     parseInfo.addSyntax("Sleeves pattern matches jacket");

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
                     parseInfo.addSyntax("Cap pattern matches jacket");
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
                     parseInfo.addSyntax("Cap pattern matches sleeves");
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
            parseInfo.addSyntax("Jacket primary pattern -> sleeves primary pattern");
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
             parseInfo.addSyntax("Jacket colour -> sleeves");
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
                        parseInfo.addSyntax("Jacket pattern -> sleeves");
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
                            parseInfo.addSyntax("Jacket colour pattern -> sleeves");
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
            parseInfo.addSyntax("Jacket primary pattern -> cap primary pattern");
            cap.setColour(jacket.getColourItem());
            cap.setPrimaryPattern(primaryJacket);
        }
         else if((cap.getColourItem() == null) && (jacket.getColourItem() != null))
    	 {
             parseInfo.addSyntax("Jacket colour -> cap");
             if ((primaryCap != null) && (primaryCap.getColour(1) != null) && (jacket.getColourItem().getText().equals(primaryCap.getColour(1).getText())))
             {
                 // about to set main colour to that of primary pattern , so pattern won't show
                 // clearly an error in expanding e.g. Maroon, Yellow Chevron, Maroon sleeves and Striped cap -> Maroon striped cap
                 // so reset pattern colour ro that of primary jacket or sleeves
                 parseInfo.addSyntax("Cap pattern colour reset as matches derived main colour");
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
                         parseInfo.addSyntax("Jacket pattern -> cap");
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
                        parseInfo.addSyntax("Jacket colour pattern -> cap");
                        pattern.setColour(primaryJacket.getColour(1));
                    }
                 }
             }
    	 }
    	 // sleeves -> cap
         if ((cap.getPatternCount() > 0) && (cap.getPrimaryPattern() != null) && (cap.getPrimaryPattern().getColour(1) == null))
         {
             parseInfo.addSyntax("Sleeves colour -> cap");
            if ((primarySleeves != null) && (primarySleeves.getColour(1) != null) && (primarySleeves.getColour(1) != cap.getColourItem()))
                cap.getPrimaryPattern().setColour(primarySleeves.getColour(1));
            else if (sleeves.getColourItem() != null)
                cap.getPrimaryPattern().setColour(sleeves.getColourItem());
         }
         return parseInfo;
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

    private ENEColoursParserMatch parseElement(String strType, ENEColoursElement element, String strDescription, String language)
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
 
       private void duplicateColourPatternCheck(ENERacingColours colours)
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
       
       // From ENEColoursEnvironment
       public ArrayList<ENEColoursParserExpand> getExpandList(String language)
       {
           return environment.getConfigExpands().getExpandList(language);
       }
    public ENERacingColours createRacingColours(String language, String description, String jacket, String sleeves, String cap) {
        return createRacingColours(language, description, null, jacket, sleeves, cap);
    }
        public ENERacingColours createRacingColours(String language, String description, String owner,String jacket, String sleeves, String cap) {

        ENERacingColours colours = new ENERacingColours(language, description, owner,
                createJacket(language, jacket),
                createSleeves(language,sleeves),
                createCap(language, cap));
        colours.setDescription(description);

        //RacingColoursParse.onCreate();

        return colours;

    }
    private ENEColoursElement createJacket(String language, String strDefinition) {
        return new ENEColoursElement(environment, language, ENEColoursElement.JACKET, strDefinition);
    }

    private ENEColoursElement createCap(String language, String strDefinition) {
        return new ENEColoursElement(environment, language, ENEColoursElement.CAP, strDefinition);
    }

    private ENEColoursElement createSleeves(String language, String strDefinition) {
        return new ENEColoursElement(environment, language, ENEColoursElement.SLEEVES, strDefinition);
    }
}
