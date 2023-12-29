import at.nanopenguin.mtcg.application.service.TestService;
import at.nanopenguin.mtcg.application.service.UserService;
import at.nanopenguin.mtcg.http.HttpMethod;
import at.nanopenguin.mtcg.http.Router;
import at.nanopenguin.mtcg.http.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Router router = new Router();
        router.addRoute(HttpMethod.GET, "/test/{var}/service", new TestService(), new int[]{2});

        /* users */
        router.addRoute(HttpMethod.POST, "/users", new UserService(), new int[]{});
        router.addRoute(HttpMethod.GET, "/users/{username}", new UserService(), new int[]{2});
        router.addRoute(HttpMethod.PUT, "/users/{username}", new UserService(), new int[]{2});
        router.addRoute(HttpMethod.POST, "/sessions", new UserService(), new int[]{});

        /* packages */
        router.addRoute(HttpMethod.POST, "/packages", new TestService(), new int[]{});
        router.addRoute(HttpMethod.POST, "/transaction/packages", new TestService(), new int[]{});

        /* cards */
        router.addRoute(HttpMethod.GET, "/cards", new TestService(), new int[]{});
        router.addRoute(HttpMethod.GET, "/deck", new TestService(), new int[]{});
        router.addRoute(HttpMethod.PUT, "/deck", new TestService(), new int[]{});

        /* game */
        router.addRoute(HttpMethod.GET, "/stats", new TestService(), new int[]{});
        router.addRoute(HttpMethod.GET, "/scoreboard", new TestService(), new int[]{});
        router.addRoute(HttpMethod.POST, "/battles", new TestService(), new int[]{});

        /* trading */
        router.addRoute(HttpMethod.GET, "/tradings", new TestService(), new int[]{});
        router.addRoute(HttpMethod.POST, "/tradings", new TestService(), new int[]{});
        router.addRoute(HttpMethod.DELETE, "/tradings/{tradingDealId}", new TestService(), new int[]{2});
        router.addRoute(HttpMethod.POST, "/tradings/{tradingDealId}", new TestService(), new int[]{2});

        Server server = new Server(10001, 10, router);
        server.start();
    }
}