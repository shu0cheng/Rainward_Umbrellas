package com.shu0cheng.umbrellamod.client;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PlacedUmbrellaGeoModel extends GeoModel<UmbrellaItem> {
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private static final ResourceLocation EMPTY = UmbrellaMod.id("animations/item/block_empty.animation.json");

    public PlacedUmbrellaGeoModel(String type, boolean inverted) {
        this.model = UmbrellaMod.id("geo/block/" + type + (inverted ? "_inverted" : "_upright") + ".geo.json");
        this.texture = UmbrellaMod.id("textures/block/" + switch (type) {
            case "straight_umbrella" -> "straight_block.png";
            case "oil_paper_umbrella" -> "oil_paper_block.png";
            default -> "telescopic_block.png";
        });
    }

    @Override
    public ResourceLocation getModelResource(UmbrellaItem animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(UmbrellaItem animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(UmbrellaItem animatable) {
        return EMPTY;
    }
}
