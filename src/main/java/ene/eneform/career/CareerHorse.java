/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.career;

import ene.eneform.utils.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author simon
 */
public class CareerHorse {
    
    // horse_name, bred, foaling_date, colour, gender, dam_name, dam_year_born, sire_name, sire_year_born, dam_sire_name, dam_sire_year_born, trainer_name, owner_name

    private String wiOwner;

    private String horseName;
    private String bred;
    private String foalingDate;
    private String colour;
    private String gender;
    private String damName;
    private int damYearBorn;
    private String sireName;
    private int sireYearBorn;
    private String damSireName;
    private int damSireYearBorn;
    private String trainerName;
    private String ownerName;
    private List<Pair<String,String>> aRaces = new ArrayList<Pair<String,String>>();
    
    public  CareerHorse(String horseName) {
        this.horseName = horseName;
    }

    public String getHorseName() {
        return horseName;
    }

    public String getBred() {
        return bred;
    }

    public void setBred(String bred) {
        this.bred = bred;
    }

    public String getFoalingDate() {
        return foalingDate;
    }

    public void setFoalingDate(String foalingDate) {
        this.foalingDate = foalingDate;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDamName() {
        return damName;
    }

    public void setDamName(String damName) {
        this.damName = damName;
    }

    public int getDamYearBorn() {
        return damYearBorn;
    }

    public void setDamYearBorn(int damYearNµBorn) {
        this.damYearBorn = damYearNµBorn;
    }

    public String getSireName() {
        return sireName;
    }

    public void setSireName(String sireName) {
        this.sireName = sireName;
    }

    public int getSireYearBorn() {
        return sireYearBorn;
    }

    public void setSireYearBorn(int sireYearBorn) {
        this.sireYearBorn = sireYearBorn;
    }

    public String getDamSireName() {
        return damSireName;
    }

    public void setDamSireName(String damSireName) {
        this.damSireName = damSireName;
    }

    public int getDamSireYearBorn() {
        return damSireYearBorn;
    }

    public void setDamSireYearBorn(int damSireYearBorn) {
        this.damSireYearBorn = damSireYearBorn;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public String getWiOwner() {
        return wiOwner;
    }

    public void setWiOwner(String wiOwner) {
        this.wiOwner = wiOwner;
    }
    public void addMajorRace(String strRaceName, String strPosition) {
        this.aRaces.add(new Pair<String,String>(strRaceName, strPosition));
    }
    public List<Pair<String,String>> getMajorRaces() {
        return this.aRaces;
    }
            
 }
