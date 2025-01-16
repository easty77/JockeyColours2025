package ene.eneform.adaptor.web.controllers;

import ene.eneform.mero.service.MeroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mero")
@RequiredArgsConstructor
@Slf4j
public class MeroController {
    private static String COLOURS1 = "Red, blue star, green cap, white diamonds";
    private static String COLOURS2 = "Red and white stripes";
    private static String COLOURS3 = "Red and white (halved), sleeves reversed, blue cap";
    private static String COLOURS4 = "Red and yellow hoops, quartered cap";
    private final MeroService meroService;
    @GetMapping("/colours")
    public String generateSVGContent(ModelMap model) {
         model.put("svg1", meroService.generateSVGContentFromDefinition(meroService.parseDescription(COLOURS1)));
        model.put("svg2", meroService.generateSVGContentFromDefinition(meroService.parseDescription(COLOURS2)));
        model.put("svg3", meroService.generateSVGContentFromDefinition(meroService.parseDescription(COLOURS3)));
        model.put("svg4", meroService.generateSVGContentFromDefinition(meroService.parseDescription(COLOURS4)));
        return "svg_content";
    }
}
