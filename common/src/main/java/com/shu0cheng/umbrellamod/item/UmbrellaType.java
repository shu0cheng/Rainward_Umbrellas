package com.shu0cheng.umbrellamod.item;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import net.minecraft.resources.ResourceLocation;

/**
 * Static data for the three umbrella variants.
 *
 * <p>All lengths are in blocks (1 block = 16 pixels).  The canopy is a shallow
 * dome of {@link #canopyRadius()} blocks radius whose rim sits
 * {@link #canopyDrop()} blocks below the hub.</p>
 */
public enum UmbrellaType {
    TELESCOPIC(
            "telescopic",
            0.52f, 0.21f,
            1.34f, 0.62f,
            10, HandleStyle.PLASTIC,
            0x9AA0A8, 0x2E3138, 0x5A5F68, 0x365C94
    ),
    STRAIGHT(
            "straight",
            0.62f, 0.24f,
            1.46f, 1.42f,
            12, HandleStyle.WOOD,
            0x7A5836, 0x5E3E22, 0x3A3B40, 0x34363E
    ),
    OIL_PAPER(
            "oil_paper",
            0.68f, 0.20f,
            1.50f, 1.47f,
            12, HandleStyle.BAMBOO,
            0x8E6A3A, 0x6E4C26, 0x5A3A1E, 0xC47634
    );

    public enum HandleStyle {
        PLASTIC,
        WOOD,
        BAMBOO
    }

    private final String id;
    private final float canopyRadius;
    private final float canopyDrop;
    private final float openHubY;
    private final float closedHubY;
    private final int segments;
    private final HandleStyle handleStyle;
    private final int shaftColor;
    private final int handleColor;
    private final int ribColor;
    private final int canopyTint;

    UmbrellaType(String id, float canopyRadius, float canopyDrop, float openHubY, float closedHubY,
                 int segments, HandleStyle handleStyle,
                 int shaftColor, int handleColor, int ribColor, int canopyTint) {
        this.id = id;
        this.canopyRadius = canopyRadius;
        this.canopyDrop = canopyDrop;
        this.openHubY = openHubY;
        this.closedHubY = closedHubY;
        this.segments = segments;
        this.handleStyle = handleStyle;
        this.shaftColor = shaftColor;
        this.handleColor = handleColor;
        this.ribColor = ribColor;
        this.canopyTint = canopyTint;
    }

    public String id() {
        return id;
    }

    public int animationTicks() {
        // Rounded up from the GeckoLib animation lengths (0.60, 0.84 and 0.85 seconds).
        return this == TELESCOPIC ? 12 : 17;
    }

    public float canopyRadius() {
        return canopyRadius;
    }

    public float canopyDrop() {
        return canopyDrop;
    }

    public float openHubY() {
        return openHubY;
    }

    public float closedHubY() {
        return closedHubY;
    }

    public int segments() {
        return segments;
    }

    public HandleStyle handleStyle() {
        return handleStyle;
    }

    public int shaftColor() {
        return shaftColor;
    }

    public int handleColor() {
        return handleColor;
    }

    public int ribColor() {
        return ribColor;
    }

    public int canopyTint() {
        return canopyTint;
    }

    public ResourceLocation canopyTexture() {
        return UmbrellaMod.id("textures/item/canopy_" + id + ".png");
    }

    public ResourceLocation iconTexture() {
        return UmbrellaMod.id("textures/item/icon_" + id + ".png");
    }
}
