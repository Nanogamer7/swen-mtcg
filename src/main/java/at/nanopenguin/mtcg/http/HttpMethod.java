package at.nanopenguin.mtcg.http;

public enum HttpMethod {
    GET,
    POST,
    DELETE,
    PUT;

    public static HttpMethod strToMethod(String methodStr) {
        return switch (methodStr) {
            case "GET" -> HttpMethod.GET;
            case "POST" -> HttpMethod.POST;
            case "DELETE" -> HttpMethod.DELETE;
            case "PUT" -> HttpMethod.PUT;
            default -> null;
        };
    }
}
