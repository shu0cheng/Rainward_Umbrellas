package com.shu0cheng.umbrellamod.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/** Networked custom transform for one umbrella display context. */
public record UmbrellaTransform(float scale, float x, float y, float z,
                                float yaw, float pitch, float roll) {
    public static final Codec<UmbrellaTransform> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.fieldOf("scale").forGetter(UmbrellaTransform::scale),
            Codec.FLOAT.fieldOf("x").forGetter(UmbrellaTransform::x),
            Codec.FLOAT.fieldOf("y").forGetter(UmbrellaTransform::y),
            Codec.FLOAT.fieldOf("z").forGetter(UmbrellaTransform::z),
            Codec.FLOAT.fieldOf("yaw").forGetter(UmbrellaTransform::yaw),
            Codec.FLOAT.fieldOf("pitch").forGetter(UmbrellaTransform::pitch),
            Codec.FLOAT.fieldOf("roll").forGetter(UmbrellaTransform::roll)
    ).apply(i, UmbrellaTransform::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, UmbrellaTransform> STREAM_CODEC =
            StreamCodec.of((buf, value) -> {
                buf.writeFloat(value.scale());
                buf.writeFloat(value.x());
                buf.writeFloat(value.y());
                buf.writeFloat(value.z());
                buf.writeFloat(value.yaw());
                buf.writeFloat(value.pitch());
                buf.writeFloat(value.roll());
            }, buf -> new UmbrellaTransform(buf.readFloat(), buf.readFloat(), buf.readFloat(),
                    buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat()));
}
