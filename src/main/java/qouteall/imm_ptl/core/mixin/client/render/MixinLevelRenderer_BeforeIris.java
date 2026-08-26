package qouteall.imm_ptl.core.mixin.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.IPCGlobal;

@Mixin(value = ChunkSectionsToRender.class, priority = 900)
public class MixinLevelRenderer_BeforeIris {
    // inject it after Iris, run before Iris
    @Inject(method = "renderGroup", at = @At("HEAD"))
    private void iris$beginTranslucents(
        ChunkSectionLayerGroup group, CallbackInfo ci
    ) {
        if (group == ChunkSectionLayerGroup.TRANSLUCENT) {
            IPCGlobal.renderer.onBeginIrisTranslucentRendering(RenderSystem.getModelViewMatrix());
        }
    }
}
