package qouteall.imm_ptl.core.render;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class IPTextureHelper {
    public static boolean isCreatingStencilDepthTexture = false;
    public static final IntSet stencilTextureIds = new IntOpenHashSet();
    
    public static boolean isStencilTextureId(int id) {
        return stencilTextureIds.contains(id);
    }
}
