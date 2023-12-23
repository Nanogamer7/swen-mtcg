import at.nanopenguin.mtcg.http.Router;
import at.nanopenguin.mtcg.http.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        Server server = new Server(10001, 10, new Router());
        server.start();
    }
}