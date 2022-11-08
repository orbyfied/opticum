package net.orbyfied.opticum;

import net.orbyfied.opticum.shader.Program;
import net.orbyfied.opticum.shader.Shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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

    // render layers
    protected List<RenderLayer> layerList = new ArrayList<>();
    protected Map<String, RenderLayer> layerMap = new HashMap<>();
    protected Map<RenderLayer, Float> layerTimes = new HashMap<>();

    // if it is active
    protected AtomicBoolean active = new AtomicBoolean();

    public boolean active() {
        return active.get();
    }

    public void active(boolean active) {
        this.active.set(active);
    }

    /*
        Render Layers
     */

    /**
     * Inserts a render layer at the correct position.
     * @param layer The layer.
     */
    public void withLayer(RenderLayer layer) {
        // insert into the correct position
        int pos = layer.pos;
        if (pos != -1) {
            if (pos >= layerList.size())
                for (int i = 0; i < (pos - layerList.size()); i++)
                    layerList.add(null);
            layerList.add(pos, layer);
        } else {
            layerList.add(layer);
            layer.pos = layerList.size() - 1;
        }

        // insert by name
        layerMap.put(layer.name, layer);
    }

    /**
     * Get a render layer by name.
     * @param name The name.
     * @return The layer or null if absent.
     */
    public RenderLayer layer(String name) {
        return layerMap.get(name);
    }

    /**
     * Get all layer timings of the last frame.
     * @return The map.
     */
    public Map<RenderLayer, Float> frameLayerTimings() {
        return layerTimes;
    }

    /*
        Updates and Status
     */

    // updates this context
    // called by the worker every frame
    protected void update() {
        // call pre update
        startUpdate();

        // poll events
        pollEvents();

        // update input
        input.update();

        // render layers
        {
            int l = layerList.size();
            for (int i = 0; i < l; i++) {
                RenderLayer layer = layerList.get(i);

                // update if active
                if (layer.active) {
                    // timings
                    long t1 = System.nanoTime();

                    // update render layer
                    layer.update(this, worker.dt);

                    // timings
                    long t2 = System.nanoTime();
                    float t = (float) ((t2 - t1) / 1E9);
                    layerTimes.put(layer, t);
                }
            }
        }

        // end update
        endUpdate();
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
        // deactivate
        active(false);

        // call disable
        onDisable();
    }

    // destroys this context
    public void destroy() {
        // disable
        disable();

        // schedule for removal
        worker.scheduleRemove(this);
    }

    /* Called on update. */
    protected abstract void startUpdate();
    protected abstract void endUpdate();
    protected abstract void pollEvents();

    /* Called for preparation. */
    protected abstract void onPrepare();
    protected abstract boolean createGraphics();
    protected abstract boolean createInput();

    /* Called to disable the context. */
    protected abstract void onDisable();

    /*
        Shaders and Shader Programs
     */

    /**
     * Create and allocate a new shader of the
     * specified type.
     * @param type The type of shader.
     * @return The shader.
     */
    public abstract Shader newShader(Shader.ShaderType type);

    /**
     * Create and allocate a new shader program.
     * @return The shader program.
     */
    public abstract Program newProgram();

    /*
        Getters and setters.
     */

    public RenderWorker worker() {
        return worker;
    }

    public RenderDriver driver() {
        return driver;
    }

    public RenderInput input() {
        return input;
    }

    public RenderGraphics graphics() {
        return graphics;
    }

}
