package qouteall.imm_ptl.core.compat.mixin.sodium;

import net.caffeinemc.mods.sodium.client.gl.device.GLRenderDevice;
import net.caffeinemc.mods.sodium.client.gl.state.GlStateTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = GLRenderDevice.class, remap = false)
public class MixinSodiumGLRenderDevice {
    @Shadow
    private GlStateTracker stateTracker;
    
    @Shadow
    private boolean isActive;
    
    @Unique
    private int ip_activeDepth = 0;
    
    @Inject(method = "makeActive", at = @At("HEAD"), cancellable = true)
    private void onMakeActive(CallbackInfo ci) {
        ip_activeDepth++;
        if (ip_activeDepth > 1) {
            if (stateTracker != null) {
                stateTracker.clear();
            }
            isActive = true;
            ci.cancel();
        }
    }
    
    @Inject(method = "makeInactive", at = @At("HEAD"), cancellable = true)
    private void onMakeInactive(CallbackInfo ci) {
        ip_activeDepth--;
        if (ip_activeDepth > 0) {
            if (stateTracker != null) {
                stateTracker.clear();
            }
            ci.cancel();
        } else {
            ip_activeDepth = 0;
        }
    }
}
