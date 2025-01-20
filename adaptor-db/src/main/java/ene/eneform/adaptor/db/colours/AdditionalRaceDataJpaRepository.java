package ene.eneform.adaptor.db.colours;

import ene.eneform.domain.colours.AdditionalRaceData;
import ene.eneform.port.out.colours.AdditionalRaceDataRepository;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AdditionalRaceDataJpaRepository extends AdditionalRaceDataRepository, JpaRepository<AdditionalRaceData, String> {
    public AdditionalRaceData findByName(String name);
}
