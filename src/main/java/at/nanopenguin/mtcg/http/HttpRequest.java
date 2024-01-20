package at.nanopenguin.mtcg.http;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    @Getter
    private final HttpMethod method;
    @Getter
    private final String path;
    @Getter
    private final String version;
    private final Map<String, String> httpHeaders = new HashMap<>();
    @Getter
    private final String body;

    public HttpRequest(BufferedReader br) throws IOException {
        String line = br.readLine();

        if (line != null) {
            String[] requestLine = line.split(" ");
            this.method = HttpMethod.strToMethod(requestLine[0]);
            this.path = requestLine[1];
            this.version = requestLine[2];

            for (line = br.readLine(); !line.isEmpty(); line = br.readLine()) {
                String[] headerEntry = line.split(": ", 2);
                this.httpHeaders.put(headerEntry[0], headerEntry[1]);
            }

            int contentLength = this.httpHeaders.containsKey("Content-Length") ? Integer.parseInt(this.httpHeaders.get("Content-Length")) : 0;
            char[] charBuffer = new char[contentLength];
            this.body = br.read(charBuffer, 0, contentLength) > 0 ? new String(charBuffer) : null;
            return;
        }

        this.method = null;
        this.path = null;
        this.version = null;
        this.body = null;
    }

    public String getHttpHeader(String header) {
        return httpHeaders.get(header);
    }

}
