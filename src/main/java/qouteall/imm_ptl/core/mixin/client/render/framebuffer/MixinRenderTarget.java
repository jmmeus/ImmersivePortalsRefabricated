package qouteall.imm_ptl.core.mixin.client.render.framebuffer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.CHelper;
import qouteall.imm_ptl.core.ducks.IEFrameBuffer;
import qouteall.imm_ptl.core.render.IPTextureHelper;

@Mixin(RenderTarget.class)
public abstract class MixinRenderTarget implements IEFrameBuffer {
    
    private boolean isStencilBufferEnabled;
    
    @Shadow
    public int width;
    @Shadow
    public int height;
    
    @Shadow
    public abstract void resize(int width, int height);
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(
        String label,
        boolean useDepth,
        CallbackInfo ci
    ) {
        isStencilBufferEnabled = false;
    }
    
    @Inject(
        method = "createBuffers",
        at = @At("HEAD")
    )
    private void onBeforeCreateBuffers(int width, int height, CallbackInfo ci) {
        if (this.isStencilBufferEnabled) {
            IPTextureHelper.isCreatingStencilDepthTexture = true;
        }
    }
    
    @Inject(
        method = "createBuffers",
        at = @At("RETURN")
    )
    private void onAfterCreateBuffers(int width, int height, CallbackInfo ci) {
        IPTextureHelper.isCreatingStencilDepthTexture = false;
    }
    
    @Inject(
        method = "copyDepthFrom",
        at = @At("RETURN")
    )
    private void onCopiedDepthFrom(RenderTarget framebuffer, CallbackInfo ci) {
        CHelper.checkGlError();
    }
    
    @Override
    public boolean ip_getIsStencilBufferEnabled() {
        return isStencilBufferEnabled;
    }
    
    @Override
    public void ip_setIsStencilBufferEnabledAndReload(boolean cond) {
        if (isStencilBufferEnabled != cond) {
            isStencilBufferEnabled = cond;
            resize(width, height);
        }
    }
}
