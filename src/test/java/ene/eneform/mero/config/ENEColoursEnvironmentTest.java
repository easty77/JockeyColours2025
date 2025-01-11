package ene.eneform.mero.config;

import ene.eneform.mero.config.ENEColoursEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class ENEColoursEnvironmentTest {
    @Autowired
    ENEColoursEnvironment env;
    @Test
    void initialise() {
        log.info(env.toString());
    }
}
