package test.opticum.platform.lwjgl;

import net.orbyfied.opticum.*;
import net.orbyfied.opticum.platform.lwjgl.GLFWWindow;
import net.orbyfied.opticum.platform.lwjgl.GLFWWindowRenderContext;
import net.orbyfied.opticum.platform.lwjgl.LWJGLRenderDriver;
import net.orbyfied.opticum.platform.lwjgl.LWJGLRenderWorker;
import net.orbyfied.opticum.shader.Program;
import net.orbyfied.opticum.shader.Shader;
import net.orbyfied.opticum.window.WindowRenderContext;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class Basic2DTest {

    private static final VertexFormat FORMAT_2D =
            VertexFormat.builder().withPos2D().build();

    public static void main(String[] args) {
        // get driver
        LWJGLRenderDriver driver = RenderDriver.get(LWJGLRenderDriver.class);
        // get worker
        LWJGLRenderWorker worker = driver.newWorker().build();
        worker.withRenderTiming(RenderTiming.frameLoop(60f));

        // create context
        WindowRenderContext<?> context = worker.newContext(WindowRenderContext.class);
        context.window()
                .setTitle("Test")
                .setSize(500, 500)
                .setResizable(true)
                .create()
                .show(true);

        // compile shaders
        Program shader = context.newProgram()
                .withShader(context.newShader(Shader.ShaderType.VERTEX)
                        .compileSource(Basic2DTest.class.getResourceAsStream("/test.vsh")))
                .withShader(context.newShader(Shader.ShaderType.FRAGMENT)
                        .compileSource(Basic2DTest.class.getResourceAsStream("/test.fsh")))
                .link()
                .cleanShaders();

        // register renderer
        context.withLayer(RenderLayer.simple("primary", (__, graphics, input, dt) -> {
            // bind vertex format
            graphics.vertexFormat(FORMAT_2D);

            // bind shar
            graphics.useProgram(shader);

            // start drawing a triangle
            graphics.begin(RenderGraphics.Primitive.TRIANGLES);
            graphics.vertex2d(0.25f, 0.25f).end();
            graphics.vertex2d(0.75f, 0.25f).end();
            graphics.vertex2d(0.5f,  0.75f).end();
            graphics.end();
        }));

        context.withLayer(RenderLayer.simple("window", (context1, graphics, input, dt) -> {
            GLFW.glfwSetWindowTitle(((GLFWWindow)context.window()).getHandle(), "Test | DT: " + worker.dt + ", FPS: " + worker.fps);
        }));

        // start worker
        worker.startSync();
    }

}
