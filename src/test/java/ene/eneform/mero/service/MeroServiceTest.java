package ene.eneform.mero.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeroServiceTest {
    @Autowired
    private final MeroService meroService;
    private static String COLOURS = "red, blue star, green cap, white diamonds";
    private static String COLOURS1 = "black, royal blue star, white sleeves, black spots, green cap, white diamonds";
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
