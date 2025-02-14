package io.github.startsmercury.totem_no_shading.impl.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class TotemNoShadingImpl {
	private static boolean enabled = true;

	public static boolean isEnabled() {
		return TotemNoShadingImpl.enabled;
	}

	public static void setEnabled(final boolean enabled) {
		TotemNoShadingImpl.enabled = enabled;
	}

	public static final String CUSTOM_SHADER_SUFFIX = "_no_shading";

	public static final ResourceLocation TARGET_VSH_SHADER =
		ResourceLocation.withDefaultNamespace(
		"shaders/core/rendertype_item_entity_translucent_cull.vsh"
		);

	public static RenderPipeline RENDERPIPELINE_ITEM_ENTITY_TRANSLUCENT_CULL;

	public static Function<ResourceLocation, RenderType> ITEM_ENTITY_TRANSLUCENT_CULL;

	public static RenderType itemEntityTranslucentCull(
		final ResourceLocation resourceLocation
	) {
		return ITEM_ENTITY_TRANSLUCENT_CULL.apply(resourceLocation);
	}

	public static RenderType TRANSLUCENT_CULL_BLOCK_SHEET;

	public static RenderType translucentItemSheet() {
		return TRANSLUCENT_CULL_BLOCK_SHEET;
	}
}