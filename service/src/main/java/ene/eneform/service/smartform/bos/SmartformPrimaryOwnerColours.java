/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.smartform.bos;

/**
 *
 * @author Simon
 */
public class SmartformPrimaryOwnerColours {

    private String m_strOwnerName;
    private String m_strColours="";
    private String m_strJacketSyntax="";
    private String m_strSleevesSyntax="";
    private String m_strCapSyntax="";

    public SmartformPrimaryOwnerColours(String strOwnerName)
    {
        m_strOwnerName = strOwnerName;
    }
    public String getOwnerName() {
        return m_strOwnerName;
    }

    public String getColours() {
        return m_strColours;
    }

    public void setColours(String strColours) {
        this.m_strColours = strColours;
    }

    public String getJacketSyntax() {
        return m_strJacketSyntax;
    }

    public void setJacketSyntax(String strJacketSyntax) {
        this.m_strJacketSyntax = strJacketSyntax;
    }

    public String getSleevesSyntax() {
        return m_strSleevesSyntax;
    }

    public void setSleevesSyntax(String strSleevesSyntax) {
        this.m_strSleevesSyntax = strSleevesSyntax;
    }

    public String getCapSyntax() {
        return m_strCapSyntax;
    }

    public void setCapSyntax(String strCapSyntax) {
        this.m_strCapSyntax = strCapSyntax;
    }
    
    
}
