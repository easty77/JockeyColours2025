package ene.eneform.adaptor.db.smartform2025;

import ene.eneform.domain.smartform2025.PreviousRace;
import ene.eneform.port.out.smartform2025.command.PreviousRaceSearch;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class PreviousRaceJpaRepositoryTest {
    @Autowired
    private PreviousRaceJpaRepository raceRepository;

    @Test
    public void findSalisburyGroupRaces()  {
        PreviousRaceSearch search = new PreviousRaceSearch();
        search.setCourse("Salisbury");
        search.setGrade("All");
    List<PreviousRace> races = raceRepository.searchPreviousRaces(search.getTitle(),
            search.getCourse(),
            search.getRaceType(),
            search.getWinner(),
            search.getYear(),
            search.getQuarter(),
            search.getMonth(),
            search.getMonth1(),
            search.getMonth2(),
            search.getHalf(),
            search.getGrade(),
            search.getAge(),
            search.getMoney(),
            search.getFurlongs()
            );
    log.info("Salisbury group racs: {} - {}", races.size(),
            races.toString());
    }

}
