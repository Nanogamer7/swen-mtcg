package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.Pair;
import at.nanopenguin.mtcg.application.service.schemas.Card;
import at.nanopenguin.mtcg.db.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Combatant {
    private final UUID userUuid;
    private List<Card> deck = new ArrayList<Card>();

    public Combatant(UUID userUuid) throws SQLException {
        this.userUuid = userUuid;
        this.deck = Arrays.asList(UserCards.get(userUuid, true));
    }

    public void updateStats(boolean win) throws SQLException {
        DbQuery.builder()
                .command(SqlCommand.UPDATE)
                .table(Table.USERS)
                .parameter(win ? "wins = wins" : "losses = losses", new Pair<Integer, SqlOperator>(1, SqlArithmeticOperators.ADD))
                .parameter("elo = elo", new Pair<Integer, SqlOperator>(
                        win ? 3 : 5,
                        win ? SqlArithmeticOperators.ADD : SqlArithmeticOperators.SUBTRACT)) // stupid hack to use my own code
                .condition("uuid", userUuid)
                .executeUpdate();
    };
}
