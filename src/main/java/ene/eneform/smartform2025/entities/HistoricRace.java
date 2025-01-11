package ene.eneform.smartform2025.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name="historic_races")
@Getter
@ToString
@NoArgsConstructor(force=true)
public class HistoricRace {
    @Id
    public Integer raceId;
    // details
    public final String raceName;
    public final String raceAbbrevName;
    public final String going;
    public Integer distanceYards;
    public final LocalDate scheduledTime;
    public final LocalDate offTime;
    public Integer race_num;
    public Integer num_runners;
    public Integer num_finishers;
    // course
    public final String course;
    public final String drawAdvantage;

    // meeting
    public Integer meetingId;
    public LocalDate meetingDate;
    // racetype
    public String raceType;
    @Column(name = "class")
    public String classType;
    public Integer minAge;
    public Integer maxAge;
    public Integer handicap;
    public Integer race_type_id;
    public String direction;
    public Integer num_fences;
    public Integer all_weather;
    public Integer seller;
    public Integer claimer;
    public Integer apprentice;
    public Integer maiden;
    public Integer  amateur;
    public Integer rating;
    public Integer group_race;
    public String conditions;
    // prize
    public Double added_money;
    //performance
    public Integer official_rating;
    public Integer speed_rating;
    public Integer private_handicap;
    public String winning_time_disp;
    public Double winning_time_secs;
    public String standard_time_disp;
    public Double standard_time_secs;
}
