package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.application.service.schemas.UserCredentials;
import at.nanopenguin.mtcg.db.DbQuery;
import at.nanopenguin.mtcg.db.SqlCommand;
import at.nanopenguin.mtcg.db.Table;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SessionHandler {
     private static SessionHandler INSTANCE;
     private final Map<UUID, Integer> Sessions = new HashMap<>();

     private SessionHandler() {

     }

     public static SessionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionHandler();
        }

        return INSTANCE;
     }

     public UUID login(UserCredentials userCredentials) throws SQLException {
         val result = DbQuery.builder()
                 .command(SqlCommand.SELECT)
                 .table(Table.USERS)
                 .column("id")
                 .column("password")
                 .condition("username", userCredentials.username())
                 .executeQuery();
         if (result.isEmpty()) {
             // user not found
             return null;
         }
         if (!result.get(0).get("password").equals(userCredentials.password())) {
             // wrong password
             return null;
         }

         UUID uuid = UUID.randomUUID();
         this.Sessions.put(uuid, (Integer) result.get(0).get("id"));
         return uuid;
     }
}
