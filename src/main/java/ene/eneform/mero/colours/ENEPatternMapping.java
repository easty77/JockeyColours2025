/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.mero.colours;

import java.io.Serializable;

/**
 *
 * @author Simon
 */
public class ENEPatternMapping implements Serializable{
    private String m_strPattern;
    private String m_strMapping;

    public ENEPatternMapping(String strPattern, String strMapping)
    {
        m_strPattern = strPattern;
        m_strMapping = strMapping;
    }

    public String getMapping()
    {
        return m_strMapping;
    }
}
