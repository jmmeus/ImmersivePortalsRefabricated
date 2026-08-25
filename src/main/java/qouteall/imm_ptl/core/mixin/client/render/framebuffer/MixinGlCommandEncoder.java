package qouteall.imm_ptl.core.mixin.client.render.framebuffer;

import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.GpuTexture;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlCommandEncoder.class)
public class MixinGlCommandEncoder {
    @Inject(
        method = "clearDepthTexture",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_glFramebufferTexture2D(IIIII)V"
        )
    )
    private void onDetachDepth(GpuTexture texture, double d, CallbackInfo ci) {
        GlStateManager._glFramebufferTexture2D(36160, GL30.GL_STENCIL_ATTACHMENT, 3553, 0, 0);
    }
}
