package com.shu0cheng.umbrellamod.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/** Networked custom transform for one umbrella display context. */
public record UmbrellaTransform(float scale, float x, float y, float z,
                                float yaw, float pitch, float roll) {
    public static final UmbrellaTransform IDENTITY = new UmbrellaTransform(1, 0, 0, 0, 0, 0, 0);

    public boolean isValid(float maxOffset) {
        return scale >= 0.1F && scale <= 3.0F
                && Math.abs(x) <= maxOffset && Math.abs(y) <= maxOffset && Math.abs(z) <= maxOffset
                && Math.abs(yaw) <= 180.0F && Math.abs(pitch) <= 180.0F && Math.abs(roll) <= 180.0F;
    }

    public UmbrellaTransform withScale(float value) {
        return new UmbrellaTransform(value, x, y, z, yaw, pitch, roll);
    }

    public UmbrellaTransform withX(float value) {
        return new UmbrellaTransform(scale, value, y, z, yaw, pitch, roll);
    }

    public UmbrellaTransform withY(float value) {
        return new UmbrellaTransform(scale, x, value, z, yaw, pitch, roll);
    }

    public UmbrellaTransform withZ(float value) {
        return new UmbrellaTransform(scale, x, y, value, yaw, pitch, roll);
    }

    public UmbrellaTransform withYaw(float value) {
        return new UmbrellaTransform(scale, x, y, z, value, pitch, roll);
    }

    public UmbrellaTransform withPitch(float value) {
        return new UmbrellaTransform(scale, x, y, z, yaw, value, roll);
    }

    public UmbrellaTransform withRoll(float value) {
        return new UmbrellaTransform(scale, x, y, z, yaw, pitch, value);
    }

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
