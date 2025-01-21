package ene.eneform.service.mero.service;

import ene.eneform.port.in.mero.MeroServiceInterface;
import ene.eneform.port.out.colours.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
public class MeroServiceTest {
    @Autowired
    private MeroServiceInterface meroService;

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

    private static String COLOURS = "red, blue star, green cap, white diamonds";
    private static String COLOURS1 = "black, royal blue star, white sleeves, black spots, green cap, white diamonds";
    private static String COLOURS4 = "Red and yellow hoops, quartered cap";
    private static String DIRECTORY = "/users/simoneast/";
    private static String FILENAME = "colours";

    @Test
    void parseDescription() {
        String output = meroService.parseDescription(COLOURS4);
        log.info(output);
        assertEquals(output.split("\\|").length, 3);
    }
    @Test
    void generateSVGFromDescription() {
        meroService.generateSVGFileFromDescription(COLOURS4, DIRECTORY, FILENAME, "white", new Point(0, 5), false);
    }


}
