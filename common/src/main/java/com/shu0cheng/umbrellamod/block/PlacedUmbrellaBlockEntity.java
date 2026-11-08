package com.shu0cheng.umbrellamod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PlacedUmbrellaBlockEntity extends BlockEntity {
    private static final String STACK_TAG = "umbrella_stack";
    private ItemStack storedStack = ItemStack.EMPTY;

    public PlacedUmbrellaBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLACED_UMBRELLA.get(), pos, state);
    }

    public void storeStack(ItemStack stack) {
        storedStack = stack.copyWithCount(1);
        setChanged();
    }

    public ItemStack copyStoredStack() {
        return storedStack.copy();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!storedStack.isEmpty()) {
            tag.put(STACK_TAG, storedStack.saveOptional(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        storedStack = tag.contains(STACK_TAG, Tag.TAG_COMPOUND)
                ? ItemStack.parseOptional(registries, tag.getCompound(STACK_TAG)).copyWithCount(1)
                : ItemStack.EMPTY;
    }
}
