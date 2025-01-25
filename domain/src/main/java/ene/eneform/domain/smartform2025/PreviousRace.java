package ene.eneform.domain.smartform2025;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PreviousRace {
    @Id
    public Integer raceId;
    public String course;
    public String raceType;
    @Column(name="y")
    Integer year;
    @Column(name="m")
    Integer month;
    @Column(name="d")
    Integer day;
    Integer furlongs;
    String age;
    Integer groupRace;
    Integer previousRaceId;
    @Column(name="year")
    Integer previousYear;
    String raceName;
    Integer money;
    @Column(name="name")
    String winner;
    String jockeyName;
    String trainerName;
    String ownerName;
    @Column(name="daily")
    Boolean isDaily;
    String arlName;
}
