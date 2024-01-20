package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.Pair;
import at.nanopenguin.mtcg.application.service.schemas.Card;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Battle {
    private record RoundResult(String winnerName, Card winnerCard, String loserName, Card loserCard, boolean draw) {};
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

        do {
            this.playRound();
        } while (combatants.left().deckSize() > 0 && combatants.right().deckSize() > 0);

        // placeholder
        boolean leftWins = new Random().nextBoolean();
        this.combatants.left().updateStats(leftWins);
        this.combatants.right().updateStats(!leftWins);
    }

    private void playRound() {
        Pair<Card, Card> cards = new Pair<>(this.combatants.left().getCard(), this.combatants.right().getCard());
        //RoundResult result = this.fight(cards.left(), cards.right());
        //this.log.add("");
    }

    private boolean fight(Card left, Card right) {
        return true;
    }

    private String createCombatString(RoundResult result) {
        return "";
    }
}
