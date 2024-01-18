package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.Pair;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Battle {
    private Pair<Combatant, Combatant> combatants;
    @Getter
    private volatile List<String> log = new ArrayList<>();
    @Getter @Setter
    private boolean firstPlayer = true;

    public Battle(Combatant combatant) {
        this.combatants = new Pair<>(combatant, null);
    }

    public void addCombatant(Combatant combatant) {
        this.combatants.setRight(combatant);
    }

    public void start() throws SQLException {

        // placeholder
        boolean leftWins = new Random().nextBoolean();
        this.combatants.left().updateStats(leftWins);
        this.combatants.right().updateStats(!leftWins);

    }
}
