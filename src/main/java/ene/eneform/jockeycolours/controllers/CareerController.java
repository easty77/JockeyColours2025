package ene.eneform.jockeycolours.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/career")
public class CareerController {
    /*
    @GetMapping("/resetEnvironment")
    public String resetEnvironment(ModelMap model) {
        CareerEnvironment.getInstance().reset();
        ENEStatement statement = SmartformConnectionPool.getInstance().getENEStatement();
        CareerEnvironment.getInstance().insertCareerHorses(statement);
        model.put("message", "resetEnvironment");
        return "message";
    } */
}
