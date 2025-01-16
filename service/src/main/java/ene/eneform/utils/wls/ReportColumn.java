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
 * Creation date: (13/06/2001 10:37:10)
 * @author: Administrator
 */

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ReportColumn
{
	private int m_nColumn;
	private String m_strCode;
	private String m_strTitle;
	private String m_strType;
	private int m_nPercent = -1;
	private boolean m_bPercentTotal = true;	// is the percentage with regard to the TOTAL of the coluimn specified by m_nPercent?
	private double m_dCount = -1;
	private boolean m_bTranslated = false;	// if it is a bundle field, has it already been translated?
	private boolean m_bSort = false;
	private int m_nSubtotal = -1;
	private boolean m_bSortAscend = true;	// true = ASC, false = DESC (only applies if m_bSort = true)
	private boolean m_bHidden = false;
	private static final String SUBTOTAL_COUNT_COLUMN = "SUBTOTAL_COUNT";
	// LI-2494
	protected boolean m_bEncrypted = false;
    protected String m_strDBType = "no";	// does current database contain native UTF-8 strings ?

/**
 * ReportColumn constructor comment.
 */
public ReportColumn(int nColumn, 
					String strCode, 
					String strType, 
					String strTitle, 
					boolean bHidden,
					boolean bEncrypted,
					String strDBType ) 
{
	m_nColumn = nColumn;
	m_strCode = strCode;
	m_strType = strType;
	m_strTitle = strTitle;
	// several fields may map onto the same bundle
	// this mapping is defined in resourcelist_en.properties
	m_bHidden = bHidden;
	m_bEncrypted = bEncrypted;
	m_strDBType = strDBType;
}

	public String getCode()
	{
		return m_strCode;
	}
	public double getCount()
	{
		return m_dCount;
	}
	public int getNumber()
	{
		return m_nColumn;
	}
/** Returns the number of the field of which this is a percent 
Returns 0 if not dependent on another field
Returns -1 if not a percentage field */
public int getPercent()
{
	return m_nPercent;
}
	public int getSort()
	{
		// returns 0 if not sorted, 1 if sorted ASC, -1 if sorted DESC
		if (m_bSort)
		{
			if (m_bSortAscend)
				return 1;
			else
				return -1;
		}
		else
			return 0;	
	}
	public int getSubtotal()
	{
		return m_nSubtotal;
	}
	public String getTitle()
	{
		return m_strTitle;
	}
	public String getType()
	{
		return m_strType;
	}
	public double incrementCount(double dIncrement)
	{
		return (m_dCount += dIncrement);
		
	}
	public boolean isColumnPercent()
	{
		// percentage calculated from division by another column
		return m_nPercent > 0;
	}
	public boolean isCount()
	{
		return (m_dCount >= 0);
	}
	public boolean isDate()
	{
		return "DATE".equalsIgnoreCase(m_strType);
	}
	public boolean isHidden()
	{
		return m_bHidden;
	}
	public boolean isNumeric()
	{
		return ("SMALLINT".equalsIgnoreCase(m_strType) || "INTEGER".equalsIgnoreCase(m_strType)
		|| "BIGINT".equalsIgnoreCase(m_strType)
		|| "FLOAT".equalsIgnoreCase(m_strType) || "DOUBLE".equalsIgnoreCase(m_strType)
			|| "DECIMAL".equalsIgnoreCase(m_strType));
	}
	public boolean isBinary()
	{
		return (m_strType.indexOf("BINARY") >= 0);
	}
	public boolean isBitData()
	{
		return (m_strType.indexOf("FOR BIT DATA") >= 0);
	}
	public boolean isPercent()
	{
		// to be displayed as a percentage
		// if nPercent = 0, the percentage is not dependent on another column
		return m_nPercent >= 0;
	}
	public boolean isPercentOfTotal()
	{
		return m_bPercentTotal;
	}
	public boolean isSubtotal()
	{
		return (m_nSubtotal >= 0);
	}
public boolean isSubtotalCount()
{
    return (m_strCode.endsWith(SUBTOTAL_COUNT_COLUMN));
}
	public boolean isTime()
	{
		return "TIME".equalsIgnoreCase(m_strType);
	}
	public boolean isTimestamp()
	{
		return "TIMESTAMP".equalsIgnoreCase(m_strType);
	}
	public boolean isTranslated()
	{
		return m_bTranslated;
	}
	public void setCount()
	{
		m_dCount = 0;
	}
	public void setSort(boolean bSortAscend)
	{
		m_bSort = true;
		m_bSortAscend = bSortAscend;
	}
	public void setSubtotal(int nSubtotal)
	{
		m_nSubtotal = nSubtotal;
	}
	public void setTitle(String strTitle)
	{
		m_strTitle = strTitle;
	}
	public void setTranslated()
	{
		m_bTranslated = true;
	}
	public Object createNumericObject(double dValue)
    {
    	if ("FLOAT".equalsIgnoreCase(m_strType)
    		|| "DOUBLE".equalsIgnoreCase(m_strType))
    	{
    		return dValue;
    	}
		else if ("DECIMAL".equalsIgnoreCase(m_strType))
			return new BigDecimal(dValue);
		else
    		return Math.round(dValue);
    }
public double getNumericObjectasDouble(Object obj)
{
	double dValue = 0;	// returned if obj is null
	if (obj != null)
	{
		if ("DOUBLE".equalsIgnoreCase(m_strType)
		|| "FLOAT".equalsIgnoreCase(m_strType))
		{
			dValue = ((Double) obj).doubleValue();
		}
		else if ("DECIMAL".equalsIgnoreCase(m_strType))
		{
			dValue = ((BigDecimal) obj).doubleValue();
		}
		else if ("BIGINT".equalsIgnoreCase(m_strType))
		{
			dValue = ((Long) obj).doubleValue();
		}
		else
		{
			dValue = ((Integer) obj).doubleValue();
		}
	}
	
	return dValue;
}
public int getNumericObjectasInt(Object obj)
{
	int nValue = 0;		// returned if obj is null
	if (obj != null)
	{
		try
		{
			if ("DOUBLE".equalsIgnoreCase(m_strType)
				|| "FLOAT".equalsIgnoreCase(m_strType))
			{
				nValue = ((Double) obj).intValue();
			}
			else if ("DECIMAL".equalsIgnoreCase(m_strType))
			{
				nValue = ((BigDecimal) obj).intValue();
			}
			else if ("BIGINT".equalsIgnoreCase(m_strType))
			{
				nValue = ((Long) obj).intValue();
			}
			else
			{
				nValue = ((Integer) obj).intValue();
			}
		}
		catch(ClassCastException e)
		{
			// Carry on and try to parse as Integer
			// Is BIGINT Long or Integer???
			nValue = ((Integer) obj).intValue();
		}
	}	
		
	return nValue;
}

public void setPercent(int nPercentColumn, boolean bPercentTotal)
{
	m_nPercent = nPercentColumn;
	m_bPercentTotal = bPercentTotal;
}
public String getStringValue(Object obj, SimpleDateFormat fmtDate, String strLanguage)
{
	String strCellValue = null;
	String strType = getType();
	// Grand Total row may have "Total" in a numeric column e.g. Months
	// thus anticipate a ClassCast exception
	try
	{
		// percentage is handled elsewhere
		if (isNumeric() && !isTranslated())
		{
			if (obj != null)
			{
				if ("INTEGER".equalsIgnoreCase(strType)
						|| "BIGINT".equalsIgnoreCase(strType)
						|| "SMALLINT".equalsIgnoreCase(strType))
				{
					int nCellValue = getNumericObjectasInt(obj);
					// answers in survey are stored as integers but translated into bundles
					strCellValue = String.valueOf(nCellValue);
				}
				else
				{
					double dCellValue = getNumericObjectasDouble(obj);
					if ("DECIMAL".equalsIgnoreCase(strType))
					{
						// getNumericObjectasInt returns 100 x decimal value
						// 2 decimal places
						//double dDecimal = nCellValue/100.0;
						DecimalFormat formatter = new DecimalFormat();
						formatter.setMinimumFractionDigits(2);
						strCellValue = formatter.format(dCellValue);
					}
					else
					{
						strCellValue = String.valueOf(dCellValue);
					}
				}
			}
			else
				strCellValue = "-";
		}
		else if (isDate())
		{
			Date dt = (Date) obj;
			if (dt == null)
				strCellValue = "-";
			else
				strCellValue = fmtDate.format(dt);
		}
		else	// text or bundle
		{
			if (obj != null)	// SE 23/4/03 ... but toString errors on null, whereas cast doesn't
			{
				strCellValue = obj.toString();	// SE 3/3/03 avoid ClassCastException
				
				// LI-2494
				if ( m_bEncrypted )
				{
					strCellValue = WLSEncrypt.AES_WLS_decrypt( strCellValue, 
											( "yes".equalsIgnoreCase( m_strDBType ) ? "UTF-8" : "ISO-8859-1" ), 
											null);
				}
			}
			else
				strCellValue = "-";
		}
	}
	catch(ClassCastException e)
	{
		strCellValue = obj.toString();
	}
	catch(Exception e)
	{
		strCellValue = obj.toString();
	}

	return strCellValue;
}
}
