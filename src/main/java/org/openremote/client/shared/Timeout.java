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

package org.openremote.client.shared;

import elemental.client.Browser;
import org.openremote.shared.func.Callback;

import java.util.HashMap;
import java.util.Map;

public class Timeout {

    static protected final Map<String, Integer> TIMEOUTS = new HashMap<>();

    static public void debounce(String name, Callback callback, int delayMillis) {
        synchronized (TIMEOUTS) {
            if (TIMEOUTS.containsKey(name)) {
                Browser.getWindow().clearTimeout(TIMEOUTS.get(name));
            }
            TIMEOUTS.put(name, Browser.getWindow().setTimeout(() -> {
                try {
                    callback.call();
                } finally {
                    synchronized (TIMEOUTS) {
                        TIMEOUTS.remove(name);
                    }
                }
            }, delayMillis));
        }
    }

}
