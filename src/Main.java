import at.nanopenguin.mtcg.application.TestService;
import at.nanopenguin.mtcg.http.HttpMethod;
import at.nanopenguin.mtcg.http.Router;
import at.nanopenguin.mtcg.http.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Router router = new Router();
        router.addRoute(HttpMethod.GET, "/test/{var}/service", new TestService(), 2);

        /* users */
        router.addRoute(HttpMethod.POST, "/users", new TestService(), 0);
        router.addRoute(HttpMethod.GET, "/users/{username}", new TestService(), 2);
        router.addRoute(HttpMethod.PUT, "/users/{username}", new TestService(), 2);
        router.addRoute(HttpMethod.POST, "/sessions", new TestService(), 0);

        /* packages */
        router.addRoute(HttpMethod.POST, "/packages", new TestService(), 0);
        router.addRoute(HttpMethod.POST, "/transaction/packages", new TestService(), 0);

        /* cards */
        router.addRoute(HttpMethod.GET, "/cards", new TestService(), 0);
        router.addRoute(HttpMethod.GET, "/deck", new TestService(), 0);
        router.addRoute(HttpMethod.PUT, "/deck", new TestService(), 0);

        /* game */
        router.addRoute(HttpMethod.GET, "/stats", new TestService(), 0);
        router.addRoute(HttpMethod.GET, "/scoreboard", new TestService(), 0);
        router.addRoute(HttpMethod.POST, "/battles", new TestService(), 0);

        /* trading */
        router.addRoute(HttpMethod.GET, "/tradings", new TestService(), 0);
        router.addRoute(HttpMethod.POST, "/tradings", new TestService(), 0);
        router.addRoute(HttpMethod.DELETE, "/tradings/{tradingdealid}", new TestService(), 2);
        router.addRoute(HttpMethod.POST, "/tradings/{tradingdealid}", new TestService(), 2);

        Server server = new Server(10001, 10, router);
        server.start();
    }
}