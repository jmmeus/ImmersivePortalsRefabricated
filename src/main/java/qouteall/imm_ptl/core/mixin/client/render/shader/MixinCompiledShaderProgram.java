package qouteall.imm_ptl.core.mixin.client.render.shader;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.ShaderProgramConfig;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.ducks.IEShader;

import java.util.List;

@Mixin(CompiledShaderProgram.class)
public abstract class MixinCompiledShaderProgram implements IEShader {
    @Shadow
    public abstract int getProgramId();
    
    @Shadow
    public abstract void registerUniform(Uniform uniform);
    
    @Nullable
    private Uniform ip_clippingEquation;
    
    @Inject(
        method = "setupUniforms",
        at = @At("HEAD")
    )
    private void onSetupUniforms(
        List<ShaderProgramConfig.Uniform> uniforms,
        List<ShaderProgramConfig.Sampler> samplers,
        CallbackInfo ci
    ) {
        int loc = Uniform.glGetUniformLocation(getProgramId(), "iportal_ClippingEquation");
        if (loc != -1) {
            ip_clippingEquation = new Uniform("iportal_ClippingEquation", 7, 4);
            ip_clippingEquation.setLocation(loc);
            registerUniform(ip_clippingEquation);
        }
    }
    
    @Nullable
    @Override
    public Uniform ip_getClippingEquationUniform() {
        return ip_clippingEquation;
    }
}
