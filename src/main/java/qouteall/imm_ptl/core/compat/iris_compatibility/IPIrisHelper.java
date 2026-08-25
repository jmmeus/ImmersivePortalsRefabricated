package qouteall.imm_ptl.core.compat.iris_compatibility;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL43C;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;

public class IPIrisHelper {
    
    public static int getTextureId(RenderTarget renderTarget, boolean depth) {
        if (depth) {
            return ((GlTexture) renderTarget.getDepthTexture()).glId();
        }
        else {
            return ((GlTexture) renderTarget.getColorTexture()).glId();
        }
    }
    
    public static void copyDepthStencil(
        RenderTarget from, RenderTarget to,
        boolean copyDepth, boolean copyStencil
    ) {
        newCopyDepthStencil(from, to);
    }
    
    private static boolean isCopyImageSubDataSupported() {
        return GL.getCapabilities().glCopyImageSubData != 0;
    }
    
    public static void newCopyDepthStencil(
        RenderTarget from, RenderTarget to
    ) {
        GL43C.glCopyImageSubData(
            getTextureId(from, true),
            GL43C.GL_TEXTURE_2D,
            0,
            0,
            0,
            0,
            getTextureId(to, true),
            GL43C.GL_TEXTURE_2D,
            0,
            0,
            0,
            0,
            from.width,
            from.height,
            1
        );
    }
    
    public static void copyColor(
        RenderTarget from, RenderTarget to
    ) {
        GL43C.glCopyImageSubData(
            getTextureId(from, false),
            GL43C.GL_TEXTURE_2D,
            0,
            0,
            0,
            0,
            getTextureId(to, false),
            GL43C.GL_TEXTURE_2D,
            0,
            0,
            0,
            0,
            from.width,
            from.height,
            1
        );
    }
    
}
