package ene.eneform.adaptor.web.controllers;

import ene.eneform.domain.smartform2025.AdditionalRaceMonthItem;
import ene.eneform.port.out.smartform2025.AdditionalRaceMonthItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminController {

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
        }
}
