package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;

public class TestService implements Service {
    private String pathVariable = null;

    @Override
    public void setPathVariable(String var) {
        this.pathVariable = var;
    }

    @Override
    public Response handleRequest(String request) {
        return new Response(HttpStatus.OK, "application/json", "{\"var\":\"" + this.pathVariable + "\"]");
    }
}
