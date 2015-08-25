package org.openremote.beta.server.catalog.widget;

import org.openremote.beta.server.catalog.WidgetNodeDescriptor;
import org.openremote.beta.server.util.IdentifierUtil;
import org.openremote.beta.shared.flow.Slot;
import org.openremote.beta.shared.model.Identifier;

public class PushButtonNodeDescriptor extends WidgetNodeDescriptor {

    public static final String TYPE = "urn:org-openremote:widget:pushbutton";
    public static final String TYPE_LABEL = "Push Button";
    public static final String COMPONENT = "or-console-widget-pushbutton";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getTypeLabel() {
        return TYPE_LABEL;
    }

    @Override
    protected String getWidgetComponent() {
        return COMPONENT;
    }

    @Override
    public Slot[] createSlots() {
        return new Slot[] {
            new Slot(new Identifier(IdentifierUtil.generateGlobalUniqueId(), Slot.TYPE_SINK), false),
            new Slot(new Identifier(IdentifierUtil.generateGlobalUniqueId(), Slot.TYPE_SOURCE))
        };
    }

}
