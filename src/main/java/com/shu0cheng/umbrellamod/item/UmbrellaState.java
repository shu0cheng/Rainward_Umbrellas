package com.shu0cheng.umbrellamod.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Persistent state of an umbrella stack.
 *
 * @param open      whether the canopy is deployed
 * @param changedAt game time of the last toggle, used by the client for the animation
 */
public record UmbrellaState(boolean open, long changedAt) {
    public static final Codec<UmbrellaState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("open").forGetter(UmbrellaState::open),
            Codec.LONG.fieldOf("changed_at").forGetter(UmbrellaState::changedAt)
    ).apply(instance, UmbrellaState::new));

    public static final StreamCodec<ByteBuf, UmbrellaState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, UmbrellaState::open,
            ByteBufCodecs.VAR_LONG, UmbrellaState::changedAt,
            UmbrellaState::new
    );

    public UmbrellaState withOpen(boolean open, long gameTime) {
        return new UmbrellaState(open, gameTime);
    }
}
