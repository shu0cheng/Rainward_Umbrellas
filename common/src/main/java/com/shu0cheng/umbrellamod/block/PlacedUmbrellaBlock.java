package com.shu0cheng.umbrellamod.block;

import com.mojang.serialization.MapCodec;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import com.shu0cheng.umbrellamod.item.UmbrellaState;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import com.shu0cheng.umbrellamod.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Placed umbrella decoration: no collision, rendered entirely by a BER. */
public class PlacedUmbrellaBlock extends BaseEntityBlock {
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 15);
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final BooleanProperty INVERTED = BooleanProperty.create("inverted");
    public static final BooleanProperty HALF = BooleanProperty.create("half");
    public static final BooleanProperty LEANING = BooleanProperty.create("leaning");

    private final UmbrellaType type;

    public PlacedUmbrellaBlock(Properties properties, UmbrellaType type) {
        super(properties);
        this.type = type;
        registerDefaultState(stateDefinition.any()
                .setValue(ROTATION, 0)
                .setValue(OPEN, false)
                .setValue(INVERTED, false)
                .setValue(HALF, false)
                .setValue(LEANING, false));
    }

    public UmbrellaType umbrellaType() {
        return type;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, OPEN, INVERTED, HALF, LEANING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlacedUmbrellaBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              BlockEntity blockEntity,
                              ItemStack tool) {
        if (!level.isClientSide) {
            ItemStack drop = blockEntity instanceof PlacedUmbrellaBlockEntity placed
                    ? placed.copyStoredStack() : ItemStack.EMPTY;
            if (!drop.is(ModItems.itemFor(type))) {
                drop = new ItemStack(ModItems.itemFor(type));
            }
            drop.set(ModComponents.UMBRELLA_STATE.get(), new UmbrellaState(state.getValue(OPEN), -1L));
            Block.popResource(level, pos, drop);
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }
}
