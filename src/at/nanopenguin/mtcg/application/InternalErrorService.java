package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;

public class InternalErrorService implements Service {
    private String pathVariable = null;

    @Override
    public void setPathVariable(String var) {
        this.pathVariable = null;
    }

    @Override
    public Response handleRequest(String request) {
        return new Response(HttpStatus.INTERNAL, "application/json", "");
    }
}
