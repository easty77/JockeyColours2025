/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.smartform.bos;

import ene.eneform.utils.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
/**
 *
 * @author Simon
 */
public class SmartformTack {

    private int m_nRace = 0;
    private int m_nRunner = 0;
    private ArrayList<String> m_lstTack = null;
    private String m_strTack = "";

    private static String TACK_HOOD = "Hood";
    private static String TACK_VISOR = "Visor";
    private static String TACK_BLINKERS = "Blinkers";
    private static String TACK_EYE_SHIELD = "Eye Shield";
    private static String TACK_EYE_COVER = "Eye Cover";
    private static String TACK_CHEEK_PIECES = "Cheek Pieces";
    private static String TACK_PACIFIERS = "Pacifiers";
    private static String TACK_TONGUE_STRAP = "Tongue Strap";

    private static String SHORT_TACK_HOOD = "h";
    private static String SHORT_TACK_VISOR = "v";
    private static String SHORT_TACK_BLINKERS = "b";
    private static String SHORT_TACK_EYE_SHIELD = "e";
    private static String SHORT_TACK_EYE_COVER = "c";
    private static String SHORT_TACK_CHEEK_PIECES = "p";
    private static String SHORT_TACK_PACIFIERS = "x";       // no examples, what is the code?
    private static String SHORT_TACK_TONGUE_STRAP = "t";
       // tack_hood, tack_visor, tack_blinkers, tack_eye_shield, tack_eye_cover, tack_cheek_piece, tack_pacifiers, tack_tongue_strap

/*    public SmartformTack(int nRace, int nRunner)
    {
        m_nRace = nRace;
        m_nRunner = nRunner;
    } */
    public SmartformTack()
    {
    }
    public SmartformTack(String strTack)
    {
        // from Racing Post or SportingLife results - no runner id
          if (strTack.indexOf(SHORT_TACK_HOOD) >= 0)
            setHood(1);
        if (strTack.indexOf(SHORT_TACK_VISOR) >= 0)
            setVisor(1);
        if (strTack.indexOf(SHORT_TACK_BLINKERS) >= 0)
            setBlinkers(1);
        if (strTack.indexOf(SHORT_TACK_EYE_SHIELD) >= 0)      // e/s Racing Post
            setEyeShield(1);
        if (strTack.indexOf(SHORT_TACK_EYE_COVER) >= 0)
            setEyeCover(1);
        if (strTack.indexOf(SHORT_TACK_CHEEK_PIECES) >= 0)
            setCheekPieces(1);
        if (strTack.indexOf(SHORT_TACK_TONGUE_STRAP) >= 0)
            setTongueStrap(1);
        if (strTack.indexOf(SHORT_TACK_PACIFIERS) >= 0)
            setPacifiers(1);
    }
 
/*    public int getRunnerId()
    {
        return m_nRunner;
    }
    public int getRaceId()
    {
        return m_nRace;
    }
*/
    public void setHood(int nTack)
    {
        if (nTack > 0)
        {
            addTack(TACK_HOOD);
            m_strTack += SHORT_TACK_HOOD;
        }
    }
    public void setVisor(int nTack)
    {
        if (nTack > 0)
        {
            addTack(TACK_VISOR);
            m_strTack += SHORT_TACK_VISOR;
        }
    }
    public void setBlinkers(int nTack)
    {
        if (nTack > 0)
        {
            addTack(TACK_BLINKERS);
            m_strTack += SHORT_TACK_BLINKERS;
        }
    }
    public void setEyeShield(int nTack)
    {
        if (nTack > 0)
        {
           addTack(TACK_EYE_SHIELD);
            m_strTack += SHORT_TACK_EYE_SHIELD;
        }
    }
    public void setEyeCover(int nTack)
    {
        if (nTack > 0)
        {
            addTack(TACK_EYE_COVER);
            m_strTack += SHORT_TACK_EYE_COVER;
        }
    }
    public void setCheekPieces(int nTack)
    {
        if (nTack > 0)
        {
            addTack(TACK_CHEEK_PIECES);
            m_strTack += SHORT_TACK_CHEEK_PIECES;
        }
    }
    public void setPacifiers(int nTack)
    {
        if (nTack > 0)
        {
            addTack(TACK_PACIFIERS);
            m_strTack += SHORT_TACK_PACIFIERS;
        }
    }
    public void setTongueStrap(int nTack)
    {
        if (nTack > 0)
        {
            addTack(TACK_TONGUE_STRAP);
            m_strTack += SHORT_TACK_TONGUE_STRAP;
        }
    }

    public boolean hasHood()
    {
        return hasTack(TACK_HOOD);
    }
    public boolean hasVisor()
    {
        return hasTack(TACK_VISOR);
    }
    public boolean hasBlinkers()
    {
        return hasTack(TACK_BLINKERS);
    }
    public boolean hasEyeShield()
    {
        return hasTack(TACK_EYE_SHIELD);
    }
    public boolean hasEyeCover()
    {
        return hasTack(TACK_EYE_COVER);
    }
    public boolean hasCheekPieces()
    {
        return hasTack(TACK_CHEEK_PIECES);
    }
    public boolean hasPacifiers()
    {
        return hasTack(TACK_PACIFIERS);
    }
    public boolean hasTongueStrap()
    {
        return hasTack(TACK_TONGUE_STRAP);
    }
    public String getShortTackString()
    {
        return m_strTack;
    }
    public String getTackString()
    {
        if (m_lstTack == null)
            return "";
        else
            return StringUtils.join(m_lstTack, ",");
    }
    public ArrayList<String> getTackList()
    {
        if (m_lstTack == null)
            return new ArrayList<String>();
        else
            return m_lstTack;
    }
    public boolean hasTack()
    {
        return (m_lstTack != null);
    }
    private void addTack(String strTack)
    {
        if (m_lstTack == null)
            m_lstTack =new ArrayList<String>();

        m_lstTack.add(strTack);
    }

    private boolean hasTack(String strTack)
    {
        if (m_lstTack == null)
            return false;

        Iterator<String> iter = m_lstTack.iterator();
        while (iter.hasNext())
        {
            String strItem = iter.next();
            if (strItem.equals(strTack))
                return true;
        }

        return false;
    }
}
