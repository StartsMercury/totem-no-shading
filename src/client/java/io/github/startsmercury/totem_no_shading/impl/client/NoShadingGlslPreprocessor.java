package io.github.startsmercury.totem_no_shading.impl.client;

import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.FileUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class NoShadingGlslPreprocessor extends GlslPreprocessor {
    @Unique
    private static final String TARGET_SYSTEM_MOJ_IMPORT = "minecraft:light.glsl";

    @Unique
    private static final String TARGET_FUNCTION_NAME = "minecraft_mix_light";

    @Unique
    private static final String TARGET_FUNCTION_RENAME_PREFIX = "_ignored";

    @Unique
    private static final Pattern TARGET_FUNCTION_SIGNATURE_PATTERN;
    static {
        final var returnType = "vec4";
        final var parameterTypes = new String[] { "vec3", "vec3", "vec3", "vec4" };

        final var optionalWhitespace = "\\s*";
        final var requiredWhitespace = "\\s+";
        final var identifier = "[a-zA-Z_][a-zA-Z0-9_]*";

        final var builder = new StringBuilder();
        builder.append('(');
        builder.append(returnType);
        builder.append(requiredWhitespace);
        builder.append(TARGET_FUNCTION_NAME);
        builder.append(')');

        builder.append("\\(");
        final var parameterCount = parameterTypes.length;
        for (var i = 0; ; ) {
            builder.append(optionalWhitespace);
            builder.append(parameterTypes[i]);
            builder.append(requiredWhitespace);
            builder.append(identifier);
            builder.append(optionalWhitespace);
            if (++i >= parameterCount) {
                break;
            }
            builder.append(',');
        }
        builder.append("\\)");

        TARGET_FUNCTION_SIGNATURE_PATTERN = Pattern.compile(builder.toString());
    }

    @Unique
    private static final String MACRO_REPLACING_TARGET_FUNCTION;
    static {
        final var somehowDissociateFromSourceLineNumbering = "#line 0";

        MACRO_REPLACING_TARGET_FUNCTION = """

            ${somehowDissociateFromSourceLineNumbering}
            #define ${TARGET_FUNCTION_NAME}(lightDir0, lightDir1, normal, color) vec4(1.0) + vec4(0.0)
            """
                .replace("${somehowDissociateFromSourceLineNumbering}", somehowDissociateFromSourceLineNumbering)
                .replace("${TARGET_FUNCTION_NAME}", TARGET_FUNCTION_NAME);
    }

    private final ResourceLocation val$resourceLocation2;

    private final Map<ResourceLocation, Resource> val$map;

    private final Set<ResourceLocation> importedLocations = new ObjectArraySet<>();

    public NoShadingGlslPreprocessor(
        final ResourceLocation val$resourceLocation2,
        final Map<ResourceLocation, Resource> val$map
    ) {
        this.val$resourceLocation2 = val$resourceLocation2;
        this.val$map = val$map;
    }

    @Override
    public String applyImport(boolean bl, String string) {
        ResourceLocation resourceLocation;
        try {
            if (bl) {
                resourceLocation = val$resourceLocation2.withPath(
                    string2 -> FileUtil.normalizeResourcePath(string2 + string)
                );
            } else {
                resourceLocation = ResourceLocation.parse(string)
                    .withPrefix("shaders/include/");
            }
        } catch (ResourceLocationException var8) {
            ShaderManager.LOGGER.error(
                "Malformed GLSL import {}: {}",
                string,
                var8.getMessage()
            );
            return "#error " + var8.getMessage();
        }

        if (!this.importedLocations.add(resourceLocation)) {
            return null;
        } else {
            try {
                Reader reader = val$map.get(resourceLocation).openAsReader();

                String var5;
                try {
                    var5 = IOUtils.toString(reader);
                } catch (Throwable var9) {
                    try {
                        reader.close();
                    } catch (Throwable var7) {
                        var9.addSuppressed(var7);
                    }

                    throw var9;
                }

                reader.close();

                return modifyLightingCalculations(var5, bl, string);
            } catch (IOException var10) {
                ShaderManager.LOGGER.error(
                    "Could not open GLSL import {}: {}",
                    resourceLocation,
                    var10.getMessage()
                );
                return "#error " + var10.getMessage();
            }
        }
    }

    private String modifyLightingCalculations(
        final String source,
        final boolean quotesUsed,
        final String file
    ) {
        if (quotesUsed || !file.equals(TARGET_SYSTEM_MOJ_IMPORT)) {
            return source;
        }

        final var matcher = TARGET_FUNCTION_SIGNATURE_PATTERN.matcher(source);
        if (!matcher.find()) {
            return source;
        }

        final var correctLineNumber =
            StringUtil.lineCount(source.substring(0, matcher.start()));

        final var beforeTargetFunction = source.substring(0, matcher.start());
        final var ignoreChangesToLineNumbering = "\n#line " + correctLineNumber + "\n";
        final var returnTypeAndOriginalName = matcher.group(1);
        final var afterReturnTypeAndChangedName = source.substring(matcher.end(1));

        return beforeTargetFunction
            + MACRO_REPLACING_TARGET_FUNCTION
            + ignoreChangesToLineNumbering
            + returnTypeAndOriginalName
            + TARGET_FUNCTION_RENAME_PREFIX
            + afterReturnTypeAndChangedName;
    }
}
