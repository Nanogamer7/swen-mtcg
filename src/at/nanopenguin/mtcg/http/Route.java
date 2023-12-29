package at.nanopenguin.mtcg.http;

import at.nanopenguin.mtcg.application.service.Service;

public record Route(Service service, boolean hasPathVariable) {
}
