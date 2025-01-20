package ene.eneform.domain.smartform2025;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class AdditionalRaceMonthItem {
    @Id
    public String name;
    public LocalDate date;
    public String course;
    public String raceType;
    public String wikipedia;
    public String frWikipedia;
    public String winner;
    public String dow;
    public String gs;
    public Integer year;
    public String country;
    public String track;
    public Integer furlongs;
    public Integer grade;
    public String age;
    public String sex;
    public Integer month;
    @Column(name = "Class")
    public String classType;
    public Integer hcap;
    public String se;
}
