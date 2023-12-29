package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.application.service.Service;
import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.Response;

public class InternalErrorService implements Service {
    /* For error in http server */

    @Override
    public Response handleRequest(HttpRequest request) {
        return new Response(HttpStatus.INTERNAL, "application/json", "");
    }
}
