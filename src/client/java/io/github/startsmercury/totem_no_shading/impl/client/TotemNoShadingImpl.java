package io.github.startsmercury.totem_no_shading.impl.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public class TotemNoShadingImpl {
    public static final Identifier TARGET_MODEL = Identifier.withDefaultNamespace("item/totem_of_undying");

	private static boolean enabled = true;

	public static boolean isEnabled() {
		return TotemNoShadingImpl.enabled;
	}

	public static void setEnabled(final boolean enabled) {
		TotemNoShadingImpl.enabled = enabled;
	}

	public static final String CUSTOM_SHADER_SUFFIX = "_no_shading";

	public static final Identifier TARGET_VSH_SHADER =
		Identifier.withDefaultNamespace(
		"shaders/core/item.vsh"
		);

    public static RenderPipeline RENDERPIPELINE_ITEM_CUTOUT;

	public static RenderPipeline RENDERPIPELINE_ITEM_TRANSLUCENT;

	public static Function<Identifier, RenderType> ITEM_CUTOUT;

    public static Function<Identifier, RenderType> ITEM_TRANSLUCENT;

    public static RenderType itemCutout(final Identifier resourceLocation) {
        return ITEM_CUTOUT.apply(resourceLocation);
    }

	public static RenderType itemTranslucent(final Identifier resourceLocation) {
		return ITEM_TRANSLUCENT.apply(resourceLocation);
	}

    public static RenderType CUTOUT_ITEM_SHEET;

	public static RenderType TRANSLUCENT_ITEM_SHEET;

	public static RenderType cutoutItemSheet() {
		return CUTOUT_ITEM_SHEET;
	}

	public static RenderType translucentItemSheet() {
		return TRANSLUCENT_ITEM_SHEET;
	}
}