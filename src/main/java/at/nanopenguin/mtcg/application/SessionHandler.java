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
    private final Map<UUID, UserInfo> sessions = new HashMap<>();

    public synchronized UUID login(UserCredentials userCredentials) throws SQLException { // avoid multiple logins of same user

        val result = DbQuery.builder()
                .command(SqlCommand.SELECT)
                .table(Table.USERS)
                .column("uuid")
                .column("password")
                .column("admin")
                .condition("username", userCredentials.username())
                .executeQuery();
        if (result.isEmpty()) {
            // user not found
            return null;
        }

        val row1 = result.get(0);
        if (!row1.get("password").equals(userCredentials.password())) {
            // wrong password
            return null;
        }

        for (val session : this.sessions.entrySet()) {
            if (userCredentials.username().equals(session.getValue().username())) {
                this.sessions.remove(session.getKey());
            }
        }

        UUID uuid = UUID.randomUUID();
        this.sessions.put(uuid, new UserInfo((UUID) row1.get("uuid"), userCredentials.username(), (boolean) row1.get("admin")));
        return uuid;
    }

    public static UUID tokenFromHttpHeader(String headerValue) {
        try {
            return headerValue == null ? null : UUID.fromString(headerValue.replaceFirst("^Bearer ", ""));
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public TokenValidity verifyUUID(UUID uuid) {
        return verifyUUID(uuid, false);
    }

    public TokenValidity verifyUUID(UUID uuid, boolean requireAdmin) {
        if (uuid == null) return TokenValidity.MISSING;
        if (!sessions.containsKey(uuid)) return TokenValidity.INVALID;
        if (sessions.get(uuid).admin() || !requireAdmin) return TokenValidity.VALID;
        return TokenValidity.FORBIDDEN;
    }

    public TokenValidity verifyUUID(UUID uuid, String username) {
        return verifyUUID(uuid, username, false);
    }

    public TokenValidity verifyUUID(UUID uuid, String username, boolean allowAdmin) {
        if (uuid == null) return TokenValidity.MISSING;
        if (!sessions.containsKey(uuid)) return TokenValidity.INVALID;
        if (username.equals(sessions.get(uuid).username())) return TokenValidity.VALID;
        if (allowAdmin && sessions.get(uuid).admin()) return TokenValidity.VALID;
        return TokenValidity.FORBIDDEN;

    }

    public UUID userUuidFromToken(UUID token) {
        return this.sessions.containsKey(token) ? this.sessions.get(token).id() : null;
    }
}