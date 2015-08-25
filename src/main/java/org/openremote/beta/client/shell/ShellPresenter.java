package org.openremote.beta.client.shell;

import com.google.gwt.core.client.js.JsExport;
import com.google.gwt.core.client.js.JsType;
import elemental.dom.Element;
import elemental.html.IFrameElement;
import org.openremote.beta.client.console.ConsoleReadyEvent;
import org.openremote.beta.client.console.ConsoleRefreshEvent;
import org.openremote.beta.client.console.ConsoleWidgetUpdatedEvent;
import org.openremote.beta.client.editor.flow.crud.FlowDeletedEvent;
import org.openremote.beta.client.editor.flow.editor.FlowEditEvent;
import org.openremote.beta.client.editor.flow.editor.FlowUpdatedEvent;
import org.openremote.beta.client.shared.session.message.MessageReceivedEvent;
import org.openremote.beta.client.shared.session.message.MessageServerConnectEvent;
import org.openremote.beta.client.shared.session.message.MessageSessionPresenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsExport
@JsType
public class ShellPresenter extends MessageSessionPresenter {

    private static final Logger LOG = LoggerFactory.getLogger(ShellPresenter.class);

    public boolean editorOpen;

    public ShellPresenter(com.google.gwt.dom.client.Element view) {
        super(view);

        addEventListener(
            MessageReceivedEvent.class,
            event -> {
                if (isEditorViewAvailable()) {
                    dispatchEvent(getEditorView(), event);
                    dispatchEvent("#messageLog", event);
                }
                if (isConsoleViewAvailable()) {
                    dispatchEvent(getConsoleView(), event);
                }
            }
        );

        addEventListener(
            EditorOpenEvent.class,
            event -> {
                if (isConsoleViewAvailable()) {
                    dispatchEvent(getConsoleView(), event);
                }
            }
        );

        addEventListener(
            EditorCloseEvent.class,
            event -> {
                if (isConsoleViewAvailable()) {
                    dispatchEvent(getConsoleView(), event);
                }
            });

        addEventListener(
            ConsoleReadyEvent.class, event -> {
                if (!isConsoleViewAvailable())
                    return;
                if (editorOpen) {
                    dispatchEvent(getConsoleView(), new EditorOpenEvent());
                } else {
                    dispatchEvent(getConsoleView(), new EditorCloseEvent());
                }
            }
        );

        addEventListener(
            ConsoleWidgetUpdatedEvent.class, event -> {
                if (isEditorViewAvailable()) {
                    dispatchEvent(getEditorView(), event);
                }
            }
        );

        addEventListener(
            FlowEditEvent.class,
            event -> {
                dispatchEvent("#messageLog", event);
                if (isConsoleViewAvailable()) {
                    dispatchEvent(getConsoleView(), new ConsoleRefreshEvent(event.getFlow()));
                }
            }
        );

        addEventListener(
            FlowUpdatedEvent.class,
            event -> {
                dispatchEvent("#messageLog", event);
                if (isConsoleViewAvailable()) {
                    dispatchEvent(getConsoleView(), new ConsoleRefreshEvent(event.getFlow()));
                }
            }
        );

        addEventListener(
            FlowDeletedEvent.class,
            event -> {
                dispatchEvent("#messageLog", event);
                if (isConsoleViewAvailable()) {
                    dispatchEvent(getConsoleView(), new ConsoleRefreshEvent(null));
                }
            }
        );
    }

    @Override
    public void attached() {
        super.attached();
        dispatchEvent(new MessageServerConnectEvent());
    }

    public void toggleEditor() {
        if (editorOpen) {
            dispatchEvent("#messageLog", new MessageLogCloseEvent());
            dispatchEvent(new EditorCloseEvent());
            editorOpen = false;
        } else {
            dispatchEvent("#messageLog", new MessageLogOpenEvent());
            dispatchEvent(new EditorOpenEvent());
            editorOpen = true;
        }
        notifyPath("editorOpen", editorOpen);
    }

    protected boolean isEditorViewAvailable() {
        IFrameElement frame = (IFrameElement) getRequiredElement("#editor");
        Element view = frame.getContentDocument().querySelector("or-editor");
        return view != null;
    }

    protected boolean isConsoleViewAvailable() {
        IFrameElement frame = (IFrameElement) getRequiredElement("#console");
        Element view = frame.getContentDocument().querySelector("or-console");
        return view != null;
    }

    protected Element getEditorView() {
        IFrameElement frame = (IFrameElement) getRequiredElement("#editor");
        Element view = frame.getContentDocument().querySelector("or-editor");
        if (view == null)
            throw new IllegalArgumentException("Missing or-editor view component in editor frame.");
        return view;
    }

    protected Element getConsoleView() {
        IFrameElement frame = (IFrameElement) getRequiredElement("#console");
        Element view = frame.getContentDocument().querySelector("or-console");
        if (view == null)
            throw new IllegalArgumentException("Missing or-console view component in console frame.");
        return view;
    }


}
