package com.shu0cheng.umbrellamod.item;

import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.registry.ModComponents;
import com.shu0cheng.umbrellamod.registry.ModSounds;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import com.shu0cheng.umbrellamod.block.ModBlocks;
import com.shu0cheng.umbrellamod.block.PlacedUmbrellaBlock;
import com.shu0cheng.umbrellamod.registry.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.EnumMap;
import java.util.HashMap;

/**
 * An umbrella.  Right click toggles between the folded (closed) and deployed
 * (open) state.  While closed the item is rendered folded, matching the
 * inventory icon.
 */
public class UmbrellaItem extends Item implements GeoItem {
    private final AnimatableInstanceCache geoCache = new SingletonAnimatableInstanceCache(this);
    private static final Map<UmbrellaType, Boolean> CLIENT_OPEN = new EnumMap<>(UmbrellaType.class);
    private static final Map<Long, AnimPhase> ANIM_PHASES = new HashMap<>();
    public static final ThreadLocal<Boolean> STATIC_RENDER = ThreadLocal.withInitial(() -> false);
    private static final RawAnimation POSE_OPEN_ANIM = RawAnimation.begin().thenLoop("pose_open");
    private static final RawAnimation POSE_CLOSED_ANIM = RawAnimation.begin().thenLoop("pose_closed");
    private static final RawAnimation DISPLAY_OPEN_ANIM = RawAnimation.begin().thenLoop("display_open");
    private static final RawAnimation OPEN_ANIM = RawAnimation.begin().thenPlayAndHold("open");
    private static final RawAnimation CLOSE_ANIM = RawAnimation.begin().thenPlayAndHold("close");
    private final UmbrellaType type;

    public UmbrellaItem(UmbrellaType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<UmbrellaItem> controller = new AnimationController<>(this, "main", 0, this::animationState);
        controllers.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public AnimatableInstanceCache geoCache() {
        return geoCache;
    }

    public UmbrellaType getUmbrellaType() {
        return type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        ItemStack stack = player.getItemInHand(hand);
        boolean open = isOpen(stack);
        CLIENT_OPEN.put(type, !open);
        UmbrellaAnimationAdapter.id(stack);
        if (!level.isClientSide) {
            stack.set(ModComponents.UMBRELLA_STATE.get(), new UmbrellaState(!open, level.getGameTime()));
            if (open) {
                player.playSound(SoundEvents.WOOL_BREAK, 0.85F, 1.15F);
            } else {
                level.playSound(null, player.getX(), player.getEyeY(), player.getZ(),
                        ModSounds.UMBRELLA_OPEN.get(), SoundSource.PLAYERS, 0.9F, 1.0F);
            }
        }
        player.getCooldowns().addCooldown(this, 24);
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        BlockPos placePos = context.getClickedPos().relative(context.getClickedFace());
        if (!level.getBlockState(placePos).canBeReplaced()) {
            return InteractionResult.FAIL;
        }
        boolean open = isOpen(stack);
        boolean inverted = open && player.getXRot() < -45.0F;
        boolean leaning = !open && context.getClickedFace().getAxis().isHorizontal()
                && level.getBlockState(placePos.below()).isSolid();
        if (!inverted && !leaning && !level.getBlockState(placePos.below()).isSolid()) {
            return InteractionResult.FAIL;
        }
        boolean half = inverted
                && context.getClickedFace() != net.minecraft.core.Direction.DOWN
                && context.getClickedFace() != net.minecraft.core.Direction.UP
                && (context.getClickLocation().y - context.getClickedPos().getY()) < 0.5D;
        int rotation = Mth.floor((player.getYRot() + 180.0F) * 16.0F / 360.0F + 0.5F) & 15;
        BlockState state = ModBlocks.forType(type).defaultBlockState()
                .setValue(PlacedUmbrellaBlock.ROTATION, rotation)
                .setValue(PlacedUmbrellaBlock.OPEN, open)
                .setValue(PlacedUmbrellaBlock.INVERTED, inverted)
                .setValue(PlacedUmbrellaBlock.HALF, half)
                .setValue(PlacedUmbrellaBlock.LEANING, leaning);
        if (!level.isClientSide) {
            level.setBlock(placePos, state, 3);
            level.playSound(null, placePos, state.getBlock().defaultBlockState().getSoundType().getPlaceSound(),
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        stack.set(ModComponents.UMBRELLA_STATE.get(), new UmbrellaState(false, level.getGameTime()));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!stack.has(ModComponents.UMBRELLA_STATE.get()) && !level.isClientSide) {
            stack.set(ModComponents.UMBRELLA_STATE.get(), new UmbrellaState(false, level.getGameTime()));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        boolean open = isOpen(stack);
        tooltip.add(Component.translatable(open ? "tooltip.umbrella.open" : "tooltip.umbrella.closed")
                .withStyle(open ? ChatFormatting.AQUA : ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.umbrella.hint").withStyle(ChatFormatting.DARK_GRAY));
    }

    public static boolean isOpen(ItemStack stack) {
        UmbrellaState state = stack.get(ModComponents.UMBRELLA_STATE.get());
        return state != null && state.open();
    }

    public static UmbrellaState getState(ItemStack stack) {
        UmbrellaState state = stack.get(ModComponents.UMBRELLA_STATE.get());
        return state == null ? new UmbrellaState(false, 0L) : state;
    }

    private PlayState animationState(AnimationState<UmbrellaItem> state) {
        ItemStack stack = state.getData(DataTickets.ITEMSTACK);
        ItemDisplayContext context = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);
        if (context == ItemDisplayContext.FIXED || context == ItemDisplayContext.GUI) {
            return state.setAndContinue(DISPLAY_OPEN_ANIM);
        }
        if (STATIC_RENDER.get()) {
            boolean staticOpen = stack != null && isOpen(stack);
            return state.setAndContinue(staticOpen ? POSE_OPEN_ANIM : POSE_CLOSED_ANIM);
        }
        boolean open = UmbrellaAnimationAdapter.open(stack);
        long id = UmbrellaAnimationAdapter.id(stack);
        AnimPhase phase = ANIM_PHASES.get(id);
        if (phase == null) {
            ANIM_PHASES.put(id, new AnimPhase(open));
            return state.setAndContinue(open ? OPEN_ANIM : POSE_CLOSED_ANIM);
        }
        if (phase.open != open) {
            phase.open = open;
        }
        // Holding the last frame avoids returning to an empty/default pose after
        // the one-shot animation finishes.
        return state.setAndContinue(open ? OPEN_ANIM : CLOSE_ANIM);
    }

    private static final class AnimPhase {
        boolean open;

        AnimPhase(boolean open) {
            this.open = open;
        }
    }

    public static boolean isClientOpen(UmbrellaType type) {
        return CLIENT_OPEN.getOrDefault(type, false);
    }

    public static boolean isUmbrella(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof UmbrellaItem;
    }
}
