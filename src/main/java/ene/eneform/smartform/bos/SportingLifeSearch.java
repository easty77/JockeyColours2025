/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.smartform.bos;

/**
 *
 * @author Simon
 */
public class SportingLifeSearch {
    private int m_nMinYear;
    private int m_nMaxYear;
    private int m_nCourseId = -1;
    private String m_strSearch;
    private String m_strCourse;

   
    public SportingLifeSearch(int nCourseId, String strCourse, String strSearch, int nMinYear, int nMaxYear)
    {
        m_nCourseId = nCourseId;
        m_strSearch = strSearch;
        m_nMinYear = nMinYear;
        m_nMaxYear = nMaxYear;
        m_strCourse = strCourse;
    }
 public SportingLifeSearch(String strCourse, String strSearch, int nMinYear, int nMaxYear)
    {
        m_strSearch = strSearch;
        m_nMinYear = nMinYear;
        m_nMaxYear = nMaxYear;
        m_strCourse = strCourse;
    }

    public int getMinYear() {
        return m_nMinYear;
    }

    public int getMaxYear() {
        return m_nMaxYear;
    }

    public int getCourseId() {
        return m_nCourseId;
    }

    public String getSearch() {
        return m_strSearch;
    }
    public String getCourse() {
        return m_strCourse;
    }

}
