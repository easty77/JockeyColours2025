package ene.eneform.service.smartform2025.entities;

import ene.eneform.service.smartform2025.entities.Run;
import ene.eneform.service.smartform2025.repositories.RunRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
