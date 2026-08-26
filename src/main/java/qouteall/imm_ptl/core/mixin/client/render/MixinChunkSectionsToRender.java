package qouteall.imm_ptl.core.mixin.client.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.CHelper;
import qouteall.imm_ptl.core.IPCGlobal;
import qouteall.imm_ptl.core.IPGlobal;
import qouteall.imm_ptl.core.render.FrontClipping;
import qouteall.imm_ptl.core.render.MyGameRenderer;
import qouteall.imm_ptl.core.render.MyRenderHelper;
import qouteall.imm_ptl.core.render.context_management.PortalRendering;

@Mixin(ChunkSectionsToRender.class)
public class MixinChunkSectionsToRender {
    @Inject(
        method = "renderGroup",
        at = @At("HEAD")
    )
    private void onBeforeRenderGroup(
        ChunkSectionLayerGroup group, CallbackInfo ci
    ) {
        if (qouteall.imm_ptl.core.compat.sodium_compatibility.SodiumInterface.invoker.isSodiumPresent()) {
            return;
        }
        
        org.joml.Matrix4f modelView = qouteall.imm_ptl.core.render.context_management.RenderStates.currentModelViewMatrix != null
            ? qouteall.imm_ptl.core.render.context_management.RenderStates.currentModelViewMatrix
            : RenderSystem.getModelViewMatrix();
        
        if (group == ChunkSectionLayerGroup.TRANSLUCENT) {
            IPCGlobal.renderer.onBeforeTranslucentRendering(modelView);
            
            MyGameRenderer.updateFogColor();
            MyGameRenderer.resetFogState();
            
            MyGameRenderer.resetDiffuseLighting();
            
            FrontClipping.disableClipping();
        }
        else if (group == ChunkSectionLayerGroup.OPAQUE && PortalRendering.isRendering()) {
            FrontClipping.setupInnerClipping(
                PortalRendering.getActiveClippingPlane(),
                modelView,
                -FrontClipping.ADJUSTMENT
                // move the clipping plane a little back, to make world wrapping portal not z-fight
            );
            
            if (PortalRendering.isRenderingOddNumberOfMirrors()) {
                MyRenderHelper.applyMirrorFaceCulling();
            }
            
            if (IPGlobal.enableDepthClampForPortalRendering) {
                CHelper.enableDepthClamp();
            }
        }
    }
    
    @Inject(
        method = "renderGroup",
        at = @At("RETURN")
    )
    private void onAfterRenderGroup(
        ChunkSectionLayerGroup group, CallbackInfo ci
    ) {
        if (qouteall.imm_ptl.core.compat.sodium_compatibility.SodiumInterface.invoker.isSodiumPresent()) {
            return;
        }
        
        org.joml.Matrix4f modelView = qouteall.imm_ptl.core.render.context_management.RenderStates.currentModelViewMatrix != null
            ? qouteall.imm_ptl.core.render.context_management.RenderStates.currentModelViewMatrix
            : RenderSystem.getModelViewMatrix();
        
        if (group == ChunkSectionLayerGroup.TRANSLUCENT) {
            IPCGlobal.renderer.onAfterTranslucentRendering(modelView);
            
            // make hand rendering normal
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.LEVEL);
        }
        else if (group == ChunkSectionLayerGroup.OPAQUE && PortalRendering.isRendering()) {
            FrontClipping.disableClipping();
            MyRenderHelper.recoverFaceCulling();
            
            if (IPGlobal.enableDepthClampForPortalRendering) {
                CHelper.disableDepthClamp();
            }
        }
    }
}
