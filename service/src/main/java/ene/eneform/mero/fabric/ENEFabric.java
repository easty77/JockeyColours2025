/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.mero.fabric;

import ene.eneform.mero.fabric.ENEFabricItem;

import java.io.Serializable;

/**
 *
 * @author Simon
 */
public class ENEFabric implements Serializable{

    private String m_strId;
    private String m_strClassName;
    private String m_strData;

    public ENEFabric(String strId, String strClassName, String strData)
    {
        // TO DO: assumes class is in tartan package, whereas fabric is better
        m_strId = strId;
        m_strClassName = "ene.mero.tartan." + strClassName;
        m_strData = strData;
    }

    public String getId()
    {
        return m_strId;
    }
    
    public ENEFabricItem getFabricItem()
    {
            try
            {
                Class fc = Class.forName(m_strClassName);
                ENEFabricItem fabric = (ENEFabricItem) fc.newInstance();
                fabric.setName(m_strId);
                fabric.setData(m_strData);

                return fabric;
             }
            catch(ClassNotFoundException e)
            {
                System.out.println("ClassNotFoundException: " + m_strClassName);
                e.printStackTrace();
            }
            catch(InstantiationException e)
            {
                 System.out.println("InstantiationException: " + m_strClassName);
                e.printStackTrace();
            }
            catch(IllegalAccessException e)
            {
                System.out.println("IllegalAccessException: " + m_strClassName);
            }

            return null;
    }
}
