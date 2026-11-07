package com.shu0cheng.umbrellamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaAnimationAdapter;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class UmbrellaGeoRenderer extends GeoItemRenderer<UmbrellaItem> {
   private final String id;

   public UmbrellaGeoRenderer(String id) {
      super(new DefaultedItemGeoModel<>(UmbrellaMod.id(id)));
      this.id = id;
   }

   public RenderType getRenderType(UmbrellaItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return this.id.equals("oil_paper_umbrella") ? RenderType.entityCutoutNoCull(texture) : RenderType.entityCutout(texture);
   }

   public void preRender(
      PoseStack pose,
      UmbrellaItem animatable,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int color
   ) {
      super.preRender(pose, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
      if (!isReRender && UmbrellaClientConfig.firstPersonFixed()) {
         boolean firstPerson = this.renderPerspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            || this.renderPerspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
         if (firstPerson && this.currentItemStack != null) {
            if (UmbrellaItem.isOpen(this.currentItemStack)) {
               Minecraft mc = Minecraft.getInstance();
               if (mc.player != null) {
                  float pitch = mc.player.getViewXRot(partialTick);
                  Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
                  float yaw = (float)Math.toRadians(camera.getYRot());
                  Vector3f axis = new Vector3f((float)Math.cos(yaw), 0.0F, (float)Math.sin(yaw));
                  Matrix4f current = new Matrix4f(pose.last().pose());
                  Matrix4f correction = new Matrix4f().rotation((float)Math.toRadians(-pitch), axis);
                  pose.last().pose().set(correction.mul(current));
               }
            }
         }
      }
   }

   public long getInstanceId(UmbrellaItem animatable) {
      boolean display = this.renderPerspective == ItemDisplayContext.FIXED || this.renderPerspective == ItemDisplayContext.GUI;
      return UmbrellaAnimationAdapter.id(this.currentItemStack, display);
   }

   public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
      boolean held = context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
         || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
         || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
         || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
      pose.pushPose();
      if (held) {
         boolean first = context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
         UmbrellaClientConfig.Transform t = first
            ? UmbrellaClientConfig.first(((UmbrellaItem)stack.getItem()).getUmbrellaType())
            : UmbrellaClientConfig.held(((UmbrellaItem)stack.getItem()).getUmbrellaType());
         pose.translate(t.x, t.y, t.z);
         pose.mulPose(Axis.YP.rotationDegrees(t.yaw));
         pose.mulPose(Axis.XP.rotationDegrees(t.pitch));
         pose.mulPose(Axis.ZP.rotationDegrees(t.roll));
         pose.scale(t.scale, t.scale, t.scale);
      }

      try {
         super.renderByItem(stack, context, pose, buffer, light, overlay);
      } finally {
         pose.popPose();
      }
   }
}
