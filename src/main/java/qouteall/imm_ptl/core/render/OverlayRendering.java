package qouteall.imm_ptl.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.CHelper;
import qouteall.imm_ptl.core.compat.iris_compatibility.IrisInterface;
import qouteall.imm_ptl.core.compat.sodium_compatibility.SodiumInterface;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.nether_portal.BlockPortalShape;
import qouteall.imm_ptl.core.portal.nether_portal.BreakablePortalEntity;
import qouteall.imm_ptl.core.render.context_management.PortalRendering;
import qouteall.imm_ptl.core.render.context_management.RenderStates;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class OverlayRendering {
    private static final RandomSource random = RandomSource.create();
    
    public static boolean shouldRenderOverlay(Portal portal) {
        if (IrisInterface.invoker.isShaders()) {
            return false;
        }
        
        if (PortalRendering.isRendering()) {
            return false;
        }
        
        return true;
    }
    
    public static void onRenderEntities(
        PoseStack matrixStack,
        MultiBufferSource.BufferSource vertexConsumerProvider
    ) {
        renderPortalOverlays(matrixStack, vertexConsumerProvider);
    }
    
    public static void onAfterRenderBlockEntities(
        PoseStack matrixStack,
        MultiBufferSource.BufferSource vertexConsumerProvider
    ) {
        renderPortalOverlays(matrixStack, vertexConsumerProvider);
    }
    
    private static void renderPortalOverlays(
        PoseStack matrixStack,
        MultiBufferSource.BufferSource vertexConsumerProvider
    ) {
        if (!shouldRenderOverlay(PortalRendering.getActiveClippingPortal())) {
            return;
        }
        
        for (Portal portal : RenderStates.renderedPortals) {
            renderOverlayFor(portal, matrixStack, vertexConsumerProvider);
        }
    }
    
    public static void onRenderPortalEntity(
        Portal portal,
        PoseStack matrixStack,
        MultiBufferSource vertexConsumerProvider
    ) {
        renderOverlayFor(portal, matrixStack, vertexConsumerProvider);
    }
    
    public static void renderOverlayFor(
        Portal portal,
        PoseStack matrixStack,
        MultiBufferSource vertexConsumerProvider
    ) {
        if (portal instanceof BreakablePortalEntity breakablePortalEntity) {
            renderBreakablePortalOverlay(
                breakablePortalEntity,
                RenderStates.getPartialTick(),
                matrixStack,
                vertexConsumerProvider
            );
        }
    }
    
    public static List<BakedQuad> getQuads(BlockStateModel model, BlockState blockState, Vec3 portalNormal) {
        Direction facing = Direction.getApproximateNearest(portalNormal.x, portalNormal.y, portalNormal.z);
        
        List<BakedQuad> result = new ArrayList<>();
        List<BlockModelPart> parts = model.collectParts(random);
        
        for (BlockModelPart part : parts) {
            result.addAll(part.getQuads(facing));
            result.addAll(part.getQuads(null));
        }
        
        return result;
    }
    
    /**
     * {@link net.minecraft.client.renderer.entity.FallingBlockRenderer}
     */
    private static void renderBreakablePortalOverlay(
        BreakablePortalEntity portal,
        float partialTick,
        PoseStack matrixStack,
        MultiBufferSource vertexConsumerProvider
    ) {
        BreakablePortalEntity.OverlayInfo overlay = portal.overlayInfo;
        if (overlay == null) {
            return;
        }
        
        BlockPortalShape blockPortalShape = portal.blockPortalShape;
        if (blockPortalShape == null) {
            return;
        }
        
        BlockState blockState = overlay.blockState();
        
        Minecraft client = Minecraft.getInstance();
        
        BlockRenderDispatcher blockRenderManager = client.getBlockRenderer();
        
        BlockStateModel model = blockRenderManager.getBlockModel(blockState);
        
        RenderType renderLayer = ItemBlockRenderTypes.getRenderType(blockState);
        
        Vec3 pos = portal.getPosition(partialTick);
        
        matrixStack.pushPose();
        
        Vec3 offset = portal.getNormal().scale(overlay.offset());
        
        matrixStack.translate(offset.x, offset.y, offset.z);
        
        VertexConsumer buffer = vertexConsumerProvider.getBuffer(renderLayer);
        
        List<BakedQuad> quads = getQuads(model, blockState, portal.getNormal());
        
        random.setSeed(0);
        
        for (BlockPos blockPos : blockPortalShape.area) {
            matrixStack.pushPose();
            matrixStack.translate(
                blockPos.getX() - pos.x, blockPos.getY() - pos.y, blockPos.getZ() - pos.z
            );
            
            if (overlay.rotation() != null) {
                matrixStack.mulPose(overlay.rotation().toMcQuaternion());
            }
            
            for (BakedQuad quad : quads) {
                SodiumInterface.invoker.markSpriteActive(quad.sprite());
                buffer.putBulkData(
                    matrixStack.last(),
                    quad,
                    new float[]{1.0F, 1.0F, 1.0F, 1.0F},
                    1.0f, 1.0f, 1.0f, (float) overlay.opacity(),
                    new int[]{14680304, 14680304, 14680304, 14680304},//packed light value
                    OverlayTexture.NO_OVERLAY,
                    true
                );
            }
            
            matrixStack.popPose();
        }
        
        matrixStack.popPose();
        
    }
}
