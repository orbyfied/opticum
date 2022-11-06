package net.orbyfied.opticum;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A driver for interfacing with a rendering platform.
 * Provides a specific type of worker and context.
 * For example, the LWJGL driver will provide LWJGL workers
 * and GLFW-wrapping contexts.
 * @param <W>  The worker type.
 * @param <WB> The worker builder type.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class RenderDriver<W extends RenderWorker, WB extends RenderWorker.Builder<W>> {

    // registered drivers
    private static final Map<Class<? extends RenderDriver>, RenderDriver> drivers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <D extends RenderDriver> D get(Class<D> dClass) {
        return (D) drivers.get(dClass);
    }

    {
        // register the driver automatically
        // when instantiated
        drivers.put(this.getClass(), this);
    }

    /////////////////////////////////////////////////////

    /**
     * A provider for creating platform specific contexts
     * for a specific base class.
     * @param <B> The base class type.
     * @param <P> The platform class type.
     */
    public static abstract class ContextProvider<B extends RenderContext, P extends B> {

        public ContextProvider(Class<B> baseType,
                               Class<P> platformType,
                               Function<RenderWorker, P> func) {
            this.baseType     = baseType;
            this.platformType = platformType;
            this.func         = func;
        }

        // the context base and platform type
        final Class<B> baseType;
        final Class<P> platformType;
        // the function
        final Function<RenderWorker, P> func;

        public Class<B> getBaseType() {
            return baseType;
        }

        public Class<P> getPlatformType() {
            return platformType;
        }

        public P createContext(RenderWorker worker) {
            P ctx = func.apply(worker);
            worker.contexts.add(ctx);
            return ctx;
        }

    }

    // the platform context implementations
    final Map<Class<? extends RenderContext>, ContextProvider>
            contextImpls = new HashMap<>();

    public void withContextProvider(Class<? extends RenderContext> k, ContextProvider f) {
        contextImpls.put(k, f);
    }

    public <B extends RenderContext, P extends B> ContextProvider<B, P> getContextProvider(Class<B> bClass) {
        return (ContextProvider<B, P>) contextImpls.get(bClass);
    }

    /**
     * Create a new worker builder of type {@code WB}.
     * @return The worker builder.
     */
    protected abstract WB createWorkerBuilder() ;

    /**
     * Build a new render worker.
     * @return The builder.
     */
    public WB newWorker() {
        return createWorkerBuilder();
    }

}
