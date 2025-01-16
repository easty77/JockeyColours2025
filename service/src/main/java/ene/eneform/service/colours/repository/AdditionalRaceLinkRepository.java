package ene.eneform.service.colours.repository;

import ene.eneform.service.colours.domain.AdditionalRaceLink;
import ene.eneform.service.colours.domain.BasicRaceId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AdditionalRaceLinkRepository extends CrudRepository<AdditionalRaceLink, BasicRaceId> {
    List<AdditionalRaceLink> findByRaceName(String raceName);
    List<AdditionalRaceLink> findByRaceNameAndYear(String raceName, Integer year);
    AdditionalRaceLink findFirstByRaceNameOrderByYearDesc(String raceName);
}
