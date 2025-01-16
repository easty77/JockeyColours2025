/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.tartan;

import java.io.Serializable;

/**
 *
 * @author Simon
 */
public class ENETartanColour implements Serializable{
    private String m_strColourCode;

     public ENETartanColour(String strColourCode, int nStart, int nWidth)
    {
        m_strColourCode = strColourCode;
        m_nStart = nStart;
        m_nWidth = nWidth;
    }

    public String getstrColourCode() {
        return m_strColourCode;
    }

    public int getWidth() {
        return m_nWidth;
    }

    public int getStart() {
        return m_nStart;
    }
    int m_nWidth;
    int m_nStart;
}
