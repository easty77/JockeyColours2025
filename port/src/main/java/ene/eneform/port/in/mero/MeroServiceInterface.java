package ene.eneform.port.in.mero;

import ene.eneform.port.out.mero.model.ENEParsedRacingColours;

import java.awt.*;

public interface MeroServiceInterface {
    String generateSVGContentFromDescription(String description);
    void generateSVGFileFromDescription(String description, String directory, String fileName, String backgroundColour, Point capOrigin, boolean compress);
    String generateSVGContentFromDefinition(String definition, String backgroundColour, Point capOrigin, boolean compress);
    String generateSVGContentFromDefinition(String strDefinition, String backgroundColour);
    String generateSVGContentFromDefinition(String definition);
    void generateSVGFileFromDefinition(String definition, String directory, String fileName, String backgroundColour, Point capOrigin, boolean compress);
    void generatePNG(String definition, String directory, String bileName, String backgroundColour, Point capOrigin, boolean compress);
    String parseDescription(String description);
    ENEParsedRacingColours createParsedRacingColours(String language, String description, String owner);
}
