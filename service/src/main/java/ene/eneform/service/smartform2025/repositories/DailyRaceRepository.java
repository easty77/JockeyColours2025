package ene.eneform.service.smartform2025.repositories;

import ene.eneform.service.smartform2025.entities.DailyRace;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyRaceRepository  extends CrudRepository<DailyRace, Integer> {

List<DailyRace> findByMeetingDate(LocalDate meetingDate);
/*
    @Query("FROM DailyRace u JOIN FETCH u.DailyRunner WHERE u.raceId = :raceId")
    Optional<DailyRace> findRaceAndRunnersByRaceId(@Param("raceId") Integer raceId);

 */
}
