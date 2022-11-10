package net.orbyfied.opticum;

import net.orbyfied.opticum.shader.Program;
import net.orbyfied.opticum.util.Vec2f;

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

    /* Vertex Builder */

    // vertex field values
    protected byte[]   vfvByte;
    protected short[]  vfvShort;
    protected int[]    vfvInt;
    protected long[]   vfvLong;
    protected float[]  vfvFloat;
    protected double[] vfvDouble;
    protected Object[] vfvRef;

    /* Graphics */

    public static final int INIT_VERTEX_BUFFER_SIZE = 2048;

    // the context
    protected final RenderContext context;
    // the driver
    protected final RenderDriver driver;

    protected RenderGraphics(RenderContext context) {
        this.context = context;
        if (context == null)
            driver = null;
        else
            this.driver  = context.driver();
    }

    public RenderContext context() {
        return context;
    }

    /*
        Implementation
     */

    protected abstract ByteBuffer allocateByteBuffer(int cap);

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
    protected ByteBuffer vertexDataBuffer = allocateByteBuffer(INIT_VERTEX_BUFFER_SIZE);

    /**
     * The current primitive to draw.
     */
    protected Primitive primitive;

    /**
     * The amount of vertices that have been appended.
     */
    protected int vertices;

    // check if there is a vertex format
    private void checkVertexFormat() {
        if (vertexFormat == null)
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
        VertexFormat oldFormat = this.vertexFormat;
        this.vertexFormat = format;

        // check if we have just set a new format
        if (oldFormat != format) {
            // reallocate vertex field value arrays
            vfvByte = new byte[vertexFormat.fByte.size()];
            vfvShort = new short[vertexFormat.fShort.size()];
            vfvInt = new int[vertexFormat.fInt.size()];
            vfvLong = new long[vertexFormat.fLong.size()];
            vfvFloat = new float[vertexFormat.fFloat.size()];
            vfvDouble = new double[vertexFormat.fDouble.size()];
            vfvRef = new Object[vertexFormat.fRef.size()];

            // switch format
            switchVertexFormat(oldFormat, vertexFormat);
        }
    }

    /**
     * Get the current bound vertex format.
     * @return The vertex format.
     */
    public VertexFormat vertexFormat() {
        return this.vertexFormat;
    }

    /**
     * Called when switching the vertex format to a new one.
     * @param oldFormat The old format.
     * @param newFormat The new format (already set).
     */
    protected abstract void switchVertexFormat(VertexFormat oldFormat, VertexFormat newFormat);

    /**
     * Binds a shader program to be used.
     * @param program The shader program.
     */
    public abstract void useProgram(Program program);

    /**
     * Begin drawing vertices.
     * @param primitive The primitive to draw.
     * @param allocate The amount of vertices to allocate.
     *                 In bytes, the vertex size times the amount.
     */
    public void begin(Primitive primitive, int allocate) {
        checkVertexFormat();
        this.primitive = primitive;
        if (allocate > vertexDataBuffer.capacity())
            this.vertexDataBuffer = allocateByteBuffer(vertexFormat.vertexSize * allocate);
        this.vertexDataBuffer.limit(vertexDataBuffer.capacity());
        this.vertexDataBuffer.position(0);
    }

    /**
     * Begin drawing vertices.
     * @param primitive The primitive to draw.
     */
    public void begin(Primitive primitive) {
        begin(primitive,
                /* pre allocate 8 vertices */ 8);
    }

    /**
     * Finish drawing vertices.
     * It will draw out the primitives.
     */
    public void end() {
        // draw vertex data
        int len = vertexDataBuffer.position() + 1;
        drawBuffer(vertexDataBuffer, len, vertices);

        // clean up
        // we dont discard the buffer because it can be reused
        this.vertexDataBuffer.position(0);
        this.primitive = null;
        this.vertices  = 0;
    }

    /**
     * Draws the current vertex data.
     */
    protected abstract void drawBuffer(ByteBuffer buffer, int bytes, int verts);

    /**
     * Start building a new vertex.
     * @return The vertex builder.
     */
    public VertexBuilder vertex() {
        return new VertexBuilder(this);
    }

    /**
     * Start building a new vertex at the specified position.
     * @param x The X position.
     * @param y The Y position.
     * @return The vertex builder.
     */
    public VertexBuilder vertex2d(float x, float y) {
        return new VertexBuilder(this).pos2d(x, y);
    }

    /**
     * Start building a new vertex at the specified position.
     * @param pos The position.
     * @return The vertex builder.
     */
    public VertexBuilder vertex2d(Vec2f pos) {
        return new VertexBuilder(this).pos2d(pos);
    }

    /**
     * Appends a new vertex to the data buffer.
     * @param v The vertex.
     */
    public void append(VertexBuilder v) {
        // check if we have enough capacity for another vertex
        int currCap = vertexDataBuffer.capacity();
        if ((currCap - vertexDataBuffer.position()) < vertexFormat.vertexSize) {
            // allocate new buffer
            int newCap = (int)((currCap + vertexFormat.vertexSize) * 1.5);
            ByteBuffer newBuf = allocateByteBuffer(newCap);

            // copy data from old buffer to new
            newBuf.put(vertexDataBuffer);

            // set as new buffer
            vertexDataBuffer = newBuf;
        }

        // write vertex to buffer
        vertexFormat.write(v, vertexDataBuffer);

        // increment vertices
        vertices++;
    }

    // create a new debug instance
    // used to access otherwise inaccessible data
    public Debug debug() {
        return new Debug();
    }

    /////////////////////////////////////////////////////

    public class Debug {
        public ByteBuffer getVertexDataBuffer() {
            return vertexDataBuffer;
        }
    }

}
