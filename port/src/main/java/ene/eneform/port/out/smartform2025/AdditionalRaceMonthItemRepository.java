package ene.eneform.port.out.smartform2025;

import ene.eneform.domain.smartform2025.AdditionalRaceMonthItem;
import ene.eneform.port.ReadWriteRepository;

import java.util.List;

public interface AdditionalRaceMonthItemRepository extends ReadWriteRepository<AdditionalRaceMonthItem, String> {
        List<AdditionalRaceMonthItem> findCurrentMonthRaces(int monthOffset);

}
