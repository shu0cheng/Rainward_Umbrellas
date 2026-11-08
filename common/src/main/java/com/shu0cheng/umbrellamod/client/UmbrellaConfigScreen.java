package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.item.UmbrellaType;
import com.shu0cheng.umbrellamod.item.UmbrellaTransform;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.network.TransformPayload;
import net.minecraft.world.item.ItemStack;
import com.shu0cheng.umbrellamod.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** In-game per-type transform editor (key O), localized by the game language. */
public final class UmbrellaConfigScreen extends Screen {
    private static final UmbrellaType[] TYPES = {
            UmbrellaType.TELESCOPIC, UmbrellaType.STRAIGHT, UmbrellaType.OIL_PAPER
    };

    private UmbrellaType type;
    private boolean firstPerson;
    private InteractionHand editingHand;
    private ItemStack editingStack;
    private boolean transformChanged;

    public UmbrellaConfigScreen(UmbrellaType type, boolean firstPerson) {
        super(Component.translatable("umbrella.config.title"));
        this.type = type;
        this.firstPerson = firstPerson;
    }

    @Override
    protected void init() {
        if (!transformChanged) selectEditingStack();
        int cx = this.width / 2;
        int left = cx - 155;
        int right = cx + 5;
        int y = 56;

        addRenderableWidget(Button.builder(typeName(type), b -> {
            int i = 0;
            for (int j = 0; j < TYPES.length; j++) if (TYPES[j] == type) i = j;
            saveChanges();
            type = TYPES[(i + 1) % TYPES.length];
            rebuildWidgets();
        }).bounds(left, 18, 145, 20).build());

        addRenderableWidget(Button.builder(
                Component.translatable(firstPerson ? "umbrella.config.mode.first" : "umbrella.config.mode.third"),
                b -> {
                    firstPerson = !firstPerson;
                    rebuildWidgets();
                }).bounds(right, 18, 150, 20).build());

        // Position column.
        addSlider("umbrella.config.scale", 0.1F, 3.0F, () -> value().scale(), v -> edit(value().withScale(v)), left, y, 150);
        addSlider("umbrella.config.x", -1.0F, 1.0F, () -> value().x(), v -> edit(value().withX(v)), left, y + 24, 150);
        addSlider("umbrella.config.y", -1.0F, 1.0F, () -> value().y(), v -> edit(value().withY(v)), left, y + 48, 150);
        addSlider("umbrella.config.z", -1.0F, 1.0F, () -> value().z(), v -> edit(value().withZ(v)), left, y + 72, 150);

        // Rotation column: left/right, forward/back and tilt.
        addSlider("umbrella.config.yaw", -180.0F, 180.0F, () -> value().yaw(), v -> edit(value().withYaw(v)), right, y, 150);
        addSlider("umbrella.config.pitch", -180.0F, 180.0F, () -> value().pitch(), v -> edit(value().withPitch(v)), right, y + 24, 150);
        addSlider("umbrella.config.roll", -180.0F, 180.0F, () -> value().roll(), v -> edit(value().withRoll(v)), right, y + 48, 150);

        Button rainButton = Button.builder(rainSoundLabel(), b -> {
            UmbrellaClientConfig.toggleRainSound();
            b.setMessage(rainSoundLabel());
        }).bounds(cx - 155, y + 84, 300, 20).build();
        addRenderableWidget(rainButton);

        Button firstPersonButton = Button.builder(firstPersonLabel(), b -> {
            UmbrellaClientConfig.toggleFirstPersonFixed();
            b.setMessage(firstPersonLabel());
        }).bounds(cx - 155, y + 108, 300, 20).build();
        addRenderableWidget(firstPersonButton);

        addRenderableWidget(Button.builder(Component.translatable("umbrella.config.reset"), b -> {
            UmbrellaClientConfig.reset(type);
            transformChanged = true;
            rebuildWidgets();
        }).bounds(cx - 105, y + 132, 96, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("umbrella.config.done"), b -> onClose())
                .bounds(cx + 9, y + 132, 96, 20).build());
    }

    private static Component firstPersonLabel() {
        return Component.translatable(UmbrellaClientConfig.firstPersonFixed()
                ? "umbrella.config.first_person.world" : "umbrella.config.first_person.hand");
    }

    private UmbrellaTransform value() {
        return UmbrellaClientConfig.transform(type, firstPerson);
    }

    private void edit(UmbrellaTransform transform) {
        UmbrellaClientConfig.setTransform(type, firstPerson, transform);
        transformChanged = true;
    }

    private void selectEditingStack() {
        editingHand = null;
        editingStack = null;
        if (minecraft == null || minecraft.player == null) return;
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack held = minecraft.player.getItemInHand(hand);
            if (held.getItem() instanceof UmbrellaItem item && item.getUmbrellaType() == type) {
                editingHand = hand;
                editingStack = held;
                return;
            }
        }
    }

    boolean isEditing(ItemStack stack) {
        return transformChanged && stack == editingStack;
    }

    private static Component rainSoundLabel() {
        return Component.translatable(UmbrellaClientConfig.rainSoundEnabled()
                ? "umbrella.config.rain_sound.on" : "umbrella.config.rain_sound.off");
    }

    private static Component typeName(UmbrellaType type) {
        return Component.translatable("umbrella.config.type." + type.id());
    }

    private void addSlider(String labelKey, float min, float max, Supplier<Float> getter,
                           Consumer<Float> setter, int x, int y, int width) {
        ConfigSlider slider = new ConfigSlider(x, y, width, 20,
                Component.translatable(labelKey), min, max, getter, setter);
        addRenderableWidget(slider);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 4, 0xFFFFFF);
        graphics.drawCenteredString(this.font, Component.translatable("umbrella.config.hint"),
                this.width / 2, 214, 0xA0A0A0);
    }

    @Override
    public void removed() {
        saveChanges();
        super.removed();
    }

    private void saveChanges() {
        if (!transformChanged) return;
        transformChanged = false;
        UmbrellaClientConfig.save();
        if (editingHand != null && minecraft != null && minecraft.player != null && minecraft.getConnection() != null
                && minecraft.player.getItemInHand(editingHand) == editingStack && UmbrellaItem.isUmbrella(editingStack)) {
            ModNetwork.sendToServer(new TransformPayload(editingHand, BuiltInRegistries.ITEM.getKey(editingStack.getItem()),
                    UmbrellaClientConfig.transform(type, false), UmbrellaClientConfig.transform(type, true)));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static final class ConfigSlider extends AbstractSliderButton {
        private final Component label;
        private final float min;
        private final float max;
        private final Supplier<Float> getter;
        private final Consumer<Float> setter;

        ConfigSlider(int x, int y, int width, int height, Component label, float min, float max,
                     Supplier<Float> getter, Consumer<Float> setter) {
            super(x, y, width, height, Component.empty(), value(getter.get(), min, max));
            this.label = label;
            this.min = min;
            this.max = max;
            this.getter = getter;
            this.setter = setter;
            updateMessage();
        }

        private static double value(float current, float min, float max) {
            return Math.max(0.0, Math.min(1.0, (current - min) / (max - min)));
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable("umbrella.config.value", label,
                    String.format(Locale.ROOT, "%.2f", getter.get())));
        }

        @Override
        protected void applyValue() {
            setter.accept(min + (float) value * (max - min));
        }
    }
}
