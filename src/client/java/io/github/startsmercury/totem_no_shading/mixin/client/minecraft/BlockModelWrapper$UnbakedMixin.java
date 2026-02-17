package io.github.startsmercury.totem_no_shading.mixin.client.minecraft;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(BlockModelWrapper.Unbaked.class)
public class BlockModelWrapper$UnbakedMixin {
    @Final
    @Shadow
    private Identifier model;

    @ModifyExpressionValue(
        method = "bake",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/QuadCollection;getAll()Ljava/util/List;"
        )
    )
    private List<BakedQuad> modifyRenderType(final List<BakedQuad> quads) {
        if (!TotemNoShadingImpl.TARGET_MODEL.equals(this.model)) {
            return quads;
        }

        final var builder = ImmutableList.<BakedQuad>builder();

        for (final var quad : quads) {
            final var spriteInfo = quad.spriteInfo();
            final var oldItemRenderType = spriteInfo.itemRenderType();
            final var newItemRenderType =
                oldItemRenderType == Sheets.cutoutItemSheet() ?
                TotemNoShadingImpl.cutoutItemSheet() :
                oldItemRenderType == Sheets.translucentItemSheet() ?
                TotemNoShadingImpl.translucentItemSheet() :
                null;

            builder.add(newItemRenderType == null
                ? quad
                : new BakedQuad(
                    quad.position0(),
                    quad.position1(),
                    quad.position2(),
                    quad.position3(),
                    quad.packedUV0(),
                    quad.packedUV1(),
                    quad.packedUV2(),
                    quad.packedUV3(),
                    quad.tintIndex(),
                    quad.direction(),
                    new BakedQuad.SpriteInfo(
                        spriteInfo.sprite(),
                        spriteInfo.layer(),
                        TotemNoShadingImpl.translucentItemSheet()
                    ),
                    quad.shade(),
                    quad.lightEmission()
                )
            );
        }

        return builder.build();
    }
}
