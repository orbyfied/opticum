package net.orbyfied.opticum;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A single threaded render worker.
 * Made by and for a specific driver.
 * Implemented by that driver.
 *
 * A render worker can work on multiple contexts
 * at once if the driver supports that. The way
 * it captures input and generally interacts with
 * the devices is provided by the driver.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class RenderWorker {

    /** Builder for basic functionality */
    public static abstract class Builder<W extends RenderWorker> {
        public Builder(RenderDriver driver) { this.driver = driver; }
        protected final RenderDriver driver;

        public abstract W build();
    }

    ////////////////////////////////////////

    public RenderWorker(RenderDriver driver) {
        this.driver = driver;
    }

    /*
        Settings
     */

    protected boolean supportsMultipleContexts = true;

    /*
        Worker
     */

    // the render driver
    protected final RenderDriver driver;

    // the contexts registered
    protected final List<RenderContext> contexts = new ArrayList<>();
    // the contexts enabled
    protected List<RenderContext> contextsEnabled = new ArrayList<>();

    /**
     * Creates a new context of the specified type.
     * @param cClass The runtime context type.
     * @param <C> The context type.
     * @return The context or null if unsupported.
     */
    public <C extends RenderContext> C newContext(Class<C> cClass) {
        // get provider
        RenderDriver.ContextProvider<C, ? extends C> provider
                = driver.getContextProvider(cClass);
        if (provider == null)
            // unsupported context type
            return null;

        // create context
        return provider.createContext(this);
    }

    List<RenderContext> toRemove = new ArrayList<>();
    protected void scheduleRemove(RenderContext context) {
        toRemove.add(context);

        // check if it should stop
        if (contextsEnabled.isEmpty())
            setActive(false);
    }

    // the render timing
    protected RenderTiming timing;

    // if the worker is currently active
    protected final AtomicBoolean active = new AtomicBoolean();
    // the thread it is currently working on
    protected volatile Thread workingThread;

    /**
     * The delta time of last frame.
     */
    public float dt;

    /**
     * The frames per second.
     * Directly calculated from the delta time.
     */
    public float fps;

    /**
     * Synchronously runs the worker.
     */
    protected void run() {
        // set active and register thread
        this.active.set(true);
        this.workingThread = Thread.currentThread();

        // prepare contexts
        contextsEnabled = new ArrayList<>();
        for (RenderContext context : contexts) {
            if (context.prepare()) {
                contextsEnabled.add(context);
            }
        }

        // while active do
        try {
            while (active.get()) {
                // yield and check timings
                if (!yieldTimings())
                    continue;

                // update contexts
                int l = contexts.size();
                for (int i = 0; i < l; i++) {
                    RenderContext context = contexts.get(i);
                    context.update();
                }

                // remove contexts that need to be
                for (RenderContext rem : toRemove) {
                    contexts.remove(rem);
                    contextsEnabled.remove(rem);
                }
            }
        } catch (Exception e) {
            // TODO: handle error
            e.printStackTrace();
        }

        // disable contexts
        for (RenderContext context : contexts)
            context.disable();
    }

    /**
     * Starts the worker on this thread.
     * This is blocking and can only be
     * released by the worker being set
     * inactive, for example by another
     * thread or the context(s) closing.
     */
    public void startSync() {
        // just call run
        run();
    }

    // waits for the next frame and
    // does some timing shit
    // returns if it should update
    private boolean yieldTimings() {
        boolean shouldUpdate = timing.yieldAndCheck();
        if (shouldUpdate) {
            if (timing.getDeltaTime() != null) {
                dt  = timing.getDeltaTime();
                fps = 1f / dt;
            }
        }
        return shouldUpdate;
    }

    /*
        Getters and setters.
     */

    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean b) {
        this.active.set(b);
    }

    public RenderDriver driver() {
        return driver;
    }

    public boolean supportsMultipleContexts() {
        return supportsMultipleContexts;
    }

}
