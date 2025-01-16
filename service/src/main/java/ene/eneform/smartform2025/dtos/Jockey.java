package ene.eneform.smartform2025.dtos;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Jockey {
    Integer id;
    String name;
    Integer claim;
}
