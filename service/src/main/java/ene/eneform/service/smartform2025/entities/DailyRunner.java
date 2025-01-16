package ene.eneform.service.smartform2025.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="daily_runners")
@ToString
@NoArgsConstructor(force=true)
public class DailyRunner {
    @EmbeddedId
    public EntryId id;

    public Integer cloth_number;
    public Integer stall_number;
    public Horse horse;
    public Breeding breeding;
    public Form form;
    public Jockey jockey;
    public Trainer trainer;
    public Owner owner;
    public Handicap handicap;
    public Betting betting;
    public Tack tack;
    @Embeddable
    public static class Breeding {
        public String damName ;
        public Integer damYearBorn ;
        public String sireName;
        public Integer sireYearBorn;
        public String damSireName;
        public Integer damSireYearBorn;
    }

    @Embeddable
    public static class Form {
        public Integer courseWinner;
        public Integer distanceWinner;
        public Integer candd_winner;
        public Integer beatenFavourite;
    }
    @Embeddable
    public static class Jockey {
        public Integer jockeyId;
        public String jockeyName;
        public Integer jockeyClaim;
    }
    @Embeddable
    public static class Trainer {
        public Integer trainerId;
        public String trainerName;
    }
    @Embeddable
    public static class Owner {
        public String ownerName;
        public String jockeyColours;
    }
    @Embeddable
    public static class Handicap {
        public Integer longHandicap;
        public Integer officialRating;
        public Integer adjustedRating;
        public Integer weightPounds;
        public Integer weightPenalty;
    }
    @Embeddable
    public static class Tack {
        public Integer tack_hood;
        public Integer tack_visor;
        public Integer tack_blinkers;
        public Integer tack_eye_shield;
        public Integer tack_eye_cover;
        public Integer tack_cheek_piece;
        public Integer tack_pacifiers;
        public Integer tack_tongue_strap;
    }
    @Embeddable
    public static class Betting {
        public String forecastPrice;
        public Double forecastPriceDecimal;
    }
}
