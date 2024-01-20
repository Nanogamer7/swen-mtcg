package at.nanopenguin.mtcg;

import at.nanopenguin.mtcg.application.SessionHandler;
import at.nanopenguin.mtcg.application.service.*;
import at.nanopenguin.mtcg.http.HttpMethod;
import at.nanopenguin.mtcg.http.Router;
import at.nanopenguin.mtcg.http.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Router router = new Router();
        SessionHandler sessionHandler = new SessionHandler();
        router.addRoute(HttpMethod.GET, "/test/{var}/service", new TestService(), new int[]{2});

        /* users */
        router.addRoute(HttpMethod.POST, "/users", new UserService(sessionHandler), new int[]{});
        router.addRoute(HttpMethod.GET, "/users/{username}", new UserService(sessionHandler), new int[]{2});
        router.addRoute(HttpMethod.PUT, "/users/{username}", new UserService(sessionHandler), new int[]{2});
        router.addRoute(HttpMethod.POST, "/sessions", new UserService(sessionHandler), new int[]{});

        /* packages */
        router.addRoute(HttpMethod.POST, "/packages", new PackagesService(sessionHandler), new int[]{});
        router.addRoute(HttpMethod.POST, "/transactions/packages", new PackagesService(sessionHandler), new int[]{});

        /* cards */
        router.addRoute(HttpMethod.GET, "/cards", new CardsService(sessionHandler), new int[]{});
        router.addRoute(HttpMethod.GET, "/deck", new CardsService(sessionHandler), new int[]{});
        router.addRoute(HttpMethod.PUT, "/deck", new CardsService(sessionHandler), new int[]{});

        /* game */
        router.addRoute(HttpMethod.GET, "/stats", new GameService(sessionHandler), new int[]{});
        router.addRoute(HttpMethod.GET, "/scoreboard", new GameService(sessionHandler), new int[]{});
        router.addRoute(HttpMethod.POST, "/battles", new GameService(sessionHandler), new int[]{});

        /* trading */
        router.addRoute(HttpMethod.GET, "/tradings", new TradingService(sessionHandler), new int[]{});
        router.addRoute(HttpMethod.POST, "/tradings", new TradingService(sessionHandler), new int[]{});
        router.addRoute(HttpMethod.DELETE, "/tradings/{tradingDealId}", new TradingService(sessionHandler), new int[]{2});
        router.addRoute(HttpMethod.POST, "/tradings/{tradingDealId}", new TradingService(sessionHandler), new int[]{2});

        Server server = new Server(10001, 10, router);
        server.start();
    }
}