package ene.eneform.smartform2025.services;

import ene.eneform.mero.colours.ENEParsedRacingColours;
import ene.eneform.mero.service.MeroService;
import ene.eneform.smartform2025.dtos.*;
import ene.eneform.smartform2025.entities.Run;
import ene.eneform.smartform2025.repositories.RunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RunnerService {
    private final RunRepository runRepository;
    private final MeroService meroService;

    public List<Runner> findRunnersByRaceId(Integer raceId) {
        List<Run> runs = runRepository.findRunsByRaceId(raceId);
        return map(runs);
    }
    private List<Runner> map(List<Run> runs) {
        return runs.stream().map(this::map).toList();
    }
    private Runner map(Run run) {
        Owner owner = map(run.owner, run.dailyRunner.owner);
        ENEParsedRacingColours parsedColours = meroService.createFullRacingColours("en", owner.getColours(), owner.getName());
        owner.setParsedRacingColours(parsedColours);
        owner.setSvgContent(meroService.generateSVGContentFromDefinition(parsedColours.getColours().getDefinition()));
        return new Runner(
                map(run.getId().runnerId, run.horse),
                map(run.jockey),
                map(run.trainer),
                owner
        );
    }
    private Horse map(Integer runnerId, ene.eneform.smartform2025.entities.Horse h) {
        return new Horse(runnerId, h.name, h.bred, h.foaling_date, h.age, h.colour, h.gender, null);
    }
    private Jockey map(ene.eneform.smartform2025.entities.HistoricRunner.Jockey j) {
        return new Jockey(j.jockeyId, j.jockeyName, j.jockeyClaim);
    }
    private Trainer map(ene.eneform.smartform2025.entities.HistoricRunner.Trainer t) {
        return new Trainer(t.trainerId, t.trainerName);
    }
    private Owner map(ene.eneform.smartform2025.entities.HistoricRunner.Owner o,
                      ene.eneform.smartform2025.entities.DailyRunner.Owner oD) {
        return new Owner(o.ownerId, o.ownerName, oD.jockeyColours);
    }
/*
    public List<Run> findByHorse(String name) {
        List<ene.eneform.smartform.entities.Run> x = runRepository.findRunsByHorseName(name);
        return map(x);
    }
    private  List<Run> map(List<ene.eneform.smartform.entities.Run> runs) {
        return runs.stream().map(this::map).toList();
    }

    private  Run map(ene.eneform.smartform.entities.Run run) {
        return new Run(run.dailyRunner, run.dailyRace, run.historicRace);
    }

    private Runner map(DailyRunner dailyRunner, HistoricRunner historicRunner) {

    }
    private Race map(DailyRace dailyRace, HistoricRace historicRace) {
        return new Race(details, course);
    }
    private Race.RaceDetails mapDetails(DailyRace dailyRace, HistoricRace historicRace) {
        return new Race.RaceDetails();
    }
    */

}
