package qouteall.imm_ptl.core.mixin.common.collision;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Projectile.class)
public abstract class MixinProjectile extends MixinEntity {
    @Shadow
    protected EntityReference<Entity> owner;
    
    // make it recognize the owner in another dimension
    @Inject(
        method = "getOwner",
        at = @At("RETURN"),
        cancellable = true
    )
    private void onGetOwner(CallbackInfoReturnable<Entity> cir) {
        if (cir.getReturnValue() == null && this.owner != null) {
            UUID uuid = this.owner.getUUID();
            if (uuid != null) {
                Level level = ((Entity) (Object) this).level();
                if (level instanceof ServerLevel serverLevel) {
                    MinecraftServer server = serverLevel.getServer();
                    for (ServerLevel world : server.getAllLevels()) {
                        if (world != serverLevel) {
                            Entity entity = world.getEntity(uuid);
                            if (entity != null) {
                                cir.setReturnValue(entity);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}