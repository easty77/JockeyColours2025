package ene.eneform.mero.config;

import ene.eneform.mero.config.ENEColoursEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
public class ENEColoursEnvironmentTest {
    @Test
    void initialise() {
        ENEColoursEnvironment env = ENEColoursEnvironment.getInstance();
        log.info(env.toString());
    }
}
