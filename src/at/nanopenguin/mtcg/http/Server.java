package at.nanopenguin.mtcg.http;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class Server {
    private final int port; // 16-bit unsigned, not enforced
    private final int threads;
    private final Router router;

    public void  start() throws IOException {
        try (ServerSocket listener = new ServerSocket(this.port)) {

            ExecutorService executorService = Executors.newFixedThreadPool(this.threads);

            System.out.println("HTTP server running on http://localhost:" + this.port);

            while (true) {
                final Socket serviceSocket = listener.accept();
                final RequestHandler requestHandler = new RequestHandler(serviceSocket, this.router);

                System.out.println("received request");
                executorService.submit(requestHandler);

            }
        }
    }
}
