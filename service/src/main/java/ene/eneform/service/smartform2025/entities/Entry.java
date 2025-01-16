package ene.eneform.service.smartform2025.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="daily_runners")
@Getter
@ToString
@NoArgsConstructor(force=true)
public class Entry {
    @EmbeddedId
    public EntryId id;

    public Integer cloth_number;
    public Integer stall_number;
    public Horse horse;
    public DailyRunner.Breeding breeding;
    public DailyRunner.Form form;
    public DailyRunner.Jockey jockey;
    public DailyRunner.Trainer trainer;
    public DailyRunner.Owner owner;
    public DailyRunner.Handicap handicap;
    public DailyRunner.Betting betting;
    public DailyRunner.Tack tack;

    @ManyToOne
    @JoinColumn(name="raceId", referencedColumnName="raceId", insertable=false, updatable=false)
    public DailyRace race;
}
