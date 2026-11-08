package com.shu0cheng.umbrellamod.platform;

import java.util.ServiceLoader;

/** Each platform jar supplies exactly one implementation through META-INF/services. */
public final class Services {
    public static final UmbrellaPlatform PLATFORM = loadPlatform();

    private Services() {}

    private static UmbrellaPlatform loadPlatform() {
        var providers = ServiceLoader.load(UmbrellaPlatform.class).stream().toList();
        if (providers.size() != 1) {
            throw new IllegalStateException("Expected one Umbrella platform provider, found " + providers.size());
        }
        return providers.getFirst().get();
    }
}
