package ene.eneform.service.smartform2025.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name="daily_races")
@Getter
@ToString
@NoArgsConstructor(force=true)
public class DailyRace {
    @Id
    public final Integer raceId;
    // details
    public final String raceTitle;
    public final String advancedGoing;
    public final Integer distanceYards;
    public final LocalDate scheduledTime;
    // course
    public final String course;
    public final String drawAdvantage;
    public final String country;
    // racetype
    public String raceType;
    public String trackType;
    @Column(name = "class")
    public String classType;
    public String ageRange;
    public Integer handicap;
    public Integer trifecta;
    public Integer showcase;

    // meeting
    public Integer meetingId;
    public LocalDate meetingDate;
    public String weather;
    public String meeting_status;
    public String meeting_abandoned_reason;
    // prize
    public Double added_money;
    public Double penalty_value;
    public Double prize_pos_1;
    public Double prize_pos_2;
    public Double prize_pos_3;
    public Double prize_pos_4;
    public Double prize_pos_5;
    public Double prize_pos_6;
    public Double prize_pos_7;
    public Double prize_pos_8;
    public Double prize_pos_9;
    // last winner
    public String last_winner_no_race;
    public Integer last_winner_year;
    public Integer last_winner_runners;
    public Integer last_winner_runner_id;
    public String last_winner_name;
    public Integer last_winner_age;
    public String last_winner_bred;
    public Integer last_winner_weight;
    public String last_winner_trainer;
    public Integer last_winner_trainer_id;
    public String last_winner_jockey;
    public Integer last_winner_jockey_id;
    public String last_winner_sp;
    public Double last_winner_sp_decimal;
    public Integer last_winner_betting_ranking;
    public Integer last_winner_course_winner;
    public Integer last_winner_distance_winner;
    public Integer last_winner_candd_winner;
    public Integer last_winner_beaten_favourite;
    /* @OneToMany(mappedBy="raceId", fetch=FetchType.EAGER)
    @MapsId("raceId")
    public List<DailyRunner> runners; */

    @NamedNativeQuery(name = "selectAdditionalRaceMonth",
            resultClass = AdditionalRaceMonthItem.class,
            query = """
                        select Date, Course, Name,Race_Type,Wikipedia, fr_wikipedia,Winner,DOW,GS,Year,Country,Track, Furlongs, Grade,Age, Sex, Month,Class, Hcap,SE 
                           from
                           ( 
                           select d.meeting_date as Date, ard_course as Course, ard_name as Name,
                           ard_race_type as Race_Type,
                           en.arw_wikipedia_ref as Wikipedia, fr.arw_wikipedia_ref as fr_wikipedia,
                           coalesce(name, cast(d.race_id as char)) as Winner,
                           case when ard_dow = dayofweek(d.meeting_date) then dayname(d.meeting_date) else concat(dayname(d.meeting_date), ard_dow) end as DOW,
                           ard_GS_ref as GS,
                           ard_start_year as Year,
                           ard_country as Country,
                           ard_track_type as Track, cast(ard_distance_yards/220.0 as decimal(3,1)) as Furlongs, ard_group_race as Grade,\s
                           ard_age_range as Age, ard_sex as Sex, ard_month as Month,
                           ard_class as Class, ard_handicap as "Hcap",
                           ard_se_key as "SE"
                           from
                           ((((((
                           daily_races d inner join additional_race_link on d.race_id=arl_race_id and arl_source='SF')
                           left outer join historic_races h on d.race_id=h.race_id)
                           left outer join historic_runners hru on h.race_id=hru.race_id and coalesce(hru.amended_position, hru.finish_position, 0)=1)
                           inner join additional_race_data on arl_name = ard_name)
                           left outer join additional_race_wikipedia en on ard_name=en.arw_name and en.arw_language='en')
                           left outer join additional_race_wikipedia fr on ard_name=fr.arw_name and fr.arw_language='fr')
                           where
                               (
                               month(d.meeting_date) = month(date_sub(current_date, interval :monthOffset month))
                                   and year(d.meeting_date)=year(current_date)
                               )   
                               or
                               (
                               year(d.meeting_date) = year(current_date) - 1 and month(d.meeting_date) = 13 - :monthOffset
                               )
                            union
                           select d.meeting_date as Date, ard_course as Course, ard_name as Name,
                           ard_race_type as Race_Type,
                           en.arw_wikipedia_ref as Wikipedia, fr.arw_wikipedia_ref as fr_wikipedia,
                           coalesce(name, cast(d.race_id as char)) as Winner,
                           case when ard_dow = dayofweek(d.meeting_date) then dayname(d.meeting_date) else concat(dayname(d.meeting_date), ard_dow) end as DOW,
                           ard_GS_ref as GS,
                           ard_start_year as Year,
                           ard_country as Country,
                           ard_track_type as Track, cast(ard_distance_yards/220 as unsigned) as Furlongs, ard_group_race as Grade,\s
                           ard_age_range as Age, ard_sex as Sex, ard_month as Month,
                           ard_class as Class, ard_handicap as "Hcap",
                           ard_se_key as "SE"
                           from
                           (((((
                           additional_races d inner join additional_race_link on d.race_id=arl_race_id and arl_source=ara_source)
                           left outer join additional_runners hru on d.race_id=hru.race_id and hru.finish_position=1)
                           inner join additional_race_data on arl_name = ard_name)
                           left outer join additional_race_wikipedia en on ard_name=en.arw_name and en.arw_language='en')
                           left outer join additional_race_wikipedia fr on ard_name=fr.arw_name and fr.arw_language='fr')
                           where
                               (
                               month(d.meeting_date) = month(date_sub(current_date, interval :monthOffset month))
                                   and year(d.meeting_date)=year(current_date)
                               ) 
                               or
                               (
                               year(d.meeting_date) = year(current_date) - 1 and month(d.meeting_date) = 13 - :monthOffset
                               )
                           ) a
    """)
    @Entity
    public static class AdditionalRaceMonthItem {
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
}
