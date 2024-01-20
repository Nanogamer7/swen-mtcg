package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.Pair;
import at.nanopenguin.mtcg.application.service.schemas.Card;
import at.nanopenguin.mtcg.db.DbQuery;
import at.nanopenguin.mtcg.db.SqlCommand;
import at.nanopenguin.mtcg.db.Table;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

import java.sql.SQLException;
import java.util.*;

public class Package {
    public static boolean create(List<Card> cards) throws SQLException {
        // TODO: assert package size == 5

        val result = DbQuery.builder()
                .command(SqlCommand.INSERT)
                .table(Table.PACKAGES)
                .parameter("uuid", UUID.randomUUID())
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

    public synchronized static Pair<PurchaseStatus, List<Card>> addToUser(UUID userUuid) throws SQLException {
        int coins = (int) DbQuery.builder()
                .command(SqlCommand.SELECT)
                .table(Table.USERS)
                .column("coins")
                .condition("uuid", userUuid)
                .executeQuery()
                .get(0)
                .get("coins");
        if (coins < 5) {
            return new Pair<>(PurchaseStatus.NOT_ENOUGH_MONEY, null);
        }

        if ((long) DbQuery.builder()
                .command(SqlCommand.SELECT)
                .table(Table.PACKAGES)
                .column("COUNT(*)")
                .executeQuery()
                .get(0)
                .get("count") == 0) {
            return new Pair<>(PurchaseStatus.NO_PACKAGE_AVAILABLE, null);
        }

        val result = DbQuery.builder()
                .customSql("""
                UPDATE cards
                SET owner = (?::uuid), package = null
                WHERE package = (
                    SELECT uuid
                    FROM packages
                    ORDER BY created_at
                    LIMIT 1
                )
                RETURNING uuid AS id, name, damage;
                """)
                .value(userUuid)
                .executeQuery();

        DbQuery.builder()
                .command(SqlCommand.UPDATE)
                .table(Table.USERS)
                .parameter("coins", coins - 5)
                .condition("uuid", userUuid)
                .executeUpdate();

        return new Pair<>(PurchaseStatus.SUCCESS, new ObjectMapper().convertValue(result, new TypeReference<List<Card>>() {}));
    }
}
