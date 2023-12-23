package at.nanopenguin.mtcg.http;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Response {
    private final HttpStatus httpStatus;
    private final String contentType;
    private final String content;

    public Response(HttpStatus httpStatus, String contentType, String content) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.content = content;
    }

    public String get() {

        String localDatetime = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("UTC")));
        return "HTTP/1.1 " + this.httpStatus.statusCode + " " + this.httpStatus.statusMessage + "\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "Connection: close\r\n" +
                "Date: " + localDatetime + "\r\n" +
                "Expires: " + localDatetime + "\r\n" +
                "Content-Type: " + this.contentType + "\r\n" +
                "Content-Length: " + this.content.length() + "\r\n" +
                "\r\n" +
                this.content;
    }
}
