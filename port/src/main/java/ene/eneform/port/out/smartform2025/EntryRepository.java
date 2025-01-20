package ene.eneform.port.out.smartform2025;

import ene.eneform.domain.smartform2025.Entry;
import ene.eneform.domain.smartform2025.EntryId;
import ene.eneform.port.ReadWriteRepository;

import java.util.List;

public interface EntryRepository extends ReadWriteRepository<Entry, EntryId> {

    List<Entry> findEntriesByRaceId(Integer raceId);
}
