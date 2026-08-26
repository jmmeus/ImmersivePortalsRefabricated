package qouteall.imm_ptl.core.mixin.client.render.framebuffer;

import com.mojang.blaze3d.opengl.GlStateManager;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.render.IPTextureHelper;

@Mixin(targets = "com.mojang.blaze3d.opengl.DirectStateAccess$Emulated")
public class MixinDirectStateAccessEmulated {
    @Inject(
        method = "bindFrameBufferTextures",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onBindFrameBufferTextures(
        int fbo, int colorTexId, int depthTexId, int mipLevel, int bindTarget,
        CallbackInfo ci
    ) {
        int target = bindTarget == 0 ? 36009 : bindTarget;
        int oldFbo = GlStateManager.getFrameBuffer(target);
        GlStateManager._glBindFramebuffer(target, fbo);
        GlStateManager._glFramebufferTexture2D(target, 36064, 3553, colorTexId, mipLevel);
        if (depthTexId != 0 && IPTextureHelper.isStencilTextureId(depthTexId)) {
            GlStateManager._glFramebufferTexture2D(target, 36096, 3553, depthTexId, mipLevel);
            GlStateManager._glFramebufferTexture2D(target, GL30.GL_STENCIL_ATTACHMENT, 3553, depthTexId, mipLevel);
        } else {
            GlStateManager._glFramebufferTexture2D(target, 36096, 3553, depthTexId, mipLevel);
            GlStateManager._glFramebufferTexture2D(target, GL30.GL_STENCIL_ATTACHMENT, 3553, 0, mipLevel);
        }
        if (bindTarget == 0) {
            GlStateManager._glBindFramebuffer(target, oldFbo);
        }
        ci.cancel();
    }
}
