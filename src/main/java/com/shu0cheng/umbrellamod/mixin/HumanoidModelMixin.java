package com.shu0cheng.umbrellamod.mixin;

import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import com.shu0cheng.umbrellamod.item.UmbrellaState;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {
   @Shadow
   @Final
   public ModelPart rightArm;
   @Shadow
   @Final
   public ModelPart leftArm;

   @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
   private void umbrella$poseArms(
      LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci
   ) {
      if (entity instanceof Player player) {
         ItemStack main = player.getMainHandItem();
         ItemStack off = player.getOffhandItem();
         boolean mainOpen = UmbrellaItem.isOpen(main);
         boolean offOpen = UmbrellaItem.isOpen(off);
         if (mainOpen || offOpen) {
            boolean mainIsRight = player.getMainArm() == HumanoidArm.RIGHT;
            ModelPart mainArm = mainIsRight ? this.rightArm : this.leftArm;
            ModelPart offArm = mainIsRight ? this.leftArm : this.rightArm;
            if (mainOpen) {
               poseArm(mainArm, mainIsRight, limbSwing, limbSwingAmount);
               if (!offOpen) {
                  poseOpeningReach(offArm, !mainIsRight, openingAmount(player, main));
               }
            }

            if (offOpen) {
               poseArm(offArm, !mainIsRight, limbSwing, limbSwingAmount);
               if (!mainOpen) {
                  poseOpeningReach(mainArm, mainIsRight, openingAmount(player, off));
               }
            }
         }
      }
   }

   private static float openingAmount(Player player, ItemStack stack) {
      UmbrellaState state = UmbrellaItem.getState(stack);
      if (state.open() && player.level() != null) {
         UmbrellaType type = ((UmbrellaItem)stack.getItem()).getUmbrellaType();

         float ticks = type.animationTicks();
         float t = Mth.clamp((float)(player.level().getGameTime() - state.changedAt()) / ticks, 0.0F, 1.0F);
         return Mth.sin(t * (float) Math.PI);
      } else {
         return 0.0F;
      }
   }

   private static void poseArm(ModelPart arm, boolean right, float limbSwing, float limbSwingAmount) {
      float side = right ? 1.0F : -1.0F;
      float amount = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
      float sway = Mth.cos(limbSwing * 0.6662F) * 0.075F * amount;
      float sideSway = Mth.sin(limbSwing * 0.6662F) * 0.03F * amount;
      arm.xRot = -0.12F + sway;
      arm.yRot = 0.0F;
      arm.zRot = 0.14F * side + sideSway;
   }

   private static void poseOpeningReach(ModelPart arm, boolean right, float amount) {
      if (!(amount <= 0.001F)) {
         float side = right ? 1.0F : -1.0F;
         arm.xRot = Mth.lerp(amount, arm.xRot, -0.95F);
         arm.yRot = side * 0.18F * amount;
         arm.zRot = Mth.lerp(amount, arm.zRot, -0.52F * side);
      }
   }
}
