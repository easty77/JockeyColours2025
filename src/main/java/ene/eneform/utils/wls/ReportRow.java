// ********************************************************************
// *
// * Copyright IBM Corporation 2001, 2004
// * 
// * Web Lecture Services
// * 
// ********************************************************************
package ene.eneform.utils.wls;

/**
 * Insert the type's description here.
 * Creation date: (14/06/2001 9:51:55)
 * @author: Administrator
 */


import java.util.Date;
import java.util.Vector;
 
public class ReportRow
{
	private int m_nRow;
	private String m_strKey;
	private Vector m_vCells;
	private boolean m_bSortAscending = true;
	private ReportTable m_table;

	public ReportRow(ReportTable table, int nRow, String strKey, Vector vCells)
	{
		this(table, nRow, strKey, vCells, true);
	}
	public ReportRow(ReportTable table, int nRow, String strKey, Vector vCells, boolean bSortAscending)
	{
		m_table = table;
		m_nRow = nRow;
		m_strKey = strKey;
		m_vCells = vCells;
		m_bSortAscending = bSortAscending;
	}
public int compareTo(Object obj1) 
{
	if (m_bSortAscending)
		return m_strKey.compareTo(((ReportRow)obj1).m_strKey);
	else	
		return ((ReportRow)obj1).m_strKey.compareTo(m_strKey);
}
public String getAttributeCellValue(int nColumn, Object obj)
{
	// Warning: Functionality is duplicated in defaultReportCell of DefaultReportFormatter
	
	// SE 5/7/02 - uses both ReportTable and ReportContentFormatter to format 
	// the contents of a cell. Is this justified, or
	// should the Tag syntax only provide support via the ReportContentFormatter?
	if (obj == null)
		return "";

	ReportColumn column = m_table.getColumn(nColumn);

	String strCellValue = null;
	// Grand Total row may have "Total" in a numeric column e.g. Months
	// thus anticipate a ClassCast exception
	try
	{
		// percentage is handled elsewhere
		if (column.isNumeric())
		{
			Integer intCellValue = (Integer) obj;

			if (intCellValue != null)
			{
        			strCellValue = String.valueOf(intCellValue);
			}
			else
				strCellValue = "-";
		}
		else if (column.isDate())
		{
			Date dt = (Date) obj;
			if (dt == null)
				strCellValue = "-";
			else
				strCellValue = m_table.getDateFormat().format(dt);
		}
		else if (column.isTimestamp())
		{
			java.sql.Timestamp timestamp = (java.sql.Timestamp) obj;
			if (timestamp == null)
				strCellValue = "-";
			else
				strCellValue = DatabaseUtils.convertToMillisecondsTimestamp(timestamp);
		}
		else if (column.isTime())
		{
			Date dt = (Date) obj;
			if (dt == null)
				strCellValue = "-";
			else
				strCellValue = m_table.getTimeFormat().format(dt);
		}
		else	// text or bundle
		{
			strCellValue = (String)obj;
			if (strCellValue == null)
				strCellValue = "-";
			/* SE 24/03/2004 - Do not do this, at is screws up the XML used by CAS
			Originally included for Netscape 4 which wouldn't display empty cells correctly
			else if ("".equals(strCellValue))
				strCellValue = "&nbsp;"; */
			else
			{
				strCellValue = strCellValue.trim();
			}
		}
	}
	catch(ClassCastException e)
	{
		strCellValue = obj.toString();
	}

	return strCellValue;

}
public String getAttributeValue(String strAttribute)
{
    if ("key".equals(strAttribute))
    	return m_strKey;
    else if (strAttribute.startsWith("column_"))
    {
	    String strCell = strAttribute.substring("column_".length());
	    try
	    {
		    int nColumn = Integer.parseInt(strCell);
		    return getAttributeCellValue(nColumn, getCell(nColumn));
	    }
	    catch(NumberFormatException e)
	    {
		    return "";
	    }
    }
    else if (strAttribute.startsWith("title_"))
    {
	    String strCell = strAttribute.substring("title_".length());
	    try
	    {
		    int nColumn = Integer.parseInt(strCell);
		    ReportColumn column = m_table.getColumn(nColumn);
		    if (column != null)
			    return column.getTitle();
	    }
	    catch(NumberFormatException e)
	    {
	    }
	    return "";
    }
    else if ("nr_columns".equals(strAttribute))
    {
	    return String.valueOf(m_table.getNrColumns());
    }
    
    // Is it a column name?
	int nColumn = m_table.getColumnNr(strAttribute);
	if (nColumn != 0)	// it is a valid column name
    {
	    return getAttributeCellValue(nColumn, getCell(nColumn));
    }

    return null;
}
public Object getCell(int nColumn)
{
    if ((nColumn >= 1) && (nColumn <= m_vCells.size()))
        return m_vCells.elementAt(nColumn - 1);
    else
        return null;
}
	public Vector getCells()
	{
		return m_vCells;
	}
}
