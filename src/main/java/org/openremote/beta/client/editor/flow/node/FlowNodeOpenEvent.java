package org.openremote.beta.client.editor.flow.node;

import com.google.gwt.core.client.js.JsExport;
import com.google.gwt.core.client.js.JsType;
import org.openremote.beta.client.editor.flow.FlowEvent;
import org.openremote.beta.shared.flow.Flow;
import org.openremote.beta.shared.flow.Node;

@JsExport
@JsType
public class FlowNodeOpenEvent extends FlowEvent {

    final protected Node node;
    final String label;
    final String editorComponent;

    public FlowNodeOpenEvent(Flow flow, Node node, String label, String editorComponent) {
        super(flow);
        this.node = node;
        this.label = label;
        this.editorComponent = editorComponent;
    }

    public Node getNode() {
        return node;
    }

    public String getLabel() {
        return label;
    }

    public String getEditorComponent() {
        return editorComponent;
    }
}
