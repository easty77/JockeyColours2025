package ene.eneform.adaptor.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminController {
    /*
@Autowired
private final AdditionalRaceMonthItemRepository additionalRaceMonthItemRepository;

    @GetMapping("")
    public String index(ModelMap model) {
        log.info("Slf4j In AdminController index");
        return "admin/index";
    }
        @GetMapping("/additionalRaceMonth")
        public String additionalRaceMonth(@RequestParam int monthOffset, ModelMap model) {

            List<AdditionalRaceMonthItem> items = additionalRaceMonthItemRepository.findCurrentMonthRaces(monthOffset);
            model.put("races", items);
            return "admin/additionalRaceMonth";
        } */
}
