package at.nanopenguin.mtcg.http;

import at.nanopenguin.mtcg.application.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private final Socket serviceSocket;
    private final Router router;

    public RequestHandler(Socket serviceSocket, Router router) {
        this.serviceSocket = serviceSocket;
        this.router = router;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream()));

            System.out.println("creating httpRequest");
            HttpRequest httpRequest = new HttpRequest(br);

            Response response;
            responseBuilder: {
                if (httpRequest.getMethod() == null) {
                    return;
                }

                System.out.println("getting service");
                Service service = router.resolveRoute(httpRequest.getMethod(), httpRequest.getPath());

                if (service == null) {
                    System.out.println("service does not exist");
                    response = new Response(HttpStatus.NOT_FOUND, "text/plain", "");
                    break responseBuilder;
                }
                System.out.println("creating response");
                response = service.handleRequest(httpRequest);
            }

            OutputStream out = this.serviceSocket.getOutputStream();
            out.write(response.get().getBytes());
            out.flush();

            System.out.println("request done");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
