package ene.eneform.smartform2025.services;

import ene.eneform.mero.colours.ENEParsedRacingColours;
import ene.eneform.mero.service.MeroService;
import ene.eneform.smartform2025.dtos.*;
import ene.eneform.smartform2025.entities.Entry;
import ene.eneform.smartform2025.repositories.EntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class DeclarationService {
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
        owner.setParsedRacingColours(parsedColours);
        owner.setSvgContent(meroService.generateSVGContentFromDefinition(parsedColours.getColours().getDefinition()));
        return new Runner(
                map(entry.getId().runnerId, entry.horse),
                map(entry.jockey),
                map(entry.trainer),
                owner
        );
    }
    private Horse map(Integer runnerId, ene.eneform.smartform2025.entities.Horse h) {
        return new Horse(runnerId, h.name, h.bred, h.foaling_date, h.age, h.colour, h.gender, null);
    }
    private Jockey map(ene.eneform.smartform2025.entities.DailyRunner.Jockey j) {
        return new Jockey(j.jockeyId, j.jockeyName, j.jockeyClaim);
    }
    private Trainer map(ene.eneform.smartform2025.entities.DailyRunner.Trainer t) {
        return new Trainer(t.trainerId, t.trainerName);
    }
    private Owner map(ene.eneform.smartform2025.entities.DailyRunner.Owner o) {
        return new Owner(null, o.ownerName, o.jockeyColours);
    }
}
