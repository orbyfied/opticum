package test.opticum.platform.lwjgl;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MinimalExample {

    private static String readResource(String res) {
        try {
            InputStream is = MinimalExample.class.getResourceAsStream(res);
            String s = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            is.close();
            return s;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // vertex data buffer
    private static final ByteBuffer buf = ByteBuffer.allocateDirect(4096);

    // shader program
    static int program;

    // render objects
    static int vao;
    static int vbo;

    public static void main(String[] args) {
        // set buffer limit
        buf.limit(4096);

        // init glfw and create window
        GLFW.glfwInit();
        long window = GLFW.glfwCreateWindow(500, 500, "Hello", 0, 0);

        // create GL
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // create vertex objects
        vao = GL30.glGenVertexArrays();
        vbo = GL30.glGenBuffers();
        GL30.glBindVertexArray(vao);
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 7 * 4, 0);
        GL30.glVertexAttribPointer(1, 4, GL30.GL_FLOAT, false, 7 * 4, 0);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        // compile and link shaders
        int vertexShader   = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
        int fragmentShader = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
        GL30.glShaderSource(vertexShader,   readResource("/test.vsh"));
        GL30.glShaderSource(fragmentShader, readResource("/test.fsh"));
        GL30.glCompileShader(vertexShader);
        GL30.glCompileShader(fragmentShader);
        program = GL30.glCreateProgram();
        GL30.glAttachShader(program, vertexShader);
        GL30.glAttachShader(program, fragmentShader);
        GL30.glLinkProgram(program);

        // render loop
        while (!GLFW.glfwWindowShouldClose(window)) {
            // clear screen
            GL30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);

            // render
            render();

            // swap buffers
            GLFW.glfwSwapBuffers(window);
        }
    }

    static void render() {
        // put vertex data
        // manual to simulate graphics library
        putVec3(0.25f, 0.25f, 1f); putVec4(1.0f, 0.0f, 0.0f, 1.0f);
        putVec3(0.75f, 0.25f, 1f); putVec4(0.0f, 1.0f, 0.0f, 1.0f);
        putVec3(0.50f, 0.75f, 1f); putVec4(0.0f, 0.0f, 1.0f, 1.0f);

        // bind program
        GL30.glUseProgram(program);

        // bind vertex array
        GL30.glBindVertexArray(vao);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        // upload graphics data and draw
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, buf, GL30.GL_STATIC_DRAW);
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 3);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        // reset vertex data buffer
        buf.position(0);
    }

    //////////////////////////////////////////

    static void putVec3(float x, float y, float z) {
        buf.putFloat(x).putFloat(y).putFloat(z);
    }

    static void putVec4(float x, float y, float z, float w) {
        buf.putFloat(x).putFloat(y).putFloat(z).putFloat(z);
    }

}
