package ene.eneform.service.smartform2025.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class EntryId implements Serializable {
    public Integer raceId;
    public Integer runnerId;
}