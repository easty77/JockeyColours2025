package ene.eneform.smartform2025.dtos;

import lombok.Value;

import java.time.LocalDate;

@Value
public class Meeting {
    Integer id;
    LocalDate date;
}
