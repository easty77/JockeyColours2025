package ene.eneform.smartform2025.services;

import ene.eneform.smartform2025.repositories.RunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RunService {
    private final RunRepository runRepository;
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
