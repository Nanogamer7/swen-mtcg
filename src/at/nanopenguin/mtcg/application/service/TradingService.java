package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.http.HttpMethod;
import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

public class TradingService implements Service {

    @Override
    public Response handleRequest(HttpRequest request) throws JsonProcessingException {
        try {
            if (request.getPath().split("/")[1].equals("tradings")) {
                return switch (request.getMethod()) {
                    case GET -> new Response(HttpStatus.NOT_IMPLEMENTED);
                    case POST -> new Response(HttpStatus.NOT_IMPLEMENTED); // request.getPath().split("/").length > 2 => path variable
                    case DELETE -> new Response(HttpStatus.NOT_IMPLEMENTED);
                    default -> new Response(HttpStatus.NOT_FOUND);
                };
            }

            return new Response(HttpStatus.NOT_FOUND);
        } catch (ArrayIndexOutOfBoundsException e) {
            return new Response(HttpStatus.BAD_REQUEST);
        }
    }
}