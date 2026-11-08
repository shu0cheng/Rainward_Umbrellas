package com.shu0cheng.umbrellamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.item.UmbrellaTransform;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/** GeckoLib item renderer. Item frames/pedestals always show the static open pose. */
public class UmbrellaGeoRenderer extends GeoItemRenderer<UmbrellaItem> {
    private final String id;

    public UmbrellaGeoRenderer(String id) {
        super(new DefaultedItemGeoModel<UmbrellaItem>(UmbrellaMod.id(id)));
        this.id = id;
    }

    public void renderInWorld(PoseStack pose, UmbrellaItem item, ItemStack stack,
                              MultiBufferSource buffer, int light, int overlay) {
        this.animatable = item;
        this.currentItemStack = stack;
        this.renderPerspective = ItemDisplayContext.NONE;
        RenderType type = getRenderType(item, getTextureLocation(item), buffer, 0.0F);
        VertexConsumer vc = buffer.getBuffer(type);
        defaultRender(pose, item, buffer, type, vc, 0.0F, 0.0F, light);
    }

    @Override
    public RenderType getRenderType(UmbrellaItem animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        // Backface culling removes the coplanar duplicate faces that flicker on
        // the straight/telescopic converted meshes. The oil-paper canopy needs
        // both sides visible.
        return id.equals("oil_paper_umbrella")
                ? RenderType.entityCutoutNoCull(texture)
                : RenderType.entityCutout(texture);
    }

    @Override
    public void preRender(PoseStack pose, UmbrellaItem animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                          float partialTick, int packedLight, int packedOverlay, int color) {
        super.preRender(pose, animatable, model, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, color);
        if (isReRender || !UmbrellaClientConfig.firstPersonFixed()) return;
        boolean firstPerson = renderPerspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || renderPerspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        if (!firstPerson || currentItemStack == null) return;
        if (!UmbrellaItem.isOpen(currentItemStack)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        // Cancel the camera pitch, keep yaw; rotate around the model origin (grip).
        // Cancel pitch in VIEW space, not around the model origin. Rotating the
        // model around its own origin makes the canopy orbit the handle and look
        // like it is being lifted when you look up.
        float pitch = mc.player.getViewXRot(partialTick);
        // The pose is still in world orientation here, so a fixed world X axis
        // only works when facing south. Rotate around the camera's horizontal
        // right axis instead.
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        float yaw = (float) Math.toRadians(camera.getYRot());
        Vector3f axis = new Vector3f((float) Math.cos(yaw), 0.0F, (float) Math.sin(yaw));
        Matrix4f current = new Matrix4f(pose.last().pose());
        Matrix4f correction = new Matrix4f().rotation((float) Math.toRadians(-pitch), axis);
        pose.last().pose().set(correction.mul(current));
    }

    @Override
    public long getInstanceId(UmbrellaItem animatable) {
        return UmbrellaAnimation.instanceId(animatable.getUmbrellaType(), currentItemStack, renderPerspective);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose,
                             MultiBufferSource buffer, int light, int overlay) {
        boolean firstPerson = context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        boolean thirdPerson = context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
        pose.pushPose();
        try {
            if (firstPerson || thirdPerson) {
                UmbrellaTransform transform = heldTransform(stack, firstPerson);
                if (transform != null && !UmbrellaTransform.IDENTITY.equals(transform)) {
                    pose.translate(transform.x(), transform.y(), transform.z());
                    pose.mulPose(Axis.YP.rotationDegrees(transform.yaw()));
                    pose.mulPose(Axis.XP.rotationDegrees(transform.pitch()));
                    pose.mulPose(Axis.ZP.rotationDegrees(transform.roll()));
                    pose.scale(transform.scale(), transform.scale(), transform.scale());
                }
            }
            super.renderByItem(stack, context, pose, buffer, light, overlay);
        } finally {
            pose.popPose();
        }
    }

    private static UmbrellaTransform heldTransform(ItemStack stack, boolean firstPerson) {
        Minecraft mc = Minecraft.getInstance();
        boolean local = mc.player != null && (stack == mc.player.getMainHandItem() || stack == mc.player.getOffhandItem());
        boolean preview = local && mc.screen instanceof UmbrellaConfigScreen screen && screen.isEditing(stack);
        UmbrellaTransform synced = firstPerson ? stack.get(ModComponents.UMBRELLA_FIRST_TRANSFORM.get())
                : stack.get(ModComponents.UMBRELLA_HELD_TRANSFORM.get());
        if (!preview && synced != null && synced.isValid(1.0F)) return synced;
        return local ? UmbrellaClientConfig.transform(((UmbrellaItem) stack.getItem()).getUmbrellaType(), firstPerson) : null;
    }
}
