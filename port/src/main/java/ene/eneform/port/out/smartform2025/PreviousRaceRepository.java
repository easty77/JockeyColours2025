package ene.eneform.port.out.smartform2025;

import ene.eneform.domain.smartform2025.PreviousRace;
import ene.eneform.port.ReadWriteRepository;

import java.util.List;

public interface PreviousRaceRepository extends ReadWriteRepository<PreviousRace, Integer> {
    List<PreviousRace> searchPreviousRaces(String title, String course, String raceType, String winner,
                                           Integer year, Integer quarter,
                                           Integer month, Integer month1, Integer month2, String half,
                                           String grade, Integer age,
                                           Integer money, Integer furlongs);

}
