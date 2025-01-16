/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.smartform.bos;

import ene.eneform.service.smartform.utils.CourseTimeList;

import java.util.ArrayList;

/**
 *
 * @author Simon
 */
public class SmartformRacecardDefinition {
    
   protected int m_nDateDiff;
   protected ArrayList<CourseTimeList> m_alCTL;
   protected String m_strType;  // tv, course, day
   protected String m_strTitle = "";
   public SmartformRacecardDefinition(String strType, int nDateDiff, ArrayList<CourseTimeList> alCTL)
   {
       m_strType = strType;
        m_alCTL = alCTL;
        m_nDateDiff = nDateDiff;    // difference from current date
   }
   public ArrayList<CourseTimeList> getCourseTimeList()
   {
       return m_alCTL;
   }
   public int getDayDiff()
   {
        return m_nDateDiff;
   }
  public String getType()
   {
        return m_strType;
   }
  public String getTitle()
   {
        return m_strTitle;
   }
  public void setTitle(String strTitle)
   {
        m_strTitle = strTitle;
   }
}
