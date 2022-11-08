package net.orbyfied.opticum.platform.lwjgl;

import net.orbyfied.opticum.RenderDriver;
import net.orbyfied.opticum.window.WindowRenderContext;
import org.lwjgl.opengl.GL;

public class LWJGLRenderDriver extends RenderDriver<LWJGLRenderWorker, LWJGLRenderWorker.Builder> {

    static {
        RenderDriver.register(new LWJGLRenderDriver());
    }

    //////////////////////////////////////////////

    {
        this.withContextProvider(new ContextProvider<>(WindowRenderContext.class,
                GLFWWindowRenderContext.class, GLFWWindowRenderContext::new));
    }

    @Override
    protected LWJGLRenderWorker.Builder createWorkerBuilder() {
        return new LWJGLRenderWorker.Builder(this);
    }

}
