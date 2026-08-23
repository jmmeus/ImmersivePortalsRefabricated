package qouteall.imm_ptl.core.mixin.client.render;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.IPCGlobal;

@Mixin(value = LevelRenderer.class, priority = 900)
public class MixinLevelRenderer_BeforeIris {
    // inject it after Iris, run before Iris
    @Inject(method = "renderSectionLayer", at = @At("HEAD"))
    private void iris$beginTranslucents(
        RenderType renderType, double x, double y, double z, Matrix4f modelView, Matrix4f projection, CallbackInfo ci
    ) {
        if (renderType == RenderType.translucent()) {
            IPCGlobal.renderer.onBeginIrisTranslucentRendering(modelView);
        }
    }
}
