package net.orbyfied.opticum.window;

import net.orbyfied.opticum.RenderContext;
import net.orbyfied.opticum.RenderWorker;

public abstract class WindowRenderContext<W extends Window> extends RenderContext {

    public WindowRenderContext(RenderWorker worker) {
        super(worker);
        this.window = createWindow();
    }

    /**
     * The window.
     */
    protected W window;

    /**
     * Create a new window.
     * @return The window.
     */
    protected abstract W createWindow();

    public W window() {
        return window;
    }

}
