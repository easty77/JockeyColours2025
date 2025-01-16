package ene.eneform.service.colours.repository;

import ene.eneform.service.colours.domain.RacingColoursParse;
import ene.eneform.service.colours.domain.RacingColoursParseId;
import org.springframework.data.repository.CrudRepository;

public interface RacingColoursParseRepository extends CrudRepository<RacingColoursParse, RacingColoursParseId> {
}
