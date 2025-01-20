package ene.eneform.adaptor.db.smartform2025;

import ene.eneform.domain.smartform2025.DailyRace;
import ene.eneform.port.out.smartform2025.DailyRaceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


public interface DailyRaceJpaRepository extends DailyRaceRepository, JpaRepository<DailyRace, Integer> {

List<DailyRace> findByMeetingDate(LocalDate meetingDate);

/*
    @Query("FROM DailyRace u JOIN FETCH u.DailyRunner WHERE u.raceId = :raceId")
    Optional<DailyRace> findRaceAndRunnersByRaceId(@Param("raceId") Integer raceId);

 */
}
