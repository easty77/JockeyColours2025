package ene.eneform.smartform2025.repositories;

import ene.eneform.smartform2025.entities.Run;
import ene.eneform.smartform2025.entities.EntryId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RunRepository extends CrudRepository<Run, EntryId> {

    List<Run> findByHorseName(String name);

    @Query("FROM Run u JOIN FETCH u.historicRace JOIN FETCH u.dailyRace JOIN FETCH u.dailyRunner WHERE u.horse.name = :name")
    List<Run> findRunsByHorseName(@Param("name") String name);

    @Query("FROM Run u JOIN FETCH u.historicRace JOIN FETCH u.dailyRace JOIN FETCH u.dailyRunner WHERE u.id.raceId = :raceId")
    List<Run> findRunsByRaceId(@Param("raceId") Integer raceId);

    List<Run> findByIdRunnerId(Integer runnerId);
}
