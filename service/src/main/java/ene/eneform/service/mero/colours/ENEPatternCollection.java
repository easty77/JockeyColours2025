/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.service.mero.colours;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author Simon
 */
public class ENEPatternCollection implements Serializable{

    private ArrayList<ENEPattern> m_alPatterns = new ArrayList<ENEPattern>();
    private ArrayList<String> m_alPatternNames = new ArrayList<String>();
    private ArrayList<String> m_alSymmetricPatternNames = new ArrayList<String>();
    private String m_strPatternListRegEx = "";

    public ENEPatternCollection(ArrayList<ENEPattern> alPatterns)
    {
        m_alPatterns = alPatterns;
        Iterator<ENEPattern> iter = m_alPatterns.iterator();
        while(iter.hasNext())
        {
            ENEPattern pattern = iter.next();
            String strPattern = pattern.getText();
            m_alPatternNames.add(strPattern);

 
            if (pattern.isSymmetric())
                m_alSymmetricPatternNames.add(strPattern);
         }
        // For RegEx needs to be ordered, so ..
         // first need to sort, with shortest first and then in alphabetical order
        Collections.sort(m_alPatternNames, new PatternComparator());
        Iterator<String> iter1 = m_alPatternNames.iterator();
        while(iter1.hasNext())
        {
            if (!"".equals(m_strPatternListRegEx))
                m_strPatternListRegEx += "|";

            m_strPatternListRegEx += iter1.next();
        }
        //System.out.println(m_strPatternListRegEx);
    }
    public ArrayList<ENEPattern> getPatternList()
    {
        return m_alPatterns;
    }
    public ArrayList<String> getPatternNameList()
    {
        return m_alPatternNames;
    }
    public ArrayList<String> getSymmetricPatternNameList()
    {
        return m_alSymmetricPatternNames;
    }
    public String getPatternListRegEx()
    {
        return m_strPatternListRegEx;
    }
    public boolean contains(String strPatternName)
    {
        return m_alPatternNames.contains(strPatternName);
    }
    public ENEPattern getPattern(String strPatternName)
    {
        for(int i = 0; i < m_alPatterns.size(); i++)
        {
            ENEPattern pattern = m_alPatterns.get(i);
            if (strPatternName.equalsIgnoreCase(pattern.getText()))
                return pattern;
        }
        return null;
    }
    public void addPattern(ENEPattern pattern)
    {
        String strName = pattern.getText();
        m_alPatterns.add(pattern);
        m_alPatternNames.add(strName);
        m_strPatternListRegEx += ("|" + strName);
    }
    private class PatternComparator implements Comparator<String>
    {
        public int compare(String strPattern1, String strPattern2)
        {
            if (strPattern1.length() < strPattern2.length())
                return -1;
            else if (strPattern1.length() == strPattern2.length())
            {
                // alphabetical
                return strPattern1.compareTo(strPattern2);
            }
            else
                return 1;
        }
    }
}
