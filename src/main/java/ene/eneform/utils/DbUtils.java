/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author Simon
 */
public class DbUtils {

public static String getDBString(ResultSet rs, String strField)
{
    String strValue;
    try
    {
        strValue = rs.getString(strField);
        if (rs.wasNull())
            strValue="";
    }
    catch(SQLException e)
    {
        // Invalid column name?
        strValue = null;
    }

    return strValue;
}
public static char getDBChar(ResultSet rs, String strField)
{
    try
    {
        String strValue = rs.getString(strField);
        if (rs.wasNull())
            strValue="";
        if (strValue.length() > 0)
            return strValue.charAt(0);
        else
            return ' ';
    }
    catch(SQLException e)
    {
        // Invalid column name?
        return ' ';
    }
}

public static int writeResultSet2csv(FileWriter fwriter, ResultSet rs) throws SQLException, IOException
{
    if (rs == null)
        return 0;
    
    int nCount = 0;
    ResultSetMetaData meta = rs.getMetaData();
    StringBuilder sb = new StringBuilder();
    while(rs.next())
    {
        int nColumns = meta.getColumnCount();
        for(int i = 1; i <= nColumns; i++)
        {
            int nType = meta.getColumnType(i);
            if ((nType == java.sql.Types.INTEGER) || (nType == java.sql.Types.SMALLINT) || (nType == java.sql.Types.TINYINT)|| (nType == java.sql.Types.BIGINT))
                sb.append(rs.getInt(i));
            else if ((nType == java.sql.Types.CHAR) || (nType == java.sql.Types.VARCHAR))
                sb.append(rs.getString(i));
            else if ((nType == java.sql.Types.DECIMAL) || (nType == java.sql.Types.FLOAT) || (nType == java.sql.Types.DOUBLE) || (nType == java.sql.Types.REAL))
                sb.append(rs.getDouble(i));
            else if (nType == java.sql.Types.DATE)
                sb.append(rs.getDate(i));
            else
                System.out.println("Unknown type: " + nType);
            
            if (i < nColumns)
                sb.append(",");
            else
                sb.append(" \r\n");
 
       }
        nCount++;

        if (nCount % 100 == 0)
        {
            fwriter.append(sb);
            sb = new StringBuilder();
        }
    }

    fwriter.append(sb);
    
    rs.close();
    
    return nCount;
}
}
