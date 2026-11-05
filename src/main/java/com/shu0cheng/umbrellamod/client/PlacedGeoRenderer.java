package com.shu0cheng.umbrellamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/** Static block-model renderer used by placed umbrellas. */
public class PlacedGeoRenderer extends GeoItemRenderer<UmbrellaItem> {
    public PlacedGeoRenderer(String type, boolean inverted) {
        super(new PlacedUmbrellaGeoModel(type, inverted));
    }

    public void renderInWorld(PoseStack pose, UmbrellaItem item, ItemStack stack,
                              MultiBufferSource buffer, int light, int overlay) {
        this.animatable = item;
        this.currentItemStack = stack;
        this.renderPerspective = ItemDisplayContext.NONE;
        RenderType type = RenderType.entityCutout(getTextureLocation(item));
        VertexConsumer vc = buffer.getBuffer(type);
        defaultRender(pose, item, buffer, type, vc, 0.0F, 0.0F, light);
    }
}
