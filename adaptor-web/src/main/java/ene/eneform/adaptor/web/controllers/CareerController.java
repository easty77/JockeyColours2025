package ene.eneform.adaptor.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/career")
public class CareerController {
/*private CareerEnvironment careerEnvironment;
    @GetMapping("/resetEnvironment")
    public String resetEnvironment(ModelMap model) {
        careerEnvironment.reset();
        ENEStatement statement = SmartformConnectionPool.getInstance().getENEStatement();
        careerEnvironment.insertCareerHorses(statement);
        model.put("message", "resetEnvironment");
        return "message";
    } */
}
