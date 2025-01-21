package ene.eneform.service.mero.service;

import ene.eneform.port.in.mero.ENEColoursEnvironmentInterface;
import ene.eneform.port.out.colours.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class ENEColoursEnvironmentPublicTest {
    @Autowired
    ENEColoursEnvironmentInterface env;

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
    void resetEnvironment() {

        env.reset();
    }
}
