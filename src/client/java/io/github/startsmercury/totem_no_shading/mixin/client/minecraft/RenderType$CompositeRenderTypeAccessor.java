package io.github.startsmercury.totem_no_shading.mixin.client.minecraft;

import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.CompositeRenderType.class)
public interface RenderType$CompositeRenderTypeAccessor {
    @Accessor
    RenderType.CompositeState getState();
}
