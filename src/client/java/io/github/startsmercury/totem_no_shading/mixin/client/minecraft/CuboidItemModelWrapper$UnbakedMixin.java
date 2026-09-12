package io.github.startsmercury.totem_no_shading.mixin.client.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import io.github.startsmercury.totem_no_shading.impl.client.TransformQuadCollectionRenderType;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CuboidItemModelWrapper.Unbaked.class)
public class CuboidItemModelWrapper$UnbakedMixin {
    @Final
    @Shadow
    private Identifier model;

    @ModifyExpressionValue(
        method = "bake",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/ResolvedModel;bakeTopGeometry(" +
                "Lnet/minecraft/client/resources/model/sprite/TextureSlots;" +
                "Lnet/minecraft/client/resources/model/ModelBaker;" +
                "Lnet/minecraft/client/renderer/block/dispatch/ModelState;" +
            ")Lnet/minecraft/client/resources/model/geometry/QuadCollection;"
        )
    )
    private QuadCollection modifyRenderType(QuadCollection original) {
        if (!TotemNoShadingImpl.TARGET_MODEL.equals(this.model)) {
            return original;
        }

        return new TransformQuadCollectionRenderType().transform(original);
    }
}
