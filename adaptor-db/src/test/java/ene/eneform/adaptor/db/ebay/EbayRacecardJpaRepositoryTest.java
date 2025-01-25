package ene.eneform.adaptor.db.ebay;

import ene.eneform.domain.ebay.EbayRacecard;
import ene.eneform.port.out.ebay.command.RacecardSearch;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class EbayRacecardJpaRepositoryTest {
    @Autowired
    private EbayRacecardJpaRepository ebayRepository;

    @Test
    public void findSalisburyRacecards()  {
        RacecardSearch search = new RacecardSearch();
        search.setCourse("Salisbury");
        search.setYear(2020);
    List<EbayRacecard> racecards = ebayRepository.searchRacecards(search.getTitle(),
            search.getCourse(),
            search.getCountry(),
            search.getMeeting(),
            search.getDayOffset(),
            search.getDayType(),
            search.getSeller(),
            search.getStatus(),
            search.getSaleType(),
            search.getArticleType(),
            search.getYear());
    log.info("Salisbury racecards: {} - {}", racecards.size(),
            racecards.toString());
    }

}
