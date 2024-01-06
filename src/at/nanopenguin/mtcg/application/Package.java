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

    public static boolean addToUser(UUID userUuid) throws SQLException {
        return DbQuery.builder()
                .customSql("""
                        DO $$
                        DECLARE user_uuid uuid;
                        DECLARE package_uuid uuid;
                        DECLARE cost int;
                        BEGIN
                            cost = ?;
                            user_uuid = ?::uuid;
                            IF (SELECT coins FROM users WHERE uuid = user_uuid) >= cost THEN
                                package_uuid = (SELECT uuid FROM packages ORDER BY created_at LIMIT 1);
                                UPDATE cards SET owner = user_uuid WHERE package = package_uuid;
                                UPDATE users SET coins = coins - cost WHERE uuid = user_uuid;
                                DELETE FROM packages WHERE uuid = package_uuid;
                            END IF;
                        END $$;""")
                .value(5) // TODO: don't hardcode cost
                .value(userUuid)
                .executeUpdate() > 0;
    }
}
