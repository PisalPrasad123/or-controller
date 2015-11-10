/*
 * Copyright 2015, OpenRemote Inc.
 *
 * See the CONTRIBUTORS.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.openremote.server.web.socket;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;
import org.apache.camel.util.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class WebsocketComponent extends UriEndpointComponent {

    private static final Logger LOG = LoggerFactory.getLogger(WebsocketComponent.class);

    final protected Map<String, WebsocketConsumer> consumers = new HashMap<>();
    final protected WebsocketSessions websocketSessions;
    protected String allowedOrigin;

    public WebsocketComponent() {
        super(WebsocketEndpoint.class);
        this.websocketSessions = new MemoryWebsocketSessions();
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        WebsocketEndpoint endpoint = new WebsocketEndpoint(this, uri, remaining, parameters);
        setProperties(endpoint, parameters);
        return endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        ServiceHelper.startService(getWebsocketSessions());
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        ServiceHelper.stopService(getWebsocketSessions());
        undeploy();
    }

    public WebsocketSessions getWebsocketSessions() {
        return websocketSessions;
    }

    public Map<String, WebsocketConsumer> getConsumers() {
        return consumers;
    }

    public String getAllowedOrigin() {
        return allowedOrigin;
    }

    public void setAllowedOrigin(String allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }

    synchronized public void connect(WebsocketConsumer consumer) {
        String resourceUri = consumer.getEndpoint().getResourceUri();
        if (consumers.containsKey(resourceUri)) {
            LOG.debug("Websocket server endpoint already connected: " + resourceUri);
            return;
        }
        consumers.put(resourceUri, consumer);
        try {
            redeploy();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    synchronized public void disconnect(WebsocketConsumer consumer) {
        String resourceUri = consumer.getEndpoint().getResourceUri();
        if (!consumers.containsKey(resourceUri)) {
            return;
        }
        consumers.remove(resourceUri);
        try {
            redeploy();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void redeploy() throws Exception {
        // TODO what happens to inflight sessions?
        undeploy();
        deploy();
    }

    protected abstract void deploy() throws Exception;
    protected abstract void undeploy() throws Exception;
}

