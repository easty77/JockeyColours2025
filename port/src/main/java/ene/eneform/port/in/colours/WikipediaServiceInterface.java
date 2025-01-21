package ene.eneform.port.in.colours;

public interface WikipediaServiceInterface {
    String generateWikipediaOwner(String strOwnerName, String strDescription, String strComment, String strLanguage, boolean bCompress, boolean bOverwrite);
    String generateRace(String raceName, Integer year, String language, String lineBreak);
    String generateRaceSequence(String raceName, String language, String lineBreak);
    String generateRace(Integer raceId, String source, String language, String lineBreak);
    String generateRace(String raceName, String language, String lineBreak);
    String getOwnerFileName(String ownerName);
    String createImageContent(String colours, String language, boolean compress);
}
