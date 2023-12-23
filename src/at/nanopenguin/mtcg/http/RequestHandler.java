package at.nanopenguin.mtcg.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private final Socket serviceSocket;
    private final Router router;
    private BufferedReader br;
    private OutputStream out;

    public RequestHandler(Socket serviceSocket, Router router) {
        this.serviceSocket = serviceSocket;
        this.router = router;
    }

    @Override
    public void run() {
        try {
            Response response = new Response(HttpStatus.OK, "application/json", "[]");

            this.out = this.serviceSocket.getOutputStream();
            out.write(response.get().getBytes());
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
