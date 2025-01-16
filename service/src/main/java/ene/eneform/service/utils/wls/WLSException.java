// ********************************************************************
// *
// * Copyright IBM Corporation 2001, 2004
// * 
// * Web Lecture Services
// * 
// ********************************************************************
package ene.eneform.service.utils.wls;

/**
 * Insert the type's description here.
 * Creation date: (12/7/2000 2:28:31 PM)
 * @author: Administrator
 */
public class WLSException extends Exception 
{
	public WLSException(Exception e, String strStatement)
	{
		this(e.getMessage() + " (" + strStatement + ")");
	}
/**
 * SqlException constructor comment.
 * @param s java.lang.String
 */
public WLSException(String s) 
{
	super(s);
}
}
