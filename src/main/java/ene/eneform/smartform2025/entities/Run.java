package ene.eneform.smartform2025.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="historic_runners")
@Getter
@ToString
@NoArgsConstructor(force=true)
public class Run {
    @EmbeddedId
    public RunId id;

    public Integer cloth_number;
    public Integer stall_number;
    public Horse horse;
    public HistoricRunner.Breeding breeding;
    public HistoricRunner.Form form;
    public HistoricRunner.Jockey jockey;
    public HistoricRunner.Trainer trainer;
    public HistoricRunner.Owner owner;
    public HistoricRunner.Handicap handicap;
    public HistoricRunner.Betting betting;
    public HistoricRunner.Tack tack;
    public HistoricRunner.Performance performance;

    @ManyToOne
    @JoinColumn(name="raceId", referencedColumnName="raceId", insertable=false, updatable=false)
    public HistoricRace dailyRace;
    @ManyToOne
    @JoinColumn(name="raceId", referencedColumnName="raceId", insertable=false, updatable=false)
    public HistoricRace historicRace;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="raceId", referencedColumnName="raceId", insertable=false, updatable=false)
    @JoinColumn(name="runnerId", referencedColumnName="runnerId", insertable=false, updatable=false)
    public DailyRunner dailyRunner;
}
