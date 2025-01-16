/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ene.eneform.smartform.bos;

/**
 *
 * @author Simon
 */
public class SmartformLastWinner {

//last_winner_no_race,last_winner_year,last_winner_runners,last_winner_runner_id,last_winner_name,last_winner_age,last_winner_bred,last_winner_weight,last_winner_trainer,last_winner_trainer_id,last_winner_jockey,last_winner_jockey_id,last_winner_sp,last_winner_sp_decimal,last_winner_betting_ranking,last_winner_course_winner,last_winner_distance_winner,last_winner_candd_winner,last_winner_beaten_favourite
    private int m_nRace=0;
    private int m_nYear=0;
    private String m_strHorse="";
    private String m_strJockey="";
    private String m_strTrainer="";
    private String m_strStartingPrice="";
    private String m_strNoRaceReason="";

   public SmartformLastWinner(int nRace)
    {
        m_nRace = nRace;
    }
    public int getYear() {
        return m_nYear;
    }

    public void setYear(int nYear) {
        this.m_nYear = nYear;
    }

    public String getHorse() {
        return m_strHorse;
    }

    public void setHorse(String strHorse) {
        this.m_strHorse = strHorse;
    }

   public String getNoRaceReason() {
        return m_strNoRaceReason;
    }
    public void setNoRaceReason(String strJockey) {
        this.m_strNoRaceReason = strJockey;
    }

    public String getJockey() {
        return m_strJockey;
    }

     public void setJockey(String strJockey) {
        this.m_strJockey = strJockey;
    }

    public String getStartingPrice() {
        return m_strStartingPrice;
    }

    public void setStartingPrice(String strStartingPrice) {
        this.m_strStartingPrice = strStartingPrice;
    }

    public String getTrainer() {
        return m_strTrainer;
    }

    public void setTrainer(String strTrainer) {
        this.m_strTrainer = strTrainer;
    }

 }
