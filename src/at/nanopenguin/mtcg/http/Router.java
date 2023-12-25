package at.nanopenguin.mtcg.http;

import at.nanopenguin.mtcg.application.Service;

import java.util.*;
import java.util.stream.IntStream;

public class Router {
    private Map<HttpMethod, Map<String, Route>> routeMap = new HashMap<>();

    public void addRoute(final HttpMethod method, final String route, final Service service, final int pathVarPos) {
        Map<String, Route> map = this.routeMap.get(method);
        if (method != null && map == null) {
            this.routeMap.put(method, (map = new HashMap<>()));
        }

        List<String> routeComponents = new ArrayList<String>(Arrays.asList(route.split("/")));
        routeComponents.remove(0);
        routeComponents.set(pathVarPos, "{var}");

        for (int i = 0; i < routeComponents.size(); i++) {
            Route routeComponent = new Route(i == routeComponents.size() - 1 ? service : null, i == pathVarPos - 1);
            String path = String.join("/", routeComponents.subList(0, i+1));
            map.put(path, routeComponent);
        }

    }

    public Service resolveRoute(final HttpMethod method, final String route) {
        System.out.println("resolving route " + route);
        String[] routeComponents = route.split("/");
        Route component = this.routeMap.get(method).get(routeComponents[1]);

        if (component == null) {
            return null;
        }

        String pathVariable = null;

        int i = 1;
        System.out.println(routeComponents[i]);
        for (String search = routeComponents[i]; component != null && component.service == null; component = this.routeMap.get(method).get(search = String.join("/", search, routeComponents[++i]))) {
            System.out.println(component);
            if (component.hasPathVariable) {
                pathVariable = routeComponents[++i];
                search = String.join("/", search, "{var}");
            }
        }

        if (component == null) {
            return null;
        }

        component.service.setPathVariable(pathVariable);
        return component.service;

    }
}
