package ene.eneform.controllers;

import ene.eneform.colours.service.WikipediaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/wikipedia")
@AllArgsConstructor
@Slf4j
public class WikipediaController {
    private final WikipediaService wikipediaService;

    @GetMapping("/generateRace/{raceName}/{year}")
    String generateRaceYear(@PathVariable("raceName") String raceName, @PathVariable("year") Integer year, ModelMap model) {
        String content = wikipediaService.generateRace(raceName, year, "en", "<br />");
        model.put("content", content);
        return "wikipedia/content";
    }
    @GetMapping("/generateRaces/{raceName}")
    String generateRaces(@PathVariable("raceName") String raceName, ModelMap model) {
        String content = wikipediaService.generateRaceSequence(raceName, "en", "<br />");
        model.put("content", content);
        return "wikipedia/content";
    }
}
