package ene.eneform.port.out.smartform2025;

import ene.eneform.domain.smartform2025.EntryId;
import ene.eneform.domain.smartform2025.Run;
import ene.eneform.port.ReadWriteRepository;

import java.util.List;

public interface RunRepository extends ReadWriteRepository<Run, EntryId> {

    List<Run> findByHorseName(String name);

     List<Run> findRunsByHorseName(String name);

     List<Run> findRunsByRaceId(Integer raceId);

    List<Run> findByIdRunnerId(Integer runnerId);
}
