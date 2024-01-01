package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.http.HttpMethod;
import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

public class GameService implements Service {

    @Override
    public Response handleRequest(HttpRequest request) throws JsonProcessingException {
        try {
            if (request.getPath().split("/")[1].equals("stats") && request.getMethod() == HttpMethod.GET) {
                return new Response(HttpStatus.NOT_IMPLEMENTED);
            }

            if (request.getPath().split("/")[1].equals("scoreboard") && request.getMethod() == HttpMethod.GET) {
                return new Response(HttpStatus.NOT_IMPLEMENTED);
            }

            if (request.getPath().split("/")[1].equals("battles") && request.getMethod() == HttpMethod.POST) {
                return new Response(HttpStatus.NOT_IMPLEMENTED);
            }

            return new Response(HttpStatus.NOT_FOUND);
        } catch (ArrayIndexOutOfBoundsException e) {
            return new Response(HttpStatus.BAD_REQUEST);
        }
    }
}
