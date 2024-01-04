package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.application.service.schemas.UserCredentials;
import at.nanopenguin.mtcg.db.DbQuery;
import at.nanopenguin.mtcg.db.SqlCommand;
import at.nanopenguin.mtcg.db.Table;
import lombok.val;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SessionHandler {
     private static SessionHandler INSTANCE;
     private final Map<UUID, UserInfo> Sessions = new HashMap<>();

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
                 .column("admin")
                 .condition("username", userCredentials.username())
                 .executeQuery();
         if (result.isEmpty()) {
             // user not found
             return null;
         }

         val row1 =  result.get(0);
         if (!row1.get("password").equals(userCredentials.password())) {
             // wrong password
             return null;
         }

         UUID uuid = UUID.randomUUID();
         this.Sessions.put(uuid, new UserInfo((int) row1.get("id"), userCredentials.username(), (boolean) row1.get("admin")));
         return uuid;
     }

    public boolean verifyUUID(UUID uuid) {
        return verifyUUID(uuid, false);
    }

     public boolean verifyUUID(UUID uuid, boolean requireAdmin) {
         return Sessions.containsKey(uuid) && (!requireAdmin || Sessions.get(uuid).admin());
     }

     public boolean verifyUUID(UUID uuid, String username) {
        return verifyUUID(uuid, username, false);
     }

    public boolean verifyUUID(UUID uuid, String username, boolean allowAdmin) {
        return Sessions.containsKey(uuid) && (username.equals(Sessions.get(uuid).username()) || (allowAdmin && Sessions.get(uuid).admin()));
    }
}
