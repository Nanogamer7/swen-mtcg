package at.nanopenguin.mtcg.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod method;
    private final String path;
    private final String version;
    private final Map<String, String> httpHeaders = new HashMap<>();
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

            this.body = this.httpHeaders.containsKey("Content-Length") ? new String(new char[Integer.parseInt(this.httpHeaders.get("Content-Length"))]) : null;

            return;
        }

        this.method = null;
        this.path = null;
        this.version = null;
        this.body = null;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public String getHttpHeader(String header) {
        return httpHeaders.get(header);
    }

    public String getBody() {
        return body;
    }
}
