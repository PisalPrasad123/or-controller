package org.openremote.beta.client.shared;

import elemental.client.Browser;

import java.util.HashMap;
import java.util.Map;

public class Timeout {

    static protected final Map<String, Integer> TIMEOUTS = new HashMap<>();

    static public void debounce(String name, Runnable runnable, int delayMillis) {
        synchronized (TIMEOUTS) {
            if (TIMEOUTS.containsKey(name)) {
                Browser.getWindow().clearTimeout(TIMEOUTS.get(name));
            }
            TIMEOUTS.put(name, Browser.getWindow().setTimeout(() -> {
                try {
                    runnable.run();
                } finally {
                    synchronized (TIMEOUTS) {
                        TIMEOUTS.remove(name);
                    }
                }
            }, delayMillis));
        }
    }

}