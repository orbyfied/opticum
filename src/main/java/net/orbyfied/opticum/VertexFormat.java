package net.orbyfied.opticum;

import net.orbyfied.opticum.util.Vec2f;

import java.nio.ByteBuffer;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class VertexFormat {

    /*
        Fields
     */

    /**
     * A field in the format.
     */
    public static abstract class Field<V> {

        public Field(String name, Class type) {
            this.name = name;
            this.type = type;
        }

        // uid accumulator
        private static int uidAcc = 0;

        {
            this.uid = uidAcc++;
        }

        // the unique id of this field
        final int uid;
        // the name of this field
        final String name;
        // the runtime type of this field
        final Class type;

        public int getUID() {
            return uid;
        }

        public String getName() {
            return name;
        }

        public Class getType() {
            return type;
        }

        /**
         * @return The size of this field in bytes.
         */
        public abstract int size();

        /**
         * Write the value to the buffer.
         * @param buffer The buffer.
         * @param value The value.
         */
        public abstract void write(ByteBuffer buffer, V value);

    }

    /* Standard Fields */
    public static final Vec2Field FIELD_POS2D = new Vec2Field("pos");

    public static FloatField floatField(String name) { return new FloatField(name); }
    public static Vec2Field  vec2Field (String name) { return new Vec2Field(name);  }

    /**
     * Calculates the size of all the fields combined.
     * @param fields The fields.
     * @return The size.
     */
    public static int calculateSize(Field[] fields) {
        int size = 0;
        for (Field field : fields) {
            size += field.size();
        }
        return size;
    }

    /**
     * Builder class.
     */
    public static class Builder {

        // the fields to include in the build
        List<Field> fields;

        public Builder with(Field field) {
            fields.add(field);
            return this;
        }

        public Builder withPos2D() {
            return with(FIELD_POS2D);
        }

        /**
         * Build the vertex format.
         * @return The format.
         */
        public VertexFormat build() {
            return new VertexFormat(fields.toArray(new Field[0]));
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    //////////////////////////////////////////

    VertexFormat(Field[] fields) {
        this.fields = fields;
        this.size   = calculateSize(fields);
    }

    // the size of the whole struct
    protected final int size;
    // the fields
    protected final Field[] fields;

    /**
     * Allocates a buffer with the correct
     * size to fit all the data in.
     * @return The buffer.
     */
    public ByteBuffer allocateBuffer() {
        return ByteBuffer.allocateDirect(size);
    }

    public ByteBuffer serialize(VertexBuilder vertex) {
        ByteBuffer buf = allocateBuffer();
        write(vertex, buf);
        return buf;
    }

    public void write(VertexBuilder vertex, ByteBuffer buf) {
        int l = fields.length;
        for (int i = 0; i < l; i++) {
            Field f = fields[i];
            f.write(buf, vertex.fieldValues.get(f.uid));
        }
    }

    /*
        Field Implementations
     */

    public static class FloatField extends Field<Float> {
        public FloatField(String name) {
            super(name, Float.class);
        }

        @Override
        public int size() {
            return 4;
        }

        @Override
        public void write(ByteBuffer buffer, Float value) {
            buffer.putFloat(value);
        }
    }

    public static class Vec2Field extends Field<Vec2f> {
        public Vec2Field(String name) {
            super(name, Float.class);
        }

        @Override
        public int size() {
            return 8;
        }

        @Override
        public void write(ByteBuffer buffer, Vec2f value) {
            buffer.putFloat(value.getX());
            buffer.putFloat(value.getX());
        }
    }

}
