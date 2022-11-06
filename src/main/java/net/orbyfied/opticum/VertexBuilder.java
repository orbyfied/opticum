package net.orbyfied.opticum;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class VertexBuilder {

    VertexBuilder(RenderGraphics graphics) {
        this.graphics = graphics;
    }

    // the field values
    protected final Int2ObjectOpenHashMap<Object> fieldValues = new Int2ObjectOpenHashMap<>();

    // the graphics object
    protected final RenderGraphics graphics;

    /**
     * @return The render graphics.
     */
    public RenderGraphics graphics() { return graphics; }

}
