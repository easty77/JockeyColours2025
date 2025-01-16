/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.service.utils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
/**
 *
 * @author Simon
 */
public class StringUtils {

 public static String join(ArrayList<String> arList, String strDelimiter)
 {
     return join(arList, strDelimiter, false);
 }
 public static String join(List<String> arList, String strDelimiter, boolean bQuotes)
 {
     StringBuilder builder = new StringBuilder();
     Iterator<String> iter = arList.iterator();
     while (iter.hasNext())
     {
         String strItem = iter.next();
         if (bQuotes)
             strItem = "'" + strItem.replace("'", "''") + "'";
         if (builder.length() > 0)
         {
            builder.append(strDelimiter).append(" ");
         }
         builder.append(strItem);
     }                    
     return builder.toString();
 }
public static String join(String[] aList, String strDelimiter)
 {
     StringBuilder builder = new StringBuilder();
     for (int i = 0; i < aList.length; i++)
     {
         String strItem = aList[i];
         {
             if (builder.length() > 0)
             {
                 builder.append(strDelimiter).append(" ");
             }
             builder.append(strItem);
         }
     }                    
     return builder.toString();
 }
public static String upperFirstLetter(String strOriginal)
    {
     String[] tokens = strOriginal.split("\\s");
     String strResult="";
     for(int i = 0; i < tokens.length; i++)
     {
         String[] tokens1 = tokens[i].split("-");
         String strResult1="";
         for(int j = 0; j < tokens1.length; j++)
         {
            char cCapital = Character.toUpperCase(tokens1[j].charAt(0));
            strResult1 += (((j > 0) ? "-" : "") + (cCapital + tokens1[j].substring(1, tokens1[j].length())));
         }
         strResult += (((i > 0) ? " " : "") + strResult1);
     }

     return strResult;
 }
 public static String convert2Singular(String strOriginal)
 {
    String strResult = strOriginal.trim();
    if (strResult.charAt(strResult.length() - 1) == 's')
        return strResult.substring(0, strResult.length()-1);
    else
        return strResult;
 }

 public static String getOrdinalString(int nCardinal)
 {
    String strOrdinal = String.valueOf(nCardinal);
    return strOrdinal + getOrdinalSuffix(nCardinal);
 }

 public static String getOrdinalSuffix(int nCardinal)
 {
     String strSuffix="";
      if ((nCardinal >= 11) && (nCardinal <= 13))
         strSuffix = "th";
     else
     {
        int nRemainder = nCardinal%10;
        if (nRemainder == 1)
        {
             strSuffix = "st";
        }
        else if (nRemainder == 2)
        {
            strSuffix = "nd";
        }
        else if (nRemainder == 3)
        {
            strSuffix = "rd";
        }
       else
           strSuffix = "th";
     }

     return strSuffix;
}
 
public static Map<String, List<String>> getQueryParams(String url) {
    try {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        String[] urlParts = url.split("\\?");
        if (urlParts.length > 1) {
            String query = urlParts[1];
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                String key = URLDecoder.decode(pair[0], "UTF-8");
                String value = "";
                if (pair.length > 1) {
                    value = URLDecoder.decode(pair[1], "UTF-8");
                }

                List<String> values = params.get(key);
                if (values == null) {
                    values = new ArrayList<String>();
                    params.put(key, values);
                }
                values.add(value);
            }
        }

        return params;
    } catch (UnsupportedEncodingException ex) {
        throw new AssertionError(ex);
    }
}

public static List<Integer> createIntegerList(int[] aInts) 
{
    List<Integer> intList = new ArrayList<Integer>();
    for (int index = 0; index < aInts.length; index++) 
    {
        intList.add(aInts[index]);
    }
    return intList;
}
public static int[] toIntArray(List<Integer> list)
{
  int[] ret = new int[list.size()];
  for(int i = 0;i < ret.length;i++)
  {
        ret[i] = list.get(i);
  }
  return ret;
}
public static long[] toLongArray(List<Long> list)
{
  long[] ret = new long[list.size()];
  for(int i = 0;i < ret.length;i++)
  {
        ret[i] = list.get(i);
  }
  return ret;
}
public static int getNumeric(String strText)
{
    strText= strText.toLowerCase();
    String[] astrSplit = strText.split("[a-z]");
    if (astrSplit.length > 0)       // the case if string is only text
    {
        String strNumeric = astrSplit[0];
        try
        {
            return Integer.parseInt(strNumeric);
        }
        catch(NumberFormatException e)
        {

        }
    }
    return 0;
}
public static int getNumericEnd(String strText)
{
    strText= strText.toLowerCase();
    String[] astrSplit = strText.split("[a-z]");
    if (astrSplit.length > 0)       // the case if string is only text
    {
        String strNumeric = astrSplit[1];
        try
        {
            return Integer.parseInt(strNumeric);
        }
        catch(NumberFormatException e)
        {

        }
    }
    return 0;
}
public static List<Integer> indexOfAll(String strInput, String strSearch)
{
    List<Integer> alOccurrences = new ArrayList<Integer>();
    int nIndex = strInput.indexOf(strSearch);
    while (nIndex >= 0) {
        alOccurrences.add(nIndex);
        nIndex = strInput.indexOf(strSearch, nIndex + 1);
    }    
    
    return alOccurrences;
}
public static String convertInputStream(InputStream inputStream)
{
    Scanner sc = new Scanner(inputStream);
      //Reading line by line from scanner to StringBuffer
      StringBuffer sb = new StringBuffer();
      while(sc.hasNext()){
         sb.append(sc.nextLine());
         sb.append('\n');
      }
      return sb.toString();
}
}
