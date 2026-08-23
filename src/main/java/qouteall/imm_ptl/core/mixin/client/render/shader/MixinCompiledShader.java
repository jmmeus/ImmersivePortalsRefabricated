package qouteall.imm_ptl.core.mixin.client.render.shader;

import com.mojang.blaze3d.shaders.CompiledShader;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import qouteall.imm_ptl.core.render.ShaderCodeTransformation;

@Mixin(CompiledShader.class)
public class MixinCompiledShader {
    @ModifyVariable(
        method = "compile",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private static String modifyShaderSource(
        String source, ResourceLocation id, CompiledShader.Type type
    ) {
        return ShaderCodeTransformation.transform(type, id.toString(), source);
    }
}
