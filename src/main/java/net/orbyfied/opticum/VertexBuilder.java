package net.orbyfied.opticum;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.orbyfied.opticum.util.Vec2f;
import net.orbyfied.opticum.util.Vec3f;
import net.orbyfied.opticum.util.Vec4f;

import java.util.Arrays;

@SuppressWarnings({ "rawtypes"})
public class VertexBuilder {

    ////////////////////////////////////////////////

    VertexBuilder(RenderGraphics graphics) {
        this.graphics = graphics;

        // allocate arrays
        this.format = graphics.vertexFormat;
        Arrays.fill(graphics.vfvByte, (byte)0);
    }

    protected final VertexFormat format;
    // the graphics object
    protected final RenderGraphics graphics;

    /**
     * @return The render graphics.
     */
    public RenderGraphics graphics() { return graphics; }

    public void end() {
        // write to graphics
        graphics.append(this);
    }

    /*
        Raw Type-based value setting.
     */

    public VertexBuilder setReference(VertexFormat.Field field, Object val) {
        return setReference(graphics.vertexFormat.getTypeLocalFieldID(field), val);
    }

    public VertexBuilder setReference(int field, Object val) {
        if (field == -1) return this;
        graphics.vfvRef[field] = val;
        return this;
    }

    public VertexBuilder setByte(VertexFormat.Field field, byte val) {
        return setByte(format.getTypeLocalFieldID(field), val);
    }

    public VertexBuilder setByte(int field, byte val) {
        if (field == -1) return this;
        graphics.vfvByte[field] = val;
        return this;
    }

    public VertexBuilder setShort(VertexFormat.Field field, short val) {
        return setShort(format.getTypeLocalFieldID(field), val);
    }

    public VertexBuilder setShort(int field, short val) {
        if (field == -1) return this;
        graphics.vfvShort[field] = val;
        return this;
    }

    public VertexBuilder setInt(VertexFormat.Field field, int val) {
        return setInt(format.getTypeLocalFieldID(field), val);
    }

    public VertexBuilder setInt(int field, int val) {
        if (field == -1) return this;
        graphics.vfvInt[field] = val;
        return this;
    }

    public VertexBuilder setLong(VertexFormat.Field field, long val) {
        return setLong(format.getTypeLocalFieldID(field), val);
    }

    public VertexBuilder setLong(int field, long val) {
        if (field == -1) return this;
        graphics.vfvLong[field] = val;
        return this;
    }

    public VertexBuilder setFloat(VertexFormat.Field field, float val) {
        return setFloat(format.getTypeLocalFieldID(field), val);
    }

    public VertexBuilder setFloat(int field, float val) {
        if (field == -1) return this;
        graphics.vfvFloat[field] = val;
        return this;
    }

    public VertexBuilder setDouble(VertexFormat.Field field, double val) {
        return setDouble(format.getTypeLocalFieldID(field), val);
    }

    public VertexBuilder setDouble(int field, double val) {
        if (field == -1) return this;
        graphics.vfvDouble[field] = val;
        return this;
    }

    /*
        Standard Fields
     */

    public VertexBuilder pos2d(float x, float y) {
        return setReference(VertexFormat.FIELD_POS2D, new Vec2f(x, y));
    }

    public VertexBuilder pos2d(Vec2f vec) {
        return setReference(VertexFormat.FIELD_POS2D, vec);
    }

    public VertexBuilder pos3d(float x, float y, float z) {
        return setReference(VertexFormat.FIELD_POS3D, new Vec3f(x, y, z));
    }

    public VertexBuilder pos3d(Vec3f vec) {
        return setReference(VertexFormat.FIELD_COL, vec);
    }

    public VertexBuilder col(float r, float g, float b, float a) {
        return setReference(VertexFormat.FIELD_COL, new Vec4f(r, g, b, a));
    }

    public VertexBuilder col(Vec4f vec) {
        return setReference(VertexFormat.FIELD_COL, vec);
    }

    public VertexBuilder uv(Vec2f vec) {
        return setReference(VertexFormat.FIELD_UV, vec);
    }

    public VertexBuilder uv(float x, float y) {
        return setReference(VertexFormat.FIELD_UV, new Vec2f(x, y));
    }

}
