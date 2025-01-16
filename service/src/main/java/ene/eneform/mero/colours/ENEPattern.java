package ene.eneform.mero.colours;

import ene.eneform.mero.action.ENEPatternAction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class ENEPattern implements Serializable{

	private String m_strId; // this defines the pattern
	private String m_strText;   // this may vary based on language
	private ENEPatternAction m_action = null;

        private boolean m_bDerive = true;   // can the same pattern from jacket -> sleeves etc
        private boolean m_bPrimary = true;   // can this pattern be a primary pattern

        private String m_astrForegroundColours[];
        private String m_strBackgroundColour = "";
        private ArrayList<String> m_alImplementations = new ArrayList<String>();

	public ENEPattern(String strId, String strText, ENEPatternAction action)
	{
                m_strId = strId;
		m_strText = strText;
		m_action = action;
	}
	
	public ENEPattern(String strId, String strText)
	{
            m_strId = strId;
		m_strText = strText;
	}

        public String getId()
	{
		return m_strId;
	}
        public String getText()
	{
		return m_strText;
	}
	public ENEPatternAction getAction()
	{
		return m_action;
	}
	public void setAction(ENEPatternAction action)
	{
		m_action = action;
	}
	public boolean isSymmetric()
	{
            // is the distribution of the 2 colours equal e.g red and white checked
            if (m_action != null)
		return m_action.isSymmetric();
            else
                return false;    // plain
	}
	public boolean canDerive()
	{
		return m_bDerive;
	}
	public boolean isPrimary()
	{
		return m_bPrimary;
	}
	public void preventDerive()
	{
            m_bDerive = false;
	}
	public void preventPrimary()
	{
            m_bPrimary = false;
	}
        public String[] getForegroundColours()
        {
            return m_astrForegroundColours;
        }
        public String getBackground()
        {
            return m_strBackgroundColour;
        }
        public void setForeground(String[] astrForeground)
        {
            m_astrForegroundColours = astrForeground;
        }
        public void setBackground(String strBackground)
        {
            m_strBackgroundColour = strBackground;
        }
        public void addImplementation(String strImplementation)
        {
            m_alImplementations.add(strImplementation);
        }
        public Iterator<String> getImplementationInterator()
        {
            return m_alImplementations.iterator();
        }
 }
