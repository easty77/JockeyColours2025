package ene.eneform.service.smartform2025.service;

import ene.eneform.service.smartform2025.entities.Horse;
import ene.eneform.service.smartform2025.entities.DailyRunner;
import ene.eneform.service.smartform2025.entities.HistoricRunner;

public class HorseService {

    private ene.eneform.service.smartform2025.dtos.Horse map(Integer id, Horse h, ene.eneform.service.smartform2025.dtos.Horse.Breeding breeding) {
        return ene.eneform.service.smartform2025.dtos.Horse.onCreate(id, h.name, h.bred, h.foaling_date,
                h.age, h.colour, h.gender,
                breeding);
    }

    private ene.eneform.service.smartform2025.dtos.Horse.Breeding map(HistoricRunner.Breeding h, DailyRunner.Breeding d)  {
        return ene.eneform.service.smartform2025.dtos.Horse.onCreateBreeding(
                new ene.eneform.service.smartform2025.dtos.Horse.Ancestor(h.damId, h.damName, null, d.damYearBorn),
                new ene.eneform.service.smartform2025.dtos.Horse.Ancestor(h.sireId, h.sireName, null, d.sireYearBorn),
                new ene.eneform.service.smartform2025.dtos.Horse.Ancestor(h.damSireId, h.damSireName, null, d.damSireYearBorn)
        );
    }
    private ene.eneform.service.smartform2025.dtos.Horse.Breeding map(DailyRunner.Breeding d)  {
        return ene.eneform.service.smartform2025.dtos.Horse.onCreateBreeding(
                new ene.eneform.service.smartform2025.dtos.Horse.Ancestor(null, d.damName, null, d.damYearBorn),
                new ene.eneform.service.smartform2025.dtos.Horse.Ancestor(null, d.sireName, null, d.sireYearBorn),
                new ene.eneform.service.smartform2025.dtos.Horse.Ancestor(null, d.damSireName, null, d.damSireYearBorn)
        );
    }

}
