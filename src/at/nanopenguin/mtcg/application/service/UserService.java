package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.application.service.schemas.UserCredentials;
import at.nanopenguin.mtcg.http.HttpMethod;
import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class UserService implements Service {

    @Override
    public Response handleRequest(HttpRequest request) throws JsonProcessingException {
        try {
            if (request.getPath().split("/")[1].equals("sessions") && request.getMethod() == HttpMethod.POST) {
                // response = login()
                return new Response(HttpStatus.NOT_IMPLEMENTED); // new ObjectMapper().readValue(request.getBody(), UserCredentials.class);
            }

            if (request.getPath().split("/")[1].equals("users")) {
                return switch (request.getMethod()) {
                    case GET -> new Response(HttpStatus.NOT_IMPLEMENTED);
                    case POST -> new Response(HttpStatus.NOT_IMPLEMENTED); // new ObjectMapper().readValue(request.getBody(), UserCredentials.class);
                    case PUT -> new Response(HttpStatus.NOT_IMPLEMENTED); // new ObjectMapper().readValue(request.getBody(), UserData.class);
                    default -> new Response(HttpStatus.NOT_FOUND);
                };
            }

            return new Response(HttpStatus.NOT_FOUND);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return new Response(HttpStatus.BAD_REQUEST);
        }
    }
}
