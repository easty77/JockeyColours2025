package ene.eneform.domain.smartform2025;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="daily_races")
@Getter
@ToString
@RequiredArgsConstructor
//@NoArgsConstructor(access= AccessLevel.PROTECTED, force=true)
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
}
