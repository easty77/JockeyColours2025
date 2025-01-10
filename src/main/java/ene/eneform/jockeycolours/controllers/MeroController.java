package ene.eneform.jockeycolours.controllers;

import ene.eneform.colours.bos.ENEColoursDBEnvironment;
import ene.eneform.colours.service.WikipediaService;
import ene.eneform.mero.config.ENEColoursEnvironment;
import ene.eneform.mero.service.MeroService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mero")
@RequiredArgsConstructor
public class MeroController {
    private final MeroService meroService;
    @GetMapping("/colours")
    public String resetEnvironment(ModelMap model) {
        ENEColoursEnvironment.getInstance().reset();
        ENEColoursDBEnvironment.getInstance().reset();
        model.put("message", "resetEnvironment");
        return "message";
    }
}
