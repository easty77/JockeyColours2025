package ene.eneform.controllers;

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
    private static String COLOURS = "Red, blue star, green cap, white diamonds";
    private final MeroService meroService;
    @GetMapping("/colours")
    public String generateSVGContent(ModelMap model) {
        String definition = meroService.parseDescription(COLOURS);
        log.info("generateSVGContent definition: {}", definition);
        String content = meroService.generateSVGContent(definition);
         model.put("svg", content);
        return "svg_content";
    }
}
