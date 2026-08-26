package qouteall.imm_ptl.core.ducks;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.fog.FogRenderer;

public interface IEGameRenderer {
    void ip_setLightmapTextureManager(LightTexture manager);
    
    boolean ip_getDoRenderHand();
    
    void ip_setDoRenderHand(boolean cond);
    
    void ip_setCamera(Camera camera);
    
    void ip_setIsRenderingPanorama(boolean cond);
    
    FogRenderer ip_getFogRenderer();
    
    void ip_setFogRenderer(FogRenderer fogRenderer);
    
    float ip_getFov(Camera camera, float partialTick, boolean isFovChanged);
}

