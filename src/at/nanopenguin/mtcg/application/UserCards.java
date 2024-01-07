package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.application.service.schemas.Card;
import at.nanopenguin.mtcg.db.DbQuery;
import at.nanopenguin.mtcg.db.SqlCommand;
import at.nanopenguin.mtcg.db.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class UserCards extends User {
    public static Card[] get(UUID userUuid, boolean deckOnly) throws SQLException {
        val dbQueryBuilder = DbQuery.builder()
                .command(SqlCommand.SELECT)
                .table(Table.CARDS)
                .column("uuid AS id")
                .column("name")
                .column("damage")
                .condition("owner", userUuid);
        if (deckOnly) dbQueryBuilder.condition("deck", true);
        ArrayList<Card> cards = new ArrayList<>();
        for (val row : dbQueryBuilder.executeQuery()) {
            cards.add(new ObjectMapper().convertValue(row, Card.class));
        }
        return cards.toArray(new Card[0]);
    }

    public static boolean setDeck(UUID[] cards, UUID userUuid) throws SQLException {
        if (DbQuery.builder()
                .command(SqlCommand.SELECT)
                .table(Table.CARDS)
                .condition("owner", userUuid)
                .condition("trade", false)
                .condition("uuid", Arrays.asList(cards))
                .executeQuery().size() != 4)
            return false;

        DbQuery.builder()
                .command(SqlCommand.UPDATE)
                .table(Table.CARDS)
                .parameter("deck", false)
                .condition("owner", userUuid)
                .executeUpdate();

        DbQuery.builder()
                .command(SqlCommand.UPDATE)
                .table(Table.CARDS)
                .parameter("deck", true)
                .condition("uuid", Arrays.asList(cards))
                .executeUpdate();

        return true;
    }
}
