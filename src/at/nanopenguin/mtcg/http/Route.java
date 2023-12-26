package at.nanopenguin.mtcg.http;

import at.nanopenguin.mtcg.application.Service;

public record Route(Service service, boolean hasPathVariable) {
}
