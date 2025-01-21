package ene.eneform.port.out.mero.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ENEParsedRacingColours {
    private final String language;
    private final String description;
    private final String owner;
    private ParseInfo parseInfo = null;

    public ENEParsedRacingColours(String language, String description, String owner) {
        this.language = language;
        this.description = description;
        this.owner = owner;
    }

}
