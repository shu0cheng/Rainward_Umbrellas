package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.item.UmbrellaTransform;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** DEVELOPMENT ONLY. In-game placement tuner for the placed umbrella block models. */
public final class UmbrellaBlockDevScreen extends Screen {
    private static final UmbrellaType[] TYPES = {
            UmbrellaType.TELESCOPIC, UmbrellaType.STRAIGHT, UmbrellaType.OIL_PAPER
    };
    private static final String[] MODES = {
            "开伞正放", "闭伞地面", "闭伞靠方块", "开伞倒放·上半砖", "开伞倒放·下半砖"
    };

    private final UmbrellaType type;
    private final int mode;
    private final int selector;
    private boolean changed;
    private EditBox input;

    public UmbrellaBlockDevScreen(UmbrellaType type, int mode, int selector) {
        super(Component.literal("伞方块开发调参"));
        this.type = type;
        this.mode = mode;
        this.selector = selector;
    }

    private boolean open() {
        return mode != 1 && mode != 2;
    }

    private boolean inverted() {
        return mode >= 3;
    }

    private boolean half() {
        return mode == 4;
    }

    private boolean leaning() {
        return mode == 2;
    }

    private UmbrellaTransform value() {
        return UmbrellaBlockDevConfig.get(type, open(), inverted(), half(), leaning(), variant());
    }

    private String variant() {
        if (inverted()) return "inverted";
        return selector == 0 ? "cardinal" : "diagonal";
    }

    private String selectorName() {
        if (inverted()) return "无方向（只看上下半砖）";
        return selector == 0 ? "正面" : "斜面";
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int left = cx - 155;
        int right = cx + 5;
        int y = 64;

        addRenderableWidget(Button.builder(Component.literal("类型: " + type.id()), b -> {
            int i = 0;
            for (int j = 0; j < TYPES.length; j++) if (TYPES[j] == type) i = j;
            minecraft.setScreen(new UmbrellaBlockDevScreen(TYPES[(i + 1) % TYPES.length], mode, selector));
        }).bounds(left, 16, 145, 20).build());
        addRenderableWidget(Button.builder(Component.literal("模式: " + MODES[mode]), b ->
                minecraft.setScreen(new UmbrellaBlockDevScreen(type, (mode + 1) % MODES.length, selector))
        ).bounds(right, 16, 150, 20).build());
        if (!inverted()) {
        addRenderableWidget(Button.builder(Component.literal("调整: " + selectorName()), b -> {
            int max = 2;
            minecraft.setScreen(new UmbrellaBlockDevScreen(type, mode, (selector + 1) % max));
        }).bounds(left, 40, 145, 20).build());
        }

        addSlider("X", -2, 2, () -> value().x(), v -> set(value().withX(v)), left, y);
        addSlider("Y", -2, 2, () -> value().y(), v -> set(value().withY(v)), left, y + 22);
        addSlider("Z", -2, 2, () -> value().z(), v -> set(value().withZ(v)), left, y + 44);
        addSlider("Yaw", -180, 180, () -> value().yaw(), v -> set(value().withYaw(v)), right, y);
        addSlider("Pitch", -180, 180, () -> value().pitch(), v -> set(value().withPitch(v)), right, y + 22);
        addSlider("Roll", -180, 180, () -> value().roll(), v -> set(value().withRoll(v)), right, y + 44);
        addSlider("Scale", 0.1f, 3.0f, () -> value().scale(), v -> set(value().withScale(v)), left, y + 88);

        input = new EditBox(font, left, y + 116, 300, 20,
                Component.literal("x,y,z,yaw,pitch,roll,scale"));
        input.setMaxLength(200);
        input.setValue(csv(value()));
        addRenderableWidget(input);

        addRenderableWidget(Button.builder(Component.literal("复制数值"), b -> {
            if (minecraft != null) minecraft.keyboardHandler.setClipboard(csv(value()));
        }).bounds(left, y + 140, 96, 20).build());
        addRenderableWidget(Button.builder(Component.literal("应用输入"), b -> {
            applyInput();
            if (minecraft != null) minecraft.setScreen(new UmbrellaBlockDevScreen(type, mode, selector));
        }).bounds(left + 102, y + 140, 96, 20).build());
        addRenderableWidget(Button.builder(Component.literal("复制 Java"), b -> {
            saveChanges();
            if (minecraft != null) minecraft.keyboardHandler.setClipboard(UmbrellaBlockDevConfig.javaCode());
        }).bounds(left + 204, y + 140, 96, 20).build());

        addRenderableWidget(Button.builder(Component.literal("保存调整"), b -> saveChanges())
                .bounds(left, y + 164, 145, 20).build());
        addRenderableWidget(Button.builder(Component.literal("完成"), b -> onClose())
                .bounds(left + 155, y + 164, 145, 20).build());
    }

    private void set(UmbrellaTransform value) {
        changed |= UmbrellaBlockDevConfig.set(type, open(), inverted(), half(), leaning(), variant(), value);
    }

    private void addSlider(String label, float min, float max, Supplier<Float> getter,
                           Consumer<Float> setter, int x, int y) {
        DevSlider slider = new DevSlider(x, y, 145, 20, Component.literal(label), min, max, getter, setter);
        addRenderableWidget(slider);
    }

    private static String csv(UmbrellaTransform t) {
        return String.format(Locale.ROOT, "%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f",
                t.x(), t.y(), t.z(), t.yaw(), t.pitch(), t.roll(), t.scale());
    }

    private void applyInput() {
        try {
            String[] p = input.getValue().split("[,\\s]+");
            if (p.length < 7) return;
            set(new UmbrellaTransform(Float.parseFloat(p[6]), Float.parseFloat(p[0]), Float.parseFloat(p[1]),
                    Float.parseFloat(p[2]), Float.parseFloat(p[3]), Float.parseFloat(p[4]), Float.parseFloat(p[5])));
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 4, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.literal("格式: x,y,z,yaw,pitch,roll,scale"), width / 2, height - 32, 0xA0A0A0);
        graphics.drawCenteredString(font, Component.literal("开发专用 - 发布前删除"), width / 2, height - 18, 0xFF8080);
    }

    @Override
    public void removed() {
        saveChanges();
        super.removed();
    }

    private void saveChanges() {
        if (changed) {
            UmbrellaBlockDevConfig.save();
            changed = false;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static final class DevSlider extends AbstractSliderButton {
        private final Component label;
        private final float min;
        private final float max;
        private final Supplier<Float> getter;
        private final Consumer<Float> setter;

        DevSlider(int x, int y, int width, int height, Component label, float min, float max,
                  Supplier<Float> getter, Consumer<Float> setter) {
            super(x, y, width, height, Component.empty(), value(getter.get(), min, max));
            this.label = label;
            this.min = min;
            this.max = max;
            this.getter = getter;
            this.setter = setter;
            updateMessage();
        }

        private static double value(float value, float min, float max) {
            return Math.max(0.0, Math.min(1.0, (value - min) / (max - min)));
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal(label.getString() + ": "
                    + String.format(Locale.ROOT, "%.3f", getter.get())));
        }

        @Override
        protected void applyValue() {
            setter.accept(min + (float) value * (max - min));
        }
    }
}
