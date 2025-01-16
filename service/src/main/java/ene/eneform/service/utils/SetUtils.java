/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.utils;

import java.util.*;

/**
 *
 * @author Simon
 */
public class SetUtils {
    
    public static Pair<Integer, Integer> getMinMax(Set<Integer> values)
    {
        int nMin = 0;
        int nMax = 0;
        List<Integer> list = new ArrayList<Integer>(values);
        Collections.sort(list);
        if (list.size() > 0)
        {
            nMin = list.get(0); 
            nMax = list.get(list.size() - 1);
        }
        return new Pair(nMin, nMax);
    }
    
    public static String toQuotedList(Collection<String> alNames)
    {
        String strReturn = "";
        Iterator<String> iter = alNames.iterator();
        while(iter.hasNext())
        {
            String strCurrent = iter.next();
            if (!"".equals(strReturn))
            {
                strReturn += ", ";
            }
            strReturn += "'" + strCurrent.replace("'", "''") + "'";
        }
        
        return strReturn;
    }
    public static int getPositionInArray(String[] astrValues, String strSearch)
    {
        for(int i = 0; i < astrValues.length; i++)
        {
            if (astrValues[i].equalsIgnoreCase(strSearch))
                return i;
        }
        return astrValues.length;
    }
}
