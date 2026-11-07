package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.item.UmbrellaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class UmbrellaConfigScreen extends Screen {
   private static final UmbrellaType[] TYPES = new UmbrellaType[]{UmbrellaType.TELESCOPIC, UmbrellaType.STRAIGHT, UmbrellaType.OIL_PAPER, UmbrellaType.BAMBOO_OIL_PAPER};
   private final UmbrellaType type;
   private final boolean firstPerson;
   private final List<UmbrellaConfigScreen.ConfigSlider> sliders = new ArrayList<>();

   public UmbrellaConfigScreen(UmbrellaType type, boolean firstPerson) {
      super(Component.translatable("umbrella.config.title"));
      this.type = type;
      this.firstPerson = firstPerson;
   }

   protected void init() {
      int cx = this.width / 2;
      int left = cx - 155;
      int right = cx + 5;
      int y = 56;
      this.addRenderableWidget(Button.builder(typeName(this.type), b -> {
         int i = 0;

         for (int j = 0; j < TYPES.length; j++) {
            if (TYPES[j] == this.type) {
               i = j;
            }
         }

         if (this.minecraft != null) {
            this.minecraft.setScreen(new UmbrellaConfigScreen(TYPES[(i + 1) % TYPES.length], this.firstPerson));
         }
      }).bounds(left, 18, 145, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable(this.firstPerson ? "umbrella.config.mode.first" : "umbrella.config.mode.third"), b -> {
         if (this.minecraft != null) {
            this.minecraft.setScreen(new UmbrellaConfigScreen(this.type, !this.firstPerson));
         }
      }).bounds(right, 18, 150, 20).build());
      UmbrellaClientConfig.Transform t = this.firstPerson ? UmbrellaClientConfig.first(this.type) : UmbrellaClientConfig.held(this.type);
      this.addSlider("umbrella.config.scale", 0.1F, 3.0F, () -> t.scale, v -> t.scale = v, left, y, 150);
      this.addSlider("umbrella.config.x", -1.0F, 1.0F, () -> t.x, v -> t.x = v, left, y + 24, 150);
      this.addSlider("umbrella.config.y", -1.0F, 1.0F, () -> t.y, v -> t.y = v, left, y + 48, 150);
      this.addSlider("umbrella.config.z", -1.0F, 1.0F, () -> t.z, v -> t.z = v, left, y + 72, 150);
      this.addSlider("umbrella.config.yaw", -180.0F, 180.0F, () -> t.yaw, v -> t.yaw = v, right, y, 150);
      this.addSlider("umbrella.config.pitch", -180.0F, 180.0F, () -> t.pitch, v -> t.pitch = v, right, y + 24, 150);
      this.addSlider("umbrella.config.roll", -180.0F, 180.0F, () -> t.roll, v -> t.roll = v, right, y + 48, 150);
      Button rainButton = Button.builder(rainSoundLabel(), b -> {
         UmbrellaClientConfig.toggleRainSound();
         b.setMessage(rainSoundLabel());
      }).bounds(cx - 155, y + 84, 300, 20).build();
      this.addRenderableWidget(rainButton);
      Button firstPersonButton = Button.builder(firstPersonLabel(), b -> {
         UmbrellaClientConfig.toggleFirstPersonFixed();
         b.setMessage(firstPersonLabel());
      }).bounds(cx - 155, y + 108, 300, 20).build();
      this.addRenderableWidget(firstPersonButton);
      this.addRenderableWidget(Button.builder(Component.translatable("umbrella.config.reset"), b -> {
         UmbrellaClientConfig.reset(this.type);
         if (this.minecraft != null) {
            this.minecraft.setScreen(new UmbrellaConfigScreen(this.type, this.firstPerson));
         }
      }).bounds(cx - 105, y + 132, 96, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("umbrella.config.done"), b -> this.onClose()).bounds(cx + 9, y + 132, 96, 20).build());
   }

   private static Component firstPersonLabel() {
      return Component.translatable(UmbrellaClientConfig.firstPersonFixed() ? "umbrella.config.first_person.world" : "umbrella.config.first_person.hand");
   }

   private static Component rainSoundLabel() {
      return Component.translatable(UmbrellaClientConfig.rainSoundEnabled() ? "umbrella.config.rain_sound.on" : "umbrella.config.rain_sound.off");
   }

   private static Component typeName(UmbrellaType type) {
      return Component.translatable("umbrella.config.type." + type.id());
   }

   private void addSlider(String labelKey, float min, float max, Supplier<Float> getter, Consumer<Float> setter, int x, int y, int width) {
      UmbrellaConfigScreen.ConfigSlider slider = new UmbrellaConfigScreen.ConfigSlider(
         x, y, width, 20, Component.translatable(labelKey), min, max, getter, setter
      );
      this.sliders.add(slider);
      this.addRenderableWidget(slider);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 4, 16777215);
      graphics.drawCenteredString(this.font, Component.translatable("umbrella.config.hint"), this.width / 2, 192, 10526880);
   }

   public void onClose() {
      UmbrellaClientConfig.save();
      if (this.minecraft != null) {
         this.minecraft.setScreen(null);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   private static final class ConfigSlider extends AbstractSliderButton {
      private final Component label;
      private final float min;
      private final float max;
      private final Supplier<Float> getter;
      private final Consumer<Float> setter;

      ConfigSlider(int x, int y, int width, int height, Component label, float min, float max, Supplier<Float> getter, Consumer<Float> setter) {
         super(x, y, width, height, Component.empty(), value(getter.get(), min, max));
         this.label = label;
         this.min = min;
         this.max = max;
         this.getter = getter;
         this.setter = setter;
         this.updateMessage();
      }

      private static double value(float current, float min, float max) {
         return Math.max(0.0, Math.min(1.0, (double)((current - min) / (max - min))));
      }

      protected void updateMessage() {
         this.setMessage(Component.translatable("umbrella.config.value", new Object[]{this.label, String.format(Locale.ROOT, "%.2f", this.getter.get())}));
      }

      protected void applyValue() {
         this.setter.accept(this.min + (float)this.value * (this.max - this.min));
         UmbrellaClientConfig.save();
      }
   }
}
