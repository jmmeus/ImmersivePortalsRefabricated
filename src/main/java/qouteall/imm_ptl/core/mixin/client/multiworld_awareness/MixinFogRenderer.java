package qouteall.imm_ptl.core.mixin.client.multiworld_awareness;

import net.minecraft.client.renderer.fog.environment.WaterFogEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.render.context_management.FogRendererContext;

@Mixin(value = WaterFogEnvironment.class, priority = 1100)
public class MixinFogRenderer {
    @Shadow
    private static int targetBiomeFog;
    @Shadow
    private static int previousBiomeFog;
    @Shadow
    private static long biomeChangedTime;
    
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClInit(CallbackInfo ci) {
        FogRendererContext.copyContextFromObject = context -> {
            targetBiomeFog = context.targetBiomeFog;
            previousBiomeFog = context.previousBiomeFog;
            biomeChangedTime = context.biomeChangedTime;
        };
        
        FogRendererContext.copyContextToObject = context -> {
            context.targetBiomeFog = targetBiomeFog;
            context.previousBiomeFog = previousBiomeFog;
            context.biomeChangedTime = biomeChangedTime;
        };
        
        FogRendererContext.init();
    }
}
