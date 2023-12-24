package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.http.Response;

public interface Service {
    String pathVariable = null;

    void setPathVariable(String var);

    Response handleRequest(String request);
}
