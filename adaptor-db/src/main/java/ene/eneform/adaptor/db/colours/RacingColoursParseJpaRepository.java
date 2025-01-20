package ene.eneform.adaptor.db.colours;

import ene.eneform.domain.colours.RacingColoursParse;
import ene.eneform.domain.colours.RacingColoursParseId;
import ene.eneform.port.out.colours.RacingColoursParseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface RacingColoursParseJpaRepository extends RacingColoursParseRepository, JpaRepository<RacingColoursParse, RacingColoursParseId> {
}
