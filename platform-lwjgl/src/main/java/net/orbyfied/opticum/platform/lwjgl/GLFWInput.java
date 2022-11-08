package net.orbyfied.opticum.platform.lwjgl;

import net.orbyfied.opticum.RenderInput;
import org.lwjgl.glfw.GLFW;

public class GLFWInput extends RenderInput {

    protected GLFWInput(GLFWWindowRenderContext context) {
        super(context);

        // set callbacks
        GLFW.glfwSetCursorPosCallback(context.window().handle,
                ((window, xPos, yPos) -> {
                    this.mouseX  = (float) xPos;
                    this.mouseY  = (float) yPos;
                    this.mouseDX = mouseX - lastMouseX;
                    this.mouseDY = mouseY - lastMouseY;
                    this.lastMouseX = mouseX;
                    this.lastMouseY = mouseY;
                }));
    }

    @Override
    public int countMouseButtons() {
        return 8;
    }

    @Override
    public int countKeys() {
        return 348;
    }

    @Override
    protected void update0() {
        GLFWWindowRenderContext context = (GLFWWindowRenderContext) this.context;
        long window = context.window().handle;

        // for each key
        for (int k = 0; k < keys.length; k++) {
            keys[k] = GLFW.glfwGetKey(window, k) == GLFW.GLFW_PRESS ? 0 : -1;
        }

        // for each mouse button
        for (int b = 0; b < mouseButtons.length; b++) {
            mouseButtons[b] = GLFW.glfwGetMouseButton(window, b);
        }

        // check if the window should close
        if (GLFW.glfwWindowShouldClose(window))
            context.destroy();
    }

}
