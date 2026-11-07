package com.shu0cheng.umbrellamod.item;

import com.shu0cheng.umbrellamod.block.ModBlocks;
import com.shu0cheng.umbrellamod.block.PlacedUmbrellaBlock;
import com.shu0cheng.umbrellamod.block.UmbrellaStructure;
import com.shu0cheng.umbrellamod.client.UmbrellaAnimation;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import com.shu0cheng.umbrellamod.registry.ModSounds;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.constant.DataTickets;

public class UmbrellaItem extends Item implements GeoItem {
   private final AnimationCache geoCache = new AnimationCache(this);
   private final Map<AnimationController<?>, UmbrellaAnimationState> animationStates = new WeakHashMap<>();
   public static final int TOGGLE_COOLDOWN_TICKS = 24;
   private static final RawAnimation POSE_OPEN_ANIM = RawAnimation.begin().thenLoop("pose_open");
   private static final RawAnimation POSE_CLOSED_ANIM = RawAnimation.begin().thenLoop("pose_closed");
   private static final RawAnimation DISPLAY_OPEN_ANIM = RawAnimation.begin().thenLoop("display_open");
   private static final RawAnimation OPEN_ANIM = RawAnimation.begin().thenPlay("open").thenLoop("pose_open");
   private static final RawAnimation CLOSE_ANIM = RawAnimation.begin().thenPlay("close").thenLoop("pose_closed");
   private final UmbrellaType type;

   public UmbrellaItem(UmbrellaType type, Properties properties) {
      super(properties);
      this.type = type;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      AnimationController<UmbrellaItem> controller = new AnimationController<>(this, "main", 0, this::animationState);
      controllers.add(controller);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.geoCache;
   }

   public AnimatableInstanceCache geoCache() {
      return this.geoCache;
   }

   public UmbrellaType getUmbrellaType() {
      return this.type;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      toggle(player, hand);
      // Consume even during cooldown, so a main-hand umbrella cannot fall through
      // and unexpectedly use the offhand. CONSUME also avoids vanilla hand swing.
      return InteractionResultHolder.consume(player.getItemInHand(hand));
   }

   public boolean toggle(Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!(player.level() instanceof ServerLevel level) || player.isSpectator()
         || stack.getItem() != this || player.getCooldowns().isOnCooldown(this)) {
         return false;
      }

      boolean open = !isOpen(stack);
      GeoItem.getOrAssignId(stack, level);
      stack.set(ModComponents.UMBRELLA_STATE.get(), new UmbrellaState(open, level.getGameTime()));
      player.getCooldowns().addCooldown(this, TOGGLE_COOLDOWN_TICKS);
      level.playSound(null, player.getX(), player.getEyeY(), player.getZ(),
         open ? ModSounds.UMBRELLA_OPEN.get() : SoundEvents.WOOL_BREAK,
         SoundSource.PLAYERS, open ? 0.9F : 0.85F, open ? 1.0F : 1.15F);
      if (player instanceof ServerPlayer serverPlayer) {
         serverPlayer.inventoryMenu.broadcastChanges();
      }
      return true;
   }

   public InteractionResult useOn(UseOnContext context) {
      Player player = context.getPlayer();
      if (player == null || !player.isShiftKeyDown()) return InteractionResult.PASS;
      return UmbrellaStructure.place((PlacedUmbrellaBlock)ModBlocks.forType(this.type),new BlockPlaceContext(context));
   }

   public boolean isFoil(ItemStack stack) {
      return false;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      boolean open = isOpen(stack);
      tooltip.add(
         Component.translatable(open ? "tooltip.umbrella.open" : "tooltip.umbrella.closed").withStyle(open ? ChatFormatting.AQUA : ChatFormatting.GRAY)
      );
      tooltip.add(Component.translatable("tooltip.umbrella.hint").withStyle(ChatFormatting.DARK_GRAY));
      tooltip.add(Component.translatable("tooltip.umbrella.placement").withStyle(ChatFormatting.DARK_GRAY));
   }

   public static boolean isOpen(ItemStack stack) {
      if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof UmbrellaItem)) {
         return false;
      }
      UmbrellaState state = stack.get(ModComponents.UMBRELLA_STATE.get());
      return state != null && state.open();
   }

   public static UmbrellaState getState(ItemStack stack) {
      UmbrellaState state = stack.get(ModComponents.UMBRELLA_STATE.get());
      return state == null ? new UmbrellaState(false, 0L) : state;
   }

   private PlayState animationState(AnimationState<UmbrellaItem> state) {
      ItemStack stack = (ItemStack)state.getData(DataTickets.ITEMSTACK);
      ItemDisplayContext context = (ItemDisplayContext)state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);
      if (context != ItemDisplayContext.FIXED && context != ItemDisplayContext.GUI) {
         boolean open = isOpen(stack);
         if (context == ItemDisplayContext.NONE) {
            return state.setAndContinue(open ? POSE_OPEN_ANIM : POSE_CLOSED_ANIM);
         }
         UmbrellaState authoritative = getState(stack);
         boolean recent = UmbrellaAnimation.isRecent(authoritative, this.type.animationTicks());
         UmbrellaAnimationState observed = this.animationStates.computeIfAbsent(state.getController(), ignored -> new UmbrellaAnimationState());
         return state.setAndContinue(switch (observed.observe(open, recent)) {
            case POSE_OPEN -> POSE_OPEN_ANIM;
            case POSE_CLOSED -> POSE_CLOSED_ANIM;
            case OPEN -> OPEN_ANIM;
            case CLOSE -> CLOSE_ANIM;
         });
      } else {
         return state.setAndContinue(DISPLAY_OPEN_ANIM);
      }
   }

   public void clearAnimationCache() {
      this.animationStates.clear();
      this.geoCache.clear();
   }

   public static boolean isUmbrella(ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() instanceof UmbrellaItem;
   }

   private static final class AnimationCache extends SingletonAnimatableInstanceCache {
      private AnimationCache(UmbrellaItem item) {
         super(item);
      }

      private void clear() {
         this.managers.clear();
      }
   }
}
