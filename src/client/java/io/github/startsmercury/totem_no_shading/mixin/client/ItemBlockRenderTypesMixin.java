package io.github.startsmercury.totem_no_shading.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin {
	@ModifyReturnValue(at = @At("RETURN"), method = """
	    getRenderType (                             \
	        Lnet/minecraft/world/item/ItemStack;    \
	    ) Lnet/minecraft/client/renderer/RenderType;\
	""")
	private static RenderType inspect(
		final RenderType original,
		final @Local(ordinal = 0, argsOnly = true) ItemStack itemStack
    ) {
		if (itemStack.is(Items.TOTEM_OF_UNDYING)) {
			if (original == Sheets.translucentItemSheet()) {
				return TotemNoShadingImpl.translucentItemSheet();
			}
		}
		return original;
	}
}
