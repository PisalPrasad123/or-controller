package org.openremote.beta.server.catalog.widget;

import org.openremote.beta.server.catalog.WidgetNodeDescriptor;
import org.openremote.beta.server.util.IdentifierUtil;
import org.openremote.beta.shared.flow.Slot;
import org.openremote.beta.shared.model.Identifier;

public class TextLabelNodeDescriptor extends WidgetNodeDescriptor {

    public static final String TYPE = "urn:org-openremote:widget:textlabel";
    public static final String TYPE_LABEL = "Text Label";
    public static final String COMPONENT = "or-console-widget-textlabel";

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
            new Slot("Text", new Identifier(IdentifierUtil.generateGlobalUniqueId(), Slot.TYPE_SINK)),
        };
    }

}
