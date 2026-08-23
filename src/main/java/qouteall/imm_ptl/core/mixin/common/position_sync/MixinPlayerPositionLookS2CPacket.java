package qouteall.imm_ptl.core.mixin.common.position_sync;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.ducks.IEPlayerPositionLookS2CPacket;
import qouteall.imm_ptl.core.network.ImmPtlNetworkConfig;

@Mixin(ClientboundPlayerPositionPacket.class)
public class MixinPlayerPositionLookS2CPacket implements IEPlayerPositionLookS2CPacket {
    @Shadow @Final @Mutable
    public static StreamCodec<FriendlyByteBuf, ClientboundPlayerPositionPacket> STREAM_CODEC;
    
    @Unique
    private ResourceKey<Level> ip_playerDimension;
    
    @Override
    public ResourceKey<Level> ip_getPlayerDimension() {
        return ip_playerDimension;
    }
    
    @Override
    public void ip_setPlayerDimension(ResourceKey<Level> dimension) {
        ip_playerDimension = dimension;
    }
    
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClInit(CallbackInfo ci) {
        StreamCodec<FriendlyByteBuf, ClientboundPlayerPositionPacket> original = STREAM_CODEC;
        STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
                original.encode(buf, packet);
                ResourceKey<Level> dim = ((IEPlayerPositionLookS2CPacket) (Object) packet).ip_getPlayerDimension();
                buf.writeResourceKey(dim != null ? dim : Level.OVERWORLD);
            },
            buf -> {
                ClientboundPlayerPositionPacket packet = original.decode(buf);
                if (ImmPtlNetworkConfig.doesServerHaveImmPtl()) {
                    ResourceKey<Level> playerDim = buf.readResourceKey(Registries.DIMENSION);
                    ((IEPlayerPositionLookS2CPacket) (Object) packet).ip_setPlayerDimension(playerDim);
                }
                return packet;
            }
        );
    }
}
