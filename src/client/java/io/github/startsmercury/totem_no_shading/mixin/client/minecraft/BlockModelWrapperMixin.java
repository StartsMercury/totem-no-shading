package io.github.startsmercury.totem_no_shading.mixin.client.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockModelWrapper.class)
public class BlockModelWrapperMixin {
    @ModifyReturnValue(method = "method_76559", at = @At("RETURN"))
    private static RenderType a(
        final RenderType original,
		final @Local(ordinal = 0, argsOnly = true) ItemStack itemStack
    ) {
		if (
            TotemNoShadingImpl.isEnabled()
                && itemStack.is(Items.TOTEM_OF_UNDYING)
                && original == Sheets.translucentItemSheet()
		) {
			return TotemNoShadingImpl.translucentItemSheet();
		} else {
			return original;
		}
    }
}
