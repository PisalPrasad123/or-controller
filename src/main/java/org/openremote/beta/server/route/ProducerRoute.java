package org.openremote.beta.server.route;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.model.RouteDefinition;
import org.openremote.beta.shared.flow.Flow;
import org.openremote.beta.shared.flow.Node;
import org.openremote.beta.shared.flow.Slot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerRoute extends NodeRoute {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerRoute.class);

    public ProducerRoute(CamelContext context, Flow flow, Node node) {
        super(context, flow, node);
    }

    @Override
    protected void configureProcessing(RouteDefinition routeDefinition) throws Exception {
        // Nothing to do
    }

    @Override
    protected boolean isPublishingMessageEvents() {
        return true;
    }

    @Override
    protected void configureDestination(RouteDefinition routeDefinition) throws Exception {
        // Many (or no) internal (subflow) consumers receive this message asynchronously
        routeDefinition
            .process(exchange -> {
                LOG.debug("Handing off exchange to asynchronous queue: " + getNode());

                ProducerTemplate producerTemplate = getContext().createProducerTemplate();

                Slot[] sourceSlots = getNode().findSlots(Slot.TYPE_SOURCE);

                for (Slot sourceSlot : sourceSlots) {
                    Exchange exchangeCopy = copyExchange(exchange, false);
                    LOG.debug("Sending message to asynchronous queue: " + sourceSlot.getId());
                    producerTemplate.send(
                        "seda:" + sourceSlot.getId() + "?multipleConsumers=true&waitForTaskToComplete=NEVER",
                        exchangeCopy
                    );
                }
            })
            .id(getProcessorId("toQueues"));
    }
}