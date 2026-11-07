package com.shu0cheng.umbrellamod.block;

import com.mojang.serialization.MapCodec;
import com.shu0cheng.umbrellamod.item.UmbrellaState;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import com.shu0cheng.umbrellamod.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import com.shu0cheng.umbrellamod.item.UmbrellaItem;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;

public class PlacedUmbrellaBlock extends BaseEntityBlock {
   public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 15);
   public static final BooleanProperty OPEN = BooleanProperty.create("open");
   public static final BooleanProperty INVERTED = BooleanProperty.create("inverted");
   public static final BooleanProperty HALF = BooleanProperty.create("half");
   public static final EnumProperty<UmbrellaPose> POSE=EnumProperty.create("pose",UmbrellaPose.class);
   private final UmbrellaType type;
   public PlacedUmbrellaBlock(Properties properties, UmbrellaType type) {
      super(properties);
      this.type = type;
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, 0)).setValue(OPEN, false))
               .setValue(INVERTED, false))
            .setValue(HALF, false).setValue(POSE,UmbrellaPose.LEGACY)
      );
   }

   public UmbrellaType umbrellaType() {
      return this.type;
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return MapCodec.unit(this);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{ROTATION, OPEN, INVERTED, HALF, POSE});
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new PlacedUmbrellaBlockEntity(pos, state);
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   @Override
   protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
      return List.of(this.recoverItem(state, params.getOptionalParameter(LootContextParams.BLOCK_ENTITY)));
   }

   @Override
   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      ItemStack clone = this.recoverItem(state, level.getBlockEntity(pos));
      // Creative picking creates a new item, not another copy of its animation ID.
      clone.remove(GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get());
      return clone;
   }

   private ItemStack recoverItem(BlockState state, @Nullable BlockEntity entity) {
      ItemStack stack = entity instanceof PlacedUmbrellaBlockEntity placed ? placed.getUmbrella() : ItemStack.EMPTY;
      if (stack.isEmpty() || stack.getItem() != ModItems.itemFor(this.type)) {
         stack = new ItemStack(ModItems.itemFor(this.type));
      }
      stack.setCount(1);
      stack.set(ModComponents.UMBRELLA_STATE.get(), new UmbrellaState(state.getValue(OPEN), 0L));
      return stack;
   }

   protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return UmbrellaGeometry.footprint(state).get(BlockPos.ZERO).shape();
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      var player=context.getPlayer();
      if(player==null) return null;
      boolean open = UmbrellaItem.isOpen(context.getItemInHand());
      UmbrellaPose pose = UmbrellaPose.select(context.getClickedFace(),player.getXRot());
      boolean inverted = pose == UmbrellaPose.HANGING;
      boolean half = inverted
         && context.getClickLocation().y - context.getClickedPos().getY() < 0.5D;
      return defaultBlockState().setValue(OPEN,open)
         .setValue(POSE,pose)
         .setValue(INVERTED,inverted)
         .setValue(HALF,half)
         .setValue(ROTATION,UmbrellaPose.rotation(player.getYRot()));
   }

   @Override
   protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos) {
      return true;
   }

   @Override
   protected BlockState updateShape(BlockState state,Direction direction,BlockState neighbour,LevelAccessor level,BlockPos pos,BlockPos neighbourPos) {
      return state;
   }

   @Override
   protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving) {
      if(!state.is(replacement.getBlock())) UmbrellaStructure.removeParts(level,pos,state);
      super.onRemove(state,level,pos,replacement,moving);
   }

   @Override protected BlockState rotate(BlockState state,Rotation rotation) {
      return state.setValue(ROTATION,rotation.rotate(state.getValue(ROTATION),16));
   }
   @Override protected BlockState mirror(BlockState state,Mirror mirror) {
      return state.setValue(ROTATION,mirror.mirror(state.getValue(ROTATION),16));
   }
}
