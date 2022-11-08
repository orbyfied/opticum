package net.orbyfied.opticum.platform.lwjgl;

import net.orbyfied.opticum.RenderWorker;
import net.orbyfied.opticum.shader.Program;
import net.orbyfied.opticum.shader.Shader;
import net.orbyfied.opticum.window.WindowRenderContext;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;

public class GLFWWindowRenderContext extends WindowRenderContext<GLFWWindow> implements GLContextLike {

    public GLFWWindowRenderContext(RenderWorker worker) {
        super(worker);

        // init glfw
        GLFW.glfwInit();
    }

    protected GLCapabilities glCapabilities;

    @Override
    public void switchContext() {
        // switch context
        GLFW.glfwMakeContextCurrent(window.handle);
        if (glCapabilities == null)
            // initialize GL
            glCapabilities = GL.createCapabilities();
        GL.setCapabilities(glCapabilities);
    }

    @Override
    public GLFWWindow createWindow() {
        return new GLFWWindow(this);
    }

    @Override
    protected void startUpdate() {
        switchContext();
    }

    @Override
    protected void endUpdate() {
        GLFW.glfwSwapInterval(0);
        GLFW.glfwSwapBuffers(window.handle);
    }

    @Override
    protected void pollEvents() {
        // poll glfw events
        GLFW.glfwPollEvents();
    }

    @Override
    protected void onPrepare() {
        // switch context
        switchContext();

        // check window
        if (window == null || window.handle == 0)
            throw new IllegalStateException("No GLFW window has been created yet.");
    }

    @Override
    protected boolean createGraphics() {
        this.graphics = new GLGraphics(this);

        // return success
        return true;
    }

    @Override
    protected boolean createInput() {
        this.input = new GLFWInput(this);

        // return success
        return true;
    }

    @Override
    protected void onDisable() {
        window.destroy();
    }

    @Override
    public Shader newShader(Shader.ShaderType type) {
        switchContext();
        return new GLShader(this, type);
    }

    @Override
    public Program newProgram() {
        switchContext();
        return new GLProgram(this);
    }

}
