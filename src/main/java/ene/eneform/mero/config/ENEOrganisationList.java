/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.mero.config;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Simon
 */
public class ENEOrganisationList implements Serializable{

    ArrayList<String> m_alItems;

    public ENEOrganisationList()
    {
       m_alItems = new ArrayList<String>();
    }
    public void addItem(String strItem)
    {
        m_alItems.add(strItem);
    }
    public ArrayList<String> getList()
    {
        return m_alItems;
    }
}
