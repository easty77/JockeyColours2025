/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.utils;

import java.util.regex.Matcher;

/**
 *
 * @author Simon
 */
public class RegExpUtils {
    
    public static int getMatcherGroupInt(Matcher m, int nGroup)
    {
        int nReturn = 0;
        
        String strReturn = m.group(nGroup);
        if ((strReturn != null) && !"".equals(strReturn))
        {
            try
            {
                nReturn = Integer.valueOf(strReturn);
            }
            catch(NumberFormatException e)
            {
                nReturn = -1;
            }
        }        
        
        return nReturn;
    }
    public static int getMatcherGroupIntxLast(Matcher m, int nGroup)
    {
        int nReturn = 0;
        
        String strReturn = m.group(nGroup);
        if ((strReturn != null) && !"".equals(strReturn))
        {
            strReturn = strReturn.trim();
            try
            {
                nReturn = Integer.valueOf(strReturn.substring(0, strReturn.length() - 1));
            }
            catch(NumberFormatException e)
            {
                nReturn = -1;
            }
        }        
        
        return nReturn;
    }
    public static double getMatcherGroupDouble(Matcher m, int nGroup)
    {
        double dReturn = 0.0;
        
        String strReturn = m.group(nGroup);
        if (strReturn != null)
        {
            try
            {
                dReturn = Double.valueOf(strReturn);
            }
            catch(NumberFormatException e)
            {
                  dReturn = -1.0;
            }
        }
         
         return dReturn;
    }
}
