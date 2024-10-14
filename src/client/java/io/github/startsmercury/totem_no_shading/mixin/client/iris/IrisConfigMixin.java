package io.github.startsmercury.totem_no_shading.mixin.client.iris;

import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.irisshaders.iris.config.IrisConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IrisConfig.class)
public class IrisConfigMixin {
    @Shadow(remap = false)
    private boolean enableShaders;

    @Inject(method = "load", at = @At("RETURN"), remap = false)
    private void onLoad(final CallbackInfo callback) {
        TotemNoShadingImpl.setEnabled(!this.enableShaders);
    }

    @Inject(method = "setShadersEnabled", at = @At("HEAD"), remap = false)
    private void onShadersEnabledChanged(
        final boolean enabled,
        final CallbackInfo callback
    ) {
        TotemNoShadingImpl.setEnabled(!enabled);
    }
}

