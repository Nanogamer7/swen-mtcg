package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.http.HttpMethod;
import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Arrays;

public class PackagesService implements Service {

    @Override
    public Response handleRequest(HttpRequest request) throws JsonProcessingException {
        try {
            if (request.getPath().split("/")[1].equals("packages") && request.getMethod() == HttpMethod.POST) {
                return new Response(HttpStatus.NOT_IMPLEMENTED); // new ObjectMapper().readValue(request.getBody(), Card.class);
            }

            if (String.join("/", Arrays.copyOfRange(request.getPath().split("/"), 1, 2)).equals("transactions/packages") && request.getMethod() == HttpMethod.POST) {
                return new Response(HttpStatus.NOT_IMPLEMENTED);
            }

            return new Response(HttpStatus.NOT_FOUND);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return new Response(HttpStatus.BAD_REQUEST);
        }
    }
}
