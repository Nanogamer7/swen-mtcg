package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.application.service.schemas.TradingDeal;
import at.nanopenguin.mtcg.application.service.schemas.UserCredentials;
import at.nanopenguin.mtcg.application.service.schemas.UserData;
import at.nanopenguin.mtcg.application.service.schemas.UserStats;
import at.nanopenguin.mtcg.db.DbQuery;
import at.nanopenguin.mtcg.db.SqlCommand;
import at.nanopenguin.mtcg.db.Table;
import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class User {
    public static boolean create(UserCredentials userCredentials) throws SQLException {
        return DbQuery.builder()
                .command(SqlCommand.INSERT)
                .table(Table.USERS)
                .parameter("username", userCredentials.username())
                .parameter("password", userCredentials.password())
                .executeUpdate() == 1;

    }

    public static boolean update(String username, UserData userData) throws SQLException {
        return DbQuery.builder()
                .command(SqlCommand.UPDATE)
                .table(Table.USERS)
                .parameter("name", userData.name())
                .parameter("bio", userData.bio())
                .parameter("image", userData.image())
                .condition("username", username)
                .executeUpdate() == 1;
    }

    public static UserData retrieve(String username) throws SQLException {
        val result = DbQuery.builder()
                .command(SqlCommand.SELECT)
                .table(Table.USERS)
                .condition("username", username)
                .executeQuery();
        if (result.isEmpty()) return null;
        val row1 = result.get(0);
        return new UserData((String) row1.get("name"), (String) row1.get("bio"), (String) row1.get("image"));
    }

    public static UserStats getStats(UUID userUuid) throws SQLException {
        return new ObjectMapper().convertValue(DbQuery.builder()
                .command(SqlCommand.SELECT)
                .table(Table.USERS)
                .condition("uuid", userUuid)
                .executeQuery()
                .get(0),
                UserStats.class);
    }

    public static UserStats[] scoreboard() throws SQLException {
        val dbQueryBuilder = DbQuery.builder()
                .command(SqlCommand.SELECT)
                .table(Table.USERS)
                .column("name")
                .column("elo")
                .column("wins")
                .column("losses")
                .postfix(" ORDER BY elo");

        ArrayList<UserStats> stats = new ArrayList<>();
        for (val row : dbQueryBuilder.executeQuery()) {
            stats.add(new ObjectMapper().convertValue(row, UserStats.class));
        }
        return stats.toArray(new UserStats[0]);
    }
}