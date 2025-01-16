/**************************************************************************************************
*	Licensed Materials - Property of IBM
*	6949 - 66C
*	� Copyright IBM Corporation 2001, 2017 All Rights Reserved
*	
*	US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA
*	ADP Schedule Contract with IBM Corporation.
**************************************************************************************************/
package ene.eneform.service.utils.wls;


import ene.eneform.service.utils.ENEStatement;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Insert the type's description here.
 * Creation date: (13/06/2001 14:23:43)
 * @author: Administrator
 */
public class ReportTable
{
    private static String CHARSET_UTF8 = "utf-8";
	private int m_nColumns ;
	private Vector m_vColumns = null;	// vector of ReportColumn objects
	private String m_strTitle = "";
	private String m_strQuery;
	private String[] m_astrSort;
	private String[] m_astrSubtotal;
	private int m_nMaxRows = 200;
	private int m_nRows = 0;

	private ENEStatement m_statement;
	private ResultSet m_rs = null;

	private Vector m_vRows = null;		// rows if pre-read required
	private Enumeration m_eRows = null;	// enumeration if pre-read required
	private ReportRow m_currentRow = null;

	// for reverse reports
	private int m_nBlocks = -1;
	private int m_nBlockSize = -1;
	private double[][] m_adSubtotals = null;

	protected String m_strDateFmt;
	protected String m_strTimeFmt; 
	protected static SimpleDateFormat m_dateFmt;
	protected static SimpleDateFormat m_timeFmt;
	protected boolean m_bDB_stream_of_bytes_UTF8;
    
 
    protected String m_strDBType = "no";	// does current database contain native UTF-8 strings ?

	private static Pattern sm_PatternChildJSON=Pattern.compile("^(?i)[A-Z]*CHILD_"); // $[.]*
	protected int[] m_anChildColumnPositions = new int[8];
	protected String[] m_astrChildColumnNames = new String[8];
	
public ReportTable(ENEStatement statement, String strQuery,
					String strTitle, 
					int nMaxRows,
					String dateFmt,
					String timeFmt,
					boolean bDB_stream_of_bytes_UTF8,
					String strDBType
)
throws SQLException
{
	// Due to the way that the contents of the ResultSet are not read until actually required
	// it is necessary to store not only the ReseultSet bu also the connection.
	// In this way, both can be released (by call to cleanup) when report is complete
	m_strQuery = strQuery;
	m_nMaxRows = nMaxRows;
	m_strTitle = strTitle;
	
   m_rs = statement.executeQuery(m_strQuery);

	m_strDateFmt = dateFmt;
	m_strTimeFmt = timeFmt;
	
    m_dateFmt = new SimpleDateFormat(dateFmt, new DateFormatSymbols(new Locale("en")) );
    m_timeFmt = new SimpleDateFormat(timeFmt, new DateFormatSymbols(new Locale("en")));
    
	m_bDB_stream_of_bytes_UTF8 = bDB_stream_of_bytes_UTF8;
	m_strDBType = strDBType;
}

public void cleanup() throws SQLException
{
	if (m_rs != null)
	{
	   	m_rs.close();
	   	m_rs = null;
	}
	if (m_statement != null)
	{
   		m_statement.close();
   		m_statement = null;
	}
}
private void createSubtotals(int nSubtotals)
{
	// each row of m_anSubtotals corresponds to a subtotal
	// one column in the array for each column, plus column 0 for an element count 
	m_adSubtotals = new double[nSubtotals][m_nColumns + 1];
	for(int i = 0; i < m_adSubtotals.length; i++)
	{
		for(int j = 0; j < m_adSubtotals[i].length; j++)
			m_adSubtotals[i][j] = 0;
	}
}
public String getAttributeValue(String strAttribute)
{
    if ("report_title".equals(strAttribute))
    	return getTitle();
	else if ("query".equals(strAttribute))
		return m_strQuery;
	else if ("nr_columns".equals(strAttribute))
    	return String.valueOf(getNrColumns());
    else if ("nr_rows".equals(strAttribute))
    {
		try
		{
    		getNrRows();
		}
		catch(SQLException e)
		{
		}
	    
    	return String.valueOf(m_nRows);
    }
    else if (strAttribute.startsWith("column_title_"))
	{
	    String strColumn = strAttribute.substring("column_title_".length());
	    try
	    {
		    int nColumn = Integer.parseInt(strColumn);
	        return getColumn(nColumn).getTitle();
	    }
	    catch(NumberFormatException e)
	    {
		    return "";
	    }
	}
	else if ("date_format".equals(strAttribute))
		return m_dateFmt.toPattern();
	else if ("time_format".equals(strAttribute))
		return m_timeFmt.toPattern();
	
    return null;
}
public ReportColumn getColumn(int nColumn)
{
	if ((nColumn > 0) && (nColumn <= m_nColumns))
		return (ReportColumn)m_vColumns.elementAt(nColumn - 1);
	else
		return null;	
}
public ReportColumn getColumn(String strColumn)
{
	for(int i = 0; i < m_vColumns.size(); i++)
	{
		ReportColumn column = (ReportColumn)m_vColumns.elementAt(i);
		if (strColumn.equalsIgnoreCase(column.getCode()))
			return column;
	}
		return null;	
}
public int getColumnNr(String strColumn)
{
    ReportColumn column = getColumn(strColumn);
    if (column != null)
        return column.getNumber();
    else
        return 0;
}
private String getColumnTitle(String strColumn)
{
	// Need context, as there may be an offering level version of the db_tab_col properties file
	return strColumn;
}
public boolean usesJSONDotSyntax() 
{
	// for use with SELECT_JSON
	// JSON structure is communicated using . as separator
	for(int i = 0; i < m_vColumns.size(); i++)
	{
		ReportColumn column = (ReportColumn)m_vColumns.elementAt(i);
		if (column.getTitle().indexOf(".") >= 0)
			return true;
	}
	
	return false;	
}
public SimpleDateFormat getDateFormat()
{
    return m_dateFmt;
}
public Object getItem(int nRow, int nColumn)
{
	return ((ReportRow)m_vRows.elementAt(nRow - 1)).getCell(nColumn);
}

public int getMaxRows()
{
	return m_nMaxRows;
}
public int getNrColumns()
{
	return m_nColumns;
}
public int getNrRows() throws SQLException
{
	getReportRows();
		
	return m_nRows;
}
public Object getObject(int nColumn)
throws SQLException
{
	if (m_currentRow != null)
		return m_currentRow.getCell(nColumn);
	else if ((m_eRows == null) && (m_rs != null))	// data has not yet been extracted from ResultSet
	{
		ReportColumn column = (ReportColumn)m_vColumns.elementAt(nColumn-1);
		if (column.isCount())
		{
			return getCountColumn(column, nColumn);
		}
		else
			return m_rs.getObject(nColumn);
	}
	else
		return null;
}
public Object getCountColumn(ReportColumn column, int nColumn)
throws SQLException
{
	String strType = column.getType();
	if ("DOUBLE".equalsIgnoreCase(strType)
	|| "FLOAT".equalsIgnoreCase(strType))
	{
		double dValue = m_rs.getDouble(nColumn);
		column.incrementCount( dValue );
		return dValue;
	}
	else if ("DECIMAL".equalsIgnoreCase(strType))
	{
		BigDecimal bd = (BigDecimal) m_rs.getObject(nColumn);
		if (bd != null)
		{
			column.incrementCount( bd.doubleValue() );
		}
		return bd;
	}
	else
	{
		int nCount = m_rs.getInt(nColumn);
		column.incrementCount(nCount);
		return nCount;
	}
}
public double getPercentCount(int nColumn, Object aoRow[])
{
	ReportColumn column = getColumn(nColumn);
    int nCountColumn = column.getPercent();
    if (nCountColumn >= 0)
    {
	    // If not based on another column then just display as a percent
	    // by dividing and multiplying by 100
        double dCount = 100;
        if (nCountColumn > 0)
        {
            if (column.isPercentOfTotal())
                dCount = getColumn(nCountColumn).getCount();
            else
                dCount = getColumn(nCountColumn).getNumericObjectasDouble(aoRow[nCountColumn - 1]);
        }

        return dCount;
    }
    else
        return -1;
}
private void getReportRows() throws SQLException
{
	if ((m_rs != null) && (m_nRows == 0) && (m_eRows == null))	// data hasn't been read yet
	{
		Object o;
		m_nRows = 0;

		m_vRows = new Vector();
		while (m_rs.next() && ((m_nMaxRows < 0) || (m_nRows < m_nMaxRows))) 
		{
			String strKey = "";	
			Vector vCells = new Vector();
			m_nRows++;
			int nSortCount = 0;
			int nSortDescendingCount = 0;
			   
			for (int i = 1; i <= m_nColumns; i++) 
			{
				ReportColumn column = getColumn(i);
				int nSort = column.getSort();

				if (column.isCount()) 
				{
					o = getCountColumn(column, i);
				}
				else if (nSort != 0)
				{
					// If internal sorting is required due to the use of bundles
					// need a key for each row which can be used by the comparator.
					// This prevents the mixing of ASC and DESC
					// Assume ASC, and only use DESC if all fields to be sorted on
					// specify DESC
						// A.T. 20/01/2009
						// deal with string data coming from DB where we store "stream of bytes" (for UTF-8 representation of a char).
						if ( m_bDB_stream_of_bytes_UTF8
							 && !column.isNumeric() 
							 && !column.isDate()  )
						{
							o = DatabaseUtils.getBinaryStreamAsString(m_rs, i, CHARSET_UTF8 );
						}
						else
						{																
							o = m_rs.getObject(i);
						}
						if (o != null)
							strKey += o.toString().toUpperCase();
						else
							strKey+="_";	
					nSortCount++;
					if (nSort == -1)
					{
						// represents DESC
						nSortDescendingCount++;
					}
				}
				else if (column.isBinary() || column.isBitData())
				{
					o = DatabaseUtils.getBinaryStreamAsString(m_rs, i, CHARSET_UTF8 );
				}
//				else if ( LanguageConstants.CHARSET_UTF8.equals(WLSLanguageStore.getXMLCharSet(m_strLanguage))
//							&& !column.isNumeric() 
//							&& !column.isDate() 
//							&& !m_bDB_stream_of_bytes_UTF8)
//				{
//					// gives error WS6 JDBC drivers
//					// o = m_rs.getBinaryStreamAsString(i, LanguageConstants.CHARSET_UTF8);
//					o = m_rs.getObject(i);					
//				}
				// A.T. 20/01/2009
				// deal with string data coming from DB where we store "stream of bytes" (for UTF-8 representation of a char).
				else if (  m_bDB_stream_of_bytes_UTF8  
						   && !column.isNumeric() 
						   && !column.isDate() )
				{
					o = DatabaseUtils.getBinaryStreamAsString(m_rs, i, CHARSET_UTF8 );
				}
				else
				{
					o = m_rs.getObject(i);
				}
			
				vCells.addElement(o);
			}

			m_vRows.addElement(new ReportRow(this, m_nRows, strKey.toUpperCase(), vCells, nSortCount != nSortDescendingCount));
		}

		cleanup();	// have finished with query and ResultSet
			
		m_eRows = m_vRows.elements();
	}
}
private void getReverseBlocks(int nKeyColumns) throws SQLException
{
	if ((m_nBlocks == -1) || (m_nBlockSize == -1))
	{
		getReportRows();

		int nBlockSize = 1;
		m_nBlocks = 0;
		m_nBlockSize = 0;
		String strBlockKey = "";
		for(int i = 1; i <= m_nRows; i++)
		{
			String strKey = "";
			for(int j = 1; j <= nKeyColumns; j++)
			{
				strKey += getItem(i, j).toString();
			}
			if (!strBlockKey.equals(strKey))
			{
				m_nBlocks++;
				strBlockKey = strKey;
				if (nBlockSize > m_nBlockSize)
					m_nBlockSize = nBlockSize;
				nBlockSize = 1;
			}
			else
				nBlockSize++;
		}
		if (nBlockSize > m_nBlockSize)
			m_nBlockSize = nBlockSize;
	}
}
public int getReverseBlockSize(int nKeyColumns)
{
	// n columns for the key, plus 1 for the headers
	return m_nColumns - (nKeyColumns + 1);
}
public int getReverseNrBlocks(int nKeyColumns) throws SQLException
{
	getReverseBlocks(nKeyColumns);
	
	return m_nBlocks;
}
public int getReverseNrColumns(int nKeyColumns) throws SQLException
{
	getReverseBlocks(nKeyColumns);
	
	return m_nBlockSize + nKeyColumns;
}
public int getReverseNrRows(int nKeyColumns) throws SQLException
{
	getReverseBlocks(nKeyColumns);
	
	return m_nBlocks * getReverseBlockSize(nKeyColumns);
}
public double getSubtotal(int nSubtotal, int nColumn)
{
	// convert column number to position in subtotals array
	int nSub = getColumn(nSubtotal).getSubtotal();

	return m_adSubtotals[nSub][nColumn];
}
public int getSubtotalCount(int nSubtotal)
{
    // convert column number to position in subtotals array
    int nSub = getColumn(nSubtotal).getSubtotal();

	// this is a counter, will always have integer values
	return ((int) m_adSubtotals[nSub][0]) + 1;
}
public SimpleDateFormat getTimeFormat()
{
    return m_timeFmt;
}
public String getTitle()
{
	return m_strTitle;
}
public boolean hasCount()
{
	for(int i = 0; i < m_vColumns.size(); i++)
	{
		if (((ReportColumn)m_vColumns.elementAt(i)).isCount())
			return true;
	}

	return false;
}
private boolean hasPercent()
{
	// has the table a percent column which is calculated using another column?
	for(int i = 0; i < m_vColumns.size(); i++)
	{
		if (((ReportColumn)m_vColumns.elementAt(i)).isColumnPercent())
			return true;
	}

	return false;
}
public boolean hasSubtotals()
{
	return (m_adSubtotals != null);
}
public void incSubtotalCount(int nSubtotal)
{
	// convert column number to position in subtotals array
	int nSub = getColumn(nSubtotal).getSubtotal();

	// this is a counter, will always have integer values
	m_adSubtotals[nSub][0] = ((int) m_adSubtotals[nSub][0]) + 1;
}
public void incSubtotals(int nColumn, double dValue)
{
	// increment all subtotals for a specified column
	for(int i = 0; i < m_adSubtotals.length; i++)
		m_adSubtotals[i][nColumn] += dValue;
}
public void initialise(boolean bPreRead) throws SQLException
{
    // 25/04/03 Allow to force an immediate read.
	// 20160927 - reports no longer force immediate read - FOR READ ONLY WITH UR is appended for all queries, so no harm in keeping connection open a little bit longer
	// the fact that results only read one row at a time (and not stored) will hopefully reduce memory usage
    // 2003: Reports want to use the ReportTable as a Current Item but the connection must be freed, so must extract data from ResultSet immediately
    
    // separate this processing from that in the constructor,
    // to allow optional setting of bundles, sort order and subtotals
	m_vColumns = new Vector();
	m_nColumns = 0;
	boolean bEncrypted = false;
	
	if (m_rs != null)
	{
		ResultSetMetaData rsmd = m_rs.getMetaData();
		
		m_nColumns = rsmd.getColumnCount();

	    String[] astrHiddenColumns = null;
		    
		List lstHiddenColumns;
		if (astrHiddenColumns != null)
		{
			for(int i = 0; i < astrHiddenColumns.length; i++)
			{
				astrHiddenColumns[i] = ((String)astrHiddenColumns[i]).toUpperCase();
			}
			lstHiddenColumns = Arrays.asList(astrHiddenColumns);
		}
		else
			lstHiddenColumns = new ArrayList();	// empty
				
	    for (int i = 1; i <= m_nColumns; i++) 
		{
			String strColumn = rsmd.getColumnLabel(i).trim();
			String strColumnType = rsmd.getColumnTypeName(i);
			String strColumnTitle = getColumnTitle(strColumn);
    		// LI-2494
    		bEncrypted = false;     // m_languageStore.isEncrypted( strColumn.toUpperCase(), "en" );
    		
                ReportColumn rptColumn = new ReportColumn( i, strColumn, strColumnType, strColumnTitle,
							lstHiddenColumns.contains(strColumn.toUpperCase()), bEncrypted, m_strDBType );

			m_vColumns.addElement(rptColumn);
			if (rptColumn.isColumnPercent())
				getColumn(rptColumn.getPercent()).setCount();
		}
	}

	// reset percentage titles
	for (int i = 1; i <= m_nColumns; i++) 
	{
		ReportColumn column = getColumn(i);
		initialiseColumn(column);
	}   

	if (m_astrSort != null)
	{
		for (int i = 0; i < m_astrSort.length; i++)
		{
			String strSort = m_astrSort[i];
			if (!"".equals(strSort))
				processSort(strSort);
		}
	}

	if (m_astrSubtotal != null)
	{
		int nSubtotal = 0;
		for (int i = 0; i < m_astrSubtotal.length; i++)
		{
			String strSubtotal = m_astrSubtotal[i];
			if (!"".equals(strSubtotal))
			{
				processSubtotal(strSubtotal, nSubtotal);
				nSubtotal++;
			}
		}
		if (nSubtotal > 0)
			createSubtotals(m_astrSubtotal.length);
	}

	bPreRead = bPreRead || needsPreRead();
	
	if (bPreRead)
		getReportRows();
}
private void initialiseColumn(ReportColumn column)    
{
	String strCode = column.getCode();
	boolean bPercentTotal = true;
	int nPercent = -1;
	
	if (strCode.indexOf("PERCENT_") != -1)
	{
		int nPercentIndex;
		String strSuffix;
		if ((nPercentIndex = strCode.indexOf("PERCENT_COLUMN_")) != -1)
		{
			strSuffix = strCode.substring(nPercentIndex + "PERCENT_COLUMN_".length());
				
			nPercent = extractColumnNumber(strSuffix);
				
			bPercentTotal = false;
		}
		else if ((nPercentIndex = strCode.indexOf("PERCENT_TOTAL_")) != -1)
		{
			strSuffix = strCode.substring(nPercentIndex + "PERCENT_TOTAL_".length());
				
			nPercent = extractColumnNumber(strSuffix);
			bPercentTotal = true;
		}
		else
		{
			// this syntax should be phased out for more explicit PERCENT_TOTAL_
			nPercentIndex = strCode.indexOf("PERCENT_");
			strSuffix = strCode.substring(nPercentIndex + "PERCENT_".length());
				
			nPercent = extractColumnNumber(strSuffix);
			bPercentTotal = true;
		}
			
		column.setPercent(nPercent, bPercentTotal);
		
		column.setCount();
	}
	else if (strCode.endsWith("COUNT") || strCode.endsWith("TOTAL"))
	{
		column.setCount();
	}

	// SE 4/11/03 Only use default if explicit trnzaslation of code to title has not been provided
	// Now support X_PERCENT_COLUMN_n  so can choose X in order to provide title
	if ((nPercent > 0) && (nPercent != column.getNumber()) && (!column.getCode().startsWith("ITEM_"))
			&& column.getCode().equals(column.getTitle()))
	{
		String strTitle1 = "Percent " + getColumn(nPercent).getTitle();
		column.setTitle(strTitle1);
	}
 }
    
private int extractColumnNumber(String strColumn)
{
	int nPercent = -1;
	// first check if it is another column in the query
	for (int i = 1; i <= m_nColumns; i++) 
	{
		ReportColumn column1 = getColumn(i);
		if (strColumn.equalsIgnoreCase(column1.getCode()))
			nPercent = i;
	}   
	// then check if a column number
	if (nPercent == -1)
	{
		try
		{
			nPercent = Integer.valueOf(strColumn).intValue();
		}
		catch(NumberFormatException e)
		{
			nPercent = 0;	
		}
	}
		
	return nPercent;			
}    
private boolean needsPreRead()
{
	// needs preload if ..
	// a) percent depending on another column is required
	// b) sort is specified for a bundle field
	return hasPercent();
}
public boolean nextRow()
throws SQLException
{
	// move to next row, and return whether one exists
	if (m_eRows != null)
	{
		// All data has already been extracted from ResultSet
		if (m_eRows.hasMoreElements())
		{
			m_currentRow = (ReportRow) m_eRows.nextElement();
			return true;
		}
		else
			return false;
	}
	else if ((m_rs != null) && ((m_nMaxRows < 0) || (m_nRows < m_nMaxRows)))
	{
		boolean bNext = m_rs.next();
		if (bNext)
			m_nRows++;
		
		return bNext;	
	}
	
	return false;
}
private void processSort(String strSort)
{
	boolean bSortAscend = true;

	strSort = strSort.toUpperCase();
	String strColumn = null;
	
	int nOrder = strSort.indexOf("DESC");
	if (nOrder > -1)
	{
		bSortAscend = false;
		strColumn = strSort.substring(0, nOrder).trim();
	}
	else
	{
		nOrder = strSort.indexOf("ASC");
		if (nOrder > -1)
		{
			strColumn = strSort.substring(0, nOrder).trim();
		}
		else
			strColumn = strSort.trim();
	}

	// strColumn may be column number or column name
	try
	{
		int nColumn = Integer.valueOf(strColumn).intValue();
		ReportColumn column = getColumn(nColumn);
		if (column != null)
			column.setSort(bSortAscend);
	}
	catch(NumberFormatException e)
	{
		ReportColumn column = getColumn(strColumn);
		if (column != null)
			column.setSort(bSortAscend);
	}
}
private void processSubtotal(String strColumn, int nSubtotal)
{
	// strColumn may be column number or column name
	try
	{
		int nColumn = Integer.valueOf(strColumn).intValue();
		ReportColumn column = getColumn(nColumn);
		if (column != null)
			column.setSubtotal(nSubtotal);
	}
	catch(NumberFormatException e)
	{
		ReportColumn column = getColumn(strColumn);
		if (column != null)
			column.setSubtotal(nSubtotal);
	}
}
public void resetSubtotals(int nSubtotal)
{
	// convert column number to position in subtotals array
	int nSub = getColumn(nSubtotal).getSubtotal();
	
	// reset all values for a specified subtotal
	// and all subsequent subtiotals,
	// as these are subtotals WITHIN the specified subtotal
	for(int i = nSub; i < m_adSubtotals.length; i++)
	{
		for(int j = 0; j < m_adSubtotals[i].length; j++)
		{
			m_adSubtotals[i][j] = 0;
		}
	}
	
}
public void setSortColumns(String[] astrSort)
{
    m_astrSort = astrSort;
}
public void setSubtotalColumns(String[] astrSubtotal)
{
    m_astrSubtotal = astrSubtotal;
}

// JSON processing
public JSONArray convertRow2JSONArray(int nRow) throws SQLException
{
	JSONArray  arr = new JSONArray();
	for  (int nColumn = 1; nColumn <= getNrColumns(); nColumn++)
	{
            Object objValue = getItem(nRow, nColumn);
            try
            {
                 arr.put(objValue);
            }
            catch(Exception e)
            {
                arr.put("Invalid value");
            }
        }
        return arr;
}
public JSONObject convertRow2JSONObject(int nRow, boolean bDefault) throws SQLException
{
	JSONObject  obj = new JSONObject();
	for  (int nColumn = 1; nColumn <= getNrColumns(); nColumn++)
	{
		ReportColumn column = getColumn(nColumn);
		Object objValue = getItem(nRow, nColumn);
		String strColTitle = column.getTitle();
		String[] astrElements = strColTitle.split("\\.");
		JSONObject currentObj = obj;
		for(int i = 0; i < astrElements.length; i++)
		{
			String strFullColName = astrElements[i];
			String strColName = (strFullColName.indexOf("a__") == 0) ? strFullColName.substring(3) : strFullColName;
			if (currentObj.has(strColName))
			{
				Object obj1 = currentObj.get(strColName);
				if (obj1 instanceof JSONArray)
				{
					currentObj = (JSONObject) ((JSONArray) obj1).get(0);	// will only be one, because relates to single row
				}
				else
				{
					currentObj = (JSONObject) obj1;
				}
			}
			else
			{
				if (i == (astrElements.length - 1))		// node, must be an attribute
                                {
                                    Object obj2 = processJSONAttributeValue(column, objValue, bDefault);
                                    if (obj2 != null)
					currentObj.put(strColName, obj2);   // don't write attribute if null
                                }
				else
				{
					JSONObject newObj = new JSONObject();
					if (strFullColName.indexOf("a__") == 0)		// this is an array
					{
						JSONArray array = new JSONArray();
						array.put(newObj);
						currentObj.put(strColName, array);
					}
                                        else if (newObj != null)    // don't write attribute if null
					{
						currentObj.put(strColName, newObj);
					}
					currentObj = newObj;
				}
			}
		}
	}
	return obj;
}
private boolean emptyJSONObject(JSONObject obj)
{
	return (obj.keySet().size() == 0);
}
public int getJSONRowAttribute(JSONArray array, int nRow, boolean bDefault) throws SQLException
{
	JSONObject parent = new JSONObject();
	array.put(parent);
	int nTotalColumns = getNrColumns();
	
	JSONArray childArray = null;
	
	int nRowsProcessed = 0;
	String strPreviousKey=null;
	while (nRow <= getNrRows())
	{
		String strKey = getItem(nRow, 1).toString().trim();
                System.out.println("Compare:" + strPreviousKey + " with " + strKey);
		if ((strPreviousKey == null) || !strPreviousKey.equals(strKey))
		{
			childArray = new JSONArray();
			parent.put(strKey.trim(), childArray);
		}

		JSONObject  obj = new JSONObject();
		for  (int nColumn = 2; nColumn <= nTotalColumns; nColumn++)		// column 1 is attribute name, so properties are from column 2
		{
			ReportColumn column = getColumn(nColumn);
			String strAttribute = processJSONAttributeName(column);
			Object attValue = processJSONAttributeValue(column, getItem(nRow, nColumn), bDefault);
			obj.put(strAttribute.trim(), attValue);
		}
		
		if (!emptyJSONObject(obj))	// it has attributes, so not all values are null
		{
			childArray.put(obj);
		}
		strPreviousKey = strKey;
		nRowsProcessed += 1;
		nRow += 1;
	}
	
	return nRowsProcessed;	// always process one row for now
}
/*
public JSONObject getJSONAttributeReport(boolean bDefault) throws SQLException
{
	JSONArray array = new JSONArray();
	int nRows = getNrRows();
	int nRow = 1; 
	while(nRow <= nRows)
	{
		int nRowsProcessed = getJSONRowAttribute(array, nRow, bDefault);
		nRow += nRowsProcessed;
	}
	if (array.isEmpty())
		return null;
	else			
		return (JSONObject)(array.get(0));
}*/
private String removeChildPrefix(String strPrefix, String strColumnName)
{
	if ("".equals(strPrefix))
		return strColumnName;
	
	String strPattern = "^(?i)" + strPrefix;
	Pattern p = Pattern.compile(strPattern);
	StringBuffer sb = new StringBuffer(strColumnName.length());
	Matcher m = p.matcher(strColumnName);
	while(m.find())
	{
		m.appendReplacement(sb, "");	
	}
	m.appendTail(sb);
	
	return sb.toString();
}


private String getChildColumnName(int nColumn)
{
	String strPrefix="";
	for(int i = 0; i < m_anChildColumnPositions.length; i++)
	{
		if ((m_anChildColumnPositions[i] == 0) || (m_anChildColumnPositions[i] > nColumn))
			break;

		strPrefix = m_astrChildColumnNames[i];
	}
	if ("child_".equals(strPrefix) || "grandchild_".equals(strPrefix) || "greatgrandchild_".equals(strPrefix) || "greatgreatgrandchild_".equals(strPrefix))
		return "children";
	else
		return strPrefix.replaceAll("_[_]+",  "");	// to reuse the same name at several levels in the parent-child hierarachy, add additional underscores
}

private String getColumnPrefix(int nColumn)
{
	String strPrefix="";
	for(int i = 0; i < m_anChildColumnPositions.length; i++)
	{
		if ((m_anChildColumnPositions[i] == 0) || (m_anChildColumnPositions[i] > nColumn))
			break;

		strPrefix = m_astrChildColumnNames[i];
	}
	return strPrefix;
}
private String processJSONAttributeName(ReportColumn column)
{
	String strValue = column.getTitle();
	String strPrefix = getColumnPrefix(column.getNumber());
	return removeChildPrefix(strPrefix, strValue);
	//return strValue.replace(strPrefix.toUpperCase(), "");
}
private Object processJSONAttributeValue(ReportColumn column, Object obj, boolean bDefault)
{
	if (obj == null)
		return bDefault ? ReportTable.processJSONAttributeDefault(column.getType()) : null;

	// use of getStringValue vital for encrypted data
	String strValue = column.getStringValue(obj, getDateFormat(), "en");
	
	// DOUBLE, FLOAT, DECIMAL
	String strColumnType = column.getType();
    //System.out.println("processJSONAttributeValue: " + column.getCode() + "-" + strColumnType + "-" + obj);
	if (strValue.indexOf("${SELECT_JSON") >= 0)
		return strValue;
	else if (strColumnType.equals("BIT"))
	{
		// remove control characters etc
		return Boolean.parseBoolean(strValue);
	}
	else if (strColumnType.indexOf("INT") >= 0)
	{
		// remove control characters etc
		return Integer.parseInt(strValue);
	}
	else if ((strColumnType.indexOf("DECIMAL") == 0) || (strColumnType.indexOf("FLOAT") == 0) || (strColumnType.indexOf("DOUBLE") == 0))
	{
		// remove control characters etc
		return Float.parseFloat(strValue);
	}
	else
	{
		// CHAR or VARCHAR
		return strValue.trim();
	}
}
public JSONObject buildCombinedObject(JSONObject combinedObj, JSONObject previousObj, JSONObject currentObj) throws IOException
{
//System.out.println("In buildCombinedObject: " + combinedObj.serialize());
// find out how much of it is the same
JSONArray latestarray = null;
JSONObject latestobj = null;
for  (int nColumn = 1; nColumn <= getNrColumns(); nColumn++)
{
ReportColumn column = getColumn(nColumn);
String strColTitle = column.getTitle();
Object prev = getJSONObjectAttribute(previousObj, strColTitle);
Object current = getJSONObjectAttribute(currentObj, strColTitle);
Object combined = getJSONObjectAttribute(combinedObj, strColTitle);
// note that column names always end in attribute, never an array or object
int nLatest = strColTitle.lastIndexOf("a__");
if (nLatest >= 0)
{
int nLatestEnd = strColTitle.substring(nLatest).indexOf(".");
String strLatest = strColTitle.substring(0,  nLatest + nLatestEnd);
//System.out.println(strLatest);
latestarray =  (JSONArray) getJSONObjectAttribute(combinedObj, strLatest);
latestobj = (JSONObject)((JSONArray)getJSONObjectAttribute(currentObj, strLatest)).get(0);
}
String strPrevious = "";
String strCurrent = "";
if (prev != null)
strPrevious = prev.toString();
if (current != null)
strCurrent = current.toString();
//System.out.println(nColumn + " " + strColTitle + "-" + strPrevious + "-" + strCurrent);
if (strColTitle.indexOf("ATT_VALUE") >= 0 || !strPrevious.equals(strCurrent))
{
// Now need to go back to previous Array and add whole object there
if (latestarray == null)
{
return null; // rows are completely separate
}
else
{
latestarray.put(latestobj);
}
break;
}
} 
//System.out.println("Out buildCombinedObject: " + combinedObj.serialize());
return combinedObj;
}
public JSONObject buildCombinedObjectX(JSONObject combinedObj, JSONObject previousObj, JSONObject currentObj) throws IOException
{
	//System.out.println("In buildCombinedObject: " + combinedObj.serialize());
	// find out how much of it is the same
	JSONArray latestarray = null;
	JSONObject latestobj = null;
	for  (int nColumn = 1; nColumn <= getNrColumns(); nColumn++)
	{
		ReportColumn column = getColumn(nColumn);
		String strColTitle = column.getTitle();
		Object prev = getJSONObjectAttribute(previousObj, strColTitle);
		Object current = getJSONObjectAttribute(currentObj, strColTitle);
		Object combined = getJSONObjectAttribute(combinedObj, strColTitle);
		// note that column names always end in attribute, never an array or object
		int nLatest = strColTitle.lastIndexOf("a__");
		if (nLatest >= 0)
		{
			int nLatestEnd = strColTitle.substring(nLatest).indexOf(".");
			String strLatest = strColTitle.substring(0,  nLatest + nLatestEnd);
			//System.out.println(strLatest);
			latestarray =  (JSONArray) getJSONObjectAttribute(combinedObj, strLatest);
			latestobj = (JSONObject)((JSONArray)getJSONObjectAttribute(currentObj, strLatest)).get(0);
		}
		if (!prev.toString().equals(current.toString()))
		{
			//System.out.println("Not Same : " + nColumn + " " + strColTitle + "-" + prev.toString() + "-" + current.toString());
			// Now need to go back to previous Array and add whole object there
			if (latestarray == null)
			{
				return null;	// rows are completely separate
			}
			else
			{
				latestarray.put(latestobj);
			}
			break;
		}
	} 
	//System.out.println("Out buildCombinedObject: " + combinedObj.serialize());
	return combinedObj;
}
public Object getJSONObjectAttribute(JSONObject obj, String strJSONPath) throws IOException
{
	String[] astrAttributes = strJSONPath.split("\\.");
	Object obj1 = obj;
	for(int i = 0; i < astrAttributes.length; i++)
	{
		String strFullColName = astrAttributes[i];
		String strColName = (strFullColName.indexOf("a__") == 0) ? strFullColName.substring(3) : strFullColName;
		if (obj1 instanceof JSONArray)
		{
			JSONArray array = (JSONArray) obj1;
			obj1 = array.get(array.length() - 1);		// last one added
			obj1 = ((JSONObject)obj1).get(strColName);
		}
		else if (obj1 instanceof JSONObject)
		{
			obj1 = ((JSONObject)obj1).get(strColName);
		}
	}
	return obj1;
}
public JSONArray getJSONReportColumns() throws SQLException
{
	JSONArray array = new JSONArray();
	int nColumn = 1;
	while(nColumn <= m_nColumns)
	{
		ReportColumn column = getColumn(nColumn);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("code", column.getCode());
		jsonObj.put("title", column.getTitle());
		jsonObj.put("type", column.getType());
		array.put(jsonObj);
		nColumn++;
	}
	return array;
}

public JSONArray getJSONReportArrays() throws SQLException
{
    JSONArray array = new JSONArray();
    int nRows = getNrRows();
    int nRow = 1; 
    while(nRow <= nRows)
    {
        JSONArray currentArr = convertRow2JSONArray(nRow);
        array.put(currentArr);
        nRow++;
    }
                
    return array;            
}
public JSONArray getJSONReportRows(boolean bDefault) throws SQLException
{
	JSONArray array = new JSONArray();
	try
	{
		int nRows = getNrRows();
		int nRow = 1; 
		JSONObject previousObj = null;
		JSONObject combinedObj = null;		// multiple rows can be single object in array	
		while(nRow <= nRows)
		{
			JSONObject currentObj = convertRow2JSONObject(nRow, bDefault);
			if (previousObj != null)
			{
				JSONObject newCombinedObject = buildCombinedObject(combinedObj, previousObj, currentObj);
				if (newCombinedObject == null)	// nothing in common
				{
					array.put(combinedObj);		// store existing object and start building the next one
					combinedObj = currentObj;
				}
				else
				{
					combinedObj = newCombinedObject;
				}
			}
			else
			{
				// first row
				combinedObj = currentObj;
			}
			
			nRow++;
			previousObj = currentObj;
		}
		
		if (combinedObj != null)	// put last object into array
			array.put(combinedObj);
	}
	catch(IOException e)
	{
		System.out.println("IOException: " + e.getMessage());
	}
	
	return array;
}
private static Object processJSONAttributeDefault(String strColumnType)
{
	if (strColumnType.indexOf("INT") >= 0)     // BIGINT, SMALLINT
	{
		// remove control characters etc
		return -1;
	}
	else if ((strColumnType.indexOf("DECIMAL") == 0) || (strColumnType.indexOf("FLOAT") == 0) || (strColumnType.indexOf("DOUBLE") == 0))
	{
		// remove control characters etc
		return -1.0;
	}
	else
	{
		// CHAR or VARCHAR
		return "";
	}
}
}
