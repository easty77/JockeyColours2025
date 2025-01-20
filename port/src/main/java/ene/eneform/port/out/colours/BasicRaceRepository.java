package ene.eneform.port.out.colours;

import ene.eneform.domain.colours.BasicRace;
import ene.eneform.domain.colours.BasicRaceId;
import ene.eneform.port.ReadWriteRepository;

import java.util.List;

public interface BasicRaceRepository extends ReadWriteRepository<BasicRace, Integer> {
    List<BasicRace> findByHorseAndBred(String horseName, String bred);

    BasicRace findById(BasicRaceId basicRaceId);
}
