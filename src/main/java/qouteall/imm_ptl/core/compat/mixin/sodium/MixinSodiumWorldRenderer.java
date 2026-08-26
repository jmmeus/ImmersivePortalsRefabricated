package qouteall.imm_ptl.core.compat.mixin.sodium;

import com.mojang.blaze3d.platform.Lighting;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.viewport.Viewport;
import net.caffeinemc.mods.sodium.client.util.FogParameters;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.CHelper;
import qouteall.imm_ptl.core.IPCGlobal;
import qouteall.imm_ptl.core.IPGlobal;
import qouteall.imm_ptl.core.compat.sodium_compatibility.IESodiumWorldRenderer;
import qouteall.imm_ptl.core.compat.sodium_compatibility.SodiumInterface;
import qouteall.imm_ptl.core.render.FrontClipping;
import qouteall.imm_ptl.core.render.FrustumCuller;
import qouteall.imm_ptl.core.render.MyGameRenderer;
import qouteall.imm_ptl.core.render.MyRenderHelper;
import qouteall.imm_ptl.core.render.context_management.PortalRendering;
import qouteall.imm_ptl.core.render.context_management.RenderStates;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
public class MixinSodiumWorldRenderer implements IESodiumWorldRenderer {
    @Shadow
    private net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager renderSectionManager;
    
    @Override
    public net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager ip_getRenderSectionManager() {
        return renderSectionManager;
    }
    
    @Inject(
        method = "setupTerrain",
        at = @At("HEAD")
    )
    private void onUpdateChunks(
        Camera camera, Viewport viewport, FogParameters fogParameters, boolean spectator, boolean updateChunksImmediately, ChunkRenderMatrices matrices, CallbackInfo ci
    ) {
        SodiumInterface.frustumCuller = new FrustumCuller();
        Vec3 cameraPos = camera.getPosition();
        SodiumInterface.frustumCuller.update(cameraPos.x, cameraPos.y, cameraPos.z);
    }
    
    @Inject(
        method = "drawChunkLayer",
        at = @At("HEAD")
    )
    private void onBeforeDrawChunkLayer(
        ChunkSectionLayerGroup group, ChunkRenderMatrices matrices, double x, double y, double z,
        CallbackInfo ci
    ) {
        Matrix4f modelView = new Matrix4f(matrices.modelView());
        Matrix4f projection = new Matrix4f(matrices.projection());
        RenderStates.currentModelViewMatrix = modelView;
        RenderStates.currentProjectionMatrix = projection;
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
        method = "drawChunkLayer",
        at = @At("RETURN")
    )
    private void onAfterDrawChunkLayer(
        ChunkSectionLayerGroup group, ChunkRenderMatrices matrices, double x, double y, double z,
        CallbackInfo ci
    ) {
        Matrix4f modelView = new Matrix4f(matrices.modelView());
        if (group == ChunkSectionLayerGroup.TRANSLUCENT) {
            IPCGlobal.renderer.onAfterTranslucentRendering(modelView);
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
