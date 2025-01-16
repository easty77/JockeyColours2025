/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.smartform.utils;

/**
 *
 * @author Simon
 */
public class CourseTimeList {
    protected String m_strCourse;
     protected String[] m_arTimes;
    
    public CourseTimeList(String strCourse, String[] arTimes)
    {
        m_strCourse = strCourse;
        m_arTimes = arTimes;
    }
    public String getCourse()
    {
        return m_strCourse;
    }
    public String[] getTimes()
    {
        return m_arTimes;
    }
}
