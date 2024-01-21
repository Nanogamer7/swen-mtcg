package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.Pair;
import at.nanopenguin.mtcg.application.service.schemas.Card;
import lombok.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Battle {

    @RequiredArgsConstructor
    private enum ElementMod {
        HALF(0.5),
        NONE(1),
        DOUBLE(2);

        public final double percentMod;
    }
    private record FightDTO(Combatant player, Card card) {};
    private record RoundResult(FightDTO winner, FightDTO loser, boolean draw, ElementMod winnerMod, ElementMod loserMod) {};
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
        boolean leftWins;

        int round = 0;
        do {
            this.playRound(++round);
            if (round == 100) return; // don't update stats on draw

            System.out.println(combatants.left().deckSize() + " - " + combatants.right().deckSize() );
        } while ((leftWins = combatants.left().deckSize() > 0) && combatants.right().deckSize() > 0);

        this.combatants.left().updateStats(leftWins);
        this.combatants.right().updateStats(!leftWins);
    }

    private void playRound(int round) {
        RoundResult result = this.fight(
                new FightDTO(
                        this.combatants.left(),
                        this.combatants.left().getAndRemoveCard()),
                new FightDTO(
                        this.combatants.right(),
                        this.combatants.right().getAndRemoveCard()));

        this.log.add(this.createCombatString(round, result));

        result.winner().player().addCard( // return card to winner deck
                result.winner().card());

        if (result.draw()) {
            result.loser().player().addCard( // on draw both players get their card
                    result.loser().card());
            return;
        }

        result.winner.player().addCard( // on lose winner gets losers card to deck
                result.loser().card());
    }

    private boolean isImmune(Card defend, Card attack) {
        if (defend.name().equals("Dragon") && attack.name().endsWith("Goblin")) return true;
        if (defend.name().equals("Wizzard") && attack.name().equals("Ork")) return true;
        if (defend.name().equals("WaterSpell") && attack.name().equals("Knight")) return true;
        if (defend.name().equals("Kraken") && attack.name().endsWith("Spell")) return true;
        if (defend.name().equals("FireElf") && attack.name().equals("Dragon")) return true;

        return false;
    }

    private enum Element {
        NORMAL,
        FIRE,
        WATER;
        private Element[] strong;
        private Element[] weak;

        // improvement: map to get mods straight from this enum

        static {
            NORMAL.strong = new Element[]{WATER};
            NORMAL.weak = new Element[]{FIRE};

            FIRE.strong = new Element[]{NORMAL};
            FIRE.weak = new Element[]{WATER};

            WATER.strong = new Element[]{FIRE};
            WATER.weak = new Element[]{NORMAL};
        }
    }

    private Pair<ElementMod, ElementMod> getElementMod(Card left, Card right) {
        Pair<ElementMod, ElementMod> returnMods = new Pair<>(ElementMod.NONE, ElementMod.NONE);

        if (!left.name().endsWith("Spell") && !right.name().endsWith("Spell")) return returnMods;

        Element leftElement = Element.NORMAL;
        Element rightElement = Element.NORMAL;

        for (val value : Element.values()) {
            if (left.name().toLowerCase().startsWith(value.name().toLowerCase())) leftElement = value;
            if (right.name().toLowerCase().startsWith(value.name().toLowerCase())) rightElement = value;
        }

        final Element finalLeftElement = leftElement; // lambdas
        final Element finalRightElement = rightElement;

        if (Arrays.stream(leftElement.strong).anyMatch(value -> value == finalRightElement)) returnMods.setLeft(ElementMod.DOUBLE);
        else if (Arrays.stream(leftElement.weak).anyMatch(value -> value == finalRightElement)) returnMods.setLeft(ElementMod.HALF);

        if (Arrays.stream(rightElement.strong).anyMatch(value -> value == finalLeftElement)) returnMods.setRight(ElementMod.DOUBLE);
        else if (Arrays.stream(rightElement.weak).anyMatch(value -> value == finalLeftElement)) returnMods.setRight(ElementMod.HALF);

        return returnMods;
    }

    private RoundResult fight(FightDTO left, FightDTO right) {
        if (this.isImmune(left.card(), right.card())) return new RoundResult(left, right, false, ElementMod.NONE, ElementMod.NONE);
        if (this.isImmune(right.card(), left.card())) return new RoundResult(right, left, false, ElementMod.NONE, ElementMod.NONE);

        Pair<ElementMod, ElementMod> dmgMods = getElementMod(left.card(), right.card());

        boolean leftWins = left.card().damage()*dmgMods.left().percentMod > right.card().damage()*dmgMods.right().percentMod;
        return new RoundResult(
                leftWins ? left : right,
                leftWins ? right : left,
                left.card().damage()*dmgMods.left().percentMod == right.card().damage()*dmgMods.right().percentMod,
                leftWins ? dmgMods.left() : dmgMods.right(),
                leftWins ? dmgMods.right() : dmgMods.left());
    }

    private String createCombatString(int round, RoundResult result) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(round)
                .append(": ")
                .append(String.format("%s: %s (%s damage)",
                                result.winner().player().name,
                                result.winner().card().name(),
                                result.winner().card().damage()))
                .append(" vs. ")
                .append(String.format("%s: %s (%s damage)",
                        result.loser().player().name,
                        result.loser().card().name(),
                        result.loser().card().damage()))
                .append(" => ");

        if (result.winnerMod() != ElementMod.NONE || result.loserMod() != ElementMod.NONE) {
            stringBuilder
                    .append(String.format("%s vs. %s",
                            result.winner().card().damage(),
                            result.loser().card().damage()))
                    .append(" -> ")
                    .append(String.format("%s vs. %s",
                            result.winner().card().damage() * result.winnerMod().percentMod,
                            result.loser().card().damage() * result.loserMod().percentMod))
                    .append(" => ");
        }

        if (result.draw()) {
            return stringBuilder.append("Draw").toString();
        }

        stringBuilder
                .append(result.winner().card().name())
                .append(" wins");

        return stringBuilder.toString();
    }
}
