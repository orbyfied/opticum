package net.orbyfied.opticum.platform.lwjgl;

import net.orbyfied.opticum.RenderContext;
import net.orbyfied.opticum.RenderGraphics;
import net.orbyfied.opticum.VertexFormat;
import net.orbyfied.opticum.shader.Program;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

public class GLGraphics extends RenderGraphics {

    protected GLGraphics(RenderContext context) {
        super(context);
        init();
    }

    @Override
    protected ByteBuffer allocateByteBuffer(int cap) {
        return ByteBuffer.allocateDirect(cap);
    }

    // vertex buffer and array objects
    protected int vbo;
    protected int vao;

    // initialize the graphics
    private void init() {
        // allocate vertex buffer
        vao = GL30.glGenVertexArrays();
        vbo = GL30.glGenBuffers();
    }

    @Override
    public void push() {

    }

    @Override
    public void pop() {

    }

    @Override
    protected void switchVertexFormat(VertexFormat oldFormat, VertexFormat newFormat) {
        // set vertex attribute pointers
        GL30.glBindVertexArray(vao);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        VertexFormat.FieldLocal[] fls = newFormat.getFields();
        for (int i = 0; i < fls.length; i++) {
            // get field and type
            VertexFormat.Field field = fls[i].field();
            VertexFormat.FieldType type = field.getType();
            // get (primitive) element type
            VertexFormat.FieldType prim = type.getElementType();

            // get OpenGL type
            int pt = switch (prim) {
                case BYTE      -> GL_BYTE;
                case SHORT     -> GL_SHORT;
                case INT       -> GL_INT;
                case FLOAT     -> GL_FLOAT;
                case DOUBLE    -> GL_DOUBLE;
                case LONG      -> throw new IllegalArgumentException("LONG cannot be used in vertex data");
                default -> throw new IllegalArgumentException("Type (" + type.name() +
                        ") has non-primitive element type: " + prim.name());
            };

            // get length
            int len = type.getLength();

            // set attribute pointer
            GL30.glEnableVertexAttribArray(i);
            GL30.glVertexAttribPointer(i, len, pt, field.isNormalized(), vertexFormat.getVertexSize(), 0);
        }

        GL30.glBindVertexArray(0);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void useProgram(Program program) {
        GL30.glUseProgram(((GLProgram)program).handle);
    }

    @Override
    protected void drawBuffer(ByteBuffer buffer, int bytes, int verts) {
        // bind buffer
        GL30.glBindVertexArray(vao);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);

        // upload data
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, buffer.flip(), GL30.GL_STATIC_DRAW);
        int mode = switch (primitive) {
            case QUADS -> GL_QUADS;
            case TRIANGLES -> GL_TRIANGLES;
        };

        // enable attributes
        int l = vertexFormat.getFields().length;
        for (int i = 0; i < l; i++)
            GL30.glEnableVertexAttribArray(i);

        // draw buffer
        GL30.glDrawArrays(mode, 0, verts);

        // unbind
        GL30.glBindVertexArray(0);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
    }

}
