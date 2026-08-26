package qouteall.imm_ptl.core.mixin.client.render.framebuffer;

import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.GpuTexture;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.IPCGlobal;

@Mixin(GlCommandEncoder.class)
public class MixinGlCommandEncoder {
    @Inject(
        method = "clearColorAndDepthTextures(Lcom/mojang/blaze3d/textures/GpuTexture;ILcom/mojang/blaze3d/textures/GpuTexture;D)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onClearColorAndDepth(
        GpuTexture colorTexture, int color, GpuTexture depthTexture, double depth, CallbackInfo ci
    ) {
        if (IPCGlobal.renderer != null && IPCGlobal.renderer.replaceFrameBufferClearing()) {
            ci.cancel();
        }
    }
    
    @Inject(
        method = "clearColorAndDepthTextures(Lcom/mojang/blaze3d/textures/GpuTexture;ILcom/mojang/blaze3d/textures/GpuTexture;DIIII)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onClearColorAndDepthRegion(
        GpuTexture colorTexture, int color, GpuTexture depthTexture, double depth,
        int x, int y, int width, int height, CallbackInfo ci
    ) {
        if (IPCGlobal.renderer != null && IPCGlobal.renderer.replaceFrameBufferClearing()) {
            ci.cancel();
        }
    }
    
    @Inject(
        method = "clearColorTexture",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onClearColor(GpuTexture texture, int color, CallbackInfo ci) {
        if (IPCGlobal.renderer != null && IPCGlobal.renderer.replaceFrameBufferClearing()) {
            ci.cancel();
        }
    }
    
    @Inject(
        method = "clearDepthTexture",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onClearDepth(GpuTexture texture, double d, CallbackInfo ci) {
        if (IPCGlobal.renderer != null && IPCGlobal.renderer.replaceFrameBufferClearing()) {
            ci.cancel();
        }
    }
    
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

