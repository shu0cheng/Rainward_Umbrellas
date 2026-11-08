package com.shu0cheng.umbrellamod.client;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/** Shared client state. Loaders register the keys, renderers and tick callback. */
public final class UmbrellaClient {
    public static final KeyMapping OPEN_CONFIG = new KeyMapping("key.umbrella.config",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, "key.categories.umbrella");
    public static final KeyMapping DEV_TUNER = new KeyMapping("key.umbrella.devtuner",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F9, "key.categories.umbrella");
    private static final Map<String, UmbrellaGeoRenderer> RENDERERS = new HashMap<>();

    private UmbrellaClient() {}

    public static UmbrellaGeoRenderer renderer(String id) {
        return RENDERERS.computeIfAbsent(id, UmbrellaGeoRenderer::new);
    }

    public static void init() {
        UmbrellaBlockDevConfig.load();
    }
}
