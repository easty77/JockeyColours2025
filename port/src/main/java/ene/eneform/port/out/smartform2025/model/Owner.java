package ene.eneform.port.out.smartform2025.model;

import ene.eneform.port.out.mero.model.ParseInfo;
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
    private ParseInfo parseInfo;
}
