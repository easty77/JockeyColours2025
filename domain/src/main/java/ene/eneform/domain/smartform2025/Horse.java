package ene.eneform.domain.smartform2025;

import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public class Horse {
    public String name;
    public LocalDate foaling_date;
    public Integer age;
    public String colour;
    public String gender;
    public String bred;
}
