package at.nanopenguin.mtcg.http;

import at.nanopenguin.mtcg.application.service.Service;
import lombok.NonNull;

import java.util.*;
import java.util.stream.IntStream;

public class Router {
    private final Map<HttpMethod, Map<String, Route>> routeMap = new HashMap<>();

    public Router() {
        for (HttpMethod httpMethod : HttpMethod.values()) {
            this.routeMap.put(httpMethod, new HashMap<>());
        }
    }

    public void addRoute(@NonNull final HttpMethod method, final String route, final Service service, final int[] pathVars) {
        Map<String, Route> map = this.routeMap.get(method);

        List<String> routeComponents = new ArrayList<>(Arrays.asList(route.split("/")));
        for ( Integer pathVarPos : pathVars) {
            routeComponents.set(pathVarPos, "{var}");
        }

        for (int i = 0; i < routeComponents.size(); i++) {
            String path = String.join("/", routeComponents.subList(0, i+1));
            Route existingRoute = map.get(path);
            int finalI = i;
            Route routeComponent = new Route(i == routeComponents.size() - 1 ? service : existingRoute != null ? existingRoute.service() : null, IntStream.of(pathVars).anyMatch(x -> x == (finalI + 1)) || (existingRoute != null && existingRoute.hasPathVariable()));
            map.put(path, routeComponent);
        }

    }

    public Service resolveRoute(final HttpMethod method, final String route) {
        System.out.println("resolving route " + route);
        String[] routeComponents = route.split("/");

        int i = 1;

        Route component = this.routeMap.get(method).get("/" + routeComponents[i]);

        for (String search = "/" + routeComponents[i]; component != null && (component.service() == null || routeComponents.length - 1 > i) && routeComponents.length - 1 >= i; component = this.routeMap.get(method).get(search = routeComponents.length - 1 > i++ ? String.join("/", search, routeComponents[i]) : search)) {
            if (component.hasPathVariable() && routeComponents.length - 1 > i) {
                ++i;
                search = String.join("/", search, "{var}");
            }
        }

        if (component == null || component.service() == null) {
            return null;
        }

        return component.service();

    }
}
