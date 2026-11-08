package com.shu0cheng.umbrellamod.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.shu0cheng.umbrellamod.block.PlacedUmbrellaBlock;
import com.shu0cheng.umbrellamod.item.UmbrellaTransform;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoVertex;

import java.util.ArrayList;
import java.util.List;

/** Immutable block mesh inputs with quads baked on first use by the chunk renderer. */
final class PlacedUmbrellaBakedModel implements BakedModel {
    private final PlacedUmbrellaMesh mesh;
    private final TextureAtlasSprite sprite;
    private final Matrix4f placement;
    private volatile List<BakedQuad> quads;

    PlacedUmbrellaBakedModel(PlacedUmbrellaMesh mesh, TextureAtlasSprite sprite, UmbrellaType type, BlockState state) {
        this.mesh = mesh;
        this.sprite = sprite;
        int rotation = state.getValue(PlacedUmbrellaBlock.ROTATION);
        boolean inverted = state.getValue(PlacedUmbrellaBlock.INVERTED);
        boolean half = state.getValue(PlacedUmbrellaBlock.HALF);
        UmbrellaTransform transform = UmbrellaBlockDevConfig.get(type, state.getValue(PlacedUmbrellaBlock.OPEN),
                inverted, half, state.getValue(PlacedUmbrellaBlock.LEANING),
                inverted ? "inverted" : UmbrellaBlockDevConfig.variant(rotation));

        float yaw = rotation * 22.5F;
        if (rotation == 2 || rotation == 4 || rotation == 10 || rotation == 12) yaw += 180.0F;
        placement = new Matrix4f().translation(0.5F, half ? -0.5F : 0, 0.5F)
                .rotate(Axis.YP.rotationDegrees(yaw))
                .translate(transform.x(), transform.y(), transform.z())
                .rotate(Axis.YP.rotationDegrees(transform.yaw()))
                .rotate(Axis.XP.rotationDegrees(transform.pitch()))
                .rotate(Axis.ZP.rotationDegrees(transform.roll()));
        if (!inverted) {
            placement.rotate(Axis.YP.rotationDegrees(-90.0F)).translate(-6F / 16, -9F / 16, -14F / 16);
        }
        placement.scale(transform.scale() * (type == UmbrellaType.OIL_PAPER ? 1.2F : 1.0F));
    }

    boolean renderHandleOutline(PoseStack matrices, VertexConsumer lines, double x, double y, double z) {
        if (mesh.handles().isEmpty()) return false;
        matrices.pushPose();
        try {
            matrices.translate(x, y, z);
            matrices.mulPose(placement);
            for (PlacedUmbrellaMesh.HandleOutline handle : mesh.handles()) {
                matrices.pushPose();
                try {
                    matrices.mulPose(handle.toModel());
                    LevelRenderer.renderLineBox(matrices, lines, handle.bounds(), 0, 0, 0, 0.4F);
                } finally {
                    matrices.popPose();
                }
            }
        } finally {
            matrices.popPose();
        }
        return true;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
        if (side != null) return List.of();
        List<BakedQuad> result = quads;
        if (result != null) return result;
        synchronized (this) {
            if (quads == null) {
                List<BakedQuad> baked = new ArrayList<>();
                for (GeoVertex[] face : mesh.faces()) {
                    appendFace(baked, face, false);
                    if (mesh.twoSided()) appendFace(baked, face, true);
                }
                quads = List.copyOf(baked);
            }
            return quads;
        }
    }

    private void appendFace(List<BakedQuad> output, GeoVertex[] face, boolean reverse) {
        Vector3f[] positions = new Vector3f[4];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = placement.transformPosition(new Vector3f(face[reverse ? 3 - i : i].position()));
        }
        Vector3f normal = new Vector3f(positions[1]).sub(positions[0])
                .cross(new Vector3f(positions[2]).sub(positions[0]));
        // The folded pose collapses some faces to zero area.
        if (normal.lengthSquared() < 1.0E-16F) return;
        normal.normalize();

        int stride = DefaultVertexFormat.BLOCK.getVertexSize() / Integer.BYTES;
        int[] data = new int[4 * stride];
        int packedNormal = (byte) (normal.x * 127) & 255
                | ((byte) (normal.y * 127) & 255) << 8
                | ((byte) (normal.z * 127) & 255) << 16;
        for (int i = 0; i < positions.length; i++) {
            GeoVertex vertex = face[reverse ? 3 - i : i];
            int offset = i * stride;
            data[offset] = Float.floatToRawIntBits(positions[i].x);
            data[offset + 1] = Float.floatToRawIntBits(positions[i].y);
            data[offset + 2] = Float.floatToRawIntBits(positions[i].z);
            data[offset + 3] = -1;
            data[offset + 4] = Float.floatToRawIntBits(sprite.getU(Mth.clamp(vertex.texU(), 0, 1)));
            data[offset + 5] = Float.floatToRawIntBits(sprite.getV(Mth.clamp(vertex.texV(), 0, 1)));
            data[offset + 7] = packedNormal;
        }
        output.add(new BakedQuad(data, -1, Direction.getNearest(normal.x, normal.y, normal.z), sprite, true));
    }

    @Override public boolean useAmbientOcclusion() { return false; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean usesBlockLight() { return true; }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public TextureAtlasSprite getParticleIcon() { return sprite; }
    @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
    @Override public ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }
}
