package io.github.startsmercury.totem_no_shading.mixin.client.minecraft;

import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Sheets.class)
public class SheetsMixin {
    static {
        @SuppressWarnings("deprecation")
        final var textureAtlas = TextureAtlas.LOCATION_ITEMS;
        TotemNoShadingImpl.TRANSLUCENT_ITEM_SHEET =
            TotemNoShadingImpl.itemTranslucent(textureAtlas);
    }
}
