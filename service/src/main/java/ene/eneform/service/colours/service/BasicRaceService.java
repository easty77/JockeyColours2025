package ene.eneform.service.colours.service;

import ene.eneform.domain.colours.BasicRace;
import ene.eneform.domain.colours.BasicRaceId;
import ene.eneform.port.out.colours.BasicRaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasicRaceService {
    private final BasicRaceRepository raceRepository;

    List<BasicRace> findByHorseAndBred(String horseName, String bred) {
        return raceRepository.findByHorseAndBred(horseName, bred);
    }

    BasicRace findById(String source, int raceId) {
        return raceRepository.findById(new BasicRaceId(source, raceId));
    }

}
