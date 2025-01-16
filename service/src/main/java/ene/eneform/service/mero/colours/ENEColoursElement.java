package ene.eneform.service.mero.colours;

//import ene.racingcolours.ENEColoursElementFullPattern;
import ene.eneform.service.mero.action.ENEPatternAction;
import ene.eneform.service.mero.config.ENEColoursEnvironment;
import ene.eneform.service.mero.config.ENEOrganisation;
import ene.eneform.service.mero.config.ENEOrganisationList;
import ene.eneform.service.mero.utils.ENEFillItem;

import java.awt.Color;
import java.awt.Shape;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


public class ENEColoursElement implements Serializable
{
    private final ENEColoursEnvironment environment;
    // only used in AWTColoursElement and derivatives
    protected Shape m_shape = null;

    public static String JACKET = "ENEJacket";
    public static String SLEEVES = "ENESleeves";
    public static String CAP = "ENECap";
    
    protected String m_strLanguage;
    protected String m_strDefinition = null;
    protected ENEFillItem m_colour = null;
    protected ArrayList<ENEColoursElementPattern> m_patternList = new ArrayList<ENEColoursElementPattern>();
    protected int m_nXOffset = 0;
    protected int m_nYOffset = 0;
    protected int m_nWidth = 0;
    protected int m_nHeight = 0;
    protected String m_strType;

    protected Color m_pageColour = Color.WHITE;       // the colour of the page behind - required for needs Outline

    public ENEColoursElement(ENEColoursEnvironment environment, String strLanguage, String strType)
    {
        this(environment, strLanguage, strType, null);
    }
   public ENEColoursElement(ENEColoursEnvironment environment, String strLanguage, String strType, String strDefinition)
    {
        this.environment = environment;
        m_strLanguage = strLanguage;
        m_strType = strType;
        m_strDefinition = strDefinition;
        // syntax is: white:NSW/albury racing club logo-dark blue,lime green
        // red:cuffs-white:armlets-blue
        // red:text in circle-W,white
        if (m_strDefinition != null)
        {
            String astrDefinitions[] = strDefinition.split(":");
            String strColour = astrDefinitions[0];
            //m_colour = ENEColoursEnvironment.getInstance().getColourItem(strColour, m_strLanguage);
            m_colour = getColourImpl(strColour);
            for(int i = 1; i < astrDefinitions.length; i++)
            {
                String strPattern = astrDefinitions[i];
                String astrPatternElements[] = strPattern.split("-");   // separates pattern from comma separated list of colours
                ENEColoursElementPattern pattern = new ENEColoursElementPattern(environment, m_strLanguage, astrPatternElements[0]);
                if (astrPatternElements.length > 1)
                {
                    String strColours = astrPatternElements[1];
                    String astrColourElements[] = strColours.split(",");
                    int nFirst = 0;
                    if (strPattern.indexOf("text") == 0)
                    {
                        pattern.setAdditionalText(astrColourElements[0]);
                        nFirst = 1;
                    }
                    for (int j = nFirst; j < astrColourElements.length; j++)
                    {
                        String strColour1 = astrColourElements[j];
                        //pattern.setColour(ENEColoursEnvironment.getInstance().getColourItem(strColour1, m_strLanguage));
                        pattern.setColour(getColourImpl(strColour1));
                    }
                }
                m_patternList.add(pattern);
            }
        }
    }
   public String getType()
   {
       return m_strType;
   }
   protected ENEPatternAction getPatternAction(String strPattern){return environment.getConfigPatterns().getPatternAction(m_strType, strPattern, m_strLanguage);}
    public boolean isValidPattern(String strPattern){return isPattern(strPattern);}
    public boolean isPattern(String strPattern){return environment.getConfigPatterns().getPatternNameList(m_strType, m_strLanguage).contains(strPattern);}

    public void setPageColour(Color pageColour)
    {
        m_pageColour = pageColour;
    }
    public ArrayList<String> getOrganisationPatterns(String strType, String strOrganisation)
    {
        ENEOrganisation organisation = environment.getConfigOrganisations().getOrganisation(strOrganisation);
        ENEOrganisationList list = organisation.getList(strType);
        return list.getList();
    }

    public  ArrayList<String> getOrganisationPatterns(String strOrganisation)
    {
        ENEOrganisation organisation = environment.getConfigOrganisations().getOrganisation(strOrganisation);
        ENEOrganisationList list = organisation.getList(m_strType);
        return list.getList();
    }
    public String getDefinition()
    {
    	if ( m_strDefinition != null)
            return m_strDefinition;
        else
        {
            String strDefinition = "";
            if (m_colour != null)
                strDefinition = m_colour.getText().trim();
            for(int i = 0; i < m_patternList.size(); i++)
            {
                ENEColoursElementPattern pattern = m_patternList.get(i);
                if (pattern != null)
                {
                    strDefinition += ":";
                    strDefinition += pattern.getDefinition();
                }
                else
                    System.out.println("getDefinition Null pattern: " + strDefinition);
            }
            return strDefinition;
        }
    }
    public ENEColoursElementPattern getPrimaryPattern()
    {
    	if (m_patternList.size() > 0)
        {
    		ENEColoursElementPattern pattern = m_patternList.get(0);
                if (environment.getConfigPatterns().isPrimaryPattern(m_strType, pattern.getPattern(), m_strLanguage))
                        return pattern;
        }
        
   	return null;
    }
    public ArrayList<String> getPatternList()
    {
    	ArrayList<String> lstPatterns = new ArrayList<String>();
    	Iterator<ENEColoursElementPattern> iter = m_patternList.iterator();
    	while(iter.hasNext())
    	{
    		String strPattern = iter.next().getPattern();
    		lstPatterns.add(strPattern);
    	}
    	return lstPatterns;
    }
    public ArrayList<String> getExtendedPatternList()
    {
    	ArrayList<String> lstPatterns = new ArrayList<String>();
    	Iterator<ENEColoursElementPattern> iter = m_patternList.iterator();
    	while(iter.hasNext())
    	{
            ENEColoursElementPattern pattern = iter.next();
    		String strPattern = pattern.getBasePattern();   // remove colours number
                 
                strPattern = environment.getConfigPatterns().convertSynonym(m_strType, strPattern, m_strLanguage);

                int nColours = pattern.getColourCount();
                strPattern += nColours;
    		lstPatterns.add(strPattern);
    	}
    	return lstPatterns;
    }
    public ArrayList<ENEFillItem> getColourList(ArrayList<ENEFillItem> lstColours)
    {
        if (!lstColours.contains(m_colour))
            lstColours.add(m_colour);
    	Iterator<ENEColoursElementPattern> iter = m_patternList.iterator();
    	while(iter.hasNext())
    	{
    		ENEColoursElementPattern pattern = iter.next();
                for(int i = 1; i <= pattern.getColourCount(); i++)
                {
                    ENEFillItem colour  = pattern.getColour(i);
                    if ((colour != null) && !lstColours.contains(colour))
                       lstColours.add(colour);
                }
      	}
    	return lstColours;
    }
    public ENEColoursElementPattern getPatternMatch(String strPattern)
    {
    	Iterator<ENEColoursElementPattern> iter = m_patternList.iterator();
    	while(iter.hasNext())
    	{
            ENEColoursElementPattern pattern = iter.next();
            String strPattern1 = pattern.getPattern();
            if (patternMatch(strPattern1,strPattern)) // need fuzzy match e.g. stripes v striped
                return pattern;
    	}

        return null;
    }
    private static boolean patternMatch(String strPattern1, String strPattern2)
    {
        if (strPattern1.equals(strPattern2))
            return true;
        else if((strPattern1.length() == strPattern2.length()) && (strPattern1.substring(0, strPattern1.length() - 1).equals(strPattern2.substring(0, strPattern2.length() - 1))))
            return true;
        else if((strPattern1.length() == strPattern2.length() + 1) && (strPattern1.substring(0, strPattern1.length() - 1).equals(strPattern2)))
            return true;
        else if((strPattern2.length() == strPattern1.length() + 1) && (strPattern2.substring(0, strPattern2.length() - 1).equals(strPattern1)))
            return true;

        return false;

    }
    public void setPrimaryPattern(ENEColoursElementPattern pattern)
    {
        m_strDefinition = null;
        ArrayList<ENEColoursElementPattern> saveList = m_patternList;
    	m_patternList = new ArrayList<ENEColoursElementPattern>();
        m_patternList.add(pattern);
        Iterator<ENEColoursElementPattern> iter = saveList.iterator();
        while(iter.hasNext())
        {
            m_patternList.add(iter.next());
        }
    }
    public void setPattern(ENEColoursElementPattern pattern)
    {
        m_strDefinition = null;
    	m_patternList.add(pattern);
    }
    public void removePatterns()
    {
        m_strDefinition = null;
    	m_patternList = new ArrayList<ENEColoursElementPattern>();
    }
    
    public void updateColour(ENEFillItem current, ENEFillItem updated)
    {
        m_strDefinition = null;
    	if ((m_colour != null) && m_colour.equals(current))
            m_colour = updated;
   	Iterator<ENEColoursElementPattern> iter = m_patternList.iterator();
    	while(iter.hasNext())
    	{
    		ENEColoursElementPattern pattern = iter.next();
                for(int i = 1; i <= pattern.getColourCount(); i++)
                {
                    ENEFillItem colour = pattern.getColour(i);
                    if ((colour != null) && (colour.equals(current)))
                       pattern.replaceColour(i, updated);
                }
      	}
    }

     public int getWidth(){return m_nWidth;}
    public int getHeight(){return m_nHeight;}

    public int getXOffset(){return m_nXOffset;}
    public int getYOffset(){return m_nYOffset;}
    protected void setXOffset(int nXOffset){m_nXOffset=nXOffset;}
    protected void setYOffset(int nYOffset){m_nYOffset=nYOffset;}
    protected void setWidth(int nWidth){m_nWidth=nWidth;}
    protected void setHeight(int nHeight){m_nHeight=nHeight;}
    public Color getPageColour(){return m_pageColour;}
private ENEFillItem getColourImpl(String strColour)
    {
    	if (environment.getConfigFabrics().isFabric(strColour, m_strLanguage))
    		return environment.getConfigFabrics().getFabricItem(strColour, m_strLanguage);
        else
    		return environment.getColourItem(strColour, m_strLanguage);
    }
    public void setColour(String strColour)
    {
        m_strDefinition = null;
    	m_colour = getColourImpl(strColour);
    }

    public void setColour(ENEFillItem colour)
    {
        m_strDefinition = null;
        m_colour = colour;
    }

    public int getPatternCount()
    {
        return m_patternList.size();
    }

    
    public Iterator<ENEColoursElementPattern> getPatternIterator()
    {
        return m_patternList.iterator();
    }

    public ENEFillItem getColourItem()
    {
        return m_colour;
    }
/*    public Color getColour()
    {
        return m_colour.getColour();
    } */
    public String getTextColour()
    {
        if (m_colour != null)
            return m_colour.getText();
        else return "";
    }

    public String getFabric()
    {
	    if ((m_colour != null) && environment.getConfigFabrics().isFabric(m_colour.getText(), m_strLanguage))
	        return environment.getConfigFabrics().getFabricItem(m_colour.getText(), m_strLanguage).getResourceName();
		
	    for(int i = 0; i < m_patternList.size(); i++)
		{
			ENEColoursElementPattern pattern = m_patternList.get(i);
                        if (pattern != null)
                            return pattern.getTartan();
		}
	
	    return null;
	}

public String getImage()
{
    for(int i = 0; i < m_patternList.size(); i++)
    {
        ENEColoursElementPattern pattern = m_patternList.get(i);
         if (pattern != null)
        {
            ENEPatternAction action = getPatternAction(pattern.getPattern());
            if ((action != null) && action.hasImage())
            {
                String strAdditionalText = pattern.getAdditionalText();
                if ((strAdditionalText == null) || "".equals(strAdditionalText))
                    return pattern.getPattern();
                else
                    return strAdditionalText;
            }
        }
    }

    return null;
}
  

public boolean duplicateColours()
{
	if (m_patternList.size() > 0)
	{
		ENEColoursElementPattern pattern = m_patternList.get(0);
		if ((m_colour != null) && m_colour.equals(pattern.getColour(1)))
			return true;
	}
        
    return false;
}


@Override public String toString()
{
	String strContent = ""; 
        strContent += getTextColour();
	Iterator<ENEColoursElementPattern> iter = getPatternIterator();
	while(iter.hasNext())
	{
		ENEColoursElementPattern pattern = iter.next();
		strContent += (":" + pattern.toString());
	}
	
	return strContent;
}

public String getErrorString()
{
	String strContent = "";
        if (m_colour == null)
            strContent += "No colour specified";
	Iterator<ENEColoursElementPattern> iter = getPatternIterator();
	while(iter.hasNext())
	{
		ENEColoursElementPattern pattern = iter.next();
                String strContent1 = pattern.getErrorString();
                if (!"".equals(strContent1))
                    strContent += (":" + strContent1);
	}

        if (!"".equals(strContent))
            return m_strType + strContent;
        else
            return strContent;
}
}