/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.mero.config;

import java.io.Serializable;


/**
 *
 * @author Simon
 */
public class ENEOrganisation implements Serializable{

    private String m_strOrganisation;
    private ENEOrganisationList m_colours = new ENEOrganisationList();
    private ENEOrganisationList m_jacketPatterns = new ENEOrganisationList();
    private ENEOrganisationList m_sleevePatterns = new ENEOrganisationList();
    private ENEOrganisationList m_capPatterns = new ENEOrganisationList();

    public ENEOrganisation(String strOrganisation)
    {
        m_strOrganisation = strOrganisation;
    }
    
    public ENEOrganisationList getList(String strList)
    {
        if ("colours".equals(strList))
            return m_colours;
        else if("jacket".equals(strList))
            return m_jacketPatterns;
        else if("sleeves".equals(strList))
            return m_sleevePatterns;
        else if("cap".equals(strList))
            return m_capPatterns;

        return null;
    }
}
