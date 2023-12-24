import at.nanopenguin.mtcg.application.TestService;
import at.nanopenguin.mtcg.http.HttpMethod;
import at.nanopenguin.mtcg.http.Router;
import at.nanopenguin.mtcg.http.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Router router = new Router();
        router.addRoute(HttpMethod.GET, "/test/{var}/service", new TestService(), 1);

        Server server = new Server(10001, 10, router);
        server.start();
    }
}