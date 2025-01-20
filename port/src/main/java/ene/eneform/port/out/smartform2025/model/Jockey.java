package ene.eneform.port.out.smartform2025.model;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Jockey {
    Integer id;
    String name;
    Integer claim;
}
