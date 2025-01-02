package ene.eneform.jockeycolours2025.controllers;

import ene.eneform.jockeycolours2025.entities.AdditionalRaceMonthItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
public class JockeyColoursController {
@Autowired
private final EntityManager entityManager;
        @GetMapping("/")
        public String index(ModelMap model) {
            //log.info("In RestController");
            System.out.println("In RestController");
            TypedQuery<AdditionalRaceMonthItem> q = entityManager.createNamedQuery("selectAdditionalRaceMonth", AdditionalRaceMonthItem.class);
            q.setParameter("monthOffset", 0);
            List<AdditionalRaceMonthItem> items = q.getResultList();
            model.put("races", items);
            return "index1";
        }
}
