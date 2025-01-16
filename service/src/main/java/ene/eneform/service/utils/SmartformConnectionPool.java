/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.utils;

/**
 *
 * @author Simon
 */
public class SmartformConnectionPool extends ConnectionPool{
    
private static SmartformConnectionPool sm_pool = null;
private SmartformConnectionPool()
{
    super("java:comp/env/jdbc/smartform");     // java:comp/env/jdbc/pooledDS
}
public static SmartformConnectionPool getInstance()
{
    if (sm_pool == null)
    {
        sm_pool = new SmartformConnectionPool();
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
