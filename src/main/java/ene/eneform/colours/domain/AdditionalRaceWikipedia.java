package ene.eneform.colours.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="additional_race_wikipedia")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class AdditionalRaceWikipedia {
    @EmbeddedId
    private final AdditionalRaceWikipediaId id;
    @Column(name="arw_wikipedia_ref")
    String href;
}
