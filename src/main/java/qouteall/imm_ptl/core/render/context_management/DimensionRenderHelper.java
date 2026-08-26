package qouteall.imm_ptl.core.render.context_management;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.level.Level;
import qouteall.imm_ptl.core.ducks.IEGameRenderer;
import qouteall.q_misc_util.Helper;

public class DimensionRenderHelper {
    private static final Minecraft client = Minecraft.getInstance();
    public final Level world;
    
    public final LightTexture lightmapTexture;
    public final FogRenderer fogRenderer;
    
    public DimensionRenderHelper(Level world) {
        this.world = world;
        
        Level mainWorld = client.player != null ? client.player.level() : client.level;
        if (mainWorld != null && mainWorld.dimension() == world.dimension()) {
            IEGameRenderer gameRenderer = (IEGameRenderer) client.gameRenderer;
            
            lightmapTexture = client.gameRenderer.lightTexture();
            fogRenderer = gameRenderer.ip_getFogRenderer();
        }
        else {
            lightmapTexture = new LightTexture(client.gameRenderer, client);
            fogRenderer = new FogRenderer();
            Helper.log("Created lightmap texture and fog renderer for " + world.dimension().location());
        }
    }
    
    public void tick() {
        if (lightmapTexture != client.gameRenderer.lightTexture()) {
            lightmapTexture.tick();
        }
    }
    
    public void endFrame() {
        if (fogRenderer != ((IEGameRenderer) client.gameRenderer).ip_getFogRenderer()) {
            fogRenderer.endFrame();
        }
    }
    
    public void cleanUp() {
        if (lightmapTexture != client.gameRenderer.lightTexture()) {
            lightmapTexture.close();
        }
        if (fogRenderer != ((IEGameRenderer) client.gameRenderer).ip_getFogRenderer()) {
            fogRenderer.close();
        }
    }
    
}

