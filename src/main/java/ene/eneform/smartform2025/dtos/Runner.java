package ene.eneform.smartform2025.dtos;

public class Runner extends Declaration {
    Performance performance;

    public Runner(Horse horse, Jockey jockey, Trainer trainer, Owner owner,
                  Tack tack, Form form, Odds odds, Handicap handicap,
                  Performance performance) {
        super(horse, jockey, trainer, owner, tack, form, odds, handicap);
        this.performance = performance;
    }
    public static class Performance {

    }
}
