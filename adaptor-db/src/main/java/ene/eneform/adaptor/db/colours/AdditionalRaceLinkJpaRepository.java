package ene.eneform.adaptor.db.colours;

import ene.eneform.domain.colours.AdditionalRaceLink;
import ene.eneform.domain.colours.BasicRaceId;
import ene.eneform.port.out.colours.AdditionalRaceLinkRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface AdditionalRaceLinkJpaRepository extends AdditionalRaceLinkRepository, JpaRepository<AdditionalRaceLink, BasicRaceId> {
    List<AdditionalRaceLink> findByRaceName(String raceName);
    List<AdditionalRaceLink> findByRaceNameAndYear(String raceName, Integer year);
    AdditionalRaceLink findFirstByRaceNameOrderByYearDesc(String raceName);
}
