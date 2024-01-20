package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.application.SessionHandler;
import at.nanopenguin.mtcg.application.TokenValidity;
import at.nanopenguin.mtcg.application.User;
import at.nanopenguin.mtcg.application.UserCards;
import at.nanopenguin.mtcg.http.HttpMethod;
import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CardsService implements Service {

    @Override
    public Response handleRequest(HttpRequest request) throws JsonProcessingException, SQLException, ArrayIndexOutOfBoundsException {

        UUID authToken = SessionHandler.tokenFromHttpHeader(request.getHttpHeader("Authorization"));
        if (SessionHandler.getInstance().verifyUUID(authToken) != TokenValidity.VALID)
            return new Response(HttpStatus.UNAUTHORIZED);
        UUID userUuid = SessionHandler.getInstance().userUuidFromToken(authToken);

        if (request.getPath().split("/")[1].equals("cards") && request.getMethod() == HttpMethod.GET) {
            val result = UserCards.get(userUuid, false);
            if (result.length == 0) return new Response(HttpStatus.NO_CONTENT);
            return new Response(HttpStatus.OK, new ObjectMapper().writeValueAsString(result));
        }

        if (request.getPath().split("/")[1].equals("deck")) {
            return switch (request.getMethod()) {
                case GET -> {
                    val result = UserCards.get(userUuid, true);
                    if (result.length == 0) yield new Response(HttpStatus.NO_CONTENT);
                    yield new Response(HttpStatus.OK, new ObjectMapper().writeValueAsString(result));
                }
                case PUT -> {
                    UUID[] cards = new ObjectMapper().readValue(request.getBody(), UUID[].class);
                    if (cards.length != 4)
                        yield new Response(HttpStatus.BAD_REQUEST);
                    yield new Response(UserCards.setDeck(cards, userUuid) ? HttpStatus.OK : HttpStatus.FORBIDDEN);
                }

                default -> new Response(HttpStatus.NOT_FOUND);
            };
        }

        return new Response(HttpStatus.NOT_FOUND);
    }
}
