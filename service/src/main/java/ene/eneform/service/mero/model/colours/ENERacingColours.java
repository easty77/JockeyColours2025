/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.mero.model.colours;

import ene.eneform.service.mero.model.ENEColoursElement;
import ene.eneform.service.mero.model.ENEFillItem;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Simon
 */
public class ENERacingColours implements Serializable{
    
    public static final int UNKNOWN = -1;
    public static final int FIRST = 0;
    public static final int JACKET = 1;
    public static final int SLEEVES = 2;
    public static final int CAP = 3;

    protected String m_strLanguage;
    protected ENEColoursElement m_jacket = null;
    protected ENEColoursElement m_sleeves = null;
    protected ENEColoursElement m_cap = null;

    protected String m_strOwner = null;
    protected String m_strDescription = null;    // original lower-case description

    protected String m_strLabel=null; // for naming generated svg file
    protected String m_strCaption=null; // text for inclusion in generated svg file
    
    private String m_strTitle=null;   // used for SVG title

    public ENERacingColours(String strLanguage, ENEColoursElement jacket, ENEColoursElement sleeves, ENEColoursElement cap)
    {
        this(strLanguage, "", "", jacket, sleeves, cap);
     }

    public ENERacingColours(String strLanguage, String strDescription, String strOwner, ENEColoursElement jacket, ENEColoursElement sleeves, ENEColoursElement cap)
    {
        m_strLanguage = strLanguage;
        m_strDescription = strDescription;
        m_strOwner = strOwner;
        m_jacket = jacket;
        m_sleeves = sleeves;
        m_cap = cap;
     }
    public String getDescription()
    {
        return m_strDescription;
    }
    public void setDescription(String strDescription)
    {
        m_strDescription = strDescription;
    }
    public String getOwner()
    {
        return m_strOwner;
    }
    public void setOwner(String strOwner)
    {
        m_strOwner = strOwner;
    }

    public void setPageColour(Color pageColour)
    {
        m_jacket.setPageColour(pageColour);
        m_sleeves.setPageColour(pageColour);
        m_cap.setPageColour(pageColour);
    }
  public ENEColoursElement getColoursElement(String strColours)
    {
        if ("Jacket".equals(strColours))
            return m_jacket;
        else if ("Sleeves".equals(strColours))
            return m_sleeves;
        else if ("Cap".equals(strColours))
            return m_cap;
        
        return null;
    }
    public ENEColoursElement getJacket()
    {
        return m_jacket;
    }
    public ENEColoursElement getSleeves()
    {
        return m_sleeves;
    }
    public ENEColoursElement getCap()
    {
        return m_cap;
    }

    public ArrayList<ENEFillItem> getColourList()
    {
        ArrayList<ENEFillItem> lstColours = new ArrayList<ENEFillItem>();
        m_jacket.getColourList(lstColours);
        m_sleeves.getColourList(lstColours);
        m_cap.getColourList(lstColours);

        return lstColours;
    }

    public void updateColour(ENEFillItem current, ENEFillItem updated)
    {
        m_jacket.updateColour(current, updated);
        m_sleeves.updateColour(current, updated);
        m_cap.updateColour(current, updated);
    }

     public String getFabric()
    {
        String strFabric = m_jacket.getFabric();
        if (strFabric == null)
            strFabric = m_sleeves.getFabric();
        if (strFabric == null)
            strFabric = m_cap.getFabric();

        return strFabric;
    }
    private String generateSVGFilename()
    {
        // All file names lower case
        return m_strDescription.replace(";", ",").replace("\"", "").replace("\\", "").replace("/", "").trim().toLowerCase();
    }
    public void setLabel(String strLabel)
    {
        m_strLabel = strLabel;
    }
    public String getLabel()
    {
        // All file names lower case
        if (m_strLabel == null)
            m_strLabel = generateSVGFilename();
        
        return m_strLabel;
    }
    public void setCaption(String strCaption)
    {
        m_strCaption = strCaption;
    }
    public String getCaption()
    {
         return m_strCaption;
    }
    public void setTitle(String strTitle)
    {
        m_strTitle = strTitle;
    }
     public String getTitle()
    {
        if (m_strTitle == null)
        {
            m_strTitle = m_strDescription.replace(" & ", " and ");
        }
        
        return m_strTitle;
    }
       public String getImage()
    {
        String strImage = m_jacket.getImage();
        if (strImage == null)
            strImage = m_sleeves.getImage();
        if (strImage == null)
            strImage = m_cap.getImage();

        return strImage;
    }
       public String getDefinition()
       {
        return getJacket().getDefinition() + "|" + getSleeves().getDefinition() + "|" + getCap().getDefinition();           
       }
    @Override public String toString()
    {
    	return m_jacket.toString() + " " + m_sleeves.toString() + " " + m_cap.toString();
    }
    public String getErrorString()
    {
        String strContent = "";
        String strJacket = m_jacket.getErrorString();
        if (!"".equals(strJacket))
            strContent += strJacket;
        String strSleeves = m_sleeves.getErrorString();
        if (!"".equals(strSleeves))
            strContent += strSleeves;
        String strCap = m_cap.getErrorString();
        if (!"".equals(strCap))
            strContent += strCap;

        return strContent;
    }
}
