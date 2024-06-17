package io.github.startsmercury.totem_no_shading.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
	@ModifyExpressionValue(
		method = """
		    render(\
		        Lnet/minecraft/world/item/ItemStack;\
		        Lnet/minecraft/world/item/ItemDisplayContext;\
		        Z\
		        Lcom/mojang/blaze3d/vertex/PoseStack;\
		        Lnet/minecraft/client/renderer/MultiBufferSource;\
		        I\
		        I\
		        Lnet/minecraft/client/resources/model/BakedModel;\
		    )V\
		""",
		at = @At(value = "INVOKE", target = """
		    Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getRenderType(\
		        Lnet/minecraft/world/item/ItemStack;\
		        Z\
		    )Lnet/minecraft/client/renderer/RenderType;\
		""")
	)
	private RenderType inspect(
		final RenderType original,
		final @Local(ordinal = 0, argsOnly = true) ItemStack itemStack,
		final @Local(ordinal = 0, argsOnly = true) ItemDisplayContext itemDisplayContext
	) {
		if (itemStack.is(Items.TOTEM_OF_UNDYING)) {
			System.out.println(original);
		}
		return original;
	}
}
