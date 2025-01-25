package ene.eneform.port.out.smartform2025.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PreviousRaceSearch {
    String title="";
    String course="";
    String raceType="";
    String winner="";
    Integer year = null;
    Integer quarter = null;
    Integer month = null;
    Integer month1 = null;
    Integer month2 = null;
    String half = "";
    String grade = "";
    Integer age = null;
    Integer money = null;
    Integer furlongs = null;
}
