package net.orbyfied.opticum;

import java.util.Arrays;

/**
 * Object used for registering input from the user.
 *
 * The format for pressable buttons per array element is,
 * the number generally describes for how many seconds the key has been pressed.
 * -1 means it is not pressed, 0 means it has just been pressed and
 * anything above 0 is the time since it has been pressed.
 */
public abstract class RenderInput {

    protected RenderInput(RenderContext context) {
        // set context
        this.context = context;

        // allocate buttons
        this.mouseButtons = new float[countMouseButtons()];
        Arrays.fill(mouseButtons, -1);
        this.keys = new float[countKeys()];
        Arrays.fill(keys, -1);
    }

    // count array sizes
    public abstract int countMouseButtons();
    public abstract int countKeys();

    // the context
    protected final RenderContext context;

    // mouse position
    public float lastMouseX;
    public float lastMouseY;
    public float mouseX;
    public float mouseY;
    public float mouseDX;
    public float mouseDY;

    // mouse scroll
    public float scrollDelta;

    // mouse buttons
    public float[] mouseButtons;

    // keys
    public float[] keys;

    // updates the input
    protected final void update() {
        // advance time on all keys and buttons
        float dt = context.worker.dt;
        int l;
        l = mouseButtons.length;
        for (int i = 0; i < l; i++) {
            if (mouseButtons[i] >= 0)
                mouseButtons[i] += dt;
        }

        // prepare for update
        mouseDX = 0;
        mouseDY = 0;
        scrollDelta = 0;

        // update
        update0();

        // update mouse position
        this.mouseX += mouseDX;
        this.mouseY += mouseDY;
    }

    /* Should poll the values from the platform. */
    protected abstract void update0();

    public boolean isKeyDown(int key) {
        return keys[key] != -1;
    }

    public boolean isKeyUp(int key) {
        return keys[key] == -1;
    }

    public float getKeyState(int key) {
        return keys[key];
    }

    public boolean isButtonDown(int b) {
        return mouseButtons[b] != -1;
    }

    public boolean isButtonUp(int b) {
        return mouseButtons[b] == -1;
    }

    public float getButtonState(int b) {
        return mouseButtons[b];
    }

    public float getMouseDX() {
        return mouseDX;
    }

    public float getMouseDY() {
        return mouseDY;
    }

    public float getMouseX() {
        return mouseX;
    }

    public float getMouseY() {
        return mouseY;
    }

    public float[] getKeys() {
        return keys;
    }

    public float[] getMouseButtons() {
        return mouseButtons;
    }

}
