package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.Pair;
import at.nanopenguin.mtcg.application.service.schemas.Card;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

public class BattleTest {
    private static Card cardWithName(String name) {
        Card cardMock = Mockito.mock(Card.class);
        Mockito.when(cardMock.name()).thenReturn(name);
        return cardMock;
    }

    private static Card cardWithNameAndDmg(String name, float dmg) {
        Card cardMock = Mockito.mock(Card.class);
        Mockito.when(cardMock.name()).thenReturn(name);
        Mockito.when(cardMock.damage()).thenReturn(dmg);
        return cardMock;
    }

    @ParameterizedTest
    @CsvSource({
            "WaterGoblin, FireSpell, DOUBLE, HALF", // generic
            "FireGoblin, NormalSpell, DOUBLE, HALF",
            "NormalGoblin, WaterSpell, DOUBLE, HALF",
            "FireGoblin, WaterSpell, HALF, DOUBLE", // generic reverse
            "NormalGoblin, FireSpell, HALF, DOUBLE",
            "WaterGoblin, NormalSpell, HALF, DOUBLE",
            "WaterGoblin, FireTroll, NONE, NONE", // no spell
            "WaterSpell, FireSpell, DOUBLE, HALF", // two spells
            "FireGoblin, FireSpell, NONE, NONE", // same element
            "FireSpell, Dragon, DOUBLE, HALF" // non elemental
    })
    void elementMods(String name1, String name2, Battle.ElementMod expectedMod1, Battle.ElementMod expectedMod2){
        Card card1 = cardWithName(name1);
        Card card2 = cardWithName(name2);

        Assertions.assertEquals(new Pair<>(expectedMod1, expectedMod2), Battle.getElementMod(card1, card2));
    }

    @ParameterizedTest
    @CsvSource({
            "Dragon, FireGoblin, true",
            "Dragon, WaterGoblin, true",
            "Dragon, NormalGoblin, true",
            "WaterGoblin, Dragon, false",
            "Wizzard, Ork, true",
            "Ork, Wizzard, false",
            "WaterSpell, Knight, true",
            "FireSpell, Knight, false",
            "Knight, WaterSpell, false",
            "Kraken, WaterSpell, true",
            "Kraken, FireSpell, true",
            "FireElf, Dragon, true",
            "NormalElf, Dragon, false"
    })
    void immunityTest(String name1, String name2, boolean expected) {
        Card card1 = cardWithName(name1);
        Card card2 = cardWithName(name2);

        Assertions.assertEquals(expected, Battle.isImmune(card1, card2));
    }

    private static Battle.FightDTO getFightDTO(String cardName, float dmg) {
        Battle.FightDTO fightDTOmock = Mockito.mock(Battle.FightDTO.class);
        Card cardMock = cardWithNameAndDmg(cardName, dmg);
        Mockito.when(fightDTOmock.card()).thenReturn(cardMock);
        return fightDTOmock;
    }

    enum fightOutcome {
        LEFT_WINS,
        DRAW,
        RIGHT_WINS
    }

    @ParameterizedTest
    @CsvSource({
            "WaterGoblin, 10, FireTroll, 15, RIGHT_WINS, NONE, NONE",
            "FireTroll, 15, WaterGoblin, 10, LEFT_WINS, NONE, NONE",
            "FireSpell, 10, WaterSpell, 20, RIGHT_WINS, DOUBLE, HALF",
            "FireSpell, 20, WaterSpell, 5, DRAW, HALF, DOUBLE",
            "FireSpell, 90, WaterSpell, 5, LEFT_WINS, HALF, DOUBLE",
            "FireSpell, 10, WaterGoblin, 10, RIGHT_WINS, DOUBLE, HALF",
            "WaterSpell, 10, WaterGoblin, 10, DRAW, NONE, NONE",
            "RegularSpell, 10, WaterGoblin, 10, LEFT_WINS, DOUBLE, HALF",
            "RegularSpell, 10, Knight, 15, RIGHT_WINS, NONE, NONE",
    })
    void fightTest(String card1, float dmg1, String card2, float dmg2, fightOutcome outcome, Battle.ElementMod expectedMod1, Battle.ElementMod expectedMod2) {
        Battle.FightDTO fightDTO1 = getFightDTO(card1, dmg1);
        Battle.FightDTO fightDTO2 = getFightDTO(card2, dmg2);

        Battle.RoundResult result = Battle.fight(fightDTO1, fightDTO2, false);

        switch (outcome) {
            case LEFT_WINS -> {
                Assertions.assertEquals(fightDTO1, result.winner());
                Assertions.assertFalse(result.draw());
            }
            case DRAW -> {
                Assertions.assertTrue(result.draw());
            }
            case RIGHT_WINS -> {
                Assertions.assertEquals(fightDTO2, result.winner());
                Assertions.assertFalse(result.draw());
            }
        }

        Assertions.assertEquals(expectedMod1, result.winnerMod());
        Assertions.assertEquals(expectedMod2, result.loserMod());
    }


}
