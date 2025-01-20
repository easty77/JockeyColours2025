package ene.eneform.service.smartform2025;

import ene.eneform.domain.smartform2025.Horse;
import ene.eneform.domain.smartform2025.DailyRunner;
import ene.eneform.domain.smartform2025.HistoricRunner;
import ene.eneform.port.in.smartform2025.HorseServiceInterface;

public class HorseService implements HorseServiceInterface {

    private ene.eneform.port.out.smartform2025.model.Horse map(Integer id, Horse h, ene.eneform.port.out.smartform2025.model.Horse.Breeding breeding) {
        return ene.eneform.port.out.smartform2025.model.Horse.onCreate(id, h.name, h.bred, h.foaling_date,
                h.age, h.colour, h.gender,
                breeding);
    }

    private ene.eneform.port.out.smartform2025.model.Horse.Breeding map(HistoricRunner.Breeding h, DailyRunner.Breeding d)  {
        return ene.eneform.port.out.smartform2025.model.Horse.onCreateBreeding(
                new ene.eneform.port.out.smartform2025.model.Horse.Ancestor(h.damId, h.damName, null, d.damYearBorn),
                new ene.eneform.port.out.smartform2025.model.Horse.Ancestor(h.sireId, h.sireName, null, d.sireYearBorn),
                new ene.eneform.port.out.smartform2025.model.Horse.Ancestor(h.damSireId, h.damSireName, null, d.damSireYearBorn)
        );
    }
    private ene.eneform.port.out.smartform2025.model.Horse.Breeding map(DailyRunner.Breeding d)  {
        return ene.eneform.port.out.smartform2025.model.Horse.onCreateBreeding(
                new ene.eneform.port.out.smartform2025.model.Horse.Ancestor(null, d.damName, null, d.damYearBorn),
                new ene.eneform.port.out.smartform2025.model.Horse.Ancestor(null, d.sireName, null, d.sireYearBorn),
                new ene.eneform.port.out.smartform2025.model.Horse.Ancestor(null, d.damSireName, null, d.damSireYearBorn)
        );
    }

}
