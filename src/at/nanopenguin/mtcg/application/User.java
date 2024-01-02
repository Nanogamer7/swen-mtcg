package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.application.service.schemas.UserCredentials;
import at.nanopenguin.mtcg.db.DbQuery;
import at.nanopenguin.mtcg.db.SqlCommand;
import at.nanopenguin.mtcg.db.Table;

import java.sql.SQLException;

public class User {
    public static int create(UserCredentials userCredentials) throws SQLException {
        return DbQuery.builder()
                .command(SqlCommand.INSERT)
                .table(Table.USERS)
                .parameter("username", userCredentials.username())
                .parameter("password", userCredentials.password())
                .executeUpdate();

    }
}