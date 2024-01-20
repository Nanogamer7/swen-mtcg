package at.nanopenguin.mtcg.http;

import at.nanopenguin.mtcg.application.service.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RouterTest {
    private static Router router;
    private static Service mockedService;
    private static Service mockedServiceUnwanted;
    @BeforeEach
    void setup() {
        router = new Router();
        mockedService = Mockito.mock(Service.class);
        mockedServiceUnwanted = Mockito.mock(Service.class);
    }

    @Test
    void simpleRoute() throws SQLException, JsonProcessingException {
        router.addRoute(HttpMethod.GET, "/get", mockedService, new int[]{});

        Service result = router.resolveRoute(HttpMethod.GET, "/get");
        Assertions.assertNotNull(result);
        result.handleRequest(null);
        Mockito.verify(mockedService, Mockito.times(1)).handleRequest(null);
    }

    @Test
    void wrongPath() throws SQLException, JsonProcessingException {
        router.addRoute(HttpMethod.GET, "/get", mockedService, new int[]{});

        Assertions.assertNull(router.resolveRoute(HttpMethod.GET, "/different"));
    }

    @Test
    void wrongMethod() throws SQLException, JsonProcessingException {
        router.addRoute(HttpMethod.GET, "/get", mockedService, new int[]{});

        Service result = router.resolveRoute(HttpMethod.GET, "/get");
        Assertions.assertNotNull(result);
        result.handleRequest(null);

        Assertions.assertNull(router.resolveRoute(HttpMethod.POST, "/get"));
        Assertions.assertNull(router.resolveRoute(HttpMethod.PUT, "/get"));
        Assertions.assertNull(router.resolveRoute(HttpMethod.DELETE, "/get"));
    }

    @Test
    void differentHttpExists() throws SQLException, JsonProcessingException {
        router.addRoute(HttpMethod.GET, "/get", mockedService, new int[]{});
        router.addRoute(HttpMethod.POST, "/get", mockedServiceUnwanted, new int[]{});

        Service result = router.resolveRoute(HttpMethod.GET, "/get");
        Assertions.assertNotNull(result);
        result.handleRequest(null);
        Mockito.verify(mockedService, Mockito.times(1)).handleRequest(null);
        Mockito.verify(mockedServiceUnwanted, Mockito.times(0)).handleRequest(null);
    }

    @Test
    void longerPathExists() throws SQLException, JsonProcessingException {
        router.addRoute(HttpMethod.GET, "/get", mockedService, new int[]{});
        router.addRoute(HttpMethod.GET, "/get/long", mockedServiceUnwanted, new int[]{});

        Service result = router.resolveRoute(HttpMethod.GET, "/get");
        Assertions.assertNotNull(result);
        result.handleRequest(null);
        Mockito.verify(mockedService, Mockito.times(1)).handleRequest(null);
        Mockito.verify(mockedServiceUnwanted, Mockito.times(0)).handleRequest(null);
    }

    @Test
    void shorterPathExists() throws SQLException, JsonProcessingException {
        router.addRoute(HttpMethod.GET, "/get/path", mockedService, new int[]{});
        router.addRoute(HttpMethod.GET, "/get", mockedServiceUnwanted, new int[]{});

        Service result = router.resolveRoute(HttpMethod.GET, "/get/path");
        Assertions.assertNotNull(result);
        result.handleRequest(null);
        Mockito.verify(mockedService, Mockito.times(1)).handleRequest(null);
        Mockito.verify(mockedServiceUnwanted, Mockito.times(0)).handleRequest(null);
    }

    @Test
    void pathWithVar() throws SQLException, JsonProcessingException {
        router.addRoute(HttpMethod.GET, "/get/{var}", mockedService, new int[]{2});

        Service result = router.resolveRoute(HttpMethod.GET, "/get/value");
        Assertions.assertNotNull(result);
        result.handleRequest(null);
        Mockito.verify(mockedService, Mockito.times(1)).handleRequest(null);
    }

    @Test
    void longerPathWithVarExists() throws SQLException, JsonProcessingException {
        router.addRoute(HttpMethod.GET, "/get", mockedService, new int[]{});
        router.addRoute(HttpMethod.GET, "/get/{var}", mockedServiceUnwanted, new int[]{2});

        Service result = router.resolveRoute(HttpMethod.GET, "/get");
        Assertions.assertNotNull(result);
        result.handleRequest(null);
        Mockito.verify(mockedService, Mockito.times(1)).handleRequest(null);
        Mockito.verify(mockedServiceUnwanted, Mockito.times(0)).handleRequest(null);
    }

    @Test
    void pathBeforeVarIsValid() throws SQLException, JsonProcessingException {
        router.addRoute(HttpMethod.GET, "/get/{var}", mockedService, new int[]{2});
        router.addRoute(HttpMethod.GET, "/get", mockedServiceUnwanted, new int[]{});

        Service result = router.resolveRoute(HttpMethod.GET, "/get/value");
        Assertions.assertNotNull(result);
        result.handleRequest(null);
        Mockito.verify(mockedService, Mockito.times(1)).handleRequest(null);
        Mockito.verify(mockedServiceUnwanted, Mockito.times(0)).handleRequest(null);
    }

    @Test
    void pathContinuesAfterVar() throws SQLException, JsonProcessingException {
        router.addRoute(HttpMethod.GET, "/get/{var}/path", mockedService, new int[]{2});

        Service result = router.resolveRoute(HttpMethod.GET, "/get/value/path");
        Assertions.assertNotNull(result);
        result.handleRequest(null);
        Mockito.verify(mockedService, Mockito.times(1)).handleRequest(null);
    }

    @Test
    void pathWithTwoVars() throws SQLException, JsonProcessingException {
        router.addRoute(HttpMethod.GET, "/get/{var}/path/{var}", mockedService, new int[]{2, 4});

        Service result = router.resolveRoute(HttpMethod.GET, "/get/value/path/other_value");
        Assertions.assertNotNull(result);
        result.handleRequest(null);
        Mockito.verify(mockedService, Mockito.times(1)).handleRequest(null);
    }
}
