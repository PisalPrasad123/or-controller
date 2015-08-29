package org.openremote.beta.client.shared.request;

import com.google.gwt.core.client.js.JsType;
import org.openremote.beta.shared.event.Event;

@JsType
public class RequestCompleteEvent extends Event {

    final public String message;

    public RequestCompleteEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
