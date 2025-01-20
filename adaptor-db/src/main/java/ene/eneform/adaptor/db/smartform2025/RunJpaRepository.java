package ene.eneform.adaptor.db.smartform2025;

import ene.eneform.domain.smartform2025.EntryId;
import ene.eneform.domain.smartform2025.Run;
import ene.eneform.port.out.smartform2025.RunRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface RunJpaRepository extends RunRepository, JpaRepository<Run, EntryId> {

    List<Run> findByHorseName(String name);

    @Query("FROM Run u JOIN FETCH u.historicRace JOIN FETCH u.dailyRace JOIN FETCH u.dailyRunner WHERE u.horse.name = :name")
    List<Run> findRunsByHorseName(@Param("name") String name);

    @Query("FROM Run u JOIN FETCH u.historicRace JOIN FETCH u.dailyRace JOIN FETCH u.dailyRunner WHERE u.id.raceId = :raceId")
    List<Run> findRunsByRaceId(@Param("raceId") Integer raceId);

    List<Run> findByIdRunnerId(Integer runnerId);
}
