package ene.eneform.service.smartform2025.entities;

import ene.eneform.service.smartform2025.entities.DailyRace;
import ene.eneform.service.smartform2025.repositories.DailyRaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class DailyRaceTests {
    @Autowired
    private DailyRaceRepository raceRepository;

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
