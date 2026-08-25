package qouteall.imm_ptl.core.mixin.client.render.framebuffer;

import com.mojang.blaze3d.opengl.GlStateManager;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.render.IPTextureHelper;

@Mixin(targets = "com.mojang.blaze3d.opengl.DirectStateAccess$Core")
public class MixinDirectStateAccessCore {
    @Inject(
        method = "bindFrameBufferTextures",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onBindFrameBufferTextures(
        int fbo, int colorTexId, int depthTexId, int mipLevel, int bindTarget,
        CallbackInfo ci
    ) {
        ARBDirectStateAccess.glNamedFramebufferTexture(fbo, 36064, colorTexId, mipLevel);
        if (depthTexId != 0 && IPTextureHelper.isStencilTextureId(depthTexId)) {
            ARBDirectStateAccess.glNamedFramebufferTexture(fbo, GL30.GL_DEPTH_ATTACHMENT, depthTexId, mipLevel);
            ARBDirectStateAccess.glNamedFramebufferTexture(fbo, GL30.GL_STENCIL_ATTACHMENT, depthTexId, mipLevel);
        } else {
            ARBDirectStateAccess.glNamedFramebufferTexture(fbo, 36096, depthTexId, mipLevel);
            ARBDirectStateAccess.glNamedFramebufferTexture(fbo, GL30.GL_STENCIL_ATTACHMENT, 0, mipLevel);
        }
        if (bindTarget != 0) {
            GlStateManager._glBindFramebuffer(bindTarget, fbo);
        }
        ci.cancel();
    }
}
