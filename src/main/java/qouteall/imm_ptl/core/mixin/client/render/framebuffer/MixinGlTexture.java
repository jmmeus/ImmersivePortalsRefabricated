package qouteall.imm_ptl.core.mixin.client.render.framebuffer;

import com.mojang.blaze3d.opengl.GlTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.render.IPTextureHelper;

@Mixin(GlTexture.class)
public class MixinGlTexture {
    @Shadow
    public int id;
    
    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        IPTextureHelper.stencilTextureIds.remove(this.id);
    }
}
