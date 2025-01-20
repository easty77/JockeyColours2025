package ene.eneform.port.out.smartform2025.model;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
@RequiredArgsConstructor
public class Entry {
    Declaration declaration;
    Race race;
}
