package qouteall.imm_ptl.core.mixin.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qouteall.imm_ptl.core.render.context_management.PortalRendering;

import java.util.function.Function;

@Mixin(ChestRenderer.class)
public class MixinChestRenderer {
    @Redirect(
        method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/world/phys/Vec3;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/Material;buffer(Lnet/minecraft/client/renderer/MultiBufferSource;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        )
    )
    private VertexConsumer onGetBuffer(
        Material material, MultiBufferSource bufferSource, Function<ResourceLocation, RenderType> defaultRenderType
    ) {
        if (PortalRendering.isRenderingOddNumberOfMirrors()) {
            RenderType renderType = RenderType.entityCutoutNoCull(material.atlasLocation());
            return material.sprite().wrap(bufferSource.getBuffer(renderType));
        }
        return material.buffer(bufferSource, defaultRenderType);
    }
}
