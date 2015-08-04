package org.openremote.beta.server.route;

import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;
import org.openremote.beta.server.route.predicate.DestinationSinkPosition;
import org.openremote.beta.server.route.predicate.PropertyIsTrue;
import org.openremote.beta.shared.flow.Flow;
import org.openremote.beta.shared.flow.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FilterRoute extends NodeRoute {

    private static final Logger LOG = LoggerFactory.getLogger(FilterRoute.class);

    public static final String FILTER_PASS = "FILTER_PASS";

    final protected Map<String, Object> instanceValues = new HashMap<>();

    public FilterRoute(CamelContext context, Flow flow, Node node) {
        super(context, flow, node);
    }

    @Override
    protected void configureProcessing(RouteDefinition routeDefinition) throws Exception {

        routeDefinition
            .choice()
            .id(getProcessorId(flow, node, "selectInputSlot"))
            .when(new DestinationSinkPosition(getNode(), 0))
                .process(exchange -> {
                    LOG.debug("Filter received data on: " + node);
                    synchronized (instanceValues) {
                        String instanceId = getInstanceId(exchange);
                        LOG.debug("Filter stores data for instance: " + instanceId);
                        instanceValues.put(instanceId, exchange.getIn().getBody());
                    }
                })
                .id(getProcessorId(flow, node, "storeInstanceValue"))
                .setHeader(FILTER_PASS, constant(true))
                .id(getProcessorId(flow, node, "assumeFilterPass"))
                .choice()
                    .id(getProcessorId(flow, node, "applyRules"))
                        .when(new PropertyIsTrue(getNode(), "onTrigger"))
                            .setHeader(FILTER_PASS, constant(false))
                            .id(getProcessorId(flow, node, "applyOnTrigger"))
                        // TODO: Other filter rules
                .end()
            .endChoice()
            .when(new DestinationSinkPosition(getNode(), 1))
                .process(exchange -> {
                    log.debug("Filter received trigger on: " + node);
                    synchronized (instanceValues) {
                        String instanceId = getInstanceId(exchange);
                        if (instanceValues.containsKey(instanceId)) {
                            log.debug("Filter has data for instance: " + instanceId);
                            exchange.getIn().setBody(instanceValues.get(instanceId));
                            exchange.getIn().setHeader(FILTER_PASS, true);
                        } else {
                            LOG.debug("Filter received trigger but has no data for instance: " + instanceId);
                        }
                    }
                })
                .id(getProcessorId(flow, node, "receiveTrigger"))
            .endChoice()
            .end()
            .choice()
                .id(getProcessorId(flow, node, "checkFilterPass"))
                .when(header(FILTER_PASS).isEqualTo(false))
                    .process(exchange -> {
                        exchange.getIn().setBody(null);
                        exchange.getIn().removeHeader(FILTER_PASS);
                    })
                    .id(getProcessorId(flow, node, "filterClear"))
                    .stop()
                    .id(getProcessorId(flow, node, "filterStop"))
                    .endChoice()
                .end()
            .removeHeader(FILTER_PASS)
            .id(getProcessorId(flow, node, "filterPass"));
    }
}
