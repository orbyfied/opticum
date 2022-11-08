package net.orbyfied.opticum;

public abstract class RenderLayer {

    public interface Simple {
        void update(RenderContext context, RenderGraphics graphics, RenderInput input, float dt);
    }

    public static RenderLayer simple(final String name, final Simple simple) {
        return new RenderLayer(name) {
            @Override
            protected void update(RenderContext context, float dt) {
                simple.update(context, context.graphics, context.input, dt);
            }
        };
    }

    public static RenderLayer simple(final String name, final int pos, final Simple simple) {
        return new RenderLayer(name, pos) {
            @Override
            protected void update(RenderContext context, float dt) {
                simple.update(context, context.graphics, context.input, dt);
            }
        };
    }

    ///////////////////////////////////////////////////////

    public RenderLayer(String name) {
        this(name, -1);
    }

    public RenderLayer(String name, int pos) {
        this.name = name;
        this.pos  = pos;
    }

    // the name and position
    protected String name;
    protected int    pos;

    // if the renderer is enabled
    protected boolean active = true;

    /**
     * Called every frame it is active.
     * @param context The context.
     * @param dt The delta time.
     */
    protected abstract void update(RenderContext context, float dt);

    public RenderLayer active(boolean active) {
        this.active = active;
        return this;
    }

    public boolean active() {
        return active;
    }

    public int position() {
        return pos;
    }

    public String name() {
        return name;
    }

}
