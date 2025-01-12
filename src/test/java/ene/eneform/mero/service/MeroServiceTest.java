package ene.eneform.mero.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeroServiceTest {
    @Autowired
    private final MeroService meroService;
    private static String COLOURS = "red, blue star, green cap, white diamonds";
    private static String COLOURS1 = "black, royal blue star, white sleeves, black spots, green cap, white diamonds";
    private static String COLOURS4 = "Red and yellow hoops, quartered cap";
    private static String DIRECTORY = "/users/simoneast/";
    private static String FILENAME = "colours";

    @Test
    void parseDescription() {
        String output = meroService.parseDescription(COLOURS4);
        log.info(output);
        assertEquals(output.split("\\|").length, 3);
    }
    @Test
    void generateSVGFromDescription() {
        meroService.generateSVGFileFromDescription(COLOURS4, DIRECTORY, FILENAME, "white", new Point(0, 5), false);
    }
}
