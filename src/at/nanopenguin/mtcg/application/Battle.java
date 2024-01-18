package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.Pair;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Battle {
    private Pair<Combatant, Combatant> combatants;
    @Getter
    private volatile List<String> log = new ArrayList<>();
    @Getter
    private boolean waiting = true;

    public Battle(Combatant combatant) {
        this.combatants = new Pair<>(combatant, null);
    }

    public void addCombatant(Combatant combatant) {
        this.combatants.setRight(combatant);
    }

    public void start() {

    }
}
