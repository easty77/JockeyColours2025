package ene.eneform.mero.parse;

import ene.eneform.mero.config.ENEColoursEnvironment;
import java.io.Serializable;

public class ENEColoursParserMatch implements Serializable
{
    private int m_nStart;
    private int m_nEnd;
    private String m_strGroup;
    private String m_strMatchType;

    public ENEColoursParserMatch(int nStart, int nEnd, String strGroup, String strMatchType)
    {
        m_nStart = nStart;
        m_nEnd = nEnd;
        m_strGroup = strGroup;
        m_strMatchType = strMatchType;
    }

    public String extractFromString(String strDescription)
    {
        String strUpdated;
        if (m_nStart == 0)
        {
            strUpdated = strDescription.substring(m_nEnd);
        }
        else
        {
            String strStart = strDescription.substring(0, m_nStart);
            String strEnd = strDescription.substring(m_nEnd);
            ENEColoursEnvironment.getInstance().debug("Non-zero string start returns:" + strStart + strEnd);
            strUpdated = strStart + strEnd;
        }

        return strUpdated.trim();
    }

    @Override public String toString()
    {
        return (m_nStart +"-" + m_nEnd + "-" + m_strMatchType + "-" + m_strGroup);
    }

    public String getMatchType()
    {
        return m_strMatchType;
    }

}
