package ene.eneform.port.out.smartform2025.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Runner extends Declaration {
    Performance performance;

    public Runner(Horse horse, Jockey jockey, Trainer trainer, Owner owner,
                  Tack tack, Form form, Odds odds, Handicap handicap,
                  Performance performance) {
        super(horse, jockey, trainer, owner, tack, form, odds, handicap);
        this.performance = performance;
    }
    public Runner(Horse horse, Jockey jockey, Trainer trainer, Owner owner) {
        this(horse, jockey, trainer, owner, null, null, null, null, null);
    }
    public static class Performance {

    }
}
