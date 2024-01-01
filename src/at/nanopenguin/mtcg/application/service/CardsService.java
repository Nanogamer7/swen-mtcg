package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.http.HttpMethod;
import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

public class CardsService implements Service {

    @Override
    public Response handleRequest(HttpRequest request) throws JsonProcessingException {
        try {
            if (request.getPath().split("/")[1].equals("cards") && request.getMethod() == HttpMethod.GET) {
                return new Response(HttpStatus.NOT_IMPLEMENTED);
            }

            if (request.getPath().split("/")[1].equals("deck")) {
                return switch (request.getMethod()) {
                    case GET -> new Response(HttpStatus.NOT_IMPLEMENTED);
                    case PUT -> new Response(HttpStatus.NOT_IMPLEMENTED); // String[] array = new ObjectMapper().readValue(request.getBody(), String[].class)
                    default -> new Response(HttpStatus.NOT_FOUND);
                };
            }

            return new Response(HttpStatus.NOT_FOUND);
        } catch (ArrayIndexOutOfBoundsException e) {
            return new Response(HttpStatus.BAD_REQUEST);
        }
    }
}
