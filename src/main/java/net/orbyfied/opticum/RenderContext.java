package net.orbyfied.opticum;

@SuppressWarnings({ "rawtypes" })
public abstract class RenderContext {

    public RenderContext(RenderWorker worker) {
        this.worker = worker;
        this.driver = worker.driver;
    }

    // the worker this render context is under
    protected final RenderWorker worker;
    // the driver this render context uses
    protected final RenderDriver driver;

    // the render graphics
    protected RenderGraphics graphics;
    // the input object
    protected RenderInput input;

    // updates this context
    // called by the worker every frame
    protected void update() {
        // call pre update
        startUpdate();

        // poll events
        pollEvents();

        // update input
        input.update();
    }

    // prepares this context
    protected boolean prepare() {
        // call prepare
        onPrepare();

        // create graphics
        if (!createGraphics()) {
            return false;
        }

        // create input
        if (!createInput()) {
            return false;
        }

        // successful
        return true;
    }

    // disables this context
    protected void disable() {
        // call disable
        onDisable();
    }

    /* Called at the start of an update. */
    protected abstract void startUpdate();
    protected abstract void pollEvents();

    /* Called for preparation. */
    protected abstract void onPrepare();
    protected abstract boolean createGraphics();
    protected abstract boolean createInput();

    /* Called to disable the context. */
    protected abstract void onDisable();

    /*
        Getters and setters.
     */

    public RenderWorker worker() {
        return worker;
    }

    public RenderDriver driver() {
        return driver;
    }

}
