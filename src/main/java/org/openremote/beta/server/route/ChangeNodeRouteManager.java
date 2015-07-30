package org.openremote.beta.server.route;

import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;
import org.openremote.beta.shared.flow.Flow;
import org.openremote.beta.shared.flow.Node;

import static org.openremote.beta.shared.util.Util.getMap;
import static org.openremote.beta.shared.util.Util.getString;

public class ChangeNodeRouteManager extends NodeRouteManager {

    public ChangeNodeRouteManager(CamelContext context, Flow flow, Node node) {
        super(context, flow, node);
    }

    @Override
    protected void configure(RouteDefinition routeDefinition) throws Exception {
        routeDefinition
            .process(exchange -> {
                if (node.hasProperties()) {
                    String append = getString(getMap(node.getProperties()), "append");
                    if (append != null) {
                        exchange.getIn().setBody(exchange.getIn().getBody(String.class) + append);
                    }
                    String prepend = getString(getMap(node.getProperties()), "prepend");
                    if (prepend != null) {
                        exchange.getIn().setBody(prepend + exchange.getIn().getBody(String.class));
                    }
                }
            })
            .id(flow.toString() + "###" + node.toString() + "###process");
    }
}