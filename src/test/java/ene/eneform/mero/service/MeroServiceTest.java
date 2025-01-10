package ene.eneform.mero.service;

import ene.eneform.mero.service.MeroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.awt.*;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeroServiceTest {
    @Autowired
        private final MeroServiceInterface meroService;
        private static String COLOURS = "Red, blue star, green cap, white diamonds";
        private static String DIRECTORY = "/users/simoneast/";
        private static String FILENAME = "colours";

        @Test
        void parseDescription() {
            String output = meroService.parseDescription(COLOURS);
            log.info(output);
        }
        @Test
        void generateSvg() {
            meroService.generateSVG(meroService.parseDescription(COLOURS), DIRECTORY, FILENAME, "white", new Point(0, 5), false);
        }
}
