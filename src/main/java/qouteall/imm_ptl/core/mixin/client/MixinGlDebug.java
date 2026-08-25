package qouteall.imm_ptl.core.mixin.client;

import com.mojang.blaze3d.opengl.GlDebug;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlDebug.class)
public class MixinGlDebug {
    private static int loggedNum = 0;
    
    @Inject(
        method = "printDebugLog", at = @At("HEAD")
    )
    private void onLogging(
        int source, int type, int id, int severity, int messageLength, long message, long l,
        CallbackInfo ci
    ) {
        if (loggedNum < 5) {
            loggedNum++;
            int boundFbo = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
            int boundDrawFbo = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
            int boundReadFbo = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
            int fboStatus = boundFbo != 0 ? GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) : -1;
            int drawStatus = boundDrawFbo != 0 ? GL30.glCheckFramebufferStatus(GL30.GL_DRAW_FRAMEBUFFER) : -1;
            
            System.err.println("=== OPENGL ERROR DEBUG TRACE (id=" + id + ", severity=" + severity + ", FBO=" + boundFbo + ", drawFBO=" + boundDrawFbo + ", readFBO=" + boundReadFbo + ", fboStatus=0x" + Integer.toHexString(fboStatus) + ", drawStatus=0x" + Integer.toHexString(drawStatus) + ") ===");
            new Exception("OpenGL Error Triggered").printStackTrace(System.err);
        }
    }
}
