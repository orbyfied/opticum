package net.orbyfied.opticum.platform.lwjgl;

import net.orbyfied.opticum.RenderDriver;
import net.orbyfied.opticum.RenderWorker;

@SuppressWarnings("rawtypes")
public class LWJGLRenderWorker extends RenderWorker {

    public static class Builder extends RenderWorker.Builder<LWJGLRenderWorker> {
        public Builder(RenderDriver driver) {
            super(driver);
        }

        @Override
        public LWJGLRenderWorker build() {
            return new LWJGLRenderWorker(driver);
        }
    }

    /////////////////////////////////////////

    public LWJGLRenderWorker(RenderDriver driver) {
        super(driver);
    }

}
