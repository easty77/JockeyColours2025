package ene.eneform.service.colours.service;

import ene.eneform.domain.colours.BasicRaceInfo;
import ene.eneform.domain.colours.ColourRunner;
import ene.eneform.port.out.colours.ColourRunnerRepository;
import ene.eneform.service.smartform.factory.SmartformRunnerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColourRunnerService {
    private final ColourRunnerRepository runnerRepository;

    List<ColourRunner> findByRaceAndNumRunners(BasicRaceInfo race, int numRunners) {
        String rcpVersion = SmartformRunnerFactory.sm_RCPVersion;
      if ("SF".equals(race.getSource())) {
          return runnerRepository.findSmartformByRaceId(race.getRaceId(), numRunners, rcpVersion);
      } else {
          return runnerRepository.findAdditionalBySourceAndRaceId(race.getSource(), race.getRaceId(), numRunners, rcpVersion);
      }
     }

}
