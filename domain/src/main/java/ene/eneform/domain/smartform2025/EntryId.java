package ene.eneform.domain.smartform2025;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class EntryId implements Serializable {
    public Integer raceId;
    public Integer runnerId;
}