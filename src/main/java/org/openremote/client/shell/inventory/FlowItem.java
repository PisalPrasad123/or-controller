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

package org.openremote.client.shell.inventory;

import jsinterop.annotations.JsType;
import org.openremote.client.shell.flowcontrol.FlowStatusDetail;
import org.openremote.shared.flow.Flow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsType
public class FlowItem {

    private static final Logger LOG = LoggerFactory.getLogger(FlowItem.class);

    final public Flow flow;
    public FlowStatusDetail status;

    public FlowItem(Flow flow, FlowStatusDetail status) {
        this.flow = flow;
        setStatus(status);
    }

    public Flow getFlow() {
        return flow;
    }

    public FlowStatusDetail getStatus() {
        return status;
    }

    public void setStatus(FlowStatusDetail status) {
        this.status = status;
    }
}
