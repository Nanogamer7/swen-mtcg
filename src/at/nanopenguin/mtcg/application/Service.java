package at.nanopenguin.mtcg.application;

import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.Response;

public interface Service {

    Response handleRequest(HttpRequest request);
}
