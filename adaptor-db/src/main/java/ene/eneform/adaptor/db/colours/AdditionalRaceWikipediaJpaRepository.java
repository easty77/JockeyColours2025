package ene.eneform.adaptor.db.colours;

import ene.eneform.domain.colours.AdditionalRaceWikipedia;
import ene.eneform.port.out.colours.AdditionalRaceWikipediaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface AdditionalRaceWikipediaJpaRepository extends AdditionalRaceWikipediaRepository,  JpaRepository<AdditionalRaceWikipedia, String> {
}
