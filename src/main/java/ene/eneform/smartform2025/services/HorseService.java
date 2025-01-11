package ene.eneform.smartform2025.services;

import ene.eneform.smartform2025.dtos.Horse;
import ene.eneform.smartform2025.entities.DailyRunner;
import ene.eneform.smartform2025.entities.HistoricRunner;

public class HorseService {

    private Horse map(Integer id, ene.eneform.smartform2025.entities.Horse h, Horse.Breeding breeding) {
        return Horse.onCreate(id, h.name, h.bred, h.foaling_date,
                h.age, h.colour, h.gender,
                breeding);
    }

    private Horse.Breeding map(HistoricRunner.Breeding h, DailyRunner.Breeding d)  {
        return Horse.onCreateBreeding(
                new Horse.Ancestor(h.damId, h.damName, null, d.damYearBorn),
                new Horse.Ancestor(h.sireId, h.sireName, null, d.sireYearBorn),
                new Horse.Ancestor(h.damSireId, h.damSireName, null, d.damSireYearBorn)
        );
    }
    private Horse.Breeding map(DailyRunner.Breeding d)  {
        return Horse.onCreateBreeding(
                new Horse.Ancestor(null, d.damName, null, d.damYearBorn),
                new Horse.Ancestor(null, d.sireName, null, d.sireYearBorn),
                new Horse.Ancestor(null, d.damSireName, null, d.damSireYearBorn)
        );
    }

}
