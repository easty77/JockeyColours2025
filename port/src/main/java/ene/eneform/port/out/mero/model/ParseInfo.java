package ene.eneform.port.out.mero.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor

public class ParseInfo {
        private final String description;
    private String definition = "";
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
