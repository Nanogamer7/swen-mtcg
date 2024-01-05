package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.application.SessionHandler;
import at.nanopenguin.mtcg.application.User;
import at.nanopenguin.mtcg.application.service.schemas.UserCredentials;
import at.nanopenguin.mtcg.application.service.schemas.UserData;
import at.nanopenguin.mtcg.db.DbQuery;
import at.nanopenguin.mtcg.db.SqlCommand;
import at.nanopenguin.mtcg.db.Table;
import at.nanopenguin.mtcg.http.HttpMethod;
import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

import java.sql.SQLException;
import java.util.UUID;

public class UserService implements Service {

    @Override
    public Response handleRequest(HttpRequest request) throws JsonProcessingException {
        try {
            if (request.getPath().split("/")[1].equals("sessions") && request.getMethod() == HttpMethod.POST) {
                // login
                UUID uuid = SessionHandler.getInstance().login(new ObjectMapper().readValue(request.getBody(), UserCredentials.class));
                return uuid != null ?
                        new Response(HttpStatus.OK, "application/json", uuid.toString()) :
                        new Response(HttpStatus.UNAUTHORIZED);
            }

            if (request.getPath().split("/")[1].equals("users")) {
                return switch (request.getMethod()) {
                    case GET -> {
                        String username = request.getPath().split("/")[2];
                        if (!SessionHandler.getInstance().verifyUUID(SessionHandler.uuidFromHttpHeader(request.getHttpHeader("Authorization")), username, true))
                            yield new Response(HttpStatus.UNAUTHORIZED);
                        val userData = User.retrieve(username);
                        yield userData != null ?
                                new Response(HttpStatus.OK, "application/json", new ObjectMapper().writeValueAsString(userData)) :
                                new Response(HttpStatus.NOT_FOUND);
                    }
                    case POST -> { // register new user
                        UserCredentials userCredentials = new ObjectMapper().readValue(request.getBody(), UserCredentials.class);
                        yield new Response(User.create(userCredentials) ? HttpStatus.CREATED : HttpStatus.CONFLICT);
                    }
                    case PUT -> {
                        String username = request.getPath().split("/")[2];
                        UserData userData = new ObjectMapper().readValue(request.getBody(), UserData.class);
                        if (!SessionHandler.getInstance().verifyUUID(SessionHandler.uuidFromHttpHeader(request.getHttpHeader("Authorization")), username, true))
                            yield new Response(HttpStatus.UNAUTHORIZED);
                        yield User.update(username, userData) ? new Response(HttpStatus.OK) : new Response(HttpStatus.NOT_FOUND);
                    }
                    default -> new Response(HttpStatus.NOT_FOUND);
                };
            }

            return new Response(HttpStatus.NOT_FOUND);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return new Response(HttpStatus.BAD_REQUEST);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return new Response(HttpStatus.INTERNAL);
        }
    }
}