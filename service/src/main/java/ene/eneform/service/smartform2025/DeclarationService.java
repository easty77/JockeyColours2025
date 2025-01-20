package ene.eneform.service.smartform2025;

import ene.eneform.domain.smartform2025.DailyRunner;
import ene.eneform.domain.smartform2025.Entry;
import ene.eneform.domain.smartform2025.Horse;
import ene.eneform.port.in.smartform2025.DeclarationServiceInterface;
import ene.eneform.service.mero.model.ENEParsedRacingColours;
import ene.eneform.port.out.smartform2025.EntryRepository;
import ene.eneform.port.out.smartform2025.model.*;
import ene.eneform.service.mero.service.MeroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class DeclarationService implements DeclarationServiceInterface {
    private final EntryRepository entryRepository;
    private final MeroService meroService;

    public List<Declaration> findDeclarationsByRaceId(Integer raceId) {
        List<Entry> entries = entryRepository.findEntriesByRaceId(raceId);
        return map(entries);
    }
    private List<Declaration> map(List<Entry> entries) {
        return entries.stream().map(this::map).toList();
    }
    private Declaration map(Entry entry) {
        Owner owner = map(entry.owner);
        ENEParsedRacingColours parsedColours = meroService.createFullRacingColours("en", owner.getColours(), owner.getName());
        //owner.setParsedRacingColours(parsedColours);
        owner.setSvgContent(meroService.generateSVGContentFromDefinition(parsedColours.getColours().getDefinition()));
        return new Runner(
                map(entry.getId().runnerId, entry.horse),
                map(entry.jockey),
                map(entry.trainer),
                owner
        );
    }
    private ene.eneform.port.out.smartform2025.model.Horse map(Integer runnerId, Horse h) {
        return new ene.eneform.port.out.smartform2025.model.Horse(runnerId, h.name, h.bred, h.foaling_date, h.age, h.colour, h.gender, null);
    }
    private Jockey map(DailyRunner.Jockey j) {
        return new Jockey(j.jockeyId, j.jockeyName, j.jockeyClaim);
    }
    private Trainer map(DailyRunner.Trainer t) {
        return new Trainer(t.trainerId, t.trainerName);
    }
    private Owner map(DailyRunner.Owner o) {
        return new Owner(null, o.ownerName, o.jockeyColours);
    }
}
