package ene.eneform.port.out.colours;

import ene.eneform.domain.colours.AdditionalRaceData;
import ene.eneform.port.ReadWriteRepository;

public interface AdditionalRaceDataRepository extends ReadWriteRepository<AdditionalRaceData, String> {
    public AdditionalRaceData findByName(String name);
}
