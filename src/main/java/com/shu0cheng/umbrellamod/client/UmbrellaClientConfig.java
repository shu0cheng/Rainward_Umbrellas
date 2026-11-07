package com.shu0cheng.umbrellamod.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import net.neoforged.fml.loading.FMLPaths;

public final class UmbrellaClientConfig {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("umbrella-client.json");
   private static final Map<UmbrellaType, UmbrellaClientConfig.Transform> HELD = new EnumMap<>(UmbrellaType.class);
   private static final Map<UmbrellaType, UmbrellaClientConfig.Transform> FIRST = new EnumMap<>(UmbrellaType.class);
   private static boolean rainSoundEnabled = true;
   private static boolean firstPersonFixed = false;
   private static final int CONFIG_VERSION = 2;

   private UmbrellaClientConfig() {
   }

   public static UmbrellaClientConfig.Transform held(UmbrellaType type) {
      return HELD.get(type);
   }

   public static UmbrellaClientConfig.Transform first(UmbrellaType type) {
      return FIRST.get(type);
   }

   public static void load() {
      try {
         if (!Files.exists(FILE)) {
            return;
         }

         JsonObject o = (JsonObject)GSON.fromJson(Files.readString(FILE), JsonObject.class);
         if (o == null) {
            return;
         }

         if (o.has("rainSoundEnabled")) {
            rainSoundEnabled = o.get("rainSoundEnabled").getAsBoolean();
         }

         if (o.has("firstPersonFixed")) {
            firstPersonFixed = o.get("firstPersonFixed").getAsBoolean();
         }

         if (o.has("version") && o.get("version").getAsInt() == 2) {
            for (UmbrellaType type : UmbrellaType.values()) {
               read(o, type, "held", HELD.get(type));
               read(o, type, "first", FIRST.get(type));
            }
         }
      } catch (Exception var5) {
      }
   }

   private static void read(JsonObject o, UmbrellaType type, String mode, UmbrellaClientConfig.Transform t) {
      String p = type.id() + "." + mode + ".";
      t.scale = get(o, p + "scale", t.scale);
      t.x = get(o, p + "x", t.x);
      t.y = get(o, p + "y", t.y);
      t.z = get(o, p + "z", t.z);
      t.yaw = get(o, p + "yaw", t.yaw);
      t.pitch = get(o, p + "pitch", t.pitch);
      t.roll = get(o, p + "roll", t.roll);
   }

   public static void save() {
      try {
         JsonObject o = new JsonObject();
         o.addProperty("version", 2);
         o.addProperty("rainSoundEnabled", rainSoundEnabled);
         o.addProperty("firstPersonFixed", firstPersonFixed);

         for (UmbrellaType type : UmbrellaType.values()) {
            write(o, type, "held", HELD.get(type));
            write(o, type, "first", FIRST.get(type));
         }

         Files.createDirectories(FILE.getParent());
         Files.writeString(FILE, GSON.toJson(o));
      } catch (Exception var5) {
      }
   }

   private static void write(JsonObject o, UmbrellaType type, String mode, UmbrellaClientConfig.Transform t) {
      String p = type.id() + "." + mode + ".";
      o.addProperty(p + "scale", t.scale);
      o.addProperty(p + "x", t.x);
      o.addProperty(p + "y", t.y);
      o.addProperty(p + "z", t.z);
      o.addProperty(p + "yaw", t.yaw);
      o.addProperty(p + "pitch", t.pitch);
      o.addProperty(p + "roll", t.roll);
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
      UmbrellaClientConfig.Transform h = HELD.get(type);
      h.scale = 1.0F;
      h.x = h.y = h.z = h.yaw = h.pitch = h.roll = 0.0F;
      UmbrellaClientConfig.Transform f = FIRST.get(type);
      f.scale = 1.0F;
      f.x = f.y = f.z = f.yaw = f.pitch = f.roll = 0.0F;
      save();
   }

   private static float get(JsonObject o, String key, float fallback) {
      return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsFloat() : fallback;
   }

   static {
      HELD.put(UmbrellaType.TELESCOPIC, new UmbrellaClientConfig.Transform(1.0F));
      HELD.put(UmbrellaType.STRAIGHT, new UmbrellaClientConfig.Transform(1.0F));
      HELD.put(UmbrellaType.OIL_PAPER, new UmbrellaClientConfig.Transform(1.0F));
      HELD.put(UmbrellaType.BAMBOO_OIL_PAPER, new UmbrellaClientConfig.Transform(1.0F));
      FIRST.put(UmbrellaType.TELESCOPIC, new UmbrellaClientConfig.Transform(1.0F));
      FIRST.put(UmbrellaType.STRAIGHT, new UmbrellaClientConfig.Transform(1.0F));
      FIRST.put(UmbrellaType.OIL_PAPER, new UmbrellaClientConfig.Transform(1.0F));
      FIRST.put(UmbrellaType.BAMBOO_OIL_PAPER, new UmbrellaClientConfig.Transform(1.0F));
      load();
   }

   public static final class Transform {
      public float scale;
      public float x;
      public float y;
      public float z;
      public float yaw;
      public float pitch;
      public float roll;

      Transform(float scale) {
         this.scale = scale;
      }
   }
}
