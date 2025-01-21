package ene.eneform.service;

import ene.eneform.port.in.colours.WikipediaServiceInterface;
import ene.eneform.port.in.mero.ENEColoursEnvironmentInterface;
import ene.eneform.port.in.mero.MeroServiceInterface;
import ene.eneform.port.in.smartform.SmartformEnvironmentInterface;
import ene.eneform.port.in.smartform2025.DeclarationServiceInterface;
import ene.eneform.port.in.smartform2025.HorseServiceInterface;
import ene.eneform.port.in.smartform2025.RunnerServiceInterface;
import ene.eneform.port.out.smartform2025.AdditionalRaceMonthItemRepository;
import ene.eneform.port.out.smartform2025.DailyRaceRepository;
import ene.eneform.port.out.smartform2025.RunRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootApplication
class SpringServiceTestConfig {
@MockBean
    WikipediaServiceInterface wikipediaService;
@MockBean
    MeroServiceInterface meroService;
@MockBean
    HorseServiceInterface horseService;
@MockBean
    RunnerServiceInterface runnerService;
@MockBean
    DeclarationServiceInterface declarationService;
@MockBean
    AdditionalRaceMonthItemRepository additionalRaceMonthItemRepository;
@MockBean
    ENEColoursEnvironmentInterface eneColours;
@MockBean
    SmartformEnvironmentInterface smartformEnvironment;
@MockBean
    DailyRaceRepository dailyRaceRepository;
    @MockBean
    RunRepository runRepository;

}
