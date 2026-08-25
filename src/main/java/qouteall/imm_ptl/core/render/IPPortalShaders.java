package qouteall.imm_ptl.core.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.logging.LogUtils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import qouteall.imm_ptl.core.CHelper;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.q_misc_util.my_util.TriangleConsumer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class IPPortalShaders {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private static boolean initialized = false;
    
    // Portal Area Shader
    private static int portalAreaProgram = 0;
    private static int paModelViewLoc = -1;
    private static int paProjLoc = -1;
    private static int paClipLoc = -1;
    
    // Portal Draw Framebuffer Shader
    private static int portalDrawFbProgram = 0;
    private static int fbModelViewLoc = -1;
    private static int fbProjLoc = -1;
    private static int fbClipLoc = -1;
    private static int fbSamplerLoc = -1;
    private static int fbWidthLoc = -1;
    private static int fbHeightLoc = -1;
    
    // Screen Quad Shader
    private static int screenQuadProgram = 0;
    private static int sqColorLoc = -1;
    
    // VAO & VBO
    private static int vaoId = 0;
    private static int vboId = 0;
    private static int screenQuadVaoId = 0;
    
    private static FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(1024 * 7 * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
    
    private static final float[] MATRIX_BUFFER = new float[16];
    
    private static int currentVertexCount = 0;
    private static float currentFogR, currentFogG, currentFogB, currentFogA;
    
    private static final TriangleConsumer AREA_TRIANGLES_CONSUMER = (p0x, p0y, p0z, p1x, p1y, p1z, p2x, p2y, p2z) -> {
        ensureCapacity(vertexBuffer.position() + 21);
        
        vertexBuffer.put((float) p0x).put((float) p0y).put((float) p0z);
        vertexBuffer.put(currentFogR).put(currentFogG).put(currentFogB).put(currentFogA);
        
        vertexBuffer.put((float) p1x).put((float) p1y).put((float) p1z);
        vertexBuffer.put(currentFogR).put(currentFogG).put(currentFogB).put(currentFogA);
        
        vertexBuffer.put((float) p2x).put((float) p2y).put((float) p2z);
        vertexBuffer.put(currentFogR).put(currentFogG).put(currentFogB).put(currentFogA);
        
        currentVertexCount += 3;
    };
    
    private static final TriangleConsumer FB_CONSUMER = (p0x, p0y, p0z, p1x, p1y, p1z, p2x, p2y, p2z) -> {
        ensureCapacity(vertexBuffer.position() + 21);
        
        vertexBuffer.put((float) p0x).put((float) p0y).put((float) p0z);
        vertexBuffer.put(1f).put(1f).put(1f).put(1f);
        
        vertexBuffer.put((float) p1x).put((float) p1y).put((float) p1z);
        vertexBuffer.put(1f).put(1f).put(1f).put(1f);
        
        vertexBuffer.put((float) p2x).put((float) p2y).put((float) p2z);
        vertexBuffer.put(1f).put(1f).put(1f).put(1f);
        
        currentVertexCount += 3;
    };
    
    private static void ensureCapacity(int neededFloats) {
        if (vertexBuffer.capacity() < neededFloats) {
            int newCap = Math.max(vertexBuffer.capacity() * 2, neededFloats);
            vertexBuffer = ByteBuffer.allocateDirect(newCap * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        }
    }
    
    public static void init() {
        if (initialized) {
            return;
        }
        
        portalAreaProgram = createProgram(
            PORTAL_AREA_VERTEX_SHADER,
            PORTAL_AREA_FRAGMENT_SHADER,
            true
        );
        paModelViewLoc = GL20.glGetUniformLocation(portalAreaProgram, "ModelViewMat");
        paProjLoc = GL20.glGetUniformLocation(portalAreaProgram, "ProjMat");
        paClipLoc = GL20.glGetUniformLocation(portalAreaProgram, "iportal_ClippingEquation");
        
        portalDrawFbProgram = createProgram(
            PORTAL_DRAW_FB_VERTEX_SHADER,
            PORTAL_DRAW_FB_FRAGMENT_SHADER,
            false
        );
        fbModelViewLoc = GL20.glGetUniformLocation(portalDrawFbProgram, "ModelViewMat");
        fbProjLoc = GL20.glGetUniformLocation(portalDrawFbProgram, "ProjMat");
        fbClipLoc = GL20.glGetUniformLocation(portalDrawFbProgram, "iportal_ClippingEquation");
        fbSamplerLoc = GL20.glGetUniformLocation(portalDrawFbProgram, "DiffuseSampler");
        fbWidthLoc = GL20.glGetUniformLocation(portalDrawFbProgram, "w");
        fbHeightLoc = GL20.glGetUniformLocation(portalDrawFbProgram, "h");
        
        screenQuadProgram = createProgram(
            SCREEN_QUAD_VERTEX_SHADER,
            SCREEN_QUAD_FRAGMENT_SHADER,
            false
        );
        sqColorLoc = GL20.glGetUniformLocation(screenQuadProgram, "Color");
        
        vaoId = GL30.glGenVertexArrays();
        vboId = GL15.glGenBuffers();
        
        GL30.glBindVertexArray(vaoId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 7 * 4, 0);
        
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 7 * 4, 3 * 4);
        
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        
        screenQuadVaoId = GL30.glGenVertexArrays();
        
        initialized = true;
        LOGGER.info("[ImmPtl] IPPortalShaders initialized successfully (optimized pipeline)");
    }
    
    public static void renderPortalAreaTriangles(
        Portal portal,
        float fogR, float fogG, float fogB, float fogA,
        org.joml.Matrix4f modelViewMatrix,
        org.joml.Matrix4f projectionMatrix
    ) {
        init();
        
        vertexBuffer.clear();
        currentVertexCount = 0;
        currentFogR = fogR;
        currentFogG = fogG;
        currentFogB = fogB;
        currentFogA = fogA;
        
        net.minecraft.world.phys.Vec3 originRelativeToCamera =
            portal.getOriginPos().subtract(CHelper.getCurrentCameraPos());
        
        portal.renderViewAreaMesh(originRelativeToCamera, AREA_TRIANGLES_CONSUMER);
        
        if (currentVertexCount == 0) {
            return;
        }
        
        vertexBuffer.flip();
        
        GL20.glUseProgram(portalAreaProgram);
        
        modelViewMatrix.get(MATRIX_BUFFER);
        GL20.glUniformMatrix4fv(paModelViewLoc, false, MATRIX_BUFFER);
        
        projectionMatrix.get(MATRIX_BUFFER);
        GL20.glUniformMatrix4fv(paProjLoc, false, MATRIX_BUFFER);
        
        setClippingUniform(paClipLoc);
        
        drawVertices(currentVertexCount);
        
        GL20.glUseProgram(0);
    }
    
    public static void renderPortalAreaWithFb(
        Portal portal,
        int textureId,
        int width, int height,
        org.joml.Matrix4f modelViewMatrix,
        org.joml.Matrix4f projectionMatrix
    ) {
        init();
        
        vertexBuffer.clear();
        currentVertexCount = 0;
        
        net.minecraft.world.phys.Vec3 originRelativeToCamera =
            portal.getOriginPos().subtract(CHelper.getCurrentCameraPos());
        
        portal.renderViewAreaMesh(originRelativeToCamera, FB_CONSUMER);
        
        if (currentVertexCount == 0) {
            return;
        }
        
        vertexBuffer.flip();
        
        GL20.glUseProgram(portalDrawFbProgram);
        
        modelViewMatrix.get(MATRIX_BUFFER);
        GL20.glUniformMatrix4fv(fbModelViewLoc, false, MATRIX_BUFFER);
        
        projectionMatrix.get(MATRIX_BUFFER);
        GL20.glUniformMatrix4fv(fbProjLoc, false, MATRIX_BUFFER);
        
        setClippingUniform(fbClipLoc);
        
        GL20.glUniform1i(fbSamplerLoc, 0);
        GL20.glUniform1f(fbWidthLoc, (float) width);
        GL20.glUniform1f(fbHeightLoc, (float) height);
        
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        
        drawVertices(currentVertexCount);
        
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL20.glUseProgram(0);
    }
    
    public static void renderScreenQuad(float r, float g, float b, float a) {
        init();
        
        GL20.glUseProgram(screenQuadProgram);
        GL20.glUniform4f(sqColorLoc, r, g, b, a);
        
        boolean cullEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        if (cullEnabled) {
            GL11.glDisable(GL11.GL_CULL_FACE);
        }
        
        GL30.glBindVertexArray(screenQuadVaoId);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        GL30.glBindVertexArray(0);
        
        if (cullEnabled) {
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
        
        GL20.glUseProgram(0);
    }
    
    private static void setClippingUniform(int uniformLoc) {
        if (uniformLoc == -1) {
            return;
        }
        
        if (FrontClipping.isClippingEnabled) {
            double[] eq = FrontClipping.getActiveClipPlaneEquationBeforeModelView();
            if (eq != null) {
                GL20.glUniform4f(uniformLoc, (float) eq[0], (float) eq[1], (float) eq[2], (float) eq[3]);
                return;
            }
        }
        GL20.glUniform4f(uniformLoc, 0f, 0f, 0f, 1f);
    }
    
    private static void drawVertices(int vertexCount) {
        GL30.glBindVertexArray(vaoId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STREAM_DRAW);
        
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }
    
    private static int createProgram(String vsh, String fsh, boolean hasColorAttrib) {
        int vertexShader = compileShader(GL20.GL_VERTEX_SHADER, vsh);
        int fragmentShader = compileShader(GL20.GL_FRAGMENT_SHADER, fsh);
        
        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vertexShader);
        GL20.glAttachShader(program, fragmentShader);
        
        GL20.glBindAttribLocation(program, 0, "Position");
        if (hasColorAttrib) {
            GL20.glBindAttribLocation(program, 1, "Color");
        }
        
        GL20.glLinkProgram(program);
        
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(program, 1024);
            LOGGER.error("[ImmPtl] Failed to link shader program: {}", log);
        }
        
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
        
        return program;
    }
    
    private static int compileShader(int type, String source) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shader, 1024);
            LOGGER.error("[ImmPtl] Failed to compile shader: {}\nSource:\n{}", log, source);
        }
        
        return shader;
    }
    
    private static final String PORTAL_AREA_VERTEX_SHADER = """
        #version 150
        in vec3 Position;
        in vec4 Color;
        uniform mat4 ModelViewMat;
        uniform mat4 ProjMat;
        uniform vec4 iportal_ClippingEquation;
        out vec4 vertexColor;
        void main() {
            gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
            vertexColor = Color;
            gl_ClipDistance[0] = dot(Position.xyz, iportal_ClippingEquation.xyz) + iportal_ClippingEquation.w;
        }
        """;
    
    private static final String PORTAL_AREA_FRAGMENT_SHADER = """
        #version 150
        in vec4 vertexColor;
        out vec4 fragColor;
        void main() {
            fragColor = vertexColor;
        }
        """;
    
    private static final String PORTAL_DRAW_FB_VERTEX_SHADER = """
        #version 150
        in vec3 Position;
        uniform mat4 ModelViewMat;
        uniform mat4 ProjMat;
        uniform vec4 iportal_ClippingEquation;
        void main() {
            gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
            gl_ClipDistance[0] = dot(Position.xyz, iportal_ClippingEquation.xyz) + iportal_ClippingEquation.w;
        }
        """;
    
    private static final String PORTAL_DRAW_FB_FRAGMENT_SHADER = """
        #version 150
        uniform sampler2D DiffuseSampler;
        uniform float w;
        uniform float h;
        out vec4 fragColor;
        void main() {
            fragColor = texture(DiffuseSampler, vec2(gl_FragCoord.x / w, gl_FragCoord.y / h));
        }
        """;
    
    private static final String SCREEN_QUAD_VERTEX_SHADER = """
        #version 150
        uniform vec4 Color;
        out vec4 vertexColor;
        const vec2 positions[6] = vec2[](
            vec2(-1.0,  1.0),
            vec2(-1.0, -1.0),
            vec2( 1.0, -1.0),
            vec2(-1.0,  1.0),
            vec2( 1.0, -1.0),
            vec2( 1.0,  1.0)
        );
        void main() {
            gl_Position = vec4(positions[gl_VertexID], 0.0, 1.0);
            vertexColor = Color;
        }
        """;
    
    private static final String SCREEN_QUAD_FRAGMENT_SHADER = """
        #version 150
        in vec4 vertexColor;
        out vec4 fragColor;
        void main() {
            fragColor = vertexColor;
        }
        """;
}
