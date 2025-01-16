package ene.eneform.service.smartform2025.service;

import ene.eneform.service.mero.colours.ENEParsedRacingColours;
import ene.eneform.service.mero.service.MeroService;
import ene.eneform.service.smartform2025.dtos.Jockey;
import ene.eneform.service.smartform2025.dtos.Owner;
import ene.eneform.service.smartform2025.dtos.Runner;
import ene.eneform.service.smartform2025.dtos.Trainer;
import ene.eneform.service.smartform2025.entities.DailyRunner;
import ene.eneform.service.smartform2025.entities.HistoricRunner;
import ene.eneform.service.smartform2025.entities.Horse;
import ene.eneform.service.smartform2025.entities.Run;
import ene.eneform.service.smartform2025.repositories.RunRepository;
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
    private ene.eneform.service.smartform2025.dtos.Horse map(Integer runnerId, Horse h) {
        return new ene.eneform.service.smartform2025.dtos.Horse(runnerId, h.name, h.bred, h.foaling_date, h.age, h.colour, h.gender, null);
    }
    private Jockey map(HistoricRunner.Jockey j) {
        return new Jockey(j.jockeyId, j.jockeyName, j.jockeyClaim);
    }
    private Trainer map(HistoricRunner.Trainer t) {
        return new Trainer(t.trainerId, t.trainerName);
    }
    private Owner map(HistoricRunner.Owner o,
                      DailyRunner.Owner oD) {
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
