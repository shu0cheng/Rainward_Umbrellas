package com.shu0cheng.umbrellamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shu0cheng.umbrellamod.UmbrellaMod;
import com.shu0cheng.umbrellamod.item.UmbrellaType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.keyframe.BoneAnimation;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.loading.FileLoader;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.GeometryTree;
import software.bernie.geckolib.util.RenderUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Flattens the authored geometry once per pose without creating an item or running its renderer. */
record PlacedUmbrellaMesh(List<GeoVertex[]> faces, boolean twoSided, List<HandleOutline> handles) {
    enum Pose { CLOSED, UPRIGHT, INVERTED }

    record HandleOutline(Matrix4f toModel, AABB bounds) {}

    static PlacedUmbrellaMesh load(ResourceManager resources, UmbrellaType type, Pose pose) {
        String name = type.id() + "_umbrella";
        String path = switch (pose) {
            case CLOSED -> "geo/item/" + name;
            case UPRIGHT -> "geo/block/" + name + "_upright";
            case INVERTED -> "geo/block/" + name + "_inverted";
        };
        var model = FileLoader.loadModelFile(UmbrellaMod.id(path + ".geo.json"), resources);
        var geometry = BakedModelFactory.getForNamespace(UmbrellaMod.MODID).constructGeoModel(GeometryTree.fromModel(model));

        Map<String, BoneAnimation> closedBones = new HashMap<>();
        if (pose == Pose.CLOSED) {
            Animation closed = FileLoader.loadAnimationsFile(
                    UmbrellaMod.id("animations/item/" + name + ".animation.json"), resources).getAnimation("pose_closed");
            if (closed == null) throw new IllegalStateException("Missing pose_closed for " + name);
            for (BoneAnimation bone : closed.boneAnimations()) closedBones.put(bone.boneName(), bone);
        }

        List<GeoVertex[]> faces = new ArrayList<>();
        List<HandleOutline> handles = new ArrayList<>();
        PoseStack matrices = new PoseStack();
        // Match the origin adjustment formerly applied by GeoItemRenderer.
        matrices.translate(0.5, 0.51, 0.5);
        for (GeoBone bone : geometry.topLevelBones()) {
            appendBone(bone, matrices, closedBones, faces, handles, null);
        }
        return new PlacedUmbrellaMesh(List.copyOf(faces), pose == Pose.CLOSED && type == UmbrellaType.OIL_PAPER,
                List.copyOf(handles));
    }

    private static void appendBone(GeoBone bone, PoseStack matrices, Map<String, BoneAnimation> closedBones,
                                   List<GeoVertex[]> faces, List<HandleOutline> handles, HandleBounds parentHandle) {
        BoneAnimation animation = closedBones.get(bone.getName());
        if (animation != null) applyPose(bone, animation);
        matrices.pushPose();
        RenderUtil.prepMatrixForBone(matrices, bone);
        HandleBounds handle = parentHandle;
        if (handle == null && ("手柄".equals(bone.getName()) || "handle".equalsIgnoreCase(bone.getName()))
                && Math.abs(matrices.last().pose().determinant()) > 1.0E-8F) {
            handle = new HandleBounds(matrices.last().pose());
        }
        if (!bone.isHidden()) {
            for (GeoCube cube : bone.getCubes()) {
                matrices.pushPose();
                RenderUtil.translateToPivotPoint(matrices, cube);
                RenderUtil.rotateMatrixAroundCube(matrices, cube);
                RenderUtil.translateAwayFromPivotPoint(matrices, cube);
                for (GeoQuad quad : cube.quads()) {
                    if (quad == null) continue;
                    GeoVertex[] vertices = new GeoVertex[4];
                    for (int i = 0; i < vertices.length; i++) {
                        GeoVertex vertex = quad.vertices()[i];
                        Vector3f position = matrices.last().pose().transformPosition(new Vector3f(vertex.position()));
                        vertices[i] = new GeoVertex(position, vertex.texU(), vertex.texV());
                        if (handle != null) handle.include(position);
                    }
                    faces.add(vertices);
                }
                matrices.popPose();
            }
        }
        if (!bone.isHidingChildren()) {
            for (GeoBone child : bone.getChildBones()) {
                appendBone(child, matrices, closedBones, faces, handles, handle);
            }
        }
        if (handle != null && handle != parentHandle && handle.min.isFinite()) handles.add(handle.outline());
        matrices.popPose();
    }

    private static final class HandleBounds {
        private final Matrix4f toModel;
        private final Matrix4f toHandle;
        private final Vector3f min = new Vector3f(Float.POSITIVE_INFINITY);
        private final Vector3f max = new Vector3f(Float.NEGATIVE_INFINITY);

        HandleBounds(Matrix4f toModel) {
            this.toModel = new Matrix4f(toModel);
            this.toHandle = new Matrix4f(toModel).invert();
        }

        void include(Vector3f position) {
            Vector3f local = toHandle.transformPosition(position, new Vector3f());
            min.min(local);
            max.max(local);
        }

        HandleOutline outline() {
            AABB bounds = new AABB(min.x, min.y, min.z, max.x, max.y, max.z).inflate(0.002);
            return new HandleOutline(toModel, bounds);
        }
    }

    private static void applyPose(GeoBone bone, BoneAnimation animation) {
        var rotation = animation.rotationKeyFrames();
        bone.setRotX(bone.getRotX() + first(rotation.xKeyframes(), 0));
        bone.setRotY(bone.getRotY() + first(rotation.yKeyframes(), 0));
        bone.setRotZ(bone.getRotZ() + first(rotation.zKeyframes(), 0));
        var position = animation.positionKeyFrames();
        bone.setPosX(first(position.xKeyframes(), 0));
        bone.setPosY(first(position.yKeyframes(), 0));
        bone.setPosZ(first(position.zKeyframes(), 0));
        var scale = animation.scaleKeyFrames();
        bone.setScaleX(first(scale.xKeyframes(), 1));
        bone.setScaleY(first(scale.yKeyframes(), 1));
        bone.setScaleZ(first(scale.zKeyframes(), 1));
    }

    private static float first(List<Keyframe<MathValue>> frames, float fallback) {
        return frames.isEmpty() ? fallback : (float) frames.getFirst().startValue().get();
    }
}
