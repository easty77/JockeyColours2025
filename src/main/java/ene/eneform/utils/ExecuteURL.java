/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.htmlcleaner.*;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author Simon
 */
public class ExecuteURL {
    
public static HttpURLConnection executeURLConnection(String strURL, String strCharset)
throws IOException, FileNotFoundException
{
	URL urlExternalAA = new URL(strURL);

  
        HttpURLConnection hucExternalAA =
	        (HttpURLConnection) urlExternalAA.openConnection();

	String strUserInfo = urlExternalAA.getUserInfo();
	String strProtocol = urlExternalAA.getProtocol();

	if ((strProtocol != null) && strProtocol.startsWith("http") && (strUserInfo != null))
	{
		strUserInfo = decodeUrl(strUserInfo, strCharset);
                Base64.Encoder encoder = Base64.getEncoder();
		hucExternalAA.setRequestProperty("Authorization", "Basic " + encoder.encode(strUserInfo.getBytes()));
	}		

        return hucExternalAA;
 }
public static InputStream executeURLStream(String strURL, String strCharset)
throws IOException, FileNotFoundException, InterruptedException
{
    return executeURLStream(strURL, strCharset, 0);
}
public static InputStream executeURLStream(String strURL, String strCharset, int nRetry)
throws IOException, FileNotFoundException, InterruptedException
{
    if (nRetry > 1)
    {
        TimeUnit.SECONDS.sleep(60);     // this is the result of an Error 403 which is returned by the Racing Post site when too many queries are made too quickly, so sleep for a minute
    }
    InputStream isExternalAA = null;
	URL urlExternalAA = new URL(strURL);

  
        HttpURLConnection hucExternalAA =
	        (HttpURLConnection) urlExternalAA.openConnection();

	String strUserInfo = urlExternalAA.getUserInfo();
	String strProtocol = urlExternalAA.getProtocol();
        HttpURLConnection.setFollowRedirects(true);
        // pretend to be a browser
        hucExternalAA.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
	if ((strProtocol != null) && strProtocol.startsWith("http") && (strUserInfo != null))
	{
		strUserInfo = decodeUrl(strUserInfo, strCharset);
               Base64.Encoder encoder = Base64.getEncoder();
		hucExternalAA.setRequestProperty("Authorization", "Basic " + encoder.encode(strUserInfo.getBytes()));
	}		
        int rc = hucExternalAA.getResponseCode();
        if (rc == 200)
        {
            // SE 03022006 - use getInputStream to solve Authorization problems
            isExternalAA = hucExternalAA.getInputStream();
            return isExternalAA;
        }
        else if (rc == 301 || rc == 302)
        {
            // SE 03022006 - use getInputStream to solve Authorization problems
            String strNewURL = hucExternalAA.getHeaderField("Location");
            if (strNewURL.indexOf("http") < 0)
                strNewURL = "https://www.racingpost.com" + strNewURL;           // This is Racing Post owner
            return executeURLStream(strNewURL, strCharset);
        }
        else 
        {
            System.out.println("executeURLStream: " + rc + "-" + nRetry + "-" + strURL);
            if ((rc == 403) && (nRetry < 3))
            {
                return executeURLStream(strURL, strCharset, ++nRetry);       
            }
            else
            {
                return isExternalAA;
            }
        }
}
public static JSONObject executeURLJSON(String strURL, String strCharset)
throws IOException, FileNotFoundException, InterruptedException
{
   JSONObject json = null;
    String strContent = executeURL(strURL, strCharset, true);
        if (strContent != null)
        {
            //System.out.println(strContent);
            json = new JSONObject(strContent);
        }
    return json;
}
public static String executeURL(String strURL, String strCharset, boolean bReturnContent)
throws IOException, FileNotFoundException, InterruptedException, NullPointerException
{
	String strHTML = "";
	
	InputStream isExternalAA = executeURLStream(strURL, strCharset);
        
	//InputStream isExternalAA = urlExternalAA.openStream();
	// hucExternalAA.setDoInput(true);

	if (bReturnContent)
	{
	    BufferedReader rdr = new BufferedReader(new InputStreamReader(isExternalAA));

	    if (rdr != null)
	    {
		    StringBuffer sbURL = new StringBuffer(4000);
		    String strLine;
            while ((strLine = rdr.readLine()) != null)
            {
	            sbURL.append(strLine);
            }

            rdr.close();
			strHTML = sbURL.toString();
			//System.out.println(strURL + "->" + strHTML);
	    }
	}
	
	return strHTML;
}

private static String decodeUrl(String text, String strCharset) throws UnsupportedEncodingException
{
	 return URLDecoder.decode(text, strCharset);
}

public static boolean exists(String URLName){
    return exists(URLName, false);
}
public static boolean exists(String URLName, boolean bRedirect){
    int rc = check(URLName, bRedirect);
    if (rc == HttpURLConnection.HTTP_OK)
    {
        return true;
    }
    System.out.println(rc + "-" + URLName);
    return false;
}
public static int check(String URLName, boolean bRedirect){
    try {
      HttpURLConnection.setFollowRedirects(bRedirect);
      // note : you may also need
      //        HttpURLConnection.setInstanceFollowRedirects(false)
      HttpURLConnection con =
         (HttpURLConnection) new URL(URLName).openConnection();
      con.setRequestMethod("HEAD");
      con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
      //con.setDoInput(false);  // say that not planning to read input, so can hopefully reuse connection 20180810
      int rc = con.getResponseCode();
      return rc;
    }
    catch (Exception e) {
       System.out.println("Exists exception: " + URLName + "-" + e.getMessage());
       return -1;
    }
  }
        public static boolean openBrowser(String strURL)
        {
            try
            {
                if (Desktop.isDesktopSupported()) 
                {
                    // Windows
                    Desktop.getDesktop().browse(new URI(strURL));
                } 
                else 
                {
                    // Ubuntu
                    Runtime runtime = Runtime.getRuntime();
                    //runtime.exec("/usr/bin/firefox -new-window " + strURL);
                    if (strURL.indexOf("http") == 0)
                        strURL = "http://" + strURL;
                    runtime.exec("xdg-open " + strURL);
                }
            }
            catch(IOException e)
            {
                System.out.println("openBrowser IOException: " + e.getMessage());
                return false;
            }
           catch(URISyntaxException e)
            {
                System.out.println("openBrowser URISyntaxException: " + e.getMessage());
                return false;
            }
            
            return true;
        }
        public static TagNode getRootNode(String strURL, String strCharset)
        {
            System.out.println("getRootNode: " + strURL);
            TagNode rootNode = null;
            CleanerProperties props = new CleanerProperties();

            HtmlCleaner cleaner = new HtmlCleaner(props);
            try
            {
                InputStream is = ExecuteURL.executeURLStream(strURL, strCharset);    // space to +, quote to ...
                if (is != null)
                {
                    rootNode = cleaner.clean(is, strCharset);
                    //System.out.println(new PrettyXmlSerializer(new CleanerProperties()).getAsString(rootNode, strCharset));

                    is.close();
                }
            }
            catch(IOException e)
            {
                System.out.println("IOException getRootNode: " + strURL + "-" + e.getMessage());
            }
            catch(InterruptedException e)
            {
                System.out.println("InterruptedException getRootNode: " + strURL + "-" + e.getMessage());
            }
            return rootNode;
        }
        public static String getRootNodeContent(String strURL, String strCharset) throws IOException
        {
            TagNode rootNode = getRootNode(strURL, strCharset);
            String strContent = new PrettyXmlSerializer(new CleanerProperties()).getAsString(rootNode, strCharset);
            
            return strContent;
        }
        public static String getPageTitle(String strURL, String strCharset) throws IOException
        {
            TagNode rootNode = getRootNode(strURL, strCharset);
            return getTagNodeTitle(rootNode);
        }
       public static String getTagNodeTitle(TagNode rootNode) throws IOException
       {
            String strTitle = rootNode.getAttributeByName("title");
            List lstTitle = rootNode.getElementListByName("title", true);
            if (lstTitle.size() > 0)
                strTitle = ((TagNode)lstTitle.get(0)).getText().toString();
            
            return strTitle;
        }
      public static String getTagNodeListString(List lstNodes, int i) throws IOException
       {
           if (i < lstNodes.size())
                return ((TagNode)lstNodes.get(i)).getText().toString().trim();
           else
               return null;
       }
      public static String getTagNodeArrayString(TagNode[] aNodes, int i) throws IOException
       {
           if (i < aNodes.length)
                return aNodes[i].getText().toString().trim();
           else
               return null;
       }

       public static String getNodeContentString(TagNode top)
        {
            return getNodeContentString(top, "");
        }
       public static String getNodeContentString(TagNode top, String strContent)
        {
            return getNodeContentString(top, strContent, " ");
        }
       public static String getNodeContentString(TagNode top, String strContent, String strSeparator)
        {
                    List lstElements = top.getAllChildren();
                    Iterator iter = lstElements.iterator();
                    int nCount = 0;
                    while(iter.hasNext())
                    {
                        nCount++;
                        try
                        {
                            Object node1 = iter.next();
                            if (node1 instanceof EndTagToken)
                                continue;
                                HtmlNode node = (HtmlNode) node1;
                                if (node instanceof TagNode) // first node contains the horse name
                                {
                                    strContent = getNodeContentString((TagNode) node, strContent, strSeparator);
                                }
                                else if (node instanceof ContentNode) 
                                {
                                    ContentNode content = ((ContentNode) node); 
                                    if (!"".equals(strContent))
                                        strContent+=strSeparator;
                                    strContent += content.getContent();
                                }
                         }
                        catch(ClassCastException e)
                        {
                           System.out.println("getNodeContentString: " + e.getMessage());
                        }

                    }
                    
           return strContent;
        }
       public static String getFirstNodeContent(TagNode top)
       {
           // return first non-empty
           ArrayList<String> al = getNodeContentList(top);
           for (int i = 0; i < al.size(); i++)
           {
                if ((al.get(i)!= null) && !"".equals(al.get(i)))
                    return al.get(i);
           }
           return "";
        }
       public static String getSearchNodeContent(TagNode top, String strSearch)
       {
           ArrayList<String> al = getNodeContentList(top);
           for (int i = 0; i < al.size(); i++)
           {
               if (al.get(i).toLowerCase().indexOf(strSearch.toLowerCase()) == 0)
               {
                   return al.get(i).substring(strSearch.length()).trim();
               }
           }
           return "";
       }
      public static String getSubsequentNodeContent(TagNode top, String strSearch)
       {
           ArrayList<String> al = getNodeContentList(top);
           for (int i = 0; i < al.size() - 1; i++)
           {
               if (strSearch.equals(al.get(i)))
               {
                   for(int j = i + 1; j < al.size(); j++)
                   {
                       if (!"".equals(al.get(j)))
                            return al.get(j);
                   }
               }
           }
           return "";
       }
       public static ArrayList<String> getNodeContentList(TagNode top)
       {
           ArrayList<String> lstContent = new ArrayList<String>();
           return getNodeContentList(top, lstContent);
       }
        public static ArrayList<String> getNodeContentList(TagNode top, ArrayList<String> lstContent)
        {
                    List lstElements = top.getAllChildren();
                    Iterator iter = lstElements.iterator();
                    int nCount = 0;
                    while(iter.hasNext())
                    {
                        nCount++;
                       try
                        {
                            Object node1 = iter.next();
                            if (node1 instanceof EndTagToken)
                                continue;
                                HtmlNode node = (HtmlNode) node1;
                                if (node instanceof TagNode) // first node contains the horse name
                                {
                                    lstContent = getNodeContentList((TagNode) node, lstContent);
                                }
                                else if (node instanceof ContentNode) 
                                {
                                    ContentNode content = ((ContentNode) node); 
                                    lstContent.add(content.getContent().replace("\n", "").replace("&nbsp;", " ").replace("&#039;", "'").replace("è", "e").replace("é", "e").replace("ê", "e").replace("ë", "e").replace("ï", "i").replace("ç", "c").replace("ü", "u").replace("ä", "a").trim());
                                }
                        }
                        catch(ClassCastException e)
                        {
                           System.out.println("getNodeContentList: " + e.getMessage());
                        }

                    }
                    
           return lstContent;
        }
        

public static PostMethod createPostMethod(String strURL, Map<String,String> formFields)
{
    PostMethod postMethod = new PostMethod(strURL);
    Iterator<String> iter = formFields.keySet().iterator();
    while(iter.hasNext())
    {
        String strKey = iter.next();
        String strValue = formFields.get(strKey);
        if (strValue != null)
        {
            if ( (!"ctl00$cphContenuCentral$btRechercher".equals(strKey)) )
            {
                //System.out.println("Set post parameter: " + strKey + "-" + ((strValue.length() > 50) ? (strValue.substring(0, 50) + "...") : strValue) );
                postMethod.setParameter(strKey, strValue);
            }
        }
  
    }
     
    return postMethod;
}
public static PostMethod createASPPostMethod(String strURL, Map<String,String> formFields)
{
    PostMethod postMethod = new PostMethod(strURL);
    postMethod.setParameter("__VIEWSTATE", formFields.get("__VIEWSTATE"));
    postMethod.setParameter("__VIEWSTATEENCRYPTED", formFields.get("__VIEWSTATEENCRYPTED"));
    postMethod.setParameter("__EVENTVALIDATION", formFields.get("__EVENTVALIDATION")); 
     
    return postMethod;
}
 public static String executePostMethodString(PostMethod postMethod)
 {
    HttpClient httpClient = new HttpClient();
     String strContent = null;
    try 
    {
        httpClient.executeMethod(postMethod);
    } 
    catch (HttpException e)
    {
        e.printStackTrace();
    } 
    catch (IOException e) 
    {
        e.printStackTrace();
    }
    int nReturnCode = postMethod.getStatusCode();
    if ( nReturnCode== HttpStatus.SC_OK)
    {
        try
        {
            strContent = postMethod.getResponseBodyAsString();
            //System.out.println(strContent);
        }
        catch(IOException e)
        {
            
        }
    } 
    else
    {
        System.out.println("executePostMethod rc: " + nReturnCode + "-" + postMethod.getStatusText() + "-" + postMethod.getPath());
    }
    return strContent;
}
public static TagNode executePostMethod(PostMethod postMethod)
 {
     return executePostMethod(postMethod, false);
 }
public static JSONObject executePostMethodJSON(PostMethod postMethod)
 {
     return executePostMethodJSON(postMethod, true);
 }
public static JSONObject executePostMethodJSON(PostMethod postMethod, boolean bTrace)
{
    JSONObject json = null;
        String strContent = executePostMethodString(postMethod);
        if (strContent != null)
        {
            if (bTrace)
                System.out.println(strContent);
                json = new JSONObject(strContent);
        }
    return json;
}
public static TagNode executePostMethod(PostMethod postMethod, boolean bTrace)
 {
     TagNode node = null;
        String strContent = executePostMethodString(postMethod);
        if (strContent != null)
        {
            if (bTrace)
                System.out.println(strContent);
            CleanerProperties props = new CleanerProperties();
            HtmlCleaner cleaner = new HtmlCleaner(props);
           
            node = cleaner.clean(strContent);
       }
        
        return node;
 }

public static Map<String,String> getViewstate(TagNode node)
{
    Map<String,String> mapFields = new HashMap<String, String>();
    TagNode[] aInput = node.getElementsByName("input", true);
    for(int i = 0; i < aInput.length; i++)
    {
        String strValue = aInput[i].getAttributeByName("value");
        mapFields.put(aInput[i].getAttributeByName("name"), strValue);    // (strValue == null) ? "" : strValue
    }
    
    return mapFields;
}
public static boolean nodeContains(TagNode node, String strName)
{
    return node.getElementsByName(strName, true).length > 0;
}
public static HashMap<String,Integer> getColumnNumbers(TagNode headerRow, String strCellType)
{
    // cell type is td or th
    TagNode[] cellsHeader = headerRow.getElementsByName(strCellType, true);
    HashMap<String,Integer> hmColumns = new HashMap<String,Integer>();
    for(int j = 0; j < cellsHeader.length; j++)
    {
        TagNode header = cellsHeader[j];
        String strHeader = header.getText().toString().replaceAll("\\n", "").trim();
        int nIndex = strHeader.indexOf(" ");
        if (nIndex > 0)
            strHeader= strHeader.substring(0, nIndex);
        hmColumns.put(strHeader.trim(), j);
    }

    return hmColumns;
}

    public static String getText(BaseToken token) {
        if (token instanceof TagNode) {
            return ((TagNode) token).getText().toString();
        }
        return token.toString();
    }
    public static String getCookie(HttpURLConnection conn, String strCookie)
    {
        List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {

            // only want the first part of the cookie header that has the value
            String strContent = cookie.split(";")[0];
            String[] astrCookie = strContent.split("=");
            String strName=astrCookie[0];
            String strValue="";
            if (astrCookie.length > 1)
                strValue = astrCookie[1];
            if (strCookie.equals(strName))        // this cookie is vital!!
                return strValue;
            }
        }
        return "";
    }
 public static void sleep(int nSeconds)
{
        try 
        {
            Thread.sleep(nSeconds * 1000);                 //1000 milliseconds is one second.
        } 
        catch(InterruptedException ex) 
        {
            Thread.currentThread().interrupt();
        }
    
}

    public static String getRowAttribute(TagNode row, String strAttribute, String strAttributeValue, String strDefault) {
        String strContent = getRowAttribute(row, strAttribute, strAttributeValue);
        if (strContent == null) {
            strContent = strDefault;
        }
        return strContent;
    }

    public static String getRowAttribute(TagNode row, String strAttribute, String strAttributeValue) {
        String strContent = null;
        TagNode[] aTN = row.getElementsByAttValue(strAttribute, strAttributeValue, true, true);
        if (aTN.length > 0) {
            strContent = aTN[0].getText().toString().trim();
        }
        return strContent;
    }

    public static String getTrimmedText(TagNode node) {
        String strContent = node.getText().toString();
        int nIndex = strContent.indexOf("\n");
        if (nIndex > 0) {
            strContent = strContent.substring(0, nIndex);
        }
        return strContent.trim();
    }

    /*  public static String getWikipediaReferencesV1(ENEStatement statement, String strARDName) throws IOException, ParseException
    {
    String strWikipediaRef="";
    String strLanguage="en";
    int nStartYear = 1994;
    int nEndYear = 2014;
    AdditionalRaceData racedata = WikipediaFactory.createAdditionalRaceData(statement, strARDName);
    if (racedata == null)
    {
    System.out.println("AdditionalRaceData  not found: " + strARDName);
    return "";
    }
    HashMap<Integer,String> hmWinners = WikipediaQuery.getWikipediaWinners(strLanguage, racedata.getWikipedia(strLanguage).getWikipediaRef());
    for (int nYear = nEndYear; nYear >= nStartYear; nYear--)
    {
    String strWinner = hmWinners.get(nYear);
    if (strWinner != null)
    {
    if (!"".equals(strWikipediaRef))
    strWikipediaRef += ", ";
    strWikipediaRef += getWikipediaReference(statement, racedata, strWinner, nYear);
    }
    else
    System.out.println(strARDName +" winner not found: " + nYear);
    }
    return strWikipediaRef;
    } */
    public static TagNode getRowAttributeNode(TagNode row, String strAttribute, String strAttributeValue) {
        TagNode[] aTN = row.getElementsByAttValue(strAttribute, strAttributeValue, true, true);
        if (aTN.length > 0) {
            return aTN[0];
        }
        return null;
    }

    public static int getRowAttributeInt(TagNode row, String strAttribute, String strAttributeValue) {
        int nContent = 0;
        String strContent = getRowAttribute(row, strAttribute, strAttributeValue);
        if (strContent != null) {
            try {
                nContent = Integer.parseInt(strContent);
            } catch (NumberFormatException e) {
            }
        }
        return nContent;
    }
}
