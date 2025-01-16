package ene.eneform.smartform2025.dtos;

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
