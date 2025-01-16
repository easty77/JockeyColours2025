package ene.eneform.service.smartform2025.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="historic_runners")
@ToString
@NoArgsConstructor(force=true)
public class HistoricRunner {
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
    public Performance performance;
    @Embeddable
    public static class Breeding {
        public String damName ;
        public Integer damId ;
        public String sireName;
        public Integer sireId;
        public String damSireName;
        public Integer damSireId;
    }
    @Embeddable
    public static class Form {
        public String form_figures;
        public Integer days_since_ran;
        public Integer distance_travelled;
        public Integer last_race_type_id;
        public String last_race_type;
        public Integer last_race_beaten_fav;
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
        public Integer ownerId;
    }
    @Embeddable
    public static class Handicap {
        public Integer longHandicap;
        public Integer officialRating;
        public Integer weightPounds;
        public Integer penaltyWeight;
        public Integer over_weight;
        public String official_rating_type;
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
        public String startingPrice;
        public Double startingPriceDecimal;
        public String bettingText;
        public Integer positionInBetting;
        public Double toteWin;
        public Double totePlace;
    }

    @Embeddable
public static class Performance {
        public Integer numFencesJumped;
        public Integer howEasyWon;
        public String inRaceComment;
        public Integer finishPosition;
        public Integer amendedPosition;
        public String unfinished;
        public Double distanceBeaten;
        public Double distanceWon;
        public Double distanceBehindWinner;
        public Integer speedRating;
        public String speedRatingType;
        public Integer privateHandicap;
        public String privateHandicapType;
        public Double prizeMoney;

    }
}
