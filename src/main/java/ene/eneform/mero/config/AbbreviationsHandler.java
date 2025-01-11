package ene.eneform.mero.config;

import ene.eneform.mero.utils.MeroUtils;
import org.springframework.stereotype.Service;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbbreviationsHandler {

	protected static final String[] sm_astrIgnoreText = 
        {
   /*     "on body, sleeves and cap",
        "on body and sleeves", */
        "[,\\s]+on front and back",
        "[,\\s]+on back and front",
//        "[,\\s]*back and front",
//        "[,\\s]*front and back",
        "in front",
        "^all ",
        "\\(", "\\)"
         };

        protected static Pattern sm_slash = Pattern.compile("/");
        protected static Pattern sm_hyphen = Pattern.compile("\\-");
        protected static Pattern sm_space = Pattern.compile("[\\s]+");
        protected static Pattern sm_comma = Pattern.compile(",");

    private final ConfigAbbreviations configAbbreviations;
    private final ConfigColours configColours;
    private final ConfigFabrics configFabrics;

    public AbbreviationsHandler(ConfigAbbreviations configAbbreviations, ConfigColours configColours,
                                    ConfigFabrics configFabrics) {
        this.configAbbreviations = configAbbreviations;
        this.configColours = configColours;
        this.configFabrics = configFabrics;
        }
     public String preparse(String strDescription, String strLanguage)
    {
         // need to preserve text in mixed case but need to convert colours to lower case for easy matching
         //Pattern pattern = Pattern.compile("logo|brand|" + ENEColoursEnvironment.getInstance().getFullColourListRegEx());
         Pattern pattern = Pattern.compile(getFullColourListRegEx(strLanguage)); // SE don't put logo on front, sorted by length
         strDescription = strDescription.replace(";", ","); // 1968 and previous use ; to separate main colour(s) from rest of description
         Matcher matcher = pattern.matcher(strDescription);
        while (matcher.find())
        {
          String strGroup = matcher.group();
          int nStart = matcher.start();
          int nEnd = matcher.end();
          strDescription = strDescription.substring(0, nStart) + strDescription.substring(nStart, nEnd).toLowerCase() + strDescription.substring(nEnd);
        }
            
            return strDescription;
    }

	public String replaceAbbreviations(String strDescription, String strLanguage)
        {
            strDescription = preparse(strDescription, strLanguage);
            String strUpdated="";
            // to do: should be array, not hashmap
            if ("en".equalsIgnoreCase(strLanguage))
            {
                strDescription = strDescription.replaceAll("&quo;", "'");
                strDescription = strDescription.replaceAll("&", "and");
                strDescription = strDescription.replaceAll(", with ", ", ");    // 20120620 UK 1920
                strDescription = strDescription.replaceAll(", and ", " and ");    // 20120620 UK 1920
                for (int i = 0; i < sm_astrIgnoreText.length; i++)
                {
                    strDescription = strDescription.replaceAll(sm_astrIgnoreText[i], "");
                }
                strDescription = strDescription.replaceAll(", and", " and");
            }
            // easier to remove word by word, to avoid substrings
            // note: small number that split by . , but also used for abbreviations, on which do not wish to split
            String[] astrItems = sm_comma.split(strDescription);
            for (int i = 0; i < astrItems.length; i++)
            {
                String strItem = astrItems[i];
                String strSpace = "[\\s]+";
                Pattern pattern1 = Pattern.compile(strSpace);
                String[] astrItems1 = sm_space.split(strItem);
                String strUpdated1="";
                for(int j = 0; j < astrItems1.length; j++)
                {
                    String strCurrentItem = astrItems1[j];
                    String[] astrItems2 = sm_slash.split(strCurrentItem);
                    String strUpdated2="";
                    for(int k = 0; k < astrItems2.length; k++)
                    {
                        String strOriginal = astrItems2[k];
                        boolean bSpace = true;
                        if ("fr".equalsIgnoreCase(strLanguage))
                        {
                            if (strOriginal.indexOf("mi-") == 0)
                            {
                                if (!"".equals(strUpdated2))
                                    strUpdated2 += " ";
                                strUpdated2 += "mi-";
                                bSpace = false;
                                strOriginal = strOriginal.substring(3);  // may need to replace abbreviated colour
                            }
                        }
                        String strUpdatedItem = replaceAbbreviation(strOriginal, strLanguage);
                        if ("fr".equalsIgnoreCase(strLanguage) && strUpdatedItem.equals(strOriginal) 
                                    && (strOriginal.indexOf("-") > 0))
                        {
                            // may be hyphen separated colours, so examine each possible half separately
                            // if there are three hyphens, then both colours have hyphens
                            // if two then either first or last
                            String strCopy=strUpdatedItem;
                            List<Integer> alSplit = MeroUtils.indexOfAll(strUpdatedItem, "-");
                            int nMin = 0;
                            int nMax = alSplit.size();
                            if (alSplit.size() == 3)
                            {
                                nMin=1;
                                nMax=2;
                            }
                            for(int l = nMin; l < nMax; l++)
                            {
                                String strFirst = strUpdatedItem.substring(0, (int)alSplit.get(l));
                                String strLast = strUpdatedItem.substring((int)alSplit.get(l) + 1);
                                strUpdatedItem = replaceAbbreviation(strFirst, strLanguage)
                                        + "-" + 
                                        replaceAbbreviation(strLast, strLanguage);
                                
                                if (!strCopy.equals(strUpdatedItem))
                                        break;
                            }
                        }
                        if (bSpace && !"".equals(strUpdated2))
                            strUpdated2 += "/";

                        strUpdated2 += strUpdatedItem;
                    }
                    if (!"".equals(strUpdated1))
                        strUpdated1 += " ";
                    
                    strUpdated1 += strUpdated2;
                }
                if (!"".equals(strUpdated))
                    strUpdated += ", ";

                strUpdated += strUpdated1;
            } 

            strUpdated = strUpdated.replaceAll("\\s+", " ");  // double spaces

            return strUpdated;
        }
    private String replaceAbbreviation(String strOriginal, String strLanguage)
    {
        if (configAbbreviations != null)
            return configAbbreviations.replaceAbbreviation(strOriginal, strLanguage);

        return null;
    }
    private String getColourListRegEx(String strLanguage)
    {
        // problem with order of execution of static elements
        return configColours.getColourListRegEx(strLanguage);
        //return "blue|red|black";
    }


    private String getFabricListRegEx(String strLanguage)
    {
        return configFabrics.getFabricListRegEx(strLanguage);
    }

    private String getFullColourListRegEx(String strLanguage)
    {
        // including fabrics
        // 20130222 put fabrics first (as longer)
        return getFabricListRegEx(strLanguage) + "|" + getColourListRegEx(strLanguage);
    }

}
