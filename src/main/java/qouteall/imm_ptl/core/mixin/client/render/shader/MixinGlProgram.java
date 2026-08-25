package qouteall.imm_ptl.core.mixin.client.render.shader;

import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.Uniform;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.ducks.IEShader;
import qouteall.imm_ptl.core.render.FrontClipping;

import java.util.List;

@Mixin(GlProgram.class)
public abstract class MixinGlProgram implements IEShader {
    @Shadow
    public abstract int getProgramId();
    
    @Shadow
    public abstract List<Uniform> getUniforms();
    
    @Nullable
    private Uniform ip_clippingEquation;
    
    @Inject(
        method = "setupUniforms",
        at = @At("HEAD")
    )
    private void onSetupUniforms(
        List<RenderPipeline.UniformDescription> uniforms,
        List<String> samplers,
        CallbackInfo ci
    ) {
        int loc = Uniform.glGetUniformLocation(getProgramId(), "iportal_ClippingEquation");
        if (loc != -1) {
            ip_clippingEquation = new Uniform("iportal_ClippingEquation", UniformType.VEC4);
            ip_clippingEquation.setLocation(loc);
            getUniforms().add(ip_clippingEquation);
        }
    }
    
    @Inject(
        method = "setDefaultUniforms",
        at = @At("RETURN")
    )
    private void onSetDefaultUniforms(
        VertexFormat.Mode mode, Matrix4f modelView, Matrix4f projection, float f1, float f2,
        CallbackInfo ci
    ) {
        if (ip_clippingEquation != null) {
            if (FrontClipping.isClippingEnabled) {
                double[] equation = FrontClipping.getActiveClipPlaneEquationBeforeModelView();
                if (equation != null) {
                    ip_clippingEquation.set(
                        (float) equation[0], (float) equation[1],
                        (float) equation[2], (float) equation[3]
                    );
                }
                else {
                    ip_clippingEquation.set(0f, 0f, 0f, 1f);
                }
            }
            else {
                ip_clippingEquation.set(0f, 0f, 0f, 1f);
            }
        }
    }
    
    @Nullable
    @Override
    public Uniform ip_getClippingEquationUniform() {
        return ip_clippingEquation;
    }
}
