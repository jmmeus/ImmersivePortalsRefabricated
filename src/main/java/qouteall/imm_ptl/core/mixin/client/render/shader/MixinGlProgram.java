package qouteall.imm_ptl.core.mixin.client.render.shader;

import com.mojang.blaze3d.opengl.GlProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import qouteall.imm_ptl.core.ducks.IEShader;

@Mixin(GlProgram.class)
public abstract class MixinGlProgram implements IEShader {
    @Shadow
    public abstract int getProgramId();
}
