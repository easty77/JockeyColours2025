package ene.eneform.adaptor.web.controllers;

import ene.eneform.smartform2025.entities.DailyRace;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminController {
@Autowired
private final EntityManager entityManager;
    @GetMapping("")
    public String index(ModelMap model) {
        log.info("Slf4j In AdminController index");
        return "admin/index";
    }
        @GetMapping("/additionalRaceMonth")
        public String additionalRaceMonth(@RequestParam int monthOffset, ModelMap model) {
             TypedQuery<DailyRace.AdditionalRaceMonthItem> q = entityManager.createNamedQuery("selectAdditionalRaceMonth", DailyRace.AdditionalRaceMonthItem.class);
            q.setParameter("monthOffset", monthOffset);
            List<DailyRace.AdditionalRaceMonthItem> items = q.getResultList();
            model.put("races", items);
            return "admin/additionalRaceMonth";
        }
}
