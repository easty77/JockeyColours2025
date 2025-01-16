/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.utils;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 * @author Simon
 */
public class ConnectionPool {
private DataSource m_dataSource = null;

protected ConnectionPool(String strDefinition)
{
    try
    {
         InitialContext ctx = new InitialContext();
 
        m_dataSource = (DataSource)ctx.lookup(strDefinition); 
    }
    catch(Exception e)
    {
        System.out.println("SQL Exception in ConnectionPool constructor: " + e.getMessage());
    }
}
public boolean isLocalDataSource()
{
    return "LocalDataSource".equals(m_dataSource.getClass().getSimpleName());
}
/*
protected ConnectionPool(String strDBURL, String strUsername, String strPassword)
{
    try
    {
          m_dataSource = DataSources.unpooledDataSource(strDBURL, strUsername, strPassword);
    }
    catch(Exception e)
    {
        System.out.println("SQL Exception in ConnectionPool constructor: " + e.getMessage());
    }
} */
public Connection getConnection()
{
    try
    {
        return m_dataSource.getConnection();
    }
    catch(SQLException e)
    {
        System.out.println("SQL Exception in getConnection: " + e.getMessage());
        return null;
    }
}

public void freeConnection(Connection c)
{
    // Don't free conenctions when running in local (executable)
    if (isLocalDataSource())
        return;
    
    try
    {
        c.close();
    }
    catch(SQLException e)
    {
       System.out.println("SQL Exception in freeConnection: " + e.getMessage());
    }
}
}
