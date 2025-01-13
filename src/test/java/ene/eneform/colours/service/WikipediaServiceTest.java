package ene.eneform.colours.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@Slf4j
@SpringBootTest
public class WikipediaServiceTest {
    private static String COLOURS = "Red, blue star, green cap, white diamonds";
    private static String OWNER_NAME="Mr K Abdulla";

    @Autowired
    private WikipediaService service;

    @Test
    void getOwnerFileName() {
        String output = service.getOwnerFileName(OWNER_NAME);
        log.info(output);
    }
    @Test
    void createImageContent() {
        try {
            String output = service.createImageContent(COLOURS, "en", false);
            log.info(output);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

}
