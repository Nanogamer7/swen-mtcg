package at.nanopenguin.mtcg.application.service;

import at.nanopenguin.mtcg.application.Package;
import at.nanopenguin.mtcg.application.SessionHandler;
import at.nanopenguin.mtcg.application.service.schemas.Card;
import at.nanopenguin.mtcg.http.HttpMethod;
import at.nanopenguin.mtcg.http.HttpRequest;
import at.nanopenguin.mtcg.http.HttpStatus;
import at.nanopenguin.mtcg.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PackagesService implements Service {

    @Override
    public Response handleRequest(HttpRequest request) throws JsonProcessingException {
        try {
            if (request.getPath().split("/")[1].equals("packages") && request.getMethod() == HttpMethod.POST) {
                UUID uuid;
                if ((uuid = SessionHandler.uuidFromHttpHeader(request.getHttpHeader("Authorization"))) == null)
                    return new Response(HttpStatus.UNAUTHORIZED); // TODO: unauthorized for invalid token
                if (!SessionHandler.getInstance().verifyUUID(uuid, true))
                    return new Response(HttpStatus.FORBIDDEN);

                return new Response(Package.create(new ObjectMapper().readValue(request.getBody(), new TypeReference<List<Card>>() {})) ?
                        HttpStatus.CREATED :
                        HttpStatus.CONFLICT);
            }

            if (String.join("/", Arrays.copyOfRange(request.getPath().split("/"), 1, 2)).equals("transactions/packages") && request.getMethod() == HttpMethod.POST) {
                return new Response(!SessionHandler.getInstance().verifyUUID(SessionHandler.uuidFromHttpHeader(request.getHttpHeader("Authorization"))) ?
                    HttpStatus.UNAUTHORIZED :
                    HttpStatus.NOT_IMPLEMENTED);
            }

            return new Response(HttpStatus.NOT_FOUND);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return new Response(HttpStatus.BAD_REQUEST);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return new Response(HttpStatus.INTERNAL);
        }
    }
}
