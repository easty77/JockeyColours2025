package ene.eneform.service.colours.service;

import ene.eneform.domain.colours.RacingColoursParse;
import ene.eneform.domain.colours.RacingColoursParseId;
import ene.eneform.port.out.colours.RacingColoursParseRepository;
import ene.eneform.service.mero.model.colours.ENERacingColours;
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
