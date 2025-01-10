/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.utils;

/**
 *
 * @author Simon
 */
public class FPLConnectionPool extends ConnectionPool{
    
private static FPLConnectionPool sm_pool = null;
private FPLConnectionPool()
{
    super("java:comp/env/jdbc/fpl");     // java:comp/env/jdbc/pooledDS
}
public static FPLConnectionPool getInstance()
{
    if (sm_pool == null)
    {
        sm_pool = new FPLConnectionPool();
    }
    return sm_pool;
}

public ENEConnection getENEConnection()
{
    return new ENEConnection(this);
}
public ENEStatement getENEStatement(boolean bFreeConnection)
{
    return new ENEStatement(new ENEConnection(this), bFreeConnection);
} 
public ENEStatement getENEStatement()
{
    // 20181207 If running with LocalDataSource (exe not web) - the ConnectionPool will not r�free connections, so always safe to use true here
    return new ENEStatement(new ENEConnection(this), true);    
} 
}
