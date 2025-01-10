// ********************************************************************
// *
// * Copyright IBM Corporation 2001, 2004
// * 
// * Web Lecture Services
// * 
// ********************************************************************
package ene.eneform.utils.wls;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


abstract public class Utility
{
static Hashtable<Character, String> mRssEncodeTable = new Hashtable<Character, String>();
private static String CHARSET_LATIN="ISO-8859-1";

static
{
	mRssEncodeTable.put(new Character('&'),"&amp;");
	mRssEncodeTable.put(new Character('<'),"&lt;");
	mRssEncodeTable.put(new Character('>'),"&gt;"); 
};
	
static Hashtable<Character, String> mHtmlEncodeTable = new Hashtable<Character, String>();

static
{
	mHtmlEncodeTable.put(new Character('\''),"&rsquo;");
	// apos; is XHTML but NOT HTML
	//mHtmlEncodeTable.put(new Character('\''),"&apos;");	
	mHtmlEncodeTable.put(new Character('"'),"&quot;");
	/*		4/2/01 SE: remove lt,gt as substitution turns valid HTML into plain text.
		Many component descriptions, contain HTML for use in formatting	
		mHtmlEncodeTable.put(new Character('<'),"&lt;");
		mHtmlEncodeTable.put(new Character('>'),"&gt;"); */
	mHtmlEncodeTable.put(new Character('�'),"&iexcl;");
	mHtmlEncodeTable.put(new Character('�'),"&cent;");
	mHtmlEncodeTable.put(new Character('�'),"&pound;");
	mHtmlEncodeTable.put(new Character('�'),"&curren;");
	mHtmlEncodeTable.put(new Character('�'),"&yen;");
	mHtmlEncodeTable.put(new Character('�'),"&brvbar;");
	mHtmlEncodeTable.put(new Character('�'),"&sect;");
	mHtmlEncodeTable.put(new Character('�'),"&uml;");
	mHtmlEncodeTable.put(new Character('�'),"&copy;");
	mHtmlEncodeTable.put(new Character('�'),"&ordf;");
	mHtmlEncodeTable.put(new Character('�'),"&laquo;");
	mHtmlEncodeTable.put(new Character('�'),"&not;");
	mHtmlEncodeTable.put(new Character('�'),"&shy;");
	mHtmlEncodeTable.put(new Character('�'),"&reg;");
	mHtmlEncodeTable.put(new Character('�'),"&macr;");
	mHtmlEncodeTable.put(new Character('�'),"&deg;");
	mHtmlEncodeTable.put(new Character('�'),"&plusmn;");
	mHtmlEncodeTable.put(new Character('�'),"&sup2;");
	mHtmlEncodeTable.put(new Character('�'),"&sup3;");
	mHtmlEncodeTable.put(new Character('�'),"&acute;");
	mHtmlEncodeTable.put(new Character('�'),"&micro;");
	mHtmlEncodeTable.put(new Character('�'),"&para;");
	mHtmlEncodeTable.put(new Character('�'),"&middot;");
	mHtmlEncodeTable.put(new Character('�'),"&cedil;");
	mHtmlEncodeTable.put(new Character('�'),"&sup1;");
	mHtmlEncodeTable.put(new Character('�'),"&ordm;");
	mHtmlEncodeTable.put(new Character('�'),"&raquo;");
	mHtmlEncodeTable.put(new Character('�'),"&frac14;");
	mHtmlEncodeTable.put(new Character('�'),"&frac12;");
	mHtmlEncodeTable.put(new Character('�'),"&frac34;");
	mHtmlEncodeTable.put(new Character('�'),"&iquest;");
	mHtmlEncodeTable.put(new Character('�'),"&Agrave;");
	mHtmlEncodeTable.put(new Character('�'),"&Aacute;");
	mHtmlEncodeTable.put(new Character('�'),"&Acirc;");
	mHtmlEncodeTable.put(new Character('�'),"&Atilde;");
	mHtmlEncodeTable.put(new Character('�'),"&Auml;");
	mHtmlEncodeTable.put(new Character('�'),"&Aring;");
	mHtmlEncodeTable.put(new Character('�'),"&AElig;");
	mHtmlEncodeTable.put(new Character('�'),"&Ccedil;");
	mHtmlEncodeTable.put(new Character('�'),"&Egrave;");
	mHtmlEncodeTable.put(new Character('�'),"&Eacute;");
	mHtmlEncodeTable.put(new Character('�'),"&Ecirc;");
	mHtmlEncodeTable.put(new Character('�'),"&Euml;");
	mHtmlEncodeTable.put(new Character('�'),"&Igrave;");
	mHtmlEncodeTable.put(new Character('�'),"&Iacute;");
	mHtmlEncodeTable.put(new Character('�'),"&Icirc;");
	mHtmlEncodeTable.put(new Character('�'),"&Iuml;");
	mHtmlEncodeTable.put(new Character('�'),"&ETH;");
	mHtmlEncodeTable.put(new Character('�'),"&Ntilde;");
	mHtmlEncodeTable.put(new Character('�'),"&Ograve;");
	mHtmlEncodeTable.put(new Character('�'),"&Oacute;");
	mHtmlEncodeTable.put(new Character('�'),"&Ocirc;");
	mHtmlEncodeTable.put(new Character('�'),"&Otilde;");
	mHtmlEncodeTable.put(new Character('�'),"&Ouml;");
	mHtmlEncodeTable.put(new Character('�'),"&times;");
	mHtmlEncodeTable.put(new Character('�'),"&Oslash;");
	mHtmlEncodeTable.put(new Character('�'),"&Ugrave;");
	mHtmlEncodeTable.put(new Character('�'),"&Uacute;");
	mHtmlEncodeTable.put(new Character('�'),"&Ucirc;");
	mHtmlEncodeTable.put(new Character('�'),"&Uuml;");
	mHtmlEncodeTable.put(new Character('�'),"&Yacute;");
	mHtmlEncodeTable.put(new Character('�'),"&THORN;");
	mHtmlEncodeTable.put(new Character('�'),"&szlig;");
	mHtmlEncodeTable.put(new Character('�'),"&agrave;");
	mHtmlEncodeTable.put(new Character('�'),"&aacute;");
	mHtmlEncodeTable.put(new Character('�'),"&acirc;");
	mHtmlEncodeTable.put(new Character('�'),"&atilde;");
	mHtmlEncodeTable.put(new Character('�'),"&auml;");
	mHtmlEncodeTable.put(new Character('�'),"&aring;");
	mHtmlEncodeTable.put(new Character('�'),"&aelig;");
	mHtmlEncodeTable.put(new Character('�'),"&ccedil;");
	mHtmlEncodeTable.put(new Character('�'),"&egrave;");
	mHtmlEncodeTable.put(new Character('�'),"&eacute;");
	mHtmlEncodeTable.put(new Character('�'),"&ecirc;");
	mHtmlEncodeTable.put(new Character('�'),"&euml;");
	mHtmlEncodeTable.put(new Character('�'),"&igrave;");
	mHtmlEncodeTable.put(new Character('�'),"&iacute;");
	mHtmlEncodeTable.put(new Character('�'),"&icirc;");
	mHtmlEncodeTable.put(new Character('�'),"&iuml;");
	mHtmlEncodeTable.put(new Character('�'),"&eth;");
	mHtmlEncodeTable.put(new Character('�'),"&ntilde;");
	mHtmlEncodeTable.put(new Character('�'),"&ograve;");
	mHtmlEncodeTable.put(new Character('�'),"&oacute;");
	mHtmlEncodeTable.put(new Character('�'),"&ocirc;");
	mHtmlEncodeTable.put(new Character('�'),"&otilde;");
	mHtmlEncodeTable.put(new Character('�'),"&otilde;");
	mHtmlEncodeTable.put(new Character('�'),"&divide;");
	mHtmlEncodeTable.put(new Character('�'),"&oslash;");
	mHtmlEncodeTable.put(new Character('�'),"&ugrave;");
	mHtmlEncodeTable.put(new Character('�'),"&uacute;");
	mHtmlEncodeTable.put(new Character('�'),"&ucirc;");
	mHtmlEncodeTable.put(new Character('�'),"&uuml;");
	mHtmlEncodeTable.put(new Character('�'),"&yacute;");
	mHtmlEncodeTable.put(new Character('�'),"&thorn;");
	mHtmlEncodeTable.put(new Character('�'),"&yuml;");
}

static Hashtable<Character, String> mHtmlEncodeTable1 = new Hashtable<Character, String>();

static
{
	mHtmlEncodeTable1.put(new Character('\''),"&#39;");
	mHtmlEncodeTable1.put(new Character(' '),"&#160;");
	mHtmlEncodeTable1.put(new Character('�'),"&#161;");
	mHtmlEncodeTable1.put(new Character('�'),"&#162;");
	mHtmlEncodeTable1.put(new Character('�'),"&#163;");
	mHtmlEncodeTable1.put(new Character('�'),"&#164;");
	mHtmlEncodeTable1.put(new Character('�'),"&#165;");
	mHtmlEncodeTable1.put(new Character('�'),"&#166;");
	mHtmlEncodeTable1.put(new Character('�'),"&#167;");
	mHtmlEncodeTable1.put(new Character('�'),"&#168;");
	mHtmlEncodeTable1.put(new Character('�'),"&#169;");
	mHtmlEncodeTable1.put(new Character('�'),"&#170;");
	mHtmlEncodeTable1.put(new Character('�'),"&#171;");
	mHtmlEncodeTable1.put(new Character('�'),"&#172;");
	mHtmlEncodeTable1.put(new Character('�'),"&#173;");
	mHtmlEncodeTable1.put(new Character('�'),"&#174;");
	mHtmlEncodeTable1.put(new Character('�'),"&#175;");
	mHtmlEncodeTable1.put(new Character('�'),"&#176;");
	mHtmlEncodeTable1.put(new Character('�'),"&#177;");
	mHtmlEncodeTable1.put(new Character('�'),"&#178;");
	mHtmlEncodeTable1.put(new Character('�'),"&#179;");
	mHtmlEncodeTable1.put(new Character('�'),"&#180;");
	mHtmlEncodeTable1.put(new Character('�'),"&#181;");
	mHtmlEncodeTable1.put(new Character('�'),"&#182;");
	mHtmlEncodeTable1.put(new Character('�'),"&#183;");
	mHtmlEncodeTable1.put(new Character('�'),"&#184;");
	mHtmlEncodeTable1.put(new Character('�'),"&#185;");
	mHtmlEncodeTable1.put(new Character('�'),"&#186;");
	mHtmlEncodeTable1.put(new Character('�'),"&#187;");
	mHtmlEncodeTable1.put(new Character('�'),"&#188;");
	mHtmlEncodeTable1.put(new Character('�'),"&#189;");
	mHtmlEncodeTable1.put(new Character('�'),"&#190;");
	mHtmlEncodeTable1.put(new Character('�'),"&#191;");
	mHtmlEncodeTable1.put(new Character('�'),"&#192;");
	mHtmlEncodeTable1.put(new Character('�'),"&#193;");
	mHtmlEncodeTable1.put(new Character('�'),"&#194;");
	mHtmlEncodeTable1.put(new Character('�'),"&#195;");
	mHtmlEncodeTable1.put(new Character('�'),"&#196;");
	mHtmlEncodeTable1.put(new Character('�'),"&#197;");
	mHtmlEncodeTable1.put(new Character('�'),"&#198;");
	mHtmlEncodeTable1.put(new Character('�'),"&#199;");
	mHtmlEncodeTable1.put(new Character('�'),"&#200;");
	mHtmlEncodeTable1.put(new Character('�'),"&#201;");
	mHtmlEncodeTable1.put(new Character('�'),"&#202;");
	mHtmlEncodeTable1.put(new Character('�'),"&#203;");
	mHtmlEncodeTable1.put(new Character('�'),"&#204;");
	mHtmlEncodeTable1.put(new Character('�'),"&#205;");
	mHtmlEncodeTable1.put(new Character('�'),"&#206;");
	mHtmlEncodeTable1.put(new Character('�'),"&#207;");
	mHtmlEncodeTable1.put(new Character('�'),"&#208;");
	mHtmlEncodeTable1.put(new Character('�'),"&#209;");
	mHtmlEncodeTable1.put(new Character('�'),"&#210;");
	mHtmlEncodeTable1.put(new Character('�'),"&#211;");
	mHtmlEncodeTable1.put(new Character('�'),"&#212;");
	mHtmlEncodeTable1.put(new Character('�'),"&#213;");
	mHtmlEncodeTable1.put(new Character('�'),"&#214;");
	mHtmlEncodeTable1.put(new Character('�'),"&#215;");
	mHtmlEncodeTable1.put(new Character('�'),"&#216;");
	mHtmlEncodeTable1.put(new Character('�'),"&#217;");
	mHtmlEncodeTable1.put(new Character('�'),"&#218;");
	mHtmlEncodeTable1.put(new Character('�'),"&#219;");
	mHtmlEncodeTable1.put(new Character('�'),"&#220;");
	mHtmlEncodeTable1.put(new Character('�'),"&#221;");
	mHtmlEncodeTable1.put(new Character('�'),"&#222;");
	mHtmlEncodeTable1.put(new Character('�'),"&#223;");
	mHtmlEncodeTable1.put(new Character('�'),"&#224;");
	mHtmlEncodeTable1.put(new Character('�'),"&#225;");
	mHtmlEncodeTable1.put(new Character('�'),"&#226;");
	mHtmlEncodeTable1.put(new Character('�'),"&#227;");
	mHtmlEncodeTable1.put(new Character('�'),"&#228;");
	mHtmlEncodeTable1.put(new Character('�'),"&#229;");
	mHtmlEncodeTable1.put(new Character('�'),"&#230;");
	mHtmlEncodeTable1.put(new Character('�'),"&#231;");
	mHtmlEncodeTable1.put(new Character('�'),"&#232;");
	mHtmlEncodeTable1.put(new Character('�'),"&#233;");
	mHtmlEncodeTable1.put(new Character('�'),"&#234;");
	mHtmlEncodeTable1.put(new Character('�'),"&#235;");
	mHtmlEncodeTable1.put(new Character('�'),"&#236;");
	mHtmlEncodeTable1.put(new Character('�'),"&#237;");
	mHtmlEncodeTable1.put(new Character('�'),"&#238;");
	mHtmlEncodeTable1.put(new Character('�'),"&#239;");
	mHtmlEncodeTable1.put(new Character('�'),"&#240;");
	mHtmlEncodeTable1.put(new Character('�'),"&#241;");
	mHtmlEncodeTable1.put(new Character('�'),"&#242;");
	mHtmlEncodeTable1.put(new Character('�'),"&#243;");
	mHtmlEncodeTable1.put(new Character('�'),"&#244;");
	mHtmlEncodeTable1.put(new Character('�'),"&#245;");
	mHtmlEncodeTable1.put(new Character('�'),"&#246;");
	mHtmlEncodeTable1.put(new Character('�'),"&#247;");
	mHtmlEncodeTable1.put(new Character('�'),"&#248;");
	mHtmlEncodeTable1.put(new Character('�'),"&#249;");
	mHtmlEncodeTable1.put(new Character('�'),"&#250;");
	mHtmlEncodeTable1.put(new Character('�'),"&#251;");
	mHtmlEncodeTable1.put(new Character('�'),"&#252;");
	mHtmlEncodeTable1.put(new Character('�'),"&#253;");
	mHtmlEncodeTable1.put(new Character('�'),"&#254;");
	mHtmlEncodeTable1.put(new Character('�'),"&#255;");
	
	// Czech - most get converted to question marks when file is saved!
	mHtmlEncodeTable1.put(new Character('�'),"&#352;");
	mHtmlEncodeTable1.put(new Character('�'),"&#353;");
	mHtmlEncodeTable1.put(new Character('�'),"&#381;");
	mHtmlEncodeTable1.put(new Character('�'),"&#382;"); 
/*
	mHtmlEncodeTable1.put(new Character('?'),"&#268;");
	mHtmlEncodeTable1.put(new Character('?'),"&#269;");
	mHtmlEncodeTable1.put(new Character('?'),"&#270;");
	mHtmlEncodeTable1.put(new Character('?'),"&#271;");
	mHtmlEncodeTable1.put(new Character('?'),"&#282;");
	mHtmlEncodeTable1.put(new Character('?'),"&#283;");
	mHtmlEncodeTable1.put(new Character('?'),"&#313;");
	mHtmlEncodeTable1.put(new Character('?'),"&#314;");
	mHtmlEncodeTable1.put(new Character('?'),"&#317;");
	mHtmlEncodeTable1.put(new Character('?'),"&#318;");
	mHtmlEncodeTable1.put(new Character('?'),"&#327;");
	mHtmlEncodeTable1.put(new Character('?'),"&#328;");
	mHtmlEncodeTable1.put(new Character('?'),"&#340;");
	mHtmlEncodeTable1.put(new Character('?'),"&#341;");
	mHtmlEncodeTable1.put(new Character('?'),"&#344;");
	mHtmlEncodeTable1.put(new Character('?'),"&#345;");
	mHtmlEncodeTable1.put(new Character('?'),"&#356;");
	mHtmlEncodeTable1.put(new Character('?'),"&#357;");
	mHtmlEncodeTable1.put(new Character('?'),"&#366;");
	mHtmlEncodeTable1.put(new Character('?'),"&#367;");*/
}

static Hashtable mRssDecodeTable = null;
static Hashtable mHtmlDecodeTable = null;

public static final String FILTER_ALL_CHARS_FOR_INPUT_PARAM 	= "|&;$%@'\"\\<>()+,\n\r"; 
public static final String FILTER_ALL_CHARS_FOR_EMAIL 			= "|&;$%'\"\\()+,\n\r"; 

private static Hashtable getDecodeHTML()
{
	if (mHtmlDecodeTable == null)
	{
		mHtmlDecodeTable = new Hashtable();
		Enumeration eEncodeKeys = mHtmlEncodeTable.keys();
		while(eEncodeKeys.hasMoreElements())
		{
			Character cKey = (Character)eEncodeKeys.nextElement();
			String strEncoded = (String) mHtmlEncodeTable.get(cKey);
			mHtmlDecodeTable.put(strEncoded, cKey);
		}
		Enumeration eEncodeKeys1 = mHtmlEncodeTable1.keys();
		while(eEncodeKeys1.hasMoreElements())
		{
			Character cKey = (Character)eEncodeKeys1.nextElement();
			String strEncoded = (String) mHtmlEncodeTable1.get(cKey);
			mHtmlDecodeTable.put(strEncoded, cKey);
		}
	}
	
	return mHtmlDecodeTable;
}

private static Hashtable getDecodeRss()
{
	if (mRssDecodeTable == null)
	{
		mRssDecodeTable = new Hashtable();
		Enumeration eEncodeKeys = mRssEncodeTable.keys();
		while(eEncodeKeys.hasMoreElements())
		{
			Character cKey = (Character)eEncodeKeys.nextElement();
			String strEncoded = (String) mRssEncodeTable.get(cKey);
			mRssDecodeTable.put(strEncoded, cKey);
		}
	}
	
	return mRssDecodeTable;
}

private static char[] m_acAlpha = {'A','B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
private static char[] m_acAlphaNumeric = {'A','B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0','1','2','3','4','5','6','7','8','9'};


public static String backslashChars(String strSource, String strChars)
{
	StringBuffer strTarget = new StringBuffer();

	for(int i = 0; i < strSource.length(); i++)
	{
		char cSource = strSource.charAt(i);
		if (strChars.indexOf(cSource) != -1)
		{
			strTarget.append('\\');
		}
		strTarget.append(cSource);
	}

	return strTarget.toString();
}


public static boolean compare2Vectors(Vector vVect1, Vector vVect2)
{
	boolean equal = false;


	if (vVect1.size() == vVect2.size())
	{
		equal = true;
		int i = 0;
		
		while(i<vVect1.size()&& (equal))
		{
			Object elem = vVect2.elementAt(i);
			boolean Found = false;
			int j = 0;

			while ((!Found) && j < vVect1.size())
			{
				if (vVect1.elementAt(j).equals(elem))
				{
					Found = true;
				}
				j++;
			}
			if (Found == false)
			{
				equal = false;
			}
			i++;	
		}
	}

	return equal;
}


public static String ConvertDBCS2HTML( String strInputString )
{
    StringBuffer strOutputString = new StringBuffer( 1024 );
    
    String strTemp = "";
    char cTemp;
    int nTemp;
    
    for ( int nCount = 0 ; nCount < strInputString.length() ; nCount++ )
    {
	    cTemp = strInputString.charAt( nCount );
	    nTemp = Character.getNumericValue( cTemp );
	    if ( nTemp == -1 )
	    {
		    nTemp = cTemp;
		    if ( nTemp >= 128 )
		    {
		    	strOutputString.append( "&#" + String.valueOf( nTemp ) +  ";" );
		    }
		    else
		    {
		    	strOutputString.append( cTemp );
		    }
	    }
	    else
	    {
		    strOutputString.append( cTemp );
	    }
    }

    return strOutputString.toString();
}


public static String convertForDb(String strSource)
{
	// 27/05/2002 A.T.
	// Just in case...
	if ( strSource == null )
	{
		return "null";
	}
	
	// 14/03/2002 A.T.
	// return duplicateChars(strSource, "'");
	String strTemp = duplicateChars(strSource, "'");
	
	return ConvertDBCS2HTML( strTemp );
}


public static String convertForDb(String strSource, int nLength)
{
	String strConverted = convertForDb(strSource);
	
	// SE 20120905: converted string containing HTML encode chars may be cut in the middle of a character
	// but at least the database write will not fail!
	if (strConverted.length() > nLength)		
		return strConverted.substring(0, nLength);
	else
		return strConverted;
}

public static String convertForDb( String strSource, String strUTF8Params )
{
	return convertForDb( strSource, strUTF8Params, 0, true );
}

public static String convertForDb( String strSource, String strUTF8Params, boolean bAddQuotes )
{
	return convertForDb( strSource, strUTF8Params, 0, bAddQuotes );
}

// strUTF8Params - possible values:
// 		no - current status
// 		stream_of_bytes - "serialized" UTF-8 params - to read from an InputStream a sequence of bytes that will be assembled to obtain UTF-8 chars
// 		yes - real UTF-8 params (reserved for future use...)
// 		ams - calls from AMS
// 		batch - calls from batches
// 		internal - internal processing (for example new session created for moving an existing
//											set of commands/sessions to prod
// 		applet - calls from CMT applet
//
// nLength - trim to nLength to match target column restrictions
public static String convertForDb( String strSource, String strUTF8Params, int nLength, boolean bAddQuotes )
{
	// 27/05/2002 A.T.
	// Just in case...
	if ( strSource == null )
	{
		// return bAddQuotes ? "'null'" : "null";
		return "null";
	}
	
	String strTemp = strSource;
	
	if ( "no".equalsIgnoreCase( strUTF8Params) )
	{
		strTemp = Utility.convertForDb( strSource );
		if ( nLength > 0 && strTemp.length() > nLength)
		{
			strTemp = strTemp.substring(0, nLength);
		}
		// this will include HTML conversion for all chars over 0x7F
		
		if ( bAddQuotes ){	strTemp = "'" + strTemp + "'"; } 
	}
	else if ( "ams".equalsIgnoreCase( strUTF8Params ) ||
			 	 "internal".equalsIgnoreCase( strUTF8Params ) ||
			 	 "applet".equalsIgnoreCase( strUTF8Params ) ||
				 "yes".equalsIgnoreCase( strUTF8Params )  )
	{
		// what we get is what we have... almost no changes at all!
		strTemp = duplicateChars( strSource, "'" );
		if ( nLength > 0 && strTemp.length() > nLength)
		{
			strTemp = strTemp.substring(0, nLength);
		}
		// or should we go via the bytes stream???

		if ( bAddQuotes ){	strTemp = "'" + strTemp + "'"; } 
	}
	else if ( "stream_of_bytes".equalsIgnoreCase( strUTF8Params )  )
	{
		String strTemp1 = null;
		// first get the equivalent stream of bytes
		strTemp1 = getUTF8Representation( strSource );
		// then duplicate single quote
		strTemp = duplicateChars( strTemp1, "'" );
		if ( nLength > 0 && strTemp.length() > nLength)
		{
			strTemp = strTemp.substring(0, nLength);
		}
		
		// 20/11/2010 A.T.
		// strTemp = "X'" + strTemp + "'";
		if ( bAddQuotes ){	strTemp = "'" + strTemp + "'"; } 
	}
	else if ( "utf8_stream_of_bytes".equalsIgnoreCase( strUTF8Params )  )
	{
		// System.out.println( "strSource=" + strSource + "|" );
		// first duplicate single quote
		String strTemp1 = duplicateChars( strSource, "'" );
		// then obtain the bytes...
		strTemp = getUTF8Representation( strTemp1 );
		if ( nLength > 0 && strTemp.length() > nLength)
		{
			strTemp = strTemp.substring(0, nLength);
		}
		if ( bAddQuotes )
		{
			// only special case where we should add this X in front of the first single quote
			strTemp = "X'" + strTemp + "'"; 
		} 
	}
	else if ( "batch".equalsIgnoreCase( strUTF8Params) )
	{
		// no change!!!
		strTemp = strSource;
		if ( nLength > 0 && strTemp.length() > nLength)
		{
			strTemp = strTemp.substring(0, nLength);
		}
		if ( bAddQuotes ){	strTemp = "'" + strTemp + "'"; } 
	}
	else
	{
		// What we get is what we have... almost no changes at all!
		strTemp = duplicateChars( strSource, "'" );
		if ( nLength > 0 && strTemp.length() > nLength)
		{
			strTemp = strTemp.substring(0, nLength);
		}
		
		if ( bAddQuotes ){	strTemp = "'" + strTemp + "'"; } 
	}
	
	return strTemp;
}

public static int countString(String stringToCountIn, String delimiter) 
{
	int current_index = 0;
	int delimiter_index = 0;
	int count = 0;
	
	while(current_index <= stringToCountIn.length())
	{
		delimiter_index = stringToCountIn.indexOf(delimiter,current_index);    
		
		if(delimiter_index == -1)
		{
			break;
		}
		else
		{
			current_index = delimiter_index + delimiter.length();
			count++;
		}
	}
	
	return count;
}


public static String duplicateChars(String strSource, String strChars)
{
	StringBuffer strTarget = new StringBuffer(1024);

	for(int i = 0; i < strSource.length(); i++)
	{
		char cSource = strSource.charAt(i);
		strTarget.append(cSource);
		if (strChars.indexOf(cSource) != -1)
		{
			strTarget.append(cSource);	// append again
		}
	}

	return strTarget.toString();
}


public static String encodeHtml(String stringToEncode)
{
	return encode(stringToEncode, mHtmlEncodeTable);
}

public static String encodeXML(String stringToEncode)
{
	String strEncoded = replaceAmpersand(stringToEncode);
	strEncoded = replaceChar(strEncoded,'<', "&lt;");
	strEncoded = replaceChar(strEncoded,'>', "&gt;");	
	
	return strEncoded;
}

public static String encodeRss(String stringToEncode)
{
	return encode(stringToEncode, mRssEncodeTable);
}


private static String encode(String stringToEncode, Hashtable htEncode)
{
	if ( stringToEncode == null )
	{
		return null;
	}
	
	StringBuffer encodedString = new StringBuffer(1024);
	char[] string_to_encode_array = stringToEncode.toCharArray();


	for(int i=0; i < string_to_encode_array.length; i++)
	{
		if(htEncode.containsKey(new Character(string_to_encode_array[i])))
		{
			encodedString.append(htEncode.get(new Character(string_to_encode_array[i])));
		}
		else
		{
			encodedString.append(string_to_encode_array[i]);
		}
	}

	return encodedString.toString();
}


public static String encodeSQL(Object objectToEncode)
{
	String stringToEncode = objectToEncode.toString();
	char[] string_to_encode = stringToEncode.toCharArray();
	StringBuffer encoded_string = new StringBuffer(1024);
	
	for(int i = 0; i < stringToEncode.length(); i++)
	{
		if(string_to_encode[i] == '\'')
		{
			encoded_string.append("''");
		}
		else
		{
			encoded_string.append(string_to_encode[i]);
		}
	}
	
	return encoded_string.toString();
}


public static String encodeUrl(String text)
{
	// the single parameter functions are deprecated in JDK1.4.2
/*	try
	{
		return URLEncoder.encode(text, "UTF-8");
	}
	catch(UnsupportedEncodingException e)
	{
		// should never happen
		return text;
	}  */
	return URLEncoder.encode(text);
}

public static String encodeUrl(String text, String strEncoding)
{
	try
	{
		return URLEncoder.encode(text, (strEncoding != null) ? strEncoding : CHARSET_LATIN );
	}
	catch(UnsupportedEncodingException e)
	{
		// should never happen
		return text;
	} 
}


public static String decodeUrl(String text)
{
	// the single parameter functions are deprecated in JDK1.4.2
/*	try
	{
		return URLDecoder.decode(text, "UTF-8");
	}
	catch(UnsupportedEncodingException e)
	{
		// should never happen
		return text;
	} */
	 return URLDecoder.decode(text);
}

public static String decodeUrl(String text, String strEncoding)
{
	try
	{
		return URLDecoder.decode(text, (strEncoding != null) ? strEncoding : CHARSET_LATIN );
	}
	catch(UnsupportedEncodingException e)
	{
		// should never happen
		return text;
	}
}


public static String decodeHtml(String strEncoded)
{
	// all HTML characters start with an ampersand
	if (strEncoded.indexOf("&") == -1)
		return strEncoded;
		
	return decode(strEncoded, getDecodeHTML());
}


public static String decodeRss(String strEncoded)
{
	return decode(strEncoded, getDecodeRss());
}


public static String decode(String strEncoded, Hashtable htDecode)
{
	StringBuffer sbDecoded = new StringBuffer(1024);

	int i = 0;
	while( i < strEncoded.length())
	{
		char cCurrent = strEncoded.charAt(i);
		if (cCurrent == '&')
		{
			// look for terminating ;
			int nIndex = strEncoded.substring(i).indexOf(";");
			if (nIndex != -1)
			{
				String strEncodedElement = strEncoded.substring(i, i+nIndex+1);
				if (htDecode.containsKey(strEncodedElement))
				{
					sbDecoded.append(htDecode.get(strEncodedElement));
					i = i + nIndex + 1;
					continue;
				}
			}
		}
		sbDecoded.append(cCurrent);
		i++;
	}

	return sbDecoded.toString();
}


public static String[] enumeration2StringArray(Enumeration enumeration)
{
	Vector string_vector = new Vector();


	while(enumeration.hasMoreElements())
	{
		string_vector.addElement(enumeration.nextElement());
	}

	String[] string_array = new String[string_vector.size()];
	string_vector.copyInto(string_array);

	return string_array;
}


public static String getExceptionStackTrace(Throwable exception)
{
	String stack_trace = null;

	StringWriter string_writer = new StringWriter();
	PrintWriter print_writer = new PrintWriter(string_writer);
	
	exception.printStackTrace(print_writer);
	
	stack_trace = string_writer.getBuffer().toString();
	
	print_writer.close();
	
	try
	{
		string_writer.close();
	}
	// JDK 1.2.2 compatibility
	catch(Throwable exception2)
	{
	}

	return stack_trace;
}


public static int getPercentage(int nIntToCompare, int nTotal)
{
	double dIntToCompare = nIntToCompare;
	double dTotal = nTotal;

	Double result = new Double((100*dIntToCompare)/dTotal);

	return result.intValue();
}


public static char getRandomAlpha()
{
	Double dRandomNum = new Double(Math.floor(Math.random()*m_acAlpha.length));
	int nRandomNum = dRandomNum.intValue();
	return m_acAlpha[nRandomNum];
}


public static char getRandomAlphaNumeric()
{
	Double dRandomNum = new Double(Math.floor(Math.random()*m_acAlphaNumeric.length));
	int nRandomNum = dRandomNum.intValue();
	return m_acAlphaNumeric[nRandomNum];
}


public static String getRandomPsw(int nLength)
{
	// Minimum 2 characters
	if (nLength < 2)
		return "";
		
	// First and last are alpha
	// Rest are alphanumeric
	char[] acPsw = new char[nLength];
	acPsw[0] = getRandomAlpha();
	acPsw[nLength - 1] = getRandomAlpha();
	for(int i = 1; i < nLength - 1; i++)
	{
		acPsw[i] = getRandomAlphaNumeric();
	}

	return new String(acPsw);
}


public static long getTimestampInMilliseconds()
{
	return ((new Date()).getTime());
}


public static long getTimestampInSeconds()
{
	long t = getTimestampInMilliseconds();
	t = t / 100;
	return t;
}


public static String getValueAfterEqual(String key)
{
	String property = "";

	int posEqual = key.indexOf("=");

	if (posEqual != -1)
	{
		property = key.substring(posEqual+1,key.length());
	}

	return property;
}


public static String joinArray(Object [] arrayToJoin, String delimiter) 
{
	if (arrayToJoin.length == 0)
	{
		return "";
	}
	else
	{
		int current_index = 0;
		String result = "";
		while(current_index < arrayToJoin.length - 1)
		{
			result = result + arrayToJoin[current_index].toString() + delimiter;
			current_index++;
		}
		
		result = result +  arrayToJoin[current_index].toString();			
		return result;
	}
}


public static String joinList(List lstToJoin, String strDelimiter) 
{
	if (lstToJoin.size() == 0)
	{
		return "";
	}
	else
	{
		int current_index = 0;
		String result = "";
		while(current_index < lstToJoin.size() - 1)
		{
			result = result + lstToJoin.get(current_index).toString() + strDelimiter;
			current_index++;
		}
		
		result = result + lstToJoin.get(current_index).toString();			
		return result;
	}
}


public static String joinVector(Vector vToJoin, String strDelimiter) 
{
	// Uses Vector, as List is not defined in JRE for our D&P applets
	if ((vToJoin == null) || (vToJoin.size() == 0))
	{
		return "";
	}

	int current_index = 0;
	String result = "";
	while(current_index < vToJoin.size() - 1)
	{
		result = result + vToJoin.elementAt(current_index).toString() + strDelimiter;
		current_index++;
	}
	
	result = result + vToJoin.elementAt(current_index).toString();			
	return result;
}


public static int numOfDelimiter(String strContent, String strDelimiter)
{
//	int current_index = 0;
//	int delimiter_index = -1;
//	boolean noFinished = true;
//
//	while(noFinished)
//	{
//		delimiter_index = strContent.indexOf(strDelimiter,current_index);
//		
//		if (delimiter_index != -1)
//		{
//			nNumOfDelimiter++;
//			current_index = delimiter_index+1;
//		}
//		else
//		{
//			noFinished = false;
//		}
//	}
//	
//	return nNumOfDelimiter;

	if ( strContent == null || strDelimiter == null )
	{
		return 0;
	}
	
	String astrTemp[] = strContent.split( strDelimiter );
	return astrTemp.length - 1;
}


public static String removeChars(String strSource, String strChars)
{
	// remove characters not specified in list
	StringBuffer strTarget = new StringBuffer(1024);

	for(int i = 0; i < strSource.length(); i++)
	{
		char cSource = strSource.charAt(i);
		if (strChars.indexOf(cSource) == -1)
		{
			strTarget.append(cSource);
		}
	}

	return strTarget.toString();
}

public static String retainChars(String strSource, String strChars)
{
	// only keep characters specified in list
	StringBuffer strTarget = new StringBuffer(1024);

	for(int i = 0; i < strSource.length(); i++)
	{
		char cSource = strSource.charAt(i);
		if (strChars.indexOf(cSource) >= 0)
		{
			strTarget.append(cSource);
		}
	}

	return strTarget.toString();
}

public static String removeHTMLTag(String strContent, String strTag)
{
	// search for beginning
	String strOpenTag = "<" + strTag;

	int nTagOpen = strContent.indexOf(strOpenTag);

	if (nTagOpen > - 1)
	{
		int nOpenLength = strTag.length() + 1;

		int nTagOpenEnd = strContent.indexOf(">", nTagOpen + nOpenLength);
	
		strContent = strContent.substring(0, nTagOpen) + strContent.substring(nTagOpenEnd + 1);

		// search for end
		String strCloseTag = "<\\" + strTag + ">";
		int nTagClose = strContent.indexOf(strCloseTag, nTagOpen);

		if (nTagClose != -1)
		{
			int nCloseLength = strTag.length() + 3;
			strContent = strContent.substring(0, nTagClose) + strContent.substring(nTagClose + nCloseLength) ;
		}
	}

	return strContent;
}


public static String removeHTMLTags(String strContent)
{
    String strNewContent = null;

    strNewContent = removeHTMLTag(strContent, "img");
    strNewContent = removeHTMLTag(strNewContent, "IMG");
    strNewContent = removeHTMLTag(strNewContent, "font");
    strNewContent = removeHTMLTag(strNewContent, "FONT");
    strNewContent = removeHTMLTag(strNewContent, "b");
    strNewContent = removeHTMLTag(strNewContent, "B");

    return strNewContent;
}


public static String replaceChar(String strSource, char cChar, String strSubstitute)
{
	StringBuffer strTarget = new StringBuffer(1024);

	for(int i = 0; i < strSource.length(); i++)
	{
		char cSource = strSource.charAt(i);
		if (cChar == cSource)
		{
			strTarget.append(strSubstitute);	// replace
		}
		else
			strTarget.append(cSource);
	}

	return strTarget.toString();
}

public static String replaceAmpersand(String strSource)
{
	if (strSource == null) return "";
	StringBuffer strTarget = new StringBuffer(1024);
	int nLength = strSource.length();
	
	for( int i = 0; i < nLength; i++ )
	{
		char cSource = strSource.charAt(i);
		if ( '&' != cSource )
		{
			strTarget.append(cSource);
		} 
		else
		{
			i += replaceAmpersand(i, strTarget, strSource);
		}
	}

	return strTarget.toString();
}

/**
 * This function replaces the ampersand at position index with an HTML entity.
 * If the ampersand starts a valid HTML sequence, it is left as is 
 * and the function returns the number of characters to skip
 * @param index int position in the source string where an ampersand is found
 * @param target StringBuffer holding the new value
 * @param source String source string
 * @return
 */
private static int replaceAmpersand(int index, StringBuffer target, String source)
{
	int skip = 0;
	
	// Check if this ampersand starts a valid HTML sequence
	int nSemiColon = source.indexOf(';', index);
	if ((nSemiColon > - 1) && (nSemiColon <= index + 7))
	{
		String strTag = source.substring(index, nSemiColon + 1);
		if ( "&lt;".equalsIgnoreCase(strTag) ||
			 "&gt;".equalsIgnoreCase(strTag) ||
			 getDecodeHTML().containsKey(strTag) ||
			 strTag.charAt(1) == '#')
		{
			target.append(strTag);		// leave it as is
			skip = strTag.length()-1;
		}
		else
		{
			target.append("&amp;");	// replace
		}
	}	
	else
	{
		target.append("&amp;");	// replace
	}
	
	return skip;
}

public static String replaceChars(String strSource, String strChars, String strSubstitute)
{
	StringBuffer strTarget = new StringBuffer(1024);

	for(int i = 0; i < strSource.length(); i++)
	{
		char cSource = strSource.charAt(i);
		if (strChars.indexOf(cSource) != -1)
		{
			strTarget.append(strSubstitute);	// replace
		}
		else
			strTarget.append(cSource);
	}

	return strTarget.toString();
}


public static String replaceInString(String sourceString, String stringToReplace, String replacementString)
{
	if (sourceString.equals(stringToReplace))
		return replacementString;
		
	Enumeration string_parts = splitString(sourceString, stringToReplace, false).elements();
	StringBuffer new_string = new StringBuffer(1024);

	while(string_parts.hasMoreElements())
	{
		String string_part = (String)string_parts.nextElement();
		new_string.append(string_part);
		if(string_parts.hasMoreElements())
		{
			new_string.append(replacementString);
		}
	}

	return new_string.toString();
}


public static void sortVector(Vector v)
{
	/* siea: Vector::toArray does not exist in Visual Age library
	Object [] temp = v.toArray();
	Arrays.sort(temp);

	for(int i=0;i<temp.length;i++)
	{
		v.setElementAt(temp[i],i);
	} */
}


public static Vector splitString(String stringToSplit, String delimiter) 
{
	return splitString(stringToSplit, delimiter, true);
}

// Do NOT convert  splitString to use String.splt(..) 
// unless delimiter paramter is parsed and any pipe or point characters are protected by double backslash
public static Vector splitString(String stringToSplit, String delimiter, boolean bRemoveBlanks) 
{
	Vector substrings = new Vector();
	int current_index = 0;
	int delimiter_index = 0;
	int nLength = stringToSplit.length();

	if (nLength == 0)
		return substrings;
	if ("".equals(delimiter))
	{
		// this is probably an error - return whole string as single item
		substrings.add(stringToSplit);
		return substrings;
	}
		
	while(current_index <= nLength)	// use current_index > nLength as signal
	{
		delimiter_index = stringToSplit.indexOf(delimiter,current_index);    
		
		if(delimiter_index == -1)
		{
			String element = stringToSplit.substring(current_index,stringToSplit.length());
			
			if(bRemoveBlanks)
				element = element.trim();
				
			substrings.addElement(element);
			current_index = stringToSplit.length() + 1;	// signal the end has been reached
		}
		else
		{
			String element = stringToSplit.substring(current_index,delimiter_index);


			if(bRemoveBlanks)
				element = element.trim();
			
			substrings.addElement(element);
			current_index = delimiter_index + delimiter.length();
			if (bRemoveBlanks)
			{
				while((current_index < nLength) && (stringToSplit.charAt(current_index) == ' '))
				{
					current_index++;
				}
			}
		}
	}
	
	return substrings;
}


public static Vector splitStringIntoIntegerVector(String strToSplit, String strDelimiter)
{
	// StringTokenizer splitString = new StringTokenizer(strToSplit, strDelimiter);
	String astrSplitResult[] = strToSplit.split( strDelimiter );
	Vector vResult = new Vector();
	for ( int i = 0 ; i < astrSplitResult.length ; i++ )
	{
		try
		{
			vResult.addElement( new Integer( Integer.parseInt( astrSplitResult[i].trim() ) ) );
		}
		catch(NumberFormatException e)
		{
			// ignore invalid values
		}
	}
	
	return vResult;
}


public static String[] splitStringToArray(String stringToSplit, String delimiter) 
{
	if (stringToSplit == null)
		return null;
		
	Vector substrings = new Vector();
	int current_index = 0;
	int delimiter_index = 0;
	
	while(current_index <= stringToSplit.length())
	{
		delimiter_index = stringToSplit.indexOf(delimiter,current_index);    
		
		if(delimiter_index == -1)
		{
			String element = new String(stringToSplit.substring(current_index,stringToSplit.length()));
			substrings.addElement(element);
			current_index = stringToSplit.length() + 1;
		}
		else
		{
			String element = new String(stringToSplit.substring(current_index,delimiter_index));
			substrings.addElement(element);
			current_index = delimiter_index + delimiter.length();
		}
	}
	
	String[] result = new String[substrings.size()];
	for(int i=0;i<substrings.size();i++)
	{
		result[i] = (String) substrings.elementAt(i);
	}
			
	return result;
}


public static Vector splitStringTokenizerInVector(String strToSplit, String strDelimiter)
{
	StringTokenizer splitString = new StringTokenizer(strToSplit, strDelimiter);
	Vector vResult = new Vector();
	while(splitString.hasMoreTokens())
	{
		vResult.addElement(splitString.nextToken());
	}
	
	return vResult;
}

public static Vector splitStringInVector(String strToSplit, String strDelimiter)
{
	// StringTokenizer splitString = new StringTokenizer(strToSplit, strDelimiter);
	String astrSplitResult[];
	astrSplitResult = strToSplit.split( strDelimiter );
	
	Vector vResult = new Vector();
	for ( int intPart = 0; intPart < astrSplitResult.length ; intPart++ )
	{
		vResult.addElement( astrSplitResult[ intPart ] );
	}
	
	return vResult;
}


public static String stripFromStringFront(String sourceString, String stringToStrip)
{
	int strip_length = stringToStrip.length();
	int new_index = 0;
	int last_index = 0;

	new_index = sourceString.indexOf(stringToStrip);
	if(new_index == 0)
	{
		do
		{
			last_index = new_index;
			new_index = sourceString.indexOf(stringToStrip, new_index+strip_length);
		}
		while(new_index != -1 && new_index == last_index+strip_length);
		
		return sourceString.substring(last_index+strip_length);
	}
	else
	{
		return sourceString;
	}
}


public static int[] toIntArray(Vector vIntegers)
{
	int nCount = 0;
	
	if (vIntegers != null)
		nCount = vIntegers.size();
		
	int[] aInts = new int[nCount];

	for(int i = 0; i < aInts.length; i++)
	{
		aInts[i] = ((Integer) vIntegers.elementAt(i)).intValue();
	}

	return aInts;
}


public static String[] toStringArray(Vector vStrings)
{
	int nCount = 0;
	
	if (vStrings != null)
		nCount = vStrings.size();
		
	String[] aStrings = new String[nCount];

	for(int i = 0; i < aStrings.length; i++)
	{
		aStrings[i] = (String) vStrings.elementAt(i);
	}

	return aStrings;
}

// Second parameter should be a regular expression
// http://javarevisited.blogspot.com/2011/09/string-split-example-in-java-tutorial.html
// In case of "|"  --> "\\|"   and also  "."  --> "\\." 
public static List splitStringToList( String strSource, String strDelim )
{
	List lstReturn = new ArrayList();
	if ( strSource == null || strDelim == null || "".equals( strSource ) )
	{
		return lstReturn;
	}
	
	// and now the analysis...
	// StringTokenizer stkSource = new StringTokenizer( strSource, strDelim );
	String astrSource[] = strSource.split( strDelim );
	for ( int i = 0; i < astrSource.length ; i++ )
	{
		lstReturn.add( astrSource[i] );
	}
	
	return lstReturn;
}


/**********************************************************************************
* 
* Functions moved here from WLSUtility, so that CAS and CMT may use them...
*
***********************************************************************************/
public static String computeInternalUserid( String strCountryCd, String strSerialNo )
{
	String strUserid = null;

	// 21/11/2001 A.T.
	// Just to be sure...
	if ( strCountryCd == null || strSerialNo == null )
	{
		return null;
	}
	
	if ( strCountryCd.length() > 3 )
	{
		strCountryCd = strCountryCd.substring( 0,3 );
	}

	int nlenSerialNo = strSerialNo.length();

	if ( nlenSerialNo > 8 )
	{
		strSerialNo = strSerialNo.substring( 0,8 );
	}

	for ( int nCount = 0; nCount < (8-nlenSerialNo); nCount++ )
	{
		strSerialNo = "0" + strSerialNo;
	}

	strUserid = strCountryCd + strSerialNo;
	
	return strUserid;
}


public static String computeCNUM( String strCountryCd, String strSerialNo )
{
	String strUserid = null;

	// 21/11/2001 A.T.
	// Just to be sure...
	if ( strCountryCd == null || strSerialNo == null )
	{
		return null;
	}
	
	if ( strCountryCd.length() > 3 )
	{
		strCountryCd = strCountryCd.substring( 0,3 );
	}

	int nlenSerialNo = strSerialNo.length();

	if ( nlenSerialNo > 6 )
	{
		strSerialNo = strSerialNo.substring( 0,6 );
	}

	for ( int nCount = 0; nCount < (6-nlenSerialNo); nCount++ )
	{
		strSerialNo = "0" + strSerialNo;
	}

	strUserid = strSerialNo + strCountryCd;
	
	return strUserid;
}


public static String getRegDate()
{
	String strDate = null;

	Calendar cal = Calendar.getInstance();

	strDate = String.valueOf(cal.get(Calendar.MONTH)+1)+"/"+String.valueOf(cal.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(cal.get(Calendar.YEAR));

	return strDate;
}


// return current timestamp in the format:
// intMode == 0  --> YYYYMMDDHHmmSSTTTTT
// intMode == 1  --> YYYY-MM-DD-HH.mm.SS.TTTTT
public static synchronized String getCurrentTimestamp( int intMode )
{
	Date dCurrent = new Date();
	// GregorianCalendar gcCurrent = new GregorianCalendar();
	String strCurrentTimeStamp = "";
	SimpleDateFormat sdfCurrent;
	switch ( intMode )
	{
		case 0:
			sdfCurrent = new SimpleDateFormat( "yyyyMMddHHmmssSSS" );
			strCurrentTimeStamp = new String( sdfCurrent.format( dCurrent ) );
			
//			strCurrentTimeStamp += gcCurrent.get( Calendar.YEAR );
//			strCurrentTimeStamp += gcCurrent.get( Calendar.MONTH );
//			strCurrentTimeStamp += gcCurrent.get( Calendar.DAY_OF_MONTH );
//			strCurrentTimeStamp += gcCurrent.get( Calendar.HOUR_OF_DAY );
//			strCurrentTimeStamp += gcCurrent.get( Calendar.MINUTE );
//			strCurrentTimeStamp += gcCurrent.get( Calendar.SECOND );
//			strCurrentTimeStamp += gcCurrent.get( Calendar.MILLISECOND );
			break;
		
		case 1:
			sdfCurrent = new SimpleDateFormat( "yyyy-MM-dd-HH.mm.ss.SSS" );
			strCurrentTimeStamp = new String( sdfCurrent.format( dCurrent ) );
//			strCurrentTimeStamp += gcCurrent.get( Calendar.YEAR );
//			strCurrentTimeStamp += "-";
//			strCurrentTimeStamp += gcCurrent.get( Calendar.MONTH );
//			strCurrentTimeStamp += "-";
//			strCurrentTimeStamp += gcCurrent.get( Calendar.DAY_OF_MONTH );
//			strCurrentTimeStamp += "-";
//			strCurrentTimeStamp += gcCurrent.get( Calendar.HOUR_OF_DAY );
//			strCurrentTimeStamp += ".";
//			strCurrentTimeStamp += gcCurrent.get( Calendar.MINUTE );
//			strCurrentTimeStamp += ".";
//			strCurrentTimeStamp += gcCurrent.get( Calendar.SECOND );
//			strCurrentTimeStamp += ".";
//			strCurrentTimeStamp += gcCurrent.get( Calendar.MILLISECOND );
			break;
		
	default:
		break;	
	}
	
	return strCurrentTimeStamp;
}


public static synchronized String getUTF8Representation( String strUTF16 )
{
	// input string is in "native" Java representation
	String strExplicitBytes = "";
	int intIndex;
	char cCurrentChar;
	int intCurrentByte;
	try {
		byte[] utf8Bytes = strUTF16.getBytes( "UTF-8" );

		// Following does not work.....		
		// strExplicitBytes = new String( utf8Bytes );

		for( intIndex = 0 ; intIndex < utf8Bytes.length ; intIndex++ )
		{
			intCurrentByte = utf8Bytes[ intIndex ] & 0xFF;
			strExplicitBytes += Integer.toHexString( intCurrentByte );
			
//			cCurrentChar = (char)intCurrentByte;
//			strExplicitBytes += cCurrentChar;
		}
		
		// byte[] aISO88591Bytes = strExplicitBytes.getBytes( "ISO-8859-1" );
		// byte[] aUSASCIIBytes = strExplicitBytes.getBytes( "US-ASCII" );
	}
	catch (UnsupportedEncodingException e) 
	{
		e.printStackTrace();
	}
	return strExplicitBytes;	
}


public static synchronized String getUTF16Representation( String strSudoUTF8 )
{
	// input string has (byte after byte) a UTF8 "expanded" string
	String strRecomposedUTF8 = null;
	try {
		byte[] aReadBytes = strSudoUTF8.getBytes( "ISO-8859-1" );
		strRecomposedUTF8 = new String( aReadBytes, "UTF-8" );
	}
	catch (UnsupportedEncodingException e) 
	{
		e.printStackTrace();
	}
	return strRecomposedUTF8;
}

// Converts all 
//		&#NNNNN; 
//		&#NNNN; 
//		&#NNN; 
// pieces of text from the input string into their UTF-16 equivalent
// 19/03/2010
// Take into account hexa values
//		&#x30EF;
// 23/03/2013
// Convert \\uNNNN received as 6 characters to the UTF-16 equivalent
// Convert also HTML encoded values

public static String convertHTML2UTF16( String strInput )
{
	return convertHTML2UTF16( strInput, false );
}

	
public static String convertHTML2UTF16( String strInput, boolean bKeepOctal )
{
	if ( strInput == null )
	{
		return null;
	}
	int nPosDelimiter = -1;
	int nPosAnt = 0;
	nPosDelimiter = strInput.indexOf( "&#", nPosAnt );
	if ( nPosDelimiter == -1 )
	{
		// nothing to do here...
		return strInput;
	}
	
	int nPosSC = 0;
	StringBuffer strbResult = new StringBuffer( 4096 );
	String strCurrentChar = "";
	int intVal;
    char acResult[];
	
	while ( nPosDelimiter >= 0 )
	{
		nPosDelimiter = strInput.indexOf( "&#", nPosAnt );
		if ( nPosDelimiter <= -1 )
		{
			// append up to the end of string
			strbResult.append( strInput.substring( nPosAnt ) );
			break;
		}
		
		if ( nPosDelimiter > nPosAnt )
		{
			strbResult.append( strInput.substring( nPosAnt, nPosDelimiter ) );
		}
		
		// and now the real work...
		nPosSC = strInput.indexOf( ";", nPosDelimiter + 2 );
		if ( (nPosSC <= -1) || 
				 ((nPosSC >= nPosDelimiter + 2) && (nPosSC - nPosDelimiter) > 9)  )
		{
			// Wrong encoding - add it without any change
			strbResult.append( "&#" );
			nPosAnt = nPosDelimiter + 2;
		}
		else
		{
			strCurrentChar = strInput.substring( nPosDelimiter + 2, nPosSC );
			try
			{
				// character might be in hexa...
				if ( bKeepOctal && strCurrentChar.length() > 1 && strCurrentChar.charAt(0) == '0' )
				{
					// Do not convert octal numbers OR numbers starting with '0'
					strbResult.append( strInput.substring( nPosDelimiter, nPosSC + 1 ) );
				}
				else
				{					
					if ( strCurrentChar.length() > 1 && 
						( strCurrentChar.charAt(0) == 'x' || strCurrentChar.charAt(0) == 'X') )
					{
						intVal = Integer.parseInt( strCurrentChar.substring(1), 16 );
					}
					else
					{
						// Note: we  DO NOT consider strings with first digit '0' as octal numbers!!
						intVal = Integer.parseInt( strCurrentChar, 10 );
					}
					acResult = Character.toChars( intVal );
					strbResult.append( acResult[0] );
				}
			}
			catch( NumberFormatException e )
			{
				strbResult.append( '?' );
			}
			nPosAnt = nPosSC + 1;
		}
	}

	// Second pass -> convert HTML encoded tokens
	String strStep2 = decodeHtml( strbResult.toString() );
	
	// Third pass -> convert \\uNNNN to their equivalent character
	nPosDelimiter = -1;
	nPosAnt = 0;
	nPosDelimiter = strStep2.indexOf( "\\u", nPosAnt );
	if ( nPosDelimiter == -1 )
	{
		// nothing to do here...
		return strStep2;
	}
	
	strbResult = new StringBuffer( 4096 );
	strCurrentChar = "";
	while ( nPosDelimiter >= 0 )
	{
		nPosDelimiter = strStep2.indexOf( "\\u", nPosAnt );
		if ( nPosDelimiter <= -1 )
		{
			// append up to the end of string
			strbResult.append( strStep2.substring( nPosAnt ) );
			break;
		}
		
		if ( nPosDelimiter > nPosAnt )
		{
			strbResult.append( strStep2.substring( nPosAnt, nPosDelimiter ) );
		}
		
		// and now the real work...
		if ( nPosDelimiter + 6 > strStep2.length() )
		{
			strbResult.append( "\\u" );
			nPosAnt = nPosDelimiter + 2;
		}
		else
		{
			strCurrentChar = strStep2.substring( nPosDelimiter + 2, nPosDelimiter + 6 );
			try
			{
				intVal = Integer.parseInt( strCurrentChar, 16 );
				acResult = Character.toChars( intVal );
				strbResult.append( acResult[0] );
			}
			catch( NumberFormatException e )
			{
				strbResult.append( '?' );
			}
			nPosAnt = nPosDelimiter + 6;
		}
	}
	
	return strbResult.toString();
}


public static synchronized void displayByteArrayContent( byte[] utf8Bytes  )
{
	int intIndex;
	int intCurrentByte;
	// System.out.println( "Display array of "  + utf8Bytes.length + " elements.");
	for( intIndex = 0 ; intIndex < utf8Bytes.length ; intIndex++ )
	{
		intCurrentByte = utf8Bytes[ intIndex ];
		// System.out.print( "\\u" + Integer.toHexString( 0x10000 | intCurrentByte ).substring(1) );
	}
	// System.out.println( "\nEND display array of "  + utf8Bytes.length + " elements.");
}


public static synchronized void displayBytesOfString( String strUTF16 )
{
	int intIndex;
	int intCurrentByte;
	try {
		byte[] abBytes = strUTF16.getBytes( "UTF-16" );
		
		// System.out.println( "Display every byte in string of "  + strUTF16.length() + " characters.");
		for( intIndex = 0 ; intIndex < abBytes.length ; intIndex++ )
		{
			intCurrentByte = abBytes[ intIndex ] & 0xFF;
			// System.out.print( "\\u" + Integer.toHexString( 0x10000 | intCurrentByte ).substring(1) );
		}
		// System.out.println( "\nEND display every byte in string of "  + strUTF16.length() + " characters.");
	}
	catch (UnsupportedEncodingException e) 
	{
		e.printStackTrace();
	}
}


public static synchronized void displayHexCharactersForString( String strUTF16 )
{
	displayHexCharactersForString( strUTF16, "" );
}
public static synchronized void displayHexCharactersForString( String strUTF16, String strVarName )
{
	int intIndex;
	int intCurrentByte;
	try {
		byte[] abBytes = strUTF16.getBytes( "UTF-16" );
		
		// System.out.println( "Display "+ strVarName + " string of "  + strUTF16.length() + " characters.");
		for( intIndex = 0 ; intIndex < strUTF16.length() ; intIndex++ )
		{
			intCurrentByte = strUTF16.charAt( intIndex );
			System.out.print( "\\u" + Integer.toHexString( 0x10000 | intCurrentByte ).substring(1) );
		}
		// System.out.println( "\nEND display "+ strVarName + " string of "  + strUTF16.length() + " characters.");
	}
	catch (UnsupportedEncodingException e) 
	{
		e.printStackTrace();
	}
}


// October 2010
// Heavy validations for input parameters
public static boolean validateRequired( String value ) 
{
   boolean isFieldValid = false;

   if (value != null && value.trim().length() > 0) {
	   isFieldValid = true;
   }
   return isFieldValid;
}
   
public static boolean validateInt(String value) {
	boolean isFieldValid = false;
	try {
		Integer.parseInt(value);
		isFieldValid = true;
	} catch (Exception e) {
		isFieldValid = false;
	}
	return isFieldValid;
} 

public static boolean validateLength( String value, int minLength, int maxLength ) {
	String validatedValue = value;
	if (!validateRequired(value)) {
		validatedValue = "";
	}
	return (validatedValue.length() >= minLength &&
			validatedValue.length() <= maxLength);
}

public static boolean validateRange(int value, int min, int max) {
	return (value >= min && value <= max);
}

public static boolean validateOption(Object[] options, Object value) {
	boolean isValidValue = false;
	try {
		List list = Arrays.asList(options);
		if (list != null) {
			isValidValue = list.contains(value);
		}
	} catch (Exception e) {
	}
	return isValidValue;
}

public static boolean matchPattern(String value, String expression) {
	boolean match = false;
	if (validateRequired(expression)) {
		match = Pattern.matches(expression, value);
	}
	return match;
}

public static String filter(String value) {
	if (value == null) {
		return "";
	}
	StringBuffer result = new StringBuffer(value.length());
	for (int i=0; i<value.length(); ++i) {
		switch (value.charAt(i)) {
		case '<':
			result.append("&lt;");
			break;
		case '>':
			result.append("&gt;");
			break;
		case '"':
			result.append("&quot;");
			break;
		case '\'':
			result.append("&#39;");
			break;
		case '%':
			result.append("&#37;");
			break;
		case ';':
			result.append("&#59;");
			break;
		case '(':
			result.append("&#40;");
			break;
		case ')':
			result.append("&#41;");
			break;
		case '&':
			i += replaceAmpersand(i, result, value);
			break;
		case '+':
			result.append("&#43;");
			break;
		case '\0':
			break;
		default:
			result.append(value.charAt(i));
		break;
		}
	}
	return result.toString();
}


/**
 * This function removes script tags and '&00' characters from the user input
 * Other 'dangerous' characters are replaced with the equivalent HTML entity
 * @param value String as found in a GET or POST parameter
 * @return String cleaned input
 */
public static String cleanHTMLParameter(String value) {
	if (value == null) {
		return null;
	}
	if (!validateRequired(value)) {
		return "";
	}
	// remove dangerous pieces
	String strValue = value.replaceAll("script>", "");
		
	// remove dangerous chars...
	return filter(strValue);
}

public static String cleanMinimumHTMLParameter(String value) {
	if (value == null) {
		return null;
	}
	if (!validateRequired(value)) {
		return "";
	}
	// only \0  ....
	String strValue = value.replaceAll("\0", "");
	return strValue;
}

public static String cleanHTMLParameter( String value, boolean bClean ) {
	return bClean ? cleanHTMLParameter( value ) : cleanMinimumHTMLParameter( value );
}


// removes all occurrences of a parameter from a query URL
public static String removeParameterFromQuery( String pstrQuery, String pstrParameter )
{
	String strReturn = pstrQuery;
	String strParameterEqual = pstrParameter + "=";
	int intPosStart = -1;
	int intPosEnd = -1;
	
	try
	{
		while( strReturn.indexOf( strParameterEqual) >= 0 )
		{
			intPosStart = strReturn.indexOf( strParameterEqual);
			// find next position for "&" string (if parameter is in the middle of the query URL...)
			intPosEnd = strReturn.indexOf( "&", intPosStart );
			if( intPosEnd == -1 )
			{
				intPosEnd = strReturn.length();
			}
	
			if ( intPosStart >= 1 && "&".equalsIgnoreCase( strReturn.substring( intPosStart - 1, intPosStart)) )
			{
				intPosStart--;
			}
			strReturn = ( (intPosStart > 0 ) ? strReturn.substring( 0, intPosStart ) : "" ) +
						( (intPosEnd < strReturn.length()) ? strReturn.substring( intPosEnd ) : "" );
		}
	}
	catch( IndexOutOfBoundsException excTemp)
	{
		return pstrQuery;
	}
	
	return strReturn;
}


}

