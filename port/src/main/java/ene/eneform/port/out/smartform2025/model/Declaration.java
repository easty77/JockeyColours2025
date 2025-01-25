package ene.eneform.port.out.smartform2025.model;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Declaration {
    Horse horse;
    Jockey jockey;
    Trainer trainer;
    Owner owner;
    Tack tack;
    Form form;
    Odds odds;
    Handicap handicap;
    public static class Tack {

    }
    public static class Form {

    }
    public static class Odds {

    }
    public static class Handicap {

    }
}
