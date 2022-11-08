package net.orbyfied.opticum.platform.lwjgl;

import net.orbyfied.opticum.RenderWorker;
import net.orbyfied.opticum.window.WindowRenderContext;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public class GLFWWindowRenderContext extends WindowRenderContext<GLFWWindow> {

    public GLFWWindowRenderContext(RenderWorker worker) {
        super(worker);

        // init glfw
        GLFW.glfwInit();
    }

    @Override
    public GLFWWindow createWindow() {
        return new GLFWWindow(this);
    }

    @Override
    protected void startUpdate() {
        // switch context
        GLFW.glfwMakeContextCurrent(window.handle);
        GL.createCapabilities();
    }

    @Override
    protected void pollEvents() {
        // poll glfw events
        GLFW.glfwPollEvents();
    }

    @Override
    protected void onPrepare() {
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

    }

}
