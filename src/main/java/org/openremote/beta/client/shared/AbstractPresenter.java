package org.openremote.beta.client.shared;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.js.JsExport;
import com.google.gwt.core.client.js.JsType;
import elemental.client.Browser;
import elemental.dom.Element;
import elemental.dom.Node;
import elemental.dom.TimeoutHandler;
import elemental.events.CustomEvent;
import elemental.events.EventRemover;
import org.openremote.beta.shared.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsExport
@JsType
public abstract class AbstractPresenter {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPresenter.class);

    /* TODO global message bus instead of manual event passing to child presenters
    final static protected Map<String, List<EventListener>> EVENT_BUS = new HashMap<>();

    protected <E extends Event> void addListener(Class<E> eventClass,
                                                 EventListener<E> listener) {
        synchronized (EVENT_BUS) {
            String eventType = Event.getType(eventClass);
            List<EventListener> listeners = EVENT_BUS.get(eventType);
            if (listeners == null) {
                listeners = new ArrayList<>();
                EVENT_BUS.put(eventType, listeners);
            }
            listeners.add(listener);
        }
    }

    @SuppressWarnings("unchecked")
    protected boolean dispatch(Event event) {
        synchronized (EVENT_BUS) {
            try {
                List<EventListener> listeners = EVENT_BUS.get(event.getType());
                for (EventListener listener : listeners) {
                    listener.on(event);
                }
                return true;
            } catch (VetoEventException ex) {
                LOG.debug("Event vetoed: " + event.getType());
                return false;
            }
        }
    }
    */

    final protected Element view;

    public AbstractPresenter(com.google.gwt.dom.client.Element gwtView) {
        this.view = gwtView.cast(); // TODO No idea why this is necessary, without this we can only use superdevmode...

        if (view == null)
            throw new IllegalArgumentException("Can't instantiate presenter without a view element: " + getClass().getName());
        LOG.debug("Creating presenter for view '" + view.getLocalName() + "': " + getClass().getName());
    }

    public Element getView() {
        return view;
    }

    public Component getViewComponent() {
        return (Component) view;
    }

    public Component.DOM getViewRootDOM() {
        return getDOMRoot(getView());
    }

    public Component.DOM getDOM(Node node) {
        return JsUtil.dom(node);
    }

    public Component.DOM getDOMRoot(Element element) {
        return JsUtil.domRoot(element);
    }

    public void ready() {
        LOG.debug("Ready: " + getView().getLocalName());
    }

    public void attached() {
        LOG.debug("Attached: " + getView().getLocalName());
    }

    protected native boolean notifyPath(String path, String s) /*-{
        return this.@AbstractPresenter::view.notifyPath("_presenter." + path, s);
    }-*/;

    protected native boolean notifyPath(String path, double d) /*-{
        return this.@AbstractPresenter::view.notifyPath("_presenter." + path, d);
    }-*/;

    protected native boolean notifyPath(String path, int i) /*-{
        return this.@AbstractPresenter::view.notifyPath("_presenter." + path, i);
    }-*/;

    protected native boolean notifyPath(String path, boolean b) /*-{
        return this.@AbstractPresenter::view.notifyPath("_presenter." + path, b);
    }-*/;

    protected native boolean notifyPath(String path, Object[] array) /*-{
        return this.@AbstractPresenter::view.notifyPath("_presenter." + path, array);
    }-*/;

    protected native boolean notifyPath(String path, JavaScriptObject jso) /*-{
        return this.@AbstractPresenter::view.notifyPath("_presenter." + path, jso);
    }-*/;

    protected native boolean notifyPathNull(String path) /*-{
        return this.@AbstractPresenter::view.notifyPath("_presenter." + path, null);
    }-*/;

    // TODO: This is dangerous, not sure if that always works, sometimes Polymer uses this value
    protected native boolean notifyPath(String path) /*-{
        return this.@AbstractPresenter::view.notifyPath("_presenter." + path, Math.random()); // Always trigger an update inside Polymer!
    }-*/;

    protected native void pushArray(String array, Object obj) /*-{
        this.@AbstractPresenter::view.push("_presenter." + array, obj);
    }-*/;

    protected <E extends Event> EventRemover addEventListener(Class<E> eventClass,
                                                              EventListener<E> listener) {
        return addEventListener(getView(), eventClass, listener);
    }

    protected <E extends Event> EventRemover addEventListener(Element originView,
                                                              Class<E> eventClass,
                                                              EventListener<E> listener) {
        return addEventListener(originView, eventClass, false, false, listener);
    }

    protected <E extends Event> EventRemover addEventListener(Class<E> eventClass,
                                                              boolean stopPropagation,
                                                              EventListener<E> listener) {
        return addEventListener(getView(), eventClass, stopPropagation, false, listener);
    }

    protected <E extends Event> EventRemover addEventListener(Class<E> eventClass,
                                                              boolean stopPropagation,
                                                              boolean stopImmediatePropagation,
                                                              EventListener<E> listener) {
        return addEventListener(getView(), eventClass, stopPropagation, stopImmediatePropagation, listener);
    }

    protected <E extends Event> EventRemover addEventListener(Element originView,
                                                              Class<E> eventClass,
                                                              boolean stopPropagation,
                                                              boolean stopImmediatePropagation,
                                                              EventListener<E> listener) {
        String eventType = Event.getType(eventClass);
        LOG.debug("Adding event listener to view '" + originView.getLocalName() + "': " + eventType);
        return originView.addEventListener(eventType, evt -> {
            LOG.debug("Received event on view '" + originView.getLocalName() + "': " + evt.getType());
            CustomEvent customEvent = (CustomEvent) evt;
            if (stopPropagation)
                customEvent.stopPropagation();
            if (stopImmediatePropagation)
                customEvent.stopImmediatePropagation();
            @SuppressWarnings("unchecked")
            E event = (E) customEvent.getDetail();
            listener.on(event);
        }, false); // Disable the capture phase, optional bubbling is easier to understand
    }

    protected int dispatchEvent(Event event) {
        return dispatchEvent(true, event);
    }

    protected int dispatchEvent(boolean canBubble, Event event) {
        return dispatchEvent(canBubble, getView(), event, 0, -1);
    }

    // We want to make sure that events dispatched on child views do not bubble up again!
    protected int dispatchEvent(String childViewSelector, Event event) {
        return dispatchEvent(false, getOptionalElement(childViewSelector), event, 0, -1);
    }

    // We want to make sure that events dispatched on child views do not bubble up again!
    protected int dispatchEvent(Element targetView, Event event) {
        return dispatchEvent(false, targetView, event, 0, -1);
    }

    protected int dispatchEvent(Event event, int delayMillis, int timeoutId) {
        return dispatchEvent(true, getView(), event, delayMillis, timeoutId);
    }

    // This should not be called directly unless you are sure what you are doing: If the
    // target view is a child view, the event might bubble up again, if it can bubble!
    // Still keeping it protected instead of private, because private is just rude to
    // anyone who wants to fix a bug in this method.
    protected int dispatchEvent(boolean canBubble, Element targetView, Event event, int delayMillis, int timeoutId) {
        if (targetView == null)
            return -1;

        if (timeoutId != -1) {
            Browser.getWindow().clearTimeout(timeoutId);
        }

        TimeoutHandler dispatchHandler = () -> {
            LOG.debug("Dispatching event on view '" + targetView.getLocalName() + "': " + event.getType());
            CustomEvent customEvent = (CustomEvent) targetView.getOwnerDocument().createEvent("CustomEvent");
            boolean bubbling = canBubble && !(event instanceof NonBubblingEvent);
            customEvent.initCustomEvent(event.getType(), bubbling, false, event);
            targetView.dispatchEvent(customEvent);
        };
        if (delayMillis > 0) {
            LOG.debug("Scheduling after " + delayMillis + " milliseconds, custom event on view '" + targetView.getLocalName() + "': " + event.getType());
            return Browser.getWindow().setTimeout(dispatchHandler, delayMillis);
        } else {
            dispatchHandler.onTimeoutHandler();
            return -1;
        }
    }

    protected boolean hasViewElement(String selector) {
        return getOptionalElement(selector) != null;
    }

    protected Element getOptionalElement(String selector) {
        return getViewComponent().$$(selector);
    }

    protected Element getRequiredElement(String selector) {
        Element child = getOptionalElement(selector);
        if (child == null)
            throw new RuntimeException("Missing child element '" + selector + "' on: " + getView().getLocalName());
        return child;
    }

    protected Component.DOM getRequiredElementDOM(String selector) {
        return getDOM(getRequiredElement(selector));
    }

    protected String getWindowQueryArgument(String parameter) {
        return JsUtil.getQueryArgument(parameter);
    }

}

