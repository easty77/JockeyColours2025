package ene.eneform.domain.colours;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class RacingColoursParseId implements Serializable {
    @Column(name = "rcp_description")
    public final String description;
    @Column(name = "rcp_version")
    public final String version;
}