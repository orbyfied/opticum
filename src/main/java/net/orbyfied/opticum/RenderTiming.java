package net.orbyfied.opticum;

/**
 * Dictates when a worker executes a frame.
 */
public abstract class RenderTiming {

    /**
     * Creates a new frame loop render timing.
     * @param maxFPS The maximum frames per second to lock to.
     *               Anything below 0 means unlocked.
     * @return The timing.
     */
    public static FrameLoopRenderTiming frameLoop(float maxFPS) {
        return new FrameLoopRenderTiming().setMaxRate(maxFPS);
    }

    /**
     * Frame loop based render timing.
     * Frames per second can be either limited or unlocked.
     */
    public static class FrameLoopRenderTiming extends RenderTiming {

        public FrameLoopRenderTiming setMaxRate(float maxRate) {
            this.maxRate = maxRate;
            if (maxRate < 0) /* unlocked fps */ {
                this.minTime = -1;
            } else /* locked */ {
                // calculate minimum time to delay
                this.minTime = 1f / maxRate;
            }

            return this;
        }

        // max frames per second
        float maxRate;
        float minTime;

        // last time
        long lastTime;

        @Override
        public boolean yieldAndCheck() {
            // calculate time since last update
            long  currTime = System.nanoTime();
            long  diffNS   = currTime - lastTime;
            float dt       = diffNS / 1_000_000_000f;

            // check if we should wait
            if (maxRate != -1 && dt < minTime) {
                float wait = dt - minTime;
                if (wait > 0)
                    sleep((int)(wait * 1_000), /* kinda todo more precise */ 0);
            }

            // set delta time
            this.dt = dt;

            // set last time
            this.lastTime = currTime;

            // always update after yielding
            return true;
        }

        private void sleep(int ms, int ns) {
            try {
                Thread.sleep(ms, ns);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /////////////////////////////////////////////////

    // delta time accessible
    protected Float dt;

    /**
     * Return the delta time or null
     * if the timing does not measure
     * the delta time.
     * @return Delta time in seconds.
     */
    public Float getDeltaTime() {
        return dt;
    }

    /**
     * Should wait the time it needs to and then dictate
     * if it should update by returning a true value for
     * yes, update this frame or false for no, don't.
     * @return If it should update.
     */
    public abstract boolean yieldAndCheck();

}
