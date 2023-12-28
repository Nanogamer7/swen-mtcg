package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;

public class TestService implements Service {

    @Override
    public Response handleRequest(HttpRequest request) {
        return new Response(HttpStatus.NOT_IMPLEMENTED, "application/json", "");
    }
}
