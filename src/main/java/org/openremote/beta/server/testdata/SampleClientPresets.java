package org.openremote.beta.server.testdata;

import org.openremote.beta.shared.inventory.ClientPreset;

import java.util.ArrayList;
import java.util.List;

public class SampleClientPresets {

        /* ###################################################################################### */

    public static final ClientPreset IPAD_LANDSCAPE = new ClientPreset(1l, "iPad Landscape", "iPad", 1024, 1024, 768, 768);

    /* ###################################################################################### */

    public static final ClientPreset NEXUS_5 = new ClientPreset(2l, "Nexus 5", "Nexus 5");

    /* ###################################################################################### */

    public static final List<ClientPreset> PRESETS = new ArrayList<ClientPreset>() {
        {
            add(IPAD_LANDSCAPE);
            add(NEXUS_5);
        }
    };
}
