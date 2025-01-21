package ene.eneform.adaptor.db.smartform2025;

import ene.eneform.domain.smartform2025.Run;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class RunJpaRepositoryTest {
    @Autowired
    private RunJpaRepository runRepository;

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
