package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.application.service.schemas.UserCredentials;
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
            if (request.getPath().split("/")[1].equals("sessions")) {
                // response = login()
                return new Response(HttpStatus.NOT_IMPLEMENTED);
            }
            return switch (request.getMethod()) {
                case GET -> new Response(HttpStatus.NO_CONTENT);
                case POST -> new Response(HttpStatus.NOT_IMPLEMENTED);
                case PUT -> new Response(HttpStatus.NOT_IMPLEMENTED);
                default -> new Response(HttpStatus.INTERNAL);
            };
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return new Response(HttpStatus.BAD_REQUEST);
        }
    }
}
