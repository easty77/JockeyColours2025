package ene.eneform.port.out.smartform2025;

import ene.eneform.domain.smartform2025.DailyRace;
import ene.eneform.port.ReadWriteRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyRaceRepository  extends ReadWriteRepository<DailyRace, Integer> {

List<DailyRace> findByMeetingDate(LocalDate meetingDate);
/*
    @Query("FROM DailyRace u JOIN FETCH u.DailyRunner WHERE u.raceId = :raceId")
    Optional<DailyRace> findRaceAndRunnersByRaceId(@Param("raceId") Integer raceId);

 */
}
