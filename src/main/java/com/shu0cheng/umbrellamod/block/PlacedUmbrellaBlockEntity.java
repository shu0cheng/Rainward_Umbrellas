package com.shu0cheng.umbrellamod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PlacedUmbrellaBlockEntity extends BlockEntity {
   private ItemStack umbrella = ItemStack.EMPTY;

   public PlacedUmbrellaBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.PLACED_UMBRELLA.get(), pos, state);
   }

   public BlockState state() {
      return this.getBlockState();
   }

   public void setUmbrella(ItemStack stack) {
      this.umbrella = stack.copyWithCount(1);
      this.setChanged();
   }

   public ItemStack getUmbrella() {
      return this.umbrella.copy();
   }

   @Override
   protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
      super.saveAdditional(tag, registries);
      if (!this.umbrella.isEmpty()) {
         tag.put("UmbrellaItem", this.umbrella.save(registries));
      }
   }

   @Override
   protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
      super.loadAdditional(tag, registries);
      this.umbrella = ItemStack.parseOptional(registries, tag.getCompound("UmbrellaItem"));
   }

   @Override
   public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   @Override
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
