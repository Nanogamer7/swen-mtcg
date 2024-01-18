package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.application.*;
import at.nanopenguin.mtcg.http.HttpMethod;
import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.UUID;

public class GameService implements Service {

    @Override
    public Response handleRequest(HttpRequest request) throws JsonProcessingException, SQLException {

        UUID authToken = SessionHandler.tokenFromHttpHeader(request.getHttpHeader("Authorization"));
        if (SessionHandler.getInstance().verifyUUID(authToken) != TokenValidity.VALID)
            return new Response(HttpStatus.UNAUTHORIZED);
        UUID userUuid = SessionHandler.getInstance().userUuidFromToken(authToken);

        if (request.getPath().split("/")[1].equals("stats") && request.getMethod() == HttpMethod.GET) {
            return new Response(HttpStatus.OK, new ObjectMapper().writeValueAsString(User.getStats(userUuid)));
        }

        if (request.getPath().split("/")[1].equals("scoreboard") && request.getMethod() == HttpMethod.GET) {
            return new Response(HttpStatus.OK, new ObjectMapper().writeValueAsString(User.scoreboard()));
        }

        if (request.getPath().split("/")[1].equals("battles") && request.getMethod() == HttpMethod.POST) {
            return new Response(HttpStatus.OK,
                    "text/plain",
                    String.join(System.lineSeparator(), BattleHandler.getInstance().startBattle(userUuid)));
        }

        return new Response(HttpStatus.NOT_FOUND);
    }
}
