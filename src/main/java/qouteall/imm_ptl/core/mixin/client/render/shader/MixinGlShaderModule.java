package qouteall.imm_ptl.core.mixin.client.render.shader;

import com.mojang.blaze3d.opengl.GlShaderModule;
import com.mojang.blaze3d.shaders.ShaderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import qouteall.imm_ptl.core.render.ShaderCodeTransformation;

@Mixin(GlShaderModule.class)
public class MixinGlShaderModule {
    @ModifyVariable(
        method = "compile",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private static String modifyShaderSource(
        String source, ResourceLocation id, ShaderType type
    ) {
        return ShaderCodeTransformation.transform(type, id.toString(), source);
    }
}
