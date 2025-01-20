package ene.eneform.adaptor.web.controllers;

import ene.eneform.domain.smartform2025.DailyRace;
import ene.eneform.domain.smartform2025.Run;
import ene.eneform.port.in.smartform2025.DeclarationServiceInterface;
import ene.eneform.port.in.smartform2025.RunnerServiceInterface;
import ene.eneform.port.out.smartform2025.DailyRaceRepository;
import ene.eneform.port.out.smartform2025.RunRepository;
import ene.eneform.port.out.smartform2025.model.Declaration;
import ene.eneform.port.out.smartform2025.model.Runner;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/smartform2025")
public class DailyRaceController {
@Autowired
    private final DailyRaceRepository dailyRaceRepository;
    @Autowired
    private final RunRepository runRepository;
    @Autowired
    private final RunnerServiceInterface runService;
    @Autowired
    private final DeclarationServiceInterface declarationService;

    @GetMapping("/today")
    String hello(ModelMap model) {
        List<DailyRace> races = dailyRaceRepository.findByMeetingDate(LocalDate.now());
        model.put("races", races);
        return "smartform2025/displayRaces";
    }
    @GetMapping("/frankel")
    String frankel(ModelMap model) {
        List<Run> runs = runRepository.findRunsByHorseName("Frankel");
        model.put("runs", runs);
        return "smartform2025/displayRuns";
    }
    @GetMapping("/race/{raceId}")
    String runsByRace(@PathVariable("raceId") Integer raceId, ModelMap model) {
        List<Runner> runners = runService.findRunnersByRaceId(raceId);
        log.info("runsByRace {} {}", raceId, runners.size());
        model.put("runners", runners);
        return "smartform2025/displayRaceRunners";
    }
    @GetMapping("/entry/{raceId}")
    String entriesByRace(@PathVariable("raceId") Integer raceId, ModelMap model) {
        List<Declaration> entries = declarationService.findDeclarationsByRaceId(raceId);
        log.info("runsByRace {} {}", raceId, entries.size());
        model.put("runners", entries);
        return "smartform2025/displayRaceRunners";
    }
}