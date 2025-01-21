package ene.eneform.adaptor.db.smartform2025;

import ene.eneform.domain.smartform2025.DailyRace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class DailyRaceJpaRepositoryTest {
    @Autowired
    private DailyRaceJpaRepository raceRepository;

    @Test
    public void findDailyRaceById()  {
    Optional<DailyRace> race = raceRepository.findById(1385186);
    if (race.isPresent()) {
        DailyRace race1 = race.get();
        System.out.println(race1);
    }
    }
    @Test
    public void findDailyRaceByDate()  {
        List<DailyRace> races = raceRepository.findByMeetingDate(LocalDate.now());
        System.out.println(races.size());
    }

}
