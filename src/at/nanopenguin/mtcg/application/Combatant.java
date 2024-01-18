package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.application.service.schemas.Card;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Combatant {
    private UUID userUuid;
    private List<Card> deck = new ArrayList<Card>();

    public Combatant(UUID userUuid) throws SQLException {
        this.userUuid = userUuid;
        this.deck = Arrays.asList(UserCards.get(userUuid, true));
    }
}
