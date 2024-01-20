package at.nanopenguin.mtcg.http;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    TEAPOT(418, "I'm a teapot"),
    INTERNAL(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented");

    public final int statusCode;
    public final String statusMessage;
}
