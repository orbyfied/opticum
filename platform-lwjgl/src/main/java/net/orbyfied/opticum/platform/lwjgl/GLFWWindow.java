package net.orbyfied.opticum.platform.lwjgl;

import net.orbyfied.opticum.util.BufferUtil;
import net.orbyfied.opticum.window.Window;
import net.orbyfied.opticum.window.WindowRenderContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class GLFWWindow extends Window {

    public GLFWWindow(WindowRenderContext context) {
        super(context);
    }

    // additional properties
    protected long monitor;

    /**
     * The GLFW window handle.
     */
    protected long handle;

    /**
     * The window hints to enable.
     */
    protected Map<Integer, Integer> enableHints = new HashMap<>();

    public long getHandle() {
        return handle;
    }

    public GLFWWindow setMonitor(long monitor) {
        this.monitor = monitor;
        return this;
    }

    public GLFWWindow enableHint(int hint, int value) {
        this.enableHints.put(hint, value);
        return this;
    }

    public GLFWWindow disableHint(int hint) {
        this.enableHints.remove(hint);
        return this;
    }

    @Override
    public GLFWWindow create() {
        // fullscreen logic
        long m = monitor;
        if (!fullScreen)
            m = 0;

        // set hints
        for (Map.Entry<Integer, Integer> hint : enableHints.entrySet())
            GLFW.glfwWindowHint(hint.getKey(), hint.getValue());

        if (((GLFWWindowRenderContext)context).debugContext)
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);

        // create window
        handle = GLFW.glfwCreateWindow(
                size.getX(), size.getY(),
                title,
                m,
                0
        );

        if (handle == 0) {
            // handle error
            PointerBuffer descPtr = PointerBuffer.allocateDirect(2048);
            int code    = GLFW.glfwGetError(descPtr);
            String desc = descPtr.getStringASCII();

            // throw exception
            throw new RuntimeException("GLFW window creation error (" + code + "): " + desc);
        }

        // hide
        if (shown)
            GLFW.glfwShowWindow(handle);
        else
            GLFW.glfwHideWindow(handle);

        return this;
    }

    @Override
    public GLFWWindow destroy() {
        // destroy window
        GLFW.glfwDestroyWindow(handle);

        return this;
    }

    @Override
    public GLFWWindow show(boolean b) {
        if (b) {
            // set properties
            GLFW.glfwSetWindowSize(handle, size.getX(), size.getY());
            if (title != null)
                GLFW.glfwSetWindowTitle(handle, title);
            if (pos != null)
                GLFW.glfwSetWindowPos(handle, pos.getX(), pos.getY());
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? 1 : 0);

            // show window
            GLFW.glfwShowWindow(handle);
        } else {
            // hide window
            GLFW.glfwHideWindow(handle);
        }

        return this;
    }

}
