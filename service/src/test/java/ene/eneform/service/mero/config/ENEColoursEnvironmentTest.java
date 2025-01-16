package ene.eneform.service.mero.config;

import ene.eneform.service.mero.config.ENEColoursEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ENEColoursEnvironmentTest {
    @Autowired
    ENEColoursEnvironment env;
    @Test
    void resetEnvironment() {
        env.reset();
    }
}
