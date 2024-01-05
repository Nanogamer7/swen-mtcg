package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.application.service.schemas.Card;
import at.nanopenguin.mtcg.db.DbQuery;
import at.nanopenguin.mtcg.db.SqlCommand;
import at.nanopenguin.mtcg.db.Table;
import lombok.val;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class Package {
    public static boolean create(List<Card> cards) throws SQLException {
        // TODO: assert package size == 5

        val result = DbQuery.builder()
                .command(SqlCommand.INSERT)
                .table(Table.PACKAGES)
                .parameter("cost", 5)
                .returnColumn("uuid")
                .executeQuery();
        if (result.isEmpty()) throw new SQLException(); // maybe change to different exception
        if (!result.get(0).containsKey("uuid")) throw new SQLException();

        UUID packageUuid = (UUID) result.get(0).get("uuid");


        for (val card : cards) {
            val query = DbQuery.builder()
                    .command(SqlCommand.INSERT)
                    .table(Table.CARDS);
            if (card.id() != null) query.parameter("uuid", card.id());
            if (query.parameter("damage", card.damage())
                    .parameter("name", card.name())
                    .parameter("package", packageUuid)
                    .executeUpdate() != 1){
                return false;
            }
        }

        return true;
    }
}
