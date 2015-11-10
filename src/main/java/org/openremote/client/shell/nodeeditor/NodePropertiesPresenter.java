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

package org.openremote.client.shell.nodeeditor;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import jsinterop.annotations.JsType;
import org.openremote.client.event.NodePropertiesModifiedEvent;
import org.openremote.client.shared.AbstractPresenter;
import org.openremote.client.shared.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsType
public class NodePropertiesPresenter extends AbstractPresenter<View> {

    private static final Logger LOG = LoggerFactory.getLogger(NodePropertiesPresenter.class);

    public NodePropertiesPresenter(View view) {
        super(view);
    }

    public void nodePropertiesChanged(String nodeId, JavaScriptObject jso) {
        String nodeProperties = new JSONObject(jso).toString();
        dispatch(new NodePropertiesModifiedEvent(nodeId, nodeProperties));
    }

}