/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.service.smartform.bos;

import java.util.ArrayList;

/**
 *
 * @author Simon
 */
public class SmartformAncestor extends SmartformHorseDefinition
{
    private double m_dGSV = -1.0;
   
    private int m_nStarts = -1;
    private int m_nWins = -1;
    private int m_nPlaces = -1;
    private int m_nShows = -1;
    
    // ConduitMareProfile
    protected int m_nBrilliance = 0;
    protected int m_nIntermediate = 0;
    protected int m_nClassic = 0;
    protected int m_nSolid = 0;
    protected int m_nProfessional = 0;

    private String m_strCareerEarnings;

    private ArrayList<SmartformAncestorGeneration> m_alGenerations = new ArrayList<SmartformAncestorGeneration>();
    
    public SmartformAncestor(String strName, int nYearBorn, String strBred)
    {
        super(strName, nYearBorn, strBred);
        for (int i= 0; i < 5; i++)
        {
            m_alGenerations.add(new SmartformAncestorGeneration(strName, nYearBorn, strBred, i));
        }
    }
    public SmartformAncestorGeneration getGeneration(int nGeneration)
    {
        return m_alGenerations.get(nGeneration);
    }

     public int getStarts() {
        return m_nStarts;
    }

    public void setStarts(int nStarts) {
        this.m_nStarts = nStarts;
    }

    public int getWins() {
        return m_nWins;
    }

    public void setWins(int nWins) {
        this.m_nWins = nWins;
    }

    public int getPlaces() {
        return m_nPlaces;
    }

    public void setPlaces(int nPlaces) {
        this.m_nPlaces = nPlaces;
    }

    public int getShows() {
        return m_nShows;
    }

    public void setShows(int nShows) {
        this.m_nShows = nShows;
    }

    public String getCareerEarnings() {
        return m_strCareerEarnings;
    }

    public void setCareerEarnings(String strCareerEarnings) {
        this.m_strCareerEarnings = strCareerEarnings;
    }
    

public int getConduitBrilliance() {
        return m_nBrilliance;
    }
public int getConduitIntermediate() {
        return m_nIntermediate;
    }
public int getConduitClassic() {
        return m_nClassic;
    }
public int getConduitSolid() {
        return m_nSolid;
    }
public int getConduitProfessional() {
        return m_nProfessional;
    }

    public void setConduitBrilliance(int nBrilliance) {
        this.m_nBrilliance = nBrilliance;
    }

    public void setConduitIntermediate(int nIntermediate) {
        this.m_nIntermediate = nIntermediate;
    }

    public void setConduitClassic(int nClassic) {
        this.m_nClassic = nClassic;
    }

    public void setConduitSolid(int nSolid) {
        this.m_nSolid = nSolid;
    }

     public void setConduitProfessional(int nProfessional) {
        this.m_nProfessional = nProfessional;
    }

    public int getBrilliance() {
        return getBrilliance(0);
    }

    public void setBrilliance(int nBrilliance) {
        setBrilliance(nBrilliance, 0);
    }

    public int getIntermediate() {
        return getIntermediate(0);
    }

    public void setIntermediate(int nIntermediate) {
        setIntermediate(nIntermediate, 0);
    }

    public int getClassic() {
        return getClassic(0);
    }

    public void setClassic(int nClassic) {
        setClassic(nClassic, 0);
    }

    public int getSolid() {
        return getSolid(0);
    }

    public void setSolid(int nSolid) {
        setSolid(nSolid, 0);
    }

    public int getProfessional() {
        return getProfessional(0);
    }

    public void setProfessional(int nProfessional) {
        setProfessional(nProfessional, 0);
    }
    
   public int getBrilliance(int nGeneration) {
        return m_alGenerations.get(nGeneration).getBrilliance();
    }

    public void setBrilliance(int nBrilliance, int nGeneration) {
        m_alGenerations.get(nGeneration).setBrilliance(nBrilliance);
    }

    public int getIntermediate(int nGeneration) {
        return m_alGenerations.get(nGeneration).getIntermediate();
    }

    public void setIntermediate(int nIntermediate, int nGeneration) {
        m_alGenerations.get(nGeneration).setIntermediate(nIntermediate);
    }

    public int getClassic(int nGeneration) {
        return m_alGenerations.get(nGeneration).getClassic();
    }

    public void setClassic(int nClassic, int nGeneration) {
        m_alGenerations.get(nGeneration).setClassic(nClassic);
    }

    public int getSolid(int nGeneration) {
        return m_alGenerations.get(nGeneration).getSolid();
    }

    public void setSolid(int nSolid, int nGeneration) {
        m_alGenerations.get(nGeneration).setSolid(nSolid);
    }

    public int getProfessional(int nGeneration) {
        return m_alGenerations.get(nGeneration).getProfessional();
    }

    public void setProfessional(int nProfessional, int nGeneration) {
        m_alGenerations.get(nGeneration).setProfessional(nProfessional);
    }
     public double getGSV() {
        return m_dGSV;
    }

    public void setGSV(double dGSV) {
        this.m_dGSV = dGSV;
    }
   public String toString()
    {
        return m_strName + ", " + m_strBred + ", " + m_nYearBorn + ", " + m_strColour + ", " + m_strCareerEarnings;
    }
}
