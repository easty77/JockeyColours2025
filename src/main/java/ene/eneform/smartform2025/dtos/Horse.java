package ene.eneform.smartform2025.dtos;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Horse {
    Integer id;
    String name;
    String bred;
    LocalDate foalingDate;
    Integer age;
    String colour;
    String gender;
    Breeding breeding;
    @Value
    @AllArgsConstructor
    public static class Breeding {
Ancestor dam;
Ancestor sire;
Ancestor damSire;
    }
    @Value
    @AllArgsConstructor
    public static class Ancestor {
Integer id;
String name;
String bred;
Integer yearBorn;
    }
    public static Horse onCreate(Integer id, String name, String bred,
                                 LocalDate foalingDate, Integer age,
            String colour, String gender, Breeding breeding) {
        return new Horse(id, name, bred, foalingDate,
                age, colour, gender,
                breeding);
    }
    public static Breeding onCreateBreeding(Ancestor dam, Ancestor sire, Ancestor damSire) {
        return new Breeding(dam, sire, damSire);
    }

}
