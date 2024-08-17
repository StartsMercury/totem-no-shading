package io.github.startsmercury.totem_no_shading.impl.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Function;

public class TotemNoShadingImpl {
	@ApiStatus.Internal
	public static @Nullable ShaderInstance rendertypeEntityTranslucentCullShader;

	public static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER =
		new RenderStateShard.ShaderStateShard(TotemNoShadingImpl::getRendertypeEntityTranslucentCullShader);

	private static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_CULL = Util.memoize(
		resourceLocation -> {
			RenderType.CompositeState compositeState = RenderType.CompositeState
				.builder()
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false))
				.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(RenderStateShard.LIGHTMAP)
				.setOverlayState(RenderStateShard.OVERLAY)
				.createCompositeState(true);
			return RenderType.create(
				"entity_translucent_cull",
				DefaultVertexFormat.NEW_ENTITY,
				VertexFormat.Mode.QUADS,
				1536,
				true,
				true,
				compositeState
			);
		}
	);

	@SuppressWarnings("deprecation")
	private static final RenderType TRANSLUCENT_CULL_BLOCK_SHEET = entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);

	public static ShaderInstance getRendertypeEntityTranslucentCullShader() {
		return rendertypeEntityTranslucentCullShader;
	}

	public static RenderType entityTranslucentCull(final ResourceLocation resourceLocation) {
		return ENTITY_TRANSLUCENT_CULL.apply(resourceLocation);
	}

	public static RenderType translucentCullBlockSheet() {
		return TRANSLUCENT_CULL_BLOCK_SHEET;
	}

	public static void init() {
		System.out.println("Hello, world!");
	}
}