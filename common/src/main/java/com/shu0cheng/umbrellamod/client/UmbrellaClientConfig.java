package com.shu0cheng.umbrellamod.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaTransform;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import com.shu0cheng.umbrellamod.platform.Services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

/** Per-umbrella-type client transforms, editable in-game with O. */
public final class UmbrellaClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = Services.PLATFORM.configDirectory().resolve("umbrella-client.json");
    private static final Map<UmbrellaType, UmbrellaTransform> HELD = new EnumMap<>(UmbrellaType.class);
    private static final Map<UmbrellaType, UmbrellaTransform> FIRST = new EnumMap<>(UmbrellaType.class);
    private static final int CONFIG_VERSION = 2;
    private static boolean rainSoundEnabled = true;
    private static boolean firstPersonFixed = false;

    static {
        load();
    }

    private UmbrellaClientConfig() {}

    public static UmbrellaTransform transform(UmbrellaType type, boolean firstPerson) {
        return (firstPerson ? FIRST : HELD).getOrDefault(type, UmbrellaTransform.IDENTITY);
    }

    public static void setTransform(UmbrellaType type, boolean firstPerson, UmbrellaTransform transform) {
        (firstPerson ? FIRST : HELD).put(type, transform);
    }

    private static void load() {
        try {
            if (!Files.exists(FILE)) return;
            JsonObject o = GSON.fromJson(Files.readString(FILE), JsonObject.class);
            if (o == null) return;
            if (o.has("rainSoundEnabled")) rainSoundEnabled = o.get("rainSoundEnabled").getAsBoolean();
            if (o.has("firstPersonFixed")) firstPersonFixed = o.get("firstPersonFixed").getAsBoolean();
            // Version 1 transforms belonged to the old renderer.
            if (o.has("version") && o.get("version").getAsInt() == CONFIG_VERSION) {
                for (UmbrellaType type : UmbrellaType.values()) {
                    HELD.put(type, read(o, type.id() + ".held."));
                    FIRST.put(type, read(o, type.id() + ".first."));
                }
            }
        } catch (Exception e) {
            UmbrellaMod.LOGGER.warn("Could not read umbrella client settings", e);
        }
    }

    private static UmbrellaTransform read(JsonObject o, String p) {
        UmbrellaTransform transform = new UmbrellaTransform(
                get(o, p + "scale", 1.0F), get(o, p + "x", 0.0F), get(o, p + "y", 0.0F),
                get(o, p + "z", 0.0F), get(o, p + "yaw", 0.0F), get(o, p + "pitch", 0.0F),
                get(o, p + "roll", 0.0F));
        return transform.isValid(1.0F) ? transform : UmbrellaTransform.IDENTITY;
    }

    public static void save() {
        try {
            JsonObject o = new JsonObject();
            o.addProperty("version", CONFIG_VERSION);
            o.addProperty("rainSoundEnabled", rainSoundEnabled);
            o.addProperty("firstPersonFixed", firstPersonFixed);
            for (UmbrellaType type : UmbrellaType.values()) {
                write(o, type.id() + ".held.", transform(type, false));
                write(o, type.id() + ".first.", transform(type, true));
            }
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(o));
        } catch (Exception e) {
            UmbrellaMod.LOGGER.warn("Could not save umbrella client settings", e);
        }
    }

    private static void write(JsonObject o, String p, UmbrellaTransform t) {
        o.addProperty(p + "scale", t.scale());
        o.addProperty(p + "x", t.x());
        o.addProperty(p + "y", t.y());
        o.addProperty(p + "z", t.z());
        o.addProperty(p + "yaw", t.yaw());
        o.addProperty(p + "pitch", t.pitch());
        o.addProperty(p + "roll", t.roll());
    }

    public static boolean firstPersonFixed() {
        return firstPersonFixed;
    }

    public static void toggleFirstPersonFixed() {
        firstPersonFixed = !firstPersonFixed;
        save();
    }

    public static boolean rainSoundEnabled() {
        return rainSoundEnabled;
    }

    public static void toggleRainSound() {
        rainSoundEnabled = !rainSoundEnabled;
        save();
        if (!rainSoundEnabled) {
            UmbrellaRainSounds.stopNow();
        }
    }

    public static void reset(UmbrellaType type) {
        HELD.remove(type);
        FIRST.remove(type);
    }

    private static float get(JsonObject o, String key, float fallback) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsFloat() : fallback;
    }
}
