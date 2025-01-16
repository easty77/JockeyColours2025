package ene.eneform.service.colours.repository;

import ene.eneform.service.colours.domain.AdditionalRaceData;
import org.springframework.data.repository.CrudRepository;

public interface AdditionalRaceDataRepository extends CrudRepository<AdditionalRaceData, String> {
    public AdditionalRaceData findByName(String name);
}
