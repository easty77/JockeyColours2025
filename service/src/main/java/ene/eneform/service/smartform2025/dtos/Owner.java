package ene.eneform.service.smartform2025.dtos;

import ene.eneform.service.mero.colours.ENEParsedRacingColours;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Owner {
   private final Integer id;
    private final String name;
    private final String colours;
    private String svgContent;
    private ENEParsedRacingColours parsedRacingColours;
}
