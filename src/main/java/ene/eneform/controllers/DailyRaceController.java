package ene.eneform.controllers;

import ene.eneform.smartform2025.entities.DailyRace;
import ene.eneform.smartform2025.entities.Run;
import ene.eneform.smartform2025.repositories.DailyRaceRepository;
import ene.eneform.smartform2025.repositories.RunRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/today")
    String hello(ModelMap model) {
        log.info("In controller hello");
        List<DailyRace> races = dailyRaceRepository.findByMeetingDate(LocalDate.now());
        model.put("races", races);
        return "smartform2025/displayRaces";
    }
    @GetMapping("/frankel")
    String frankel(ModelMap model) {
        log.info("In controller hello");
        List<Run> runs = runRepository.findRunsByHorseName("Frankel");
        model.put("runs", runs);
        return "smartform2025/displayRuns";
    }
}