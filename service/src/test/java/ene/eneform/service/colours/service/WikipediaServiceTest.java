package ene.eneform.service.colours.service;

import ene.eneform.port.in.colours.WikipediaServiceInterface;
import ene.eneform.port.out.colours.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Slf4j
@SpringBootTest
public class WikipediaServiceTest {
    private static String COLOURS = "Red, blue star, green cap, white diamonds";
    private static String OWNER_NAME="Mr K Abdulla";
private static String RACE_NAME="Derby";
private static Integer RACE_YEAR = 2010;
    @Autowired
    private WikipediaServiceInterface service;

    @MockitoBean
    AdditionalRaceLinkRepository arlRepository;
    @MockitoBean
    BasicRaceRepository raceRepository;
    @MockitoBean
    ColourRunnerRepository runnerRepository;
    @MockitoBean
    RacingColoursParseRepository rcpRepository;
    @MockitoBean
    UnregisteredColourSyntaxRepository ucsRepository;
    @MockitoBean
    WikipediaImageRepository wiRepository;

    @Test
    void getOwnerFileName() {
        String output = service.getOwnerFileName(OWNER_NAME);
        log.info(output);
    }
    @Test
    void createImageContent() {

            String output = service.createImageContent(COLOURS, "en", false);
            log.info(output);
    }
    @Test
    void generateRace() {
        String content = service.generateRace(RACE_NAME, RACE_YEAR, "en", "br />");
        log.info(content);
    }

}
