/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.utils;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Simon
 */
public class ArrayUtils {
    
public static int[] convertIntegers(List<Integer> integers)
{
    int[] ret = new int[integers.size()];
    Iterator<Integer> iterator = integers.iterator();
    for (int i = 0; i < ret.length; i++)
    {
        ret[i] = iterator.next().intValue();
    }
    return ret;
}
public static boolean contains(String[] astrValues, String strValue, boolean bMatchCase)
{
    if (!bMatchCase)
        return contains(astrValues, strValue);
    
    for(int i = 0; i < astrValues.length; i++)
    {
        if (astrValues[i].equalsIgnoreCase(strValue))
            return true;
    }
    return false;
}
public static boolean contains(String[] astrValues, String strValue)
{
    for(int i = 0; i < astrValues.length; i++)
    {
        if (astrValues[i].equals(strValue))
            return true;
    }
    return false;
}
}
