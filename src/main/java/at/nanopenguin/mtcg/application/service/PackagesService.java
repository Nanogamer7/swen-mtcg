package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.application.Package;
import at.nanopenguin.mtcg.application.PurchaseStatus;
import at.nanopenguin.mtcg.application.SessionHandler;
import at.nanopenguin.mtcg.application.TokenValidity;
import at.nanopenguin.mtcg.application.service.schemas.Card;
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

public class PackagesService implements Service {
    private final SessionHandler sessionHandler;

    public PackagesService(SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    @Override
    public Response handleRequest(HttpRequest request) throws JsonProcessingException, SQLException, ArrayIndexOutOfBoundsException {
        UUID token = SessionHandler.tokenFromHttpHeader(request.getHttpHeader("Authorization"));

        if (request.getPath().split("/")[1].equals("packages") && request.getMethod() == HttpMethod.POST) {
            return switch (this.sessionHandler.verifyUUID(token, true)) {
                case MISSING, INVALID -> new Response(HttpStatus.UNAUTHORIZED);
                case FORBIDDEN -> new Response(HttpStatus.FORBIDDEN);
                case VALID -> new Response(
                        Package.create(new ObjectMapper().readValue(request.getBody(), new TypeReference<List<Card>>() {})) ?
                                HttpStatus.CREATED :
                                HttpStatus.CONFLICT);
            };
        }

        if (String.join("/", Arrays.copyOfRange(request.getPath().split("/"), 1, 3)).equals("transactions/packages") && request.getMethod() == HttpMethod.POST) {
            if (this.sessionHandler.verifyUUID(token) != TokenValidity.VALID) return new Response(HttpStatus.UNAUTHORIZED);
            val result = Package.addToUser(this.sessionHandler.userUuidFromToken(token));
            if (result.left() == PurchaseStatus.SUCCESS) {
                return new Response(HttpStatus.OK, "application/json", new ObjectMapper().writeValueAsString(result.right()));
            }
            return new Response(switch (Package.addToUser(this.sessionHandler.userUuidFromToken(token)).left()) {
                case NO_PACKAGE_AVAILABLE -> HttpStatus.NOT_FOUND;
                case NOT_ENOUGH_MONEY -> HttpStatus.FORBIDDEN;
                default -> HttpStatus.INTERNAL;
            });
        }

        return new Response(HttpStatus.NOT_FOUND);
    }
}
