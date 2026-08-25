package qouteall.imm_ptl.core.mixin.client.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qouteall.imm_ptl.core.IPGlobal;
import qouteall.imm_ptl.core.render.context_management.RenderStates;
import qouteall.imm_ptl.core.render.optimization.GLResourceCache;

@Mixin(value = GlStateManager.class, remap = false)
public abstract class MixinGlStateManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("ImmPtlGlDebug");
    private static int incompleteLogCount = 0;
    
    @Inject(
        method = "_clear",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void onClear(int mask, CallbackInfo ci) {
        if (qouteall.imm_ptl.core.IPCGlobal.renderer != null && qouteall.imm_ptl.core.IPCGlobal.renderer.replaceFrameBufferClearing()) {
            ci.cancel();
        }
    }
    
    @Inject(
        method = "_glBindFramebuffer",
        at = @At("RETURN")
    )
    private static void onBindFramebuffer(int target, int framebuffer, CallbackInfo ci) {
        if (framebuffer != 0 && incompleteLogCount < 20) {
            int status = GL30.glCheckFramebufferStatus(target);
            if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
                incompleteLogCount++;
                LOGGER.error("BOUND INCOMPLETE FRAMEBUFFER! target={}, fbo={}, status=0x{}",
                    target, framebuffer, Integer.toHexString(status), new Exception("FBO Stack Trace"));
            }
        }
    }
    
    @Shadow
    public static void _disableCull() {
        throw new RuntimeException();
    }
    
    @Inject(
        method = "_enableCull",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void onEnableCull(CallbackInfo ci) {
        if (RenderStates.shouldForceDisableCull) {
            _disableCull();
            ci.cancel();
        }
    }
    
    @Inject(
        method = "_glGenBuffers",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void onGenBuffers(CallbackInfoReturnable<Integer> cir) {
        if (IPGlobal.cacheGlBuffer) {
            cir.setReturnValue(GLResourceCache.bufferCache.getNewResourceId());
            cir.cancel();
        }
    }
    
    @Inject(
        method = "_glGenVertexArrays",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void onGenVertexArrays(CallbackInfoReturnable<Integer> cir) {
        if (IPGlobal.cacheGlBuffer) {
            cir.setReturnValue(GLResourceCache.vertexArrayCache.getNewResourceId());
            cir.cancel();
        }
    }
}
