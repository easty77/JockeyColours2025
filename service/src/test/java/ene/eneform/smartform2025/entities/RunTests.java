package ene.eneform.smartform2025.entities;

import ene.eneform.smartform2025.repositories.DailyRaceRepository;
import ene.eneform.smartform2025.repositories.RunRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class RunTests {
    @Autowired
    private RunRepository runRepository;

    @Test
    public void findRunnersByName()  {
        List<Run> runs = runRepository.findByHorseName("Frankel");
        System.out.println(runs.size());
    }
    @Test
    public void findRunnersByName2()  {
        List<Run> runs = runRepository.findRunsByHorseName("Frankel");
        System.out.println(runs.size());
        runs.forEach(System.out::println);
    }
}
