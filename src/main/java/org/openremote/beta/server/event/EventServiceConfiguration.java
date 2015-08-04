package org.openremote.beta.server.event;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.AmbiguousMethodCallException;
import org.openremote.beta.server.Configuration;
import org.openremote.beta.server.Environment;
import org.openremote.beta.server.flow.FlowService;
import org.openremote.beta.server.route.RouteManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventServiceConfiguration implements Configuration {

    private static final Logger LOG = LoggerFactory.getLogger(EventServiceConfiguration.class);

    @Override
    public void apply(Environment environment, CamelContext context) throws Exception {

        if (context.hasService(RouteManagementService.class) == null)
            throw new RuntimeException("Required service missing, check Configuration order: " + RouteManagementService.class.getName());

        if (context.hasService(FlowService.class) == null)
            throw new RuntimeException("Required service missing, check Configuration order: " + FlowService.class.getName());

        EventService eventService = new EventService(
            context,
            context.hasService(FlowService.class),
            context.hasService(RouteManagementService.class)
        );

        context.addService(eventService);

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(EventService.FLOW_EVENTS_QUEUE)
                    .routeId("Handle flow events")
                    .doTry()
                    .bean(getContext().hasService(EventService.class), "onFlowEvent")
                    .doCatch(AmbiguousMethodCallException.class) // No overloaded method for given event subtype
                    .log(LoggingLevel.DEBUG, LOG, "Ignoring unhandled event: ${body}")
                    .stop()
                    .endDoTry()
                ;
            }
        });
    }
}
