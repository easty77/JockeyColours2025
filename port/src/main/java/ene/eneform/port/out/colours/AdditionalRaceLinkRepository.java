package ene.eneform.port.out.colours;

import ene.eneform.domain.colours.AdditionalRaceLink;
import ene.eneform.domain.colours.BasicRaceId;
import ene.eneform.port.ReadWriteRepository;

import java.util.List;

public interface AdditionalRaceLinkRepository extends ReadWriteRepository<AdditionalRaceLink, BasicRaceId> {
    List<AdditionalRaceLink> findByRaceName(String raceName);
    List<AdditionalRaceLink> findByRaceNameAndYear(String raceName, Integer year);
    AdditionalRaceLink findFirstByRaceNameOrderByYearDesc(String raceName);
}
