package ene.eneform.service.mero.model;

import ene.eneform.service.mero.config.ENEColoursEnvironment;

import java.io.Serializable;
import java.util.ArrayList;

public class ENEColoursElementPattern implements Serializable{
    private ENEColoursEnvironment environment;
	protected ArrayList<ENEFillItem> m_alColours = new ArrayList<ENEFillItem>();

    protected String m_strLanguage;
    protected String m_strPattern;

    protected boolean m_bPropagate = true;	// colour/pattern can be applied to subsequent elements (e.g. jacket -> sleeves)

    protected String m_strAdditionalText = null;

    public ENEColoursElementPattern(ENEColoursEnvironment environment, String strLanguage, String strPattern)
    {
        this.environment = environment;
        m_strLanguage = strLanguage;
        m_strPattern = strPattern;
    }
   public ENEColoursElementPattern(ENEColoursEnvironment environment, String strLanguage, String strPattern, String strColour)
    {
        this.environment = environment;
        m_strLanguage = strLanguage;
        m_strPattern = strPattern;
        setColour(strColour);
    }
   public ENEColoursElementPattern(ENEColoursEnvironment environment, String strLanguage, String strPattern, ArrayList<String> astrColourList)
    {
        this.environment = environment;
        m_strLanguage = strLanguage;
        m_strPattern = strPattern;
        for(int i = 0; i < astrColourList.size(); i++)
        {
            setColour(astrColourList.get(i));
        }
    }
    public ENEColoursElementPattern(ENEColoursEnvironment environment, ENEColoursElementPattern pattern)
    {
        this.environment = environment;
        m_strLanguage = pattern.getLanguage();
        m_strPattern = pattern.getPattern();
        for(int i = 1; i <= pattern.getColourCount(); i++)
        {
                setColour(pattern.getColour(i));
        }
    }

     public String getLanguage()
    {
            return m_strLanguage;
    }
   public String getPattern()
    {
            return m_strPattern;
    }
   public String getDefinition()
   {
       String strDefinition = m_strPattern;
       if (m_strAdditionalText != null)
           strDefinition += ("-" + m_strAdditionalText);
       for(int i = 0; i < m_alColours.size(); i++)
       {
           ENEFillItem colour = m_alColours.get(i);
           if ((i == 0) && (m_strAdditionalText == null))
               strDefinition += "-";
           else
               strDefinition += ",";
           strDefinition += colour.getText();
       }
       
       return strDefinition;
   }
    public String getBasePattern()
    {
        String strPattern = m_strPattern;
        String strLastChar = strPattern.substring(strPattern.length() - 1);     // remove colours number
        if ("0123456789".indexOf(strLastChar) >= 0)
        {
             strPattern = strPattern.substring(0, strPattern.length() - 1);
        }
        
        return strPattern;
    }
    public int getColourCount()
    {
            return m_alColours.size();
    }
    public ArrayList<ENEFillItem> getColourList()
    {
            return m_alColours;
    }

    public final void setColour(String strColour)
    {
        if (environment.isFabric(strColour, m_strLanguage))
        {
            m_alColours.add(environment.getFabricItem(strColour, m_strLanguage));
        }
        else
        {
            ENEColourItem item = environment.getColourItem(strColour, m_strLanguage);
            if (item != null)
                m_alColours.add(item);
            else
                System.out.println("setColour: Invalid colour-" + strColour);
        }
    }
    public final void setColour(ENEFillItem colour)
    {
        m_alColours.add(colour);
    }
    public void replaceColour(int nColour, ENEFillItem colour)
    {
        if(m_alColours.size() >= nColour)
            m_alColours.set(nColour - 1, colour);
    }

    public ENEFillItem getColour(int nColour)
    {
        if ((nColour > 0) && (nColour <= m_alColours.size()))
            return m_alColours.get(nColour-1);
        else
            return null;
    }
    public boolean canPropagate()
    {
        return m_bPropagate;
    }
    public void setPropagate(boolean bPropagate)
    {
        m_bPropagate = bPropagate;
    }

    @Override public String toString()
    {
        String strContent=getPattern() + "-";

        if ("text".equals(m_strPattern))
            strContent += ("=" + m_strAdditionalText);

        for(int i = 0; i < m_alColours.size(); i++)
        {
                if (i > 0)
                        strContent += ",";
                if (m_alColours.get(i) != null)
                    strContent += m_alColours.get(i).getText();
                else
                    strContent += "Unknown Colour";
        }

        return strContent;
    }

    public String getAdditionalText()
    {
        return m_strAdditionalText;
    }

    public void setAdditionalText(String strAdditionalText)
    {
        m_strAdditionalText = strAdditionalText;
    }
    public String getTartan()
    {
        for(int i = 0; i < m_alColours.size(); i++)
        {
            String strColour = m_alColours.get(i).getText();
            if(environment.isFabric(strColour, m_strLanguage))
                    return environment.getFabricItem(strColour, m_strLanguage).getResourceName();
        }

        return null;
    }
    public String getErrorString()
    {
        String strContent="";

        if(m_alColours.size() == 0)
        {
            strContent += (m_strPattern + "-No colour");
        }

        if ("text".equals(m_strPattern) && ((m_strAdditionalText == null) || "".equals(m_strAdditionalText)))
            strContent += ("Text missing");

        return strContent;
    }
}


