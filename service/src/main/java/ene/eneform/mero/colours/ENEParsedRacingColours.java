package ene.eneform.mero.colours;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ENEParsedRacingColours {
    private final String language;
    private final String description;
    private final String owner;
    private ENERacingColours colours;
    private ParseInfo parseInfo = null;

    public ENEParsedRacingColours(String language, String description, String owner) {
        this.language = language;
        this.description = description;
        this.owner = owner;
    }
    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class ParseInfo {
        private final String description;
        private String expanded = "";
        private String remainder = "";
        private String syntax = "";

        public void addSyntax(String add)
        {
            if (!"".equals(this.syntax))
                syntax += "-";
            this.syntax += add;
        }
    }
}
