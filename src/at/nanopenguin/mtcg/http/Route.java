package at.nanopenguin.mtcg.http;

import at.nanopenguin.mtcg.application.Service;

public class Route {
    public final Service service;
    public final boolean hasPathVariable;

    public Route(Service service, boolean hasPathVariable) {
        this.service = service;
        this.hasPathVariable = hasPathVariable;
    }
}
