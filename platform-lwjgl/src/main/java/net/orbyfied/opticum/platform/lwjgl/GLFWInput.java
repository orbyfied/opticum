package net.orbyfied.opticum.platform.lwjgl;

import net.orbyfied.opticum.RenderInput;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;

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

        GLFW.glfwSetKeyCallback(context.window().handle, (window, key, scancode, action, mods) -> {
            // TODO
        });
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

        // for each mouse button
        for (int b = 0; b < mouseButtons.length; b++) {
            mouseButtons[b] = GLFW.glfwGetMouseButton(window, b);
        }

        // check if the window should close
        if (GLFW.glfwWindowShouldClose(window))
            context.destroy();
    }

}
