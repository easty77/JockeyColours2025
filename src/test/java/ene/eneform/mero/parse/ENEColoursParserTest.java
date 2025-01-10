package ene.eneform.mero.parse;

import ene.eneform.mero.colours.ENERacingColours;
import ene.eneform.mero.parse.ENEColoursParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
public class ENEColoursParserTest {
    private static String COLOURS = "Red, blue star, green cap, white diamonds";
    private static String OWNER_NAME="Mr K Abdulla";

    @Test
    public void test() {
        ENEColoursParser parser = new ENEColoursParser("en", COLOURS, OWNER_NAME);
        ENERacingColours colours = parser.parse();
        log.info(parser.getSyntax());
        log.info(colours.getDefinition());
    }
}
