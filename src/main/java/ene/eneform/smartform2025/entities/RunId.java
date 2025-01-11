package ene.eneform.smartform2025.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class RunId implements Serializable {
    public Integer raceId;
    public Integer runnerId;
}