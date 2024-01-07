package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.application.SessionHandler;
import at.nanopenguin.mtcg.application.TokenValidity;
import at.nanopenguin.mtcg.application.Trade;
import at.nanopenguin.mtcg.application.service.schemas.TradingDeal;
import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

import java.sql.SQLException;
import java.util.UUID;

public class TradingService implements Service {
    @Override
    public Response handleRequest(HttpRequest request) throws JsonProcessingException, ArrayIndexOutOfBoundsException, SQLException {

        UUID authToken = SessionHandler.tokenFromHttpHeader(request.getHttpHeader("Authorization"));
        if (SessionHandler.getInstance().verifyUUID(authToken) != TokenValidity.VALID)
            return new Response(HttpStatus.UNAUTHORIZED);
        UUID userUuid = SessionHandler.getInstance().userUuidFromToken(authToken);

        if (request.getPath().split("/")[1].equals("tradings")) {
            return switch (request.getMethod()) {
                case GET -> {
                    val result = Trade.get();
                    if (result.length == 0) yield new Response(HttpStatus.NO_CONTENT);
                    yield new Response(HttpStatus.OK, new ObjectMapper().writeValueAsString(result));
                }
                case POST -> {
                    if (request.getPath().split("/").length < 3) {
                        try {
                            yield new Response(Trade.addTrade(new ObjectMapper().readValue(request.getBody(), TradingDeal.class), userUuid) ?
                                    HttpStatus.CREATED : HttpStatus.FORBIDDEN);
                        } catch (SQLException e) {
                            if (!e.getSQLState().equals("23505"))
                                throw e;
                            yield new Response(HttpStatus.CONFLICT);
                        }
                    }

                    try {
                        yield new Response(Trade.acceptTrade(
                                UUID.fromString(request.getPath().split("/")[3]),
                                UUID.fromString(request.getBody()),
                                userUuid) ?
                                HttpStatus.OK :
                                HttpStatus.FORBIDDEN);
                    }
                    catch (NullPointerException e) {
                        yield new Response(HttpStatus.NOT_FOUND);
                    }
                }
                case DELETE -> {
                    try {
                        yield new Response(Trade.removeTrade(
                                UUID.fromString(request.getPath().split("/")[3]),
                                userUuid) ?
                                HttpStatus.OK :
                                HttpStatus.FORBIDDEN);
                    }
                    catch (NullPointerException e) {
                        yield new Response(HttpStatus.NOT_FOUND);
                    }
                }
                default -> new Response(HttpStatus.NOT_FOUND);
            };
        }

        return new Response(HttpStatus.NOT_FOUND);
    }
}