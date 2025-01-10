/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.mero.config;

import ene.eneform.mero.action.ENEPatternAction;
import ene.eneform.mero.action.ENESVGAction;
import ene.eneform.mero.colours.ENEPattern;
import ene.eneform.mero.colours.ENEPatternCollection;
import ene.eneform.mero.utils.MeroUtils;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Simon
 */
public class ConfigPatterns  extends ConfigXML {
    
    // by language
    private HashMap<String, ENEConfigPatterns> m_hmLanguages= new HashMap<String, ENEConfigPatterns>();
 
    public ConfigPatterns(SAXParser parser, String strFileName)
    {
        super(parser, strFileName);
    }
    public boolean load(StandardPatternHandler standard)
    {
        setHandler(new ENEPatternsHandler(standard));
        return loadXML();
    }
    public Iterator<ENEPatternCollection> getPatternIterator(String strLanguage)
    {
         return m_hmLanguages.get(strLanguage).m_hmPatterns.values().iterator();
    }
    public ENEPatternCollection getPatternCollection(String strType, String strLanguage)
    {
        return m_hmLanguages.get(strLanguage).m_hmPatterns.get(strType);
    }
    public HashMap<String,ENEPatternAction> getStandardActions(String strType, String strLanguage)
    {
        return m_hmLanguages.get(strLanguage).m_hmStandardActions.get(strType);
    }
    public HashMap<String,String> getMeroMappings(String strType, String strLanguage)
    {
         return m_hmLanguages.get(strLanguage).m_hmMeroMappings.get(strType);
    }
    public String convertSynonym(String strType, String strSynonym, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        HashMap<String, String> hmSynonyms = m_hmLanguages.get(strLanguage).m_hmSynonyms.get(strType);
        String strPattern = hmSynonyms.get(strSynonym);
        if (strPattern == null)
            strPattern = strSynonym;
        
        return strPattern;
    }
    public boolean isPattern(String strPattern, String strLanguage)
    {
        Iterator<ENEPatternCollection> iter = getPatternIterator(strLanguage);
        while(iter.hasNext())
        {
            ENEPatternCollection collection = iter.next();
            if (collection.getPatternNameList().contains(strPattern))
                return true;
        }

        return false;
    }
    public String getPatternListRegExAll(String strLanguage)
    {
        // All Patterns - Jacket, Sleeves, Cap together
        String strPatternListRegEx = "";
        Iterator<ENEPatternCollection> iter = getPatternIterator(strLanguage);
        while(iter.hasNext())
        {
            ENEPatternCollection collection = iter.next();
            if (!"".equals(strPatternListRegEx))
                strPatternListRegEx += "|";
            strPatternListRegEx += collection.getPatternListRegEx();
         }

        return strPatternListRegEx;
    }
    public String getPatternListRegEx(String strType, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        ENEPatternCollection collection = getPatternCollection(strType, strLanguage);
        if (collection != null)
            return collection.getPatternListRegEx();
        else
            return "";
    }
    public String getPatternMapping(String strType, String strPattern, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        HashMap<String,String> collection = getMeroMappings(strType, strLanguage);
        String strMapping = null;
        if ((collection != null) && collection.containsKey(strPattern))
        {
            strMapping = collection.get(strPattern);
            if (strMapping == null)
            {
                String strStub = strPattern.substring(0, strPattern.length() - 1);
                String strLastChar = strPattern.substring(strPattern.length() - 1);
                // check whether last char is an integer
                if ("123456789".indexOf(strLastChar) >= 0)
                {
                    int nImplementation = Integer.parseInt(strLastChar);
                    for(int i = nImplementation - 1; i >= 0; i--)
                    {
                       String strPattern1 = strStub;
                        if (i > 0)
                            strPattern1 += i;
                        
                        strMapping = collection.get(strPattern1);
                        if (strMapping != null)
                            break;
                    }
                }
            }
        }
        if (strMapping == null)
            return strPattern;  // keep original value
        else
            return strMapping;
    }
    public ArrayList<ENEPattern> getPatternList(String strType, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        ENEPatternCollection collection = getPatternCollection(strType, strLanguage);
        if (collection != null)
            return collection.getPatternList();
        else
            return new ArrayList<ENEPattern>();
    }
   public ArrayList<String> getPatternNameList(String strType, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
         ENEPatternCollection collection = getPatternCollection(strType, strLanguage);
        if (collection != null)
            return collection.getPatternNameList();
        else
            return new ArrayList<String>();
    }

    public ENEPattern getDefaultPattern(String strType, String strPattern)
    {
        return getPattern(strType, strPattern, ENEColoursEnvironment.DEFAULT_LANGUAGE);
    } 
    public ENEPattern getPattern(String strType, String strPattern, String strLanguage)
    {
        return getPatternCollection(strType, strLanguage).getPattern(strPattern);
    }
    public ENEPatternAction getPatternAction(String strType, String strPattern, String strLanguage)
    {
       HashMap<String,ENEPatternAction> hmActions = getStandardActions(strType, strLanguage);
       ENEPatternAction action = hmActions.get(strPattern);
       if (action == null)
       {
                String strStub = strPattern.substring(0, strPattern.length() - 1);
                String strLastChar = strPattern.substring(strPattern.length() - 1);
                // check whether last char is an integer
                if ("123456789".indexOf(strLastChar) >= 0)
                {
                    int nImplementation = Integer.parseInt(strLastChar);
                    for(int i = nImplementation - 1; i >= 0; i--)
                    {
                        String strPattern1 = strStub;
                        if (i > 0)
                            strPattern1 += i;
                        action = hmActions.get(strPattern1);
                        if (action != null)
                            break;
                    }
                }
        }

       return action;
    }
    public boolean isDerivePattern(String strType, String strPattern, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
        ENEPattern pattern = getPattern(strType, strPattern, strLanguage);
        if ((pattern != null)&& pattern.canDerive())
            return true;
        else
            return false;
     }
    public boolean isPrimaryPattern(String strType, String strPattern, String strLanguage)
    {
        // strType is ENEJacket, ENESleeves, ENECap
       ENEPattern pattern = getPattern(strType, strPattern, strLanguage);
        if ((pattern != null)&& pattern.isPrimary())
            return true;
        else
            return false;
    }
private class ENEConfigPatterns implements Serializable
{
   private HashMap<String,ENEPatternCollection> m_hmPatterns = new HashMap<String,ENEPatternCollection>();   // keys are ENEJacket, ENESleeves, ENECap
    private HashMap<String,HashMap<String,ENEPatternAction>>  m_hmStandardActions = new HashMap<String,HashMap<String,ENEPatternAction>>();    // element is Jacket, Sleeves or Cap
    private HashMap<String,HashMap<String,String>> m_hmMeroMappings = new HashMap<String,HashMap<String,String>>();   // keys are ENEJacket, ENESleeves, ENECap
    private HashMap<String,HashMap<String,String>> m_hmSynonyms = new HashMap<String,HashMap<String,String>>();   // pattern synonyms
    
    public ENEConfigPatterns(HashMap<String,ENEPatternCollection> hmPatterns,
             HashMap<String,HashMap<String,ENEPatternAction>>  hmStandardActions,
             HashMap<String,HashMap<String,String>> hmMeroMappings,
             HashMap<String,HashMap<String,String>> hmSynonyms
             )
     {
         m_hmPatterns = hmPatterns;
         m_hmStandardActions = hmStandardActions;
         m_hmMeroMappings = hmMeroMappings;
         m_hmSynonyms = hmSynonyms;
     }
    {
        
    }
}
private class ENEPatternsHandler extends DefaultHandler implements Serializable
{
       private String m_strCurrentLanguage ="";

    private HashMap<String,ENEPatternCollection> m_hmPatterns = null;
    private HashMap<String,HashMap<String,ENEPatternAction>>  m_hmStandardActions = null;
    private HashMap<String,HashMap<String,String>> m_hmMeroMappings = null;
    private HashMap<String,HashMap<String,String>> m_hmSynonyms = null;

    // need to produce a single lookup table of patterns for parsing - including derive and primary data
    // Map for standard patterns returning ENEPatternAction
    // Map for Mero returning string mapping
    private ArrayList<ENEPattern>  m_elementPatternList = null;
    private ArrayList<String>  m_currentPatternNames = new ArrayList<String>();
    private ENEPattern m_currentPattern = null;
    private HashMap<String,ENEPatternAction>  m_hmElementStdActions = null;    
   private HashMap<String,String>  m_hmElementMeroMappings = null;
   private HashMap<String,String> m_hmCurrentSynonyms = null;
   /*
    private HashMap<String,ENEPatternAction>  m_hmCurrentStdActions = new HashMap<String,ENEPatternAction>();
    private HashMap<String,String>  m_hmCurrentMeroMappings = new HashMap<String,String>();
*/
   private String m_strCurrentElement = "";             // element is ENEJacket, ENESleeves or ENECap
  private ENEPatternAction m_currentStdAction = null;   
  private String m_strCurrentMeroMapping = "";
private StandardPatternHandler m_standardHandler;
    public ENEPatternsHandler(StandardPatternHandler standard)
    {
        m_standardHandler = standard;
     }
   @Override public void startDocument ()
    {
    }

    @Override public void endDocument ()
    {
    }

    @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
       if ("language".equals(qName))
        {
            m_strCurrentLanguage = attributes.getValue("id");
             
            m_hmPatterns = new HashMap<String,ENEPatternCollection>();
            m_hmStandardActions = new HashMap<String,HashMap<String,ENEPatternAction>>();
            m_hmMeroMappings = new HashMap<String,HashMap<String,String>>();
            m_hmSynonyms = new HashMap<String,HashMap<String,String>>();
        }
       else if ("jacket".equals(qName))
        {
            m_elementPatternList = new ArrayList<ENEPattern>();
            m_hmElementStdActions = new HashMap<String,ENEPatternAction>();     // Standard
            m_hmElementMeroMappings = new HashMap<String,String>();     // Mero
            m_hmCurrentSynonyms = new HashMap<String,String>();
            m_strCurrentElement = "ENEJacket";
         }
        else  if ("sleeves".equals(qName))
        {
            m_elementPatternList = new ArrayList<ENEPattern>();
            m_hmElementStdActions = new HashMap<String,ENEPatternAction>();     // Standard
            m_hmElementMeroMappings = new HashMap<String,String>();     // Mero
            m_hmCurrentSynonyms = new HashMap<String,String>();
            m_strCurrentElement = "ENESleeves";
        }
        else if ("cap".equals(qName))
        {
            m_elementPatternList = new ArrayList<ENEPattern>();
            m_hmElementStdActions = new HashMap<String,ENEPatternAction>();     // Standard
            m_hmElementMeroMappings = new HashMap<String,String>();     // Mero
            m_hmCurrentSynonyms = new HashMap<String,String>();
            m_strCurrentElement = "ENECap";
        }
        else if ("pattern".equals(qName))
        {
            String strId = attributes.getValue("id");
            m_currentPatternNames.add(strId);
            m_strCurrentMeroMapping = null;
            
            String strEquivalent  = attributes.getValue("equivalent");
            ENEPattern pattern = null;
            if (strEquivalent != null)
            {
                // equivalent - used by French patterns to define in terms of English patterns
                ENEPattern masterPattern = getPatternCollection(m_strCurrentElement, ENEColoursEnvironment.DEFAULT_LANGUAGE).getPattern(strEquivalent);
                //m_currentStdAction = masterPattern.getAction();
                pattern = new ENEPattern(strEquivalent, strId,  null);      // m_currentStdAction);
                if (!masterPattern.canDerive())
                    pattern.preventDerive();
                if (!masterPattern.isPrimary())
                    pattern.preventPrimary();
                m_strCurrentMeroMapping = getMeroMappings(m_strCurrentElement, ENEColoursEnvironment.DEFAULT_LANGUAGE).get(strEquivalent);
                
                m_currentPattern = pattern;
                m_elementPatternList.add(pattern);
                m_hmElementMeroMappings.put(strId, m_strCurrentMeroMapping);
                m_hmElementStdActions.put(strId, m_currentStdAction);
                
                Iterator<String> iter = masterPattern.getImplementationInterator();
                while(iter.hasNext())
                {
                    String strImplementation = iter.next();
                    m_hmElementMeroMappings.put(strId + strImplementation, getMeroMappings(m_strCurrentElement, ENEColoursEnvironment.DEFAULT_LANGUAGE).get(strEquivalent + strImplementation));
                    ENEPattern implementationPattern = getPatternCollection(m_strCurrentElement, ENEColoursEnvironment.DEFAULT_LANGUAGE).getPattern(strEquivalent + strImplementation);
                    if (implementationPattern != null)
                        m_hmElementStdActions.put(strId + strImplementation, implementationPattern.getAction()); 
                }
            }
            else
            {
                pattern = createPattern(attributes);

                String strStdClassName = attributes.getValue("standard");

                //m_currentStdAction = null;
                if ((strStdClassName != null) && !"".equals(strStdClassName))
                {
                    // standard - maps to a Java class
                    // 20160408 - only process SVGAction classes as these ambed SVGs into new designs
                    // all other standard actions are now replaced by Mero SVG
                    if (strStdClassName.indexOf("SVGAction") > 0)
                    {
                       m_currentStdAction = createSVGAction(strStdClassName, attributes);
                    }
                    else
                    {
                        m_currentStdAction = m_standardHandler.createStandardAction(strStdClassName, m_strCurrentElement);

                        // mero uses SVG Action data - so no need for specific
                        m_strCurrentMeroMapping = attributes.getValue("ene/eneform/mero");
                   }
                    if (m_currentStdAction != null)
                    pattern.setAction(m_currentStdAction);
                }
                else
                    System.out.println("No standard implementation for: " + strId);
 
                m_currentPattern = pattern;
                m_elementPatternList.add(pattern);
                if ((m_strCurrentMeroMapping != null) && !"".equals(m_strCurrentMeroMapping))
                    m_hmElementMeroMappings.put(strId, m_strCurrentMeroMapping);

                if ((m_currentStdAction != null) && !"".equals(m_currentStdAction))
                    m_hmElementStdActions.put(strId, m_currentStdAction); 
               }

          }
        else if ("synonym".equals(qName))
        {
            // apply same actions as current pattern
            // derive, primary attributes
           ENEPattern pattern = createPattern(attributes);
           m_elementPatternList.add(pattern);
           String strText = pattern.getText();
           m_hmCurrentSynonyms.put(strText, m_currentPattern.getId());
           m_currentPatternNames.add(strText);
           if (m_currentStdAction != null)
                m_hmElementStdActions.put(strText, m_currentStdAction);  
           if ((m_strCurrentMeroMapping != null) && !"".equals(m_strCurrentMeroMapping))
                m_hmElementMeroMappings.put(strText, m_strCurrentMeroMapping);
       }
        else if ("implementation".equals(qName))
        {
            // append all current patterns with implementation id, and use specified actions
            // standard, mero attributes
            // can assume implementations come after ALL synonyms
            String strId = attributes.getValue("id");
            String strStdClassName = attributes.getValue("standard");
            String strMapping = attributes.getValue("ene/eneform/mero");
            Iterator<String> iter = m_currentPatternNames.iterator();
            while(iter.hasNext())
            {
                ENEPatternAction action = null;
               if (strStdClassName.indexOf("SVGAction") > 0)
                {
                   action = createSVGAction(strStdClassName, attributes);
                }
                else
                {
                    action = m_standardHandler.createStandardAction(strStdClassName, m_strCurrentElement);
                }

                String strPattern = iter.next();
               if ((action != null) && (strStdClassName != null) && !"".equals(strStdClassName))
                    m_hmElementStdActions.put(strPattern + strId, action);  // better to store container of actions than generate each time!
                    
                if ((strMapping != null) && !"".equals(strMapping))
                    m_hmElementMeroMappings.put(strPattern + strId, strMapping);
            }
            m_currentPattern.addImplementation(strId);
        }
}

    @Override public void endElement(String uri, String localName, String qName) throws SAXException
    {
       if ("language".equals(qName))
        {
            ENEConfigPatterns ecp = new ENEConfigPatterns(m_hmPatterns, m_hmStandardActions, m_hmMeroMappings, m_hmSynonyms);
            m_hmLanguages.put(m_strCurrentLanguage, ecp);
        }
       else if ("jacket".equals(qName) || "sleeves".equals(qName) || "cap".equals(qName))
       {
            ENEPatternCollection collection = new ENEPatternCollection(m_elementPatternList);
            m_hmPatterns.put(m_strCurrentElement, collection);
            m_hmStandardActions.put(m_strCurrentElement, m_hmElementStdActions);
            m_hmMeroMappings.put(m_strCurrentElement, m_hmElementMeroMappings);
            m_hmSynonyms.put(m_strCurrentElement, m_hmCurrentSynonyms);
       }
       else if ("pattern".equals(qName))
       {
           m_currentPattern = null;
           // can now insert all synonyms and implementations
           m_currentPatternNames = new ArrayList<String>();
       }
     }

    @Override public void characters(char[] chars, int start, int length) throws SAXException
    {
        // currently no content in node
    }
    private ENEPattern createPattern(Attributes attributes)
    {
            String strId = attributes.getValue("id");
            String strDerive = attributes.getValue("derive");
            String strPrimary = attributes.getValue("primary");

            ENEPattern pattern = new ENEPattern(strId, strId);
            if ("no".equals(strDerive))
                    pattern.preventDerive();
            if ("no".equals(strPrimary))
                    pattern.preventPrimary();

            return pattern;
    }
    private ENESVGAction createSVGAction(String strStdClassName, Attributes attributes)
    {
        String strSVGName = attributes.getValue("svg");
        
        ENESVGAction action = null;
        String strFullClassName = "ene.eneform.mero.action.";
        String[] astrClassNames = strStdClassName.split("\\.");
        strFullClassName += astrClassNames[0];
        if (astrClassNames.length > 1)  
            strFullClassName += ("$" + astrClassNames[1]);

        try
        {
            Class fc = Class.forName(strFullClassName);
            Constructor c = fc.getConstructor(new Class[] {String.class} );
            action = (ENESVGAction) c.newInstance(new Object[] {strSVGName} );
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        
        String strDimensions = attributes.getValue("dimensions");
        Rectangle rectangle = MeroUtils.getViewBoxRectangle(strDimensions);
        if (rectangle != null)
            action.setDimensions(ENESVGAction.STANDARD_TEMPLATE_TYPE, rectangle); 

        String strMeroDimensions = attributes.getValue("ene/eneform/mero");
        rectangle = MeroUtils.getViewBoxRectangle(strMeroDimensions);
        if (rectangle != null)
            action.setDimensions(ENESVGAction.MERO_TEMPLATE_TYPE, rectangle); 
        
        return action;
    }
     }
}
