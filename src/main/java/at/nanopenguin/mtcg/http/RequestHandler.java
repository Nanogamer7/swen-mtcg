package at.nanopenguin.mtcg.http;

import at.nanopenguin.mtcg.application.service.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;

@RequiredArgsConstructor
public class RequestHandler implements Runnable {
    private final Socket serviceSocket;
    private final Router router;

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream()));

            System.out.println("creating httpRequest");
            HttpRequest httpRequest = new HttpRequest(br);

            Response response;
            responseBuilder: {
                if (httpRequest.getMethod() == null) {
                    System.out.println("Thanks, postman");
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
                try {
                    response = service.handleRequest(httpRequest);
                }
                catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
                    response = new Response(HttpStatus.BAD_REQUEST);
                }
                catch (Exception e) {
                    System.out.println(e.getMessage()); // TODO: more info
                    System.out.println(Arrays.toString(e.getStackTrace())
                            .replace("[", "\t")
                            .replace(", ", System.lineSeparator() + "\t")
                            .replace("]", ""));
                    response = new Response(HttpStatus.INTERNAL);
                }
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
