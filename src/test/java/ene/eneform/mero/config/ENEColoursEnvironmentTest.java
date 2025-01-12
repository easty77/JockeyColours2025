package ene.eneform.mero.config;

import ene.eneform.JockeyColours2025Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = JockeyColours2025Application.class)
public class ENEColoursEnvironmentTest {
    @Autowired
    ENEColoursEnvironment env;
    @Test
    void resetEnvironment() {
        env.reset();
    }
}
