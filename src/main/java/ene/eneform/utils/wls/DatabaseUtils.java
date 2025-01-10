/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.utils.wls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.sql.ResultSet;

/**
 *
 * @author simon
 */
public class DatabaseUtils {
    
public static String convertToMillisecondsTimestamp(java.sql.Timestamp timestamp)
{
	return convertToMillisecondsTimestamp(timestamp, false);
}
public static String convertToMillisecondsTimestamp(java.sql.Timestamp timestamp, boolean bJDK14)
{
	long lMilliSeconds = timestamp.getTime();
	long lNanoSeconds = timestamp.getNanos();

	if (bJDK14)
	{
		// in JDK 1.4, nanoseconds contains milliseconds too
		// so need to remove 1st three digits, to make it work as for JDK 1.3
		long lNanoMilli = lNanoSeconds/1000000;
		long lNanoSeconds1 = lNanoSeconds - (lNanoMilli * 1000000); 

		return String.valueOf((1000 * lMilliSeconds) + (lNanoSeconds1/1000));
	}
	else
		return String.valueOf((1000 * lMilliSeconds) + (lNanoSeconds/1000)); 
}
public static String getBinaryStreamAsString(ResultSet rs, int nColumnIndex, String strCharset) 
throws java.sql.SQLException
{
	String strContent="";
	   InputStream is = rs.getBinaryStream(nColumnIndex);
	
	if (is != null)
		strContent = convertInputStream(is, strCharset);
		
	return strContent;
}
private static String convertInputStream(InputStream is, String strCharset)
{
	String strContent = "";

	   Charset charsetCurrent = Charset.forName( strCharset );

	   CharsetDecoder charsetDecoderCurrent = charsetCurrent.newDecoder();
	charsetDecoderCurrent.reset();
	charsetDecoderCurrent.replaceWith("X");
	charsetDecoderCurrent.onMalformedInput(CodingErrorAction.REPLACE);

	char cChar;
	int iCharRead;
	try
	{
		          InputStreamReader  isr = new InputStreamReader( is, charsetDecoderCurrent );
		          BufferedReader br = new BufferedReader(isr);
		String strLine;
		while((strLine = br.readLine()) != null)
		{
			if (!"".equals(strContent))
				strContent += '\n';
				
			strContent += strLine;
		}
		br.close();
		// Try to read it char after char.....
		//		while ( (iCharRead = isr.read()) != -1 )
		//		{
		//			strContent += (char)iCharRead;
		//		}
		isr.close();
		is.close();
	}
	catch(IOException e)
	{
		strContent = "?????";
	}
	
	return strContent;
}
}
