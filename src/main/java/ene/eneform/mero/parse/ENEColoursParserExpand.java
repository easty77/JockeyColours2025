package ene.eneform.mero.parse;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ENEColoursParserExpand implements Serializable
{
    private String m_strSearch;
    private String m_strReplace;
    private String m_strExpandType;
    private Pattern m_pattern;

    public ENEColoursParserExpand(String strSearch, String strReplace, String strExpandType)
    {
        m_strSearch = strSearch;
        m_strReplace = strReplace;
        m_strExpandType = strExpandType;
        m_pattern = Pattern.compile(strSearch);
    }
    public String expandString(String strDescription)
    {
         Matcher matcher = m_pattern.matcher(strDescription);
         String strExpanded = matcher.replaceAll(m_strReplace);

//         if (!strExpanded.equals(strDescription))
//             trace("Expanded: " + strExpanded);

         return strExpanded;
    }
    public String getExpandType()
    {
         return m_strExpandType;
    }
    @Override public String toString()
    {
 	   return m_strSearch + "-" + m_strReplace + "-" + m_strExpandType;
    }
}
