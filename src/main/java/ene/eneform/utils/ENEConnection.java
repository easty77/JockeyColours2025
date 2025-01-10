/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.utils;

import java.sql.*;

/**
 *
 * @author Simon
 */
public class ENEConnection {
    
    private static String m_strDBURL = "";
    private static String m_strUsername="";
    private static String m_strPassword="";

    private ConnectionPool m_connectionPool = null;
    
    private Connection m_connection = null;
    
    public ENEConnection(ConnectionPool pool)
    {
        m_connectionPool = pool;
    }
    public ENEConnection(String strDBURL, String strUsername, String strPassword)
    {
        m_strDBURL = strDBURL;
        m_strUsername = strUsername;
        m_strPassword = strPassword;
    }
    
    public PreparedStatement getPreparedStatement(String strUpdate)
    {
        Connection connection = getConnection();
        if (connection != null)
        {
            try
            {
                return connection.prepareStatement(strUpdate);
            }
            catch(SQLException e)
            {
                System.out.println("EXCEPTION getPreparedStatement: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return null;
    }
    public Statement getStatement()
    {
        Connection connection = getConnection();
        if (connection != null)
        {
            try
            {
                return connection.createStatement();
            }
            catch(SQLException e)
            {
                System.out.println("EXCEPTION getStatement: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return null;
    }
    private Connection getConnection()
    {
        if (m_connection == null)
        {
            if (m_connectionPool != null)
            {
                m_connection = m_connectionPool.getConnection();
            }
            else
            {
                try
                {
                    m_connection = DriverManager.getConnection(m_strDBURL, m_strUsername, m_strPassword);
                }
                catch(SQLException e)
                {
                    System.out.println("EXCEPTION getConnection: " + e.getMessage());
                }
            }
        }
        
        return m_connection;
    }
   public void close()
   {
       //System.out.println("ENEConnection close");
        try
        {
            if ((m_connectionPool != null) && (m_connection != null))
                m_connectionPool.freeConnection(m_connection);
            else
            {
                    if (m_connection != null)
                        m_connection.close();
            }
        }
        catch(SQLException e)
        {
            System.out.println("EXCEPTION ENEConnection close: " + e.getMessage());
        }
     }
public boolean isLocalDataSource()
{
    if (m_connectionPool != null)
        return m_connectionPool.isLocalDataSource();
    else
        return false;
}
}
