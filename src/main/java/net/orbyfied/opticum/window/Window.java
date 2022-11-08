package net.orbyfied.opticum.window;

import net.orbyfied.opticum.util.Vec2i;

public abstract class Window {

    public Window(WindowRenderContext context) {
        this.context = context;
    }

    // the render context this window is bound to
    protected final WindowRenderContext context;

    // window properties
    protected String  title;
    protected Vec2i   size = new Vec2i(500, 500);
    protected Vec2i   pos;
    protected boolean fullScreen;
    protected boolean resizable;

    // window state
    protected boolean shown;
    protected Vec2i actualSize;
    protected Vec2i actualPos;

    public boolean isShown() {
        return shown;
    }

    /**
     * Creates the window.
     */
    public abstract Window create();

    /**
     * Destroys the window.
     */
    public abstract Window destroy();

    /**
     * Shows or hides the window depending on the boolean.
     * @param b If it should be visible.
     */
    public abstract Window show(boolean b);

    /*
        Getters and setters.
     */

    public WindowRenderContext context() {
        return context;
    }

    public Window setTitle(String title) {
        this.title = title;
        return this;
    }

    public Window setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
        return this;
    }

    public Window setResizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    public Window setSize(Vec2i vec) {
        this.pos = vec;
        return this;
    }

    public Window setSize(int w, int h) {
        return setSize(new Vec2i(w, h));
    }

    public Window setPosition(Vec2i vec) {
        this.pos = vec;
        return this;
    }

    public Window setPosition(int x, int y) {
        return setPosition(new Vec2i(x, y));
    }

}
