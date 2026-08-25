package qouteall.imm_ptl.core.mixin.client.render.framebuffer;

import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import qouteall.imm_ptl.core.render.IPTextureHelper;

@Mixin(GlDevice.class)
public class MixinGlDevice {
    @ModifyArgs(
        method = "createTexture(Ljava/lang/String;Lcom/mojang/blaze3d/textures/TextureFormat;III)Lcom/mojang/blaze3d/textures/GpuTexture;",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V"
        )
    )
    private void onTexImage2D(Args args) {
        int internalFormat = args.get(2);
        // 33191 = GL_DEPTH_COMPONENT32F (TextureFormat.DEPTH32), 6402 = GL_DEPTH_COMPONENT
        // NOTE: 32856 is GL_RGBA8 and MUST NOT be matched here!
        if (IPTextureHelper.isCreatingStencilDepthTexture && (internalFormat == 33191 || internalFormat == 6402)) {
            args.set(2, GL30.GL_DEPTH24_STENCIL8);
            args.set(6, GL30.GL_DEPTH_STENCIL);
            args.set(7, GL30.GL_UNSIGNED_INT_24_8);
        }
    }
    
    @Inject(
        method = "createTexture(Ljava/lang/String;Lcom/mojang/blaze3d/textures/TextureFormat;III)Lcom/mojang/blaze3d/textures/GpuTexture;",
        at = @At("RETURN")
    )
    private void onReturnCreateTexture(
        String label, TextureFormat format, int width, int height, int mipLevels,
        CallbackInfoReturnable<GpuTexture> cir
    ) {
        if (IPTextureHelper.isCreatingStencilDepthTexture && format.hasDepthAspect()) {
            GpuTexture texture = cir.getReturnValue();
            if (texture instanceof GlTexture glTexture) {
                IPTextureHelper.stencilTextureIds.add(glTexture.glId());
            }
        }
    }
}
