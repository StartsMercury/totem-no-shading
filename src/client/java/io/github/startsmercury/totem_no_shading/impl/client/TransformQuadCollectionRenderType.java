package io.github.startsmercury.totem_no_shading.impl.client;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.core.Direction;

public class TransformQuadCollectionRenderType {
    /**
     * Whether changes warrant building a new quad collection.
     */
    private boolean unmodified = true;

    public QuadCollection transform(final QuadCollection quads) {
        this.unmodified = true;

        final var builder = new QuadCollection.Builder();

        for (final var quad : quads.getQuads(null)) {
            builder.addUnculledFace(this.withQuad(quad));
        }

        for (final Direction direction : Direction.values()) {
            for (final var quad : quads.getQuads(direction)) {
                builder.addCulledFace(direction, this.withQuad(quad));
            }
        }

        if (unmodified) {
            return quads;
        } else {
            return builder.build();
        }
    }

    public BakedQuad withQuad(final BakedQuad quad) {
        final var materialInfo = quad.materialInfo();
        final var oldItemRenderType = materialInfo.itemRenderType();
        final var newItemRenderType =
            oldItemRenderType == Sheets.cutoutItemSheet() ?
            TotemNoShadingImpl.cutoutItemSheet() :
            oldItemRenderType == Sheets.translucentItemSheet() ?
            TotemNoShadingImpl.translucentItemSheet() :
            null;

        if (newItemRenderType == null) {
            return quad;
        } else {
            unmodified = false;
            return new BakedQuad(
                quad.position0(),
                quad.position1(),
                quad.position2(),
                quad.position3(),
                quad.packedUV0(),
                quad.packedUV1(),
                quad.packedUV2(),
                quad.packedUV3(),
                quad.direction(),
                new BakedQuad.MaterialInfo(
                    materialInfo.sprite(),
                    materialInfo.layer(),
                    TotemNoShadingImpl.translucentItemSheet(),
                    materialInfo.tintIndex(),
                    materialInfo.shade(),
                    materialInfo.lightEmission()
                )
            );
        }
    }
}
