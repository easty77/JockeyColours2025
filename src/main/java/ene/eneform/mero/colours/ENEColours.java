package ene.eneform.mero.colours;

import ene.eneform.mero.config.ENEColoursEnvironment;
import ene.eneform.mero.config.ENEOrganisation;
import ene.eneform.mero.config.ENEOrganisationList;
import ene.eneform.mero.utils.ENEColourItem;

import java.awt.Color;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Set;

public class ENEColours implements Serializable{
    
    private static String sm_strColourListRegEx = null;
    
    private static void addColour(String strColour, ENEColourItem colour)
    {
    	//sm_Colours.put(strColour, colour);

    /*	if (!"".equals(sm_strColourListRegEx))
    		sm_strColourListRegEx += "|";
    	sm_strColourListRegEx += strColour; */
}
    public static ENEColourItem getColourItem(String strColour, String strLanguage)
    {
        if (strColour == null)
            return null;
        else
        {
            return ENEColoursEnvironment.getInstance().getColourItem(strColour, strLanguage);
        }
    }

    public static Color convertColour(String strColour, String strLanguage)
    {
        ENEColourItem item = getColourItem(strColour, strLanguage);

        if (item == null)
            return null;
        else
            return item.getColour();
    }
    
    public static Set<String> getColourNames(String strLanguage)
    {
        return ENEColoursEnvironment.getInstance().getColours(strLanguage);
    }
    public static ArrayList<String> getOrganisationColourNames(String strOrganisation)
    {
        ENEOrganisation organisation = ENEColoursEnvironment.getInstance().getOrganisation(strOrganisation);
        ENEOrganisationList list = organisation.getList("colours");
        return list.getList();
        /* HashSet<String> set = new HashSet<String>();
        Collection<ENEColourItem> colours = sm_Colours.values();
        Iterator<ENEColourItem> iter = colours.iterator();
        while(iter.hasNext())
        {
            ENEColourItem item = iter.next();
            if (item.isOrganisation(strOrganisation))
                set.add(item.getText());
        }

        return set; */
    }

    public static boolean isColour(String strColour, String strLanguage)
    {
        return ((ENEColoursEnvironment.getInstance().getColourItem(strColour, strLanguage) != null) || false); // ENETartan.isTartan(strColour));
    }

    // public static String getColourListRegEx(){return sm_strColourListRegEx;}

}
