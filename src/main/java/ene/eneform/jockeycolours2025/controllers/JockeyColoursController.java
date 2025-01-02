package ene.eneform.jockeycolours2025.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
@Slf4j
public class JockeyColoursController {

        @GetMapping("/")
        public String index(ModelMap model) {
            //log.info("In RestController");
            System.out.println("In RestController");
            model.put("data", "hello data");
            return "index1";
        }
}
