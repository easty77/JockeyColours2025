/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Simon
 */
public class ENEStatement {
 
    protected static boolean sm_bTrace = true;
    protected ENEConnection m_connection = null;
    protected Statement m_statement  = null;
    protected boolean m_bFreeConnection;
    
    public ENEStatement(ENEConnection connection)
    {
        this(connection, false);
    }
   public ENEStatement(ENEConnection connection, boolean bFreeConnection)
    {
        m_connection = connection;
        m_bFreeConnection = bFreeConnection;
    }
    
    private Statement getStatement()
    {
        // to do: make private
        if (m_statement == null)
        {
            if (m_connection != null)
                m_statement = m_connection.getStatement();
        }
        return m_statement; 
    }
    public PreparedStatement getPreparedStatement(String strUpdate)
    {
        return m_connection.getPreparedStatement(strUpdate);
    }
    public ResultSet executeQuery(String strQuery)
    {
        Statement statement = getStatement();
        if (statement != null)
        {
            try
            {
                if (sm_bTrace)
                    System.out.println("executeQuery: " + strQuery);
                return statement.executeQuery(strQuery);
            }
            catch(SQLException e)
            {
                System.out.println("EXCEPTION executeQuery: " + e.getMessage() + "-" + strQuery);
                try
                {
                    statement.close();
                }
                catch(SQLException e1)
                {
                    System.out.println("Exception closing statement");
                }
                finally
                {
                    m_statement = null;                        
                }
            }
         }

         return null;
    }
    public int executeUpdate(String strUpdate)
    {
        Statement statement = getStatement();
        if (statement != null)
        {
            try
            {
                if (sm_bTrace)
                    System.out.println("executeUpdate: " + strUpdate);
                return statement.executeUpdate(strUpdate);
            }
            catch(SQLException e)
            {
                System.out.println("EXCEPTION executeUpdate: " + e.getMessage() + "-" + strUpdate);
                return -1;
            }
        }
        else
            return 0;
    }
    public void close()
    {
        if (m_connection != null && m_connection.isLocalDataSource())
            return;
        
        if (m_statement != null)
        {
            try
            {
                m_statement.close();
                //System.out.println("ENEStatement close - close statement");
            }
            catch(SQLException e)
            {
                 System.out.println("EXCEPTION ENEStatement close: " + e.getMessage());
            }
            m_statement = null;
        }
        
        if (m_bFreeConnection && (m_connection != null))
        {
            //System.out.println("ENEStatement close - close connection");
            m_connection.close();
        }
    }
}
