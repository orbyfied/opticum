package net.orbyfied.opticum;

import java.nio.ByteBuffer;

/**
 * The main object for actually drawing
 * graphics to the screen.
 */
@SuppressWarnings({ "rawtypes" })
public abstract class RenderGraphics {

    public enum Primitive {

        TRIANGLES,
        QUADS

    }

    ///////////////////////////////////////////////

    // the context
    protected final RenderContext context;
    // the driver
    protected final RenderDriver driver;

    protected RenderGraphics(RenderContext context) {
        this.context = context;
        this.driver  = context.driver();
    }

    public RenderContext context() {
        return context;
    }

    /*
        Graphics
     */

    /**
     * The current vertex format.
     */
    protected VertexFormat vertexFormat;

    /**
     * The intermediate vertex data buffer.
     */
    protected ByteBuffer vertexDataBuffer;

    /**
     * The current primitive to draw.
     */
    protected Primitive primitive;

    // check if there is a vertex format
    private void checkVertexFormat() {
        if (vertexFormat != null)
            throw new IllegalStateException("No vertex format has been assigned.");
    }

    /** Pushes a new configuration frame. */
    public abstract void push();
    /** Pops the top configuration frame. */
    public abstract void pop();

    /**
     * Binds a new vertex format to the graphics object.
     * @param format The format.
     */
    public void vertexFormat(VertexFormat format) {
        this.vertexFormat = format;
    }

    /**
     * Get the current bound vertex format.
     * @return The vertex format.
     */
    public VertexFormat vertexFormat() {
        return this.vertexFormat;
    }

    /**
     * Begin drawing vertices.
     * @param primitive The primitive to draw.
     * @param allocate The amount of vertices to allocate.
     *                 In bytes, the vertex size times the amount.
     */
    public void begin(Primitive primitive, int allocate) {
        checkVertexFormat();
        this.primitive        = primitive;
        this.vertexDataBuffer = ByteBuffer.allocateDirect(vertexFormat.size * allocate);
    }

    /**
     * Begin drawing vertices.
     * @param primitive The primitive to draw.
     */
    public void begin(Primitive primitive) {
        begin(primitive,
                /* allocate 2 vertices, minimum for a line */ 2);
    }

    /**
     * Finish drawing vertices.
     * It will draw out the primitives.
     */
    public void end() {
        // draw vertex data
        drawArrays();

        // clean up
        this.vertexDataBuffer = null;
        this.primitive = null;
    }

    /**
     * Draws the current vertex data.
     */
    protected abstract void drawArrays();

    /**
     * Start building a new vertex.
     * @return The vertex builder.
     */
    public VertexBuilder vertex() {
        return new VertexBuilder(this);
    }

    // appends a new vertex to the
    // vertex data buffer
    protected void appendVertex(VertexBuilder v) {
        // check if we have enough capacity for another vertex
        int currCap = vertexDataBuffer.capacity();
        if (currCap < vertexFormat.size) {
            // allocate new buffer
            int newCap = currCap * 2;
            ByteBuffer newBuf = ByteBuffer.allocateDirect(newCap);

            // copy data from old buffer to new
            newBuf.put(vertexDataBuffer);

            // set as new buffer
            vertexDataBuffer = newBuf;
        }

        // write vertex to buffer
        vertexFormat.write(v, vertexDataBuffer);
    }

}
