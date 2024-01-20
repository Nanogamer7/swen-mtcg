package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.Pair;
import at.nanopenguin.mtcg.application.service.schemas.Card;
import at.nanopenguin.mtcg.db.*;

import java.sql.SQLException;
import java.util.*;

public class Combatant {

    static class RandomList<E> extends ArrayList<E> {
        Random r = new Random();
        public E popRandom() {
            int i = r.nextInt(this.size());
            E e = this.get(i);
            this.remove(i);
            return e;
        }
    }
    private final UUID userUuid;
    public final String name;
    private RandomList<Card> deck = new RandomList<>();

    public Combatant(UUID userUuid) throws SQLException {
        this.userUuid = userUuid;
        this.name = (String) DbQuery.builder()
                .command(SqlCommand.SELECT)
                .table(Table.USERS)
                .column("name")
                .condition("uuid", userUuid)
                .executeQuery()
                .get(0)
                .get("name");
        this.deck.addAll(Arrays.asList(UserCards.get(userUuid, true)));
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

    public Card getCard() {
        return this.deck.popRandom();
    }

    public void addCard(Card card) {
        this.deck.add(card);
    }

    public int deckSize() {
        return this.deck.size();
    }
}
