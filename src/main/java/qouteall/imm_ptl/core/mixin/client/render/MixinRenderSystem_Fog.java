package qouteall.imm_ptl.core.mixin.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.FogParameters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import qouteall.imm_ptl.core.render.MyRenderHelper;

@Mixin(RenderSystem.class)
public class MixinRenderSystem_Fog {
    @ModifyVariable(
        method = "setShaderFog", at = @At("HEAD"), argsOnly = true
    )
    private static FogParameters onSetShaderFog(FogParameters params) {
        if (params == null || params == FogParameters.NO_FOG) {
            return params;
        }
        return new FogParameters(
            MyRenderHelper.transformFogDistance(params.start()),
            MyRenderHelper.transformFogDistance(params.end()),
            params.shape(),
            params.red(),
            params.green(),
            params.blue(),
            params.alpha()
        );
    }
}
