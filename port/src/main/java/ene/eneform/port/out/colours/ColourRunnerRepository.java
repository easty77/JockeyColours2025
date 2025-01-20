package ene.eneform.port.out.colours;

import ene.eneform.domain.colours.ColourRunner;
import ene.eneform.domain.colours.ColourRunnerId;
import ene.eneform.port.ReadWriteRepository;

import java.util.List;

public interface ColourRunnerRepository extends ReadWriteRepository<ColourRunner, ColourRunnerId> {

List<ColourRunner> findSmartformByRaceId(Integer raceId, Integer numRunners, String rcpVersion);

    List<ColourRunner> findAdditionalBySourceAndRaceId(String source, Integer raceId, Integer numRunners, String rcpVersion);
}
