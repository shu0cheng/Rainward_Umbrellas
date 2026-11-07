package com.shu0cheng.umbrellamod.item;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import net.minecraft.resources.ResourceLocation;

public enum UmbrellaType {
   TELESCOPIC("telescopic", 0.52F, 0.21F, 1.34F, 0.62F, 10, UmbrellaType.HandleStyle.PLASTIC, 10133672, 3027256, 5922664, 3562644),
   STRAIGHT("straight", 0.62F, 0.24F, 1.46F, 1.42F, 12, UmbrellaType.HandleStyle.WOOD, 8017974, 6176290, 3816256, 3421758),
   OIL_PAPER("oil_paper", 0.68F, 0.2F, 1.5F, 1.47F, 12, UmbrellaType.HandleStyle.BAMBOO, 9333306, 7228454, 5913118, 12875316),
   BAMBOO_OIL_PAPER("bamboo_oil_paper", 0.68F, 0.2F, 1.5F, 1.47F, 12, UmbrellaType.HandleStyle.BAMBOO, 9333306, 7228454, 5913118, 12875316);

   private final String id;
   private final float canopyRadius;
   private final float canopyDrop;
   private final float openHubY;
   private final float closedHubY;
   private final int segments;
   private final UmbrellaType.HandleStyle handleStyle;
   private final int shaftColor;
   private final int handleColor;
   private final int ribColor;
   private final int canopyTint;

   private UmbrellaType(
      String id,
      float canopyRadius,
      float canopyDrop,
      float openHubY,
      float closedHubY,
      int segments,
      UmbrellaType.HandleStyle handleStyle,
      int shaftColor,
      int handleColor,
      int ribColor,
      int canopyTint
   ) {
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
      return this.id;
   }

   public float animationTicks() {
      return switch (this) {
         case TELESCOPIC -> 12.0F;
         case STRAIGHT -> 16.8F;
         case OIL_PAPER, BAMBOO_OIL_PAPER -> 17.0F;
      };
   }

   public float canopyRadius() {
      return this.canopyRadius;
   }

   public float canopyDrop() {
      return this.canopyDrop;
   }

   public float openHubY() {
      return this.openHubY;
   }

   public float closedHubY() {
      return this.closedHubY;
   }

   public int segments() {
      return this.segments;
   }

   public UmbrellaType.HandleStyle handleStyle() {
      return this.handleStyle;
   }

   public int shaftColor() {
      return this.shaftColor;
   }

   public int handleColor() {
      return this.handleColor;
   }

   public int ribColor() {
      return this.ribColor;
   }

   public int canopyTint() {
      return this.canopyTint;
   }

   public ResourceLocation canopyTexture() {
      return UmbrellaMod.id("textures/item/canopy_" + this.id + ".png");
   }

   public ResourceLocation iconTexture() {
      return UmbrellaMod.id("textures/item/icon_" + this.id + ".png");
   }

   public static enum HandleStyle {
      PLASTIC,
      WOOD,
      BAMBOO;
   }
}
