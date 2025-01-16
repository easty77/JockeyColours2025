package ene.eneform.service.colours.service;

import ene.eneform.service.colours.domain.RacingColoursParse;
import ene.eneform.service.colours.domain.RacingColoursParseId;
import ene.eneform.service.colours.repository.RacingColoursParseRepository;
import ene.eneform.service.mero.colours.ENERacingColours;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RacingColoursParseService {
    private final RacingColoursParseRepository rcpRepository;

    public void insertRacingColoursParse(String version, ENERacingColours colours, String unresolved, String expanded, String syntax) {
        rcpRepository.findById(new RacingColoursParseId(colours.getDescription(), version)).ifPresentOrElse(
                rcp -> {
                    RacingColoursParse save = rcp.onUpdate(colours.getJacket().toString(), colours.getSleeves().toString(), colours.getCap().toString(),
                                expanded, unresolved, syntax);
                        rcpRepository.save(save);

                },
                () -> {
                    RacingColoursParse rcp = RacingColoursParse.onCreate(colours.getDescription(), version, colours.getJacket().toString(), colours.getSleeves().toString(), colours.getCap().toString(),
                            expanded, unresolved, syntax);
        rcpRepository.save(rcp);
                }
        );
    }
}
