package io.github.startsmercury.totem_no_shading.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.startsmercury.totem_no_shading.impl.client.ResourceProviderWrapper;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.regex.Pattern;

@Mixin(targets = "net.minecraft.client.renderer.ShaderInstance$1")
public abstract class ShaderInstance$1Mixin {
    @Unique
    private static final String TARGET_SYSTEM_MOJ_IMPORT = "shaders/include/light.glsl";

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

    @Final
    @Shadow
    ResourceProvider val$resourceProvider;

    /**
     * Modifies the {@code minecraft_mix_light} function in Minecraft's
     * {@code shaders/include/light.glsl} shader to enforce Simply No Shading features.
     *
     * @param source the included shader source code
     * @param quotesUsed true for double-quote include; false for angle-brackets
     * @param file the file path to include
     * @return the modified shader source code
     */
    @ModifyReturnValue(
            method = "applyImport(ZLjava/lang/String;)Ljava/lang/String;",
            at = @At("RETURN")
    )
    private String modifyLightingCalculations(
            final String source,
            final boolean quotesUsed,
            final String file
    ) {
        if (val$resourceProvider instanceof ResourceProviderWrapper) {
            System.out.println(file);
        }
        if (
            !(val$resourceProvider instanceof ResourceProviderWrapper)
                || quotesUsed
                || !file.equals(TARGET_SYSTEM_MOJ_IMPORT)
        ) {
            return source;
        }

        final var matcher = TARGET_FUNCTION_SIGNATURE_PATTERN.matcher(source);
        if (!matcher.find()) {
            return source;
        }

        final var correctLineNumber = StringUtil.lineCount(source.substring(0, matcher.start()));

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
