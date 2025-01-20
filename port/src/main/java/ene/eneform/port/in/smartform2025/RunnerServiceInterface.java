package ene.eneform.port.in.smartform2025;

import ene.eneform.port.out.smartform2025.model.Runner;

import java.util.List;

public interface RunnerServiceInterface {
    List<Runner> findRunnersByRaceId(Integer raceId);
}
