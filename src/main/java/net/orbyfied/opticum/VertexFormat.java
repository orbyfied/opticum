package net.orbyfied.opticum;

import net.orbyfied.opticum.util.Vec2f;
import net.orbyfied.opticum.util.Vec3f;
import net.orbyfied.opticum.util.Vec4f;

import java.nio.ByteBuffer;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class VertexFormat {

    private static int addAndGetIndex(List<Field> list, Field field) {
        list.add(field);
        return list.size() - 1;
    }

    /*
        Fields
     */

    public record FieldLocal(Field field, int id) {
        public FieldLocation toLocation() { return new FieldLocation(field.type, id); }
    }

    public record FieldLocation(FieldType type, int id) { }

    public enum FieldType {

        BYTE(1, false),
        SHORT(2, false),
        INT(4, false),
        LONG(8, false),
        FLOAT(4, false),
        DOUBLE(8, false),

        VEC2(FLOAT.size * 2, true, 2, FLOAT),
        VEC3(FLOAT.size * 3, true, 3, FLOAT),
        VEC4(FLOAT.size * 4, true, 4, FLOAT);

        int size;
        boolean isReferenceValue;

        int len;
        FieldType element;

        FieldType(int size, boolean isReferenceValue, int len, FieldType element) {
            this.size = size;
            this.isReferenceValue = isReferenceValue;
            this.element = element;
            this.len = len;
        }

        FieldType(int size, boolean isReferenceValue) {
            this(size, isReferenceValue, 1, null);
            this.element = this;
        }

        public int getLength() {
            return len;
        }

        public FieldType getElementType() {
            return element;
        }

        public int getSize() {
            return size;
        }

        public boolean isReferenceValue() {
            return isReferenceValue;
        }

    }

    /**
     * A field in the format.
     */
    public static class Field {

        public Field(String name, FieldType type, boolean normalized) {
            this.name = name;
            this.type = type;
            this.normalized = normalized;
        }

        public Field(String name, FieldType type) {
            this(name, type, false);
        }

        // uid accumulator
        static int uidAcc = 0;

        {
            this.uid = uidAcc++;
        }

        // the unique id of this field
        final int uid;
        // the name of this field
        final String name;
        // the primitive type
        final FieldType type;
        // if it is normalized
        final boolean normalized;

        public int getUID() {
            return uid;
        }

        public String getName() {
            return name;
        }

        public FieldType getType() {
            return type;
        }

        public boolean isNormalized() {
            return normalized;
        }

        public int size() {
            return type.size;
        }

    }

    public abstract static class ReferenceField<V> extends Field {

        public ReferenceField(String name, FieldType type) {
            super(name, type);
        }

        public abstract void write(ByteBuffer buf, V value);

    }

    /* Standard Fields */
    public static final Vec2Field FIELD_POS2D = new Vec2Field("pos");
    public static final Vec3Field FIELD_POS3D = new Vec3Field("pos");
    public static final Vec4Field FIELD_COL   = new Vec4Field("col");
    public static final Vec2Field FIELD_UV    = new Vec2Field("uv");

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
        List<Field> fields = new ArrayList<>();

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
        this.fields = new FieldLocal[fields.length];
        this.size   = calculateSize(fields);

        // map all fields
        this.fieldIds = new int[Field.uidAcc];
        Arrays.fill(fieldIds, -1);
        int i = 0;
        for (Field field : fields) {
            FieldType type = field.type;
            int id = switch (type.isReferenceValue ? (byte)1 : (byte)0) {
                case 0 -> switch (type) {
                    case BYTE -> addAndGetIndex(fByte, field);
                    case SHORT -> addAndGetIndex(fShort, field);
                    case INT -> addAndGetIndex(fInt, field);
                    case LONG -> addAndGetIndex(fLong, field);
                    case FLOAT -> addAndGetIndex(fFloat, field);
                    case DOUBLE -> addAndGetIndex(fDouble, field);
                    default -> throw new IllegalArgumentException();
                };

                case 1 -> addAndGetIndex(fRef, field);
                default -> throw new IllegalStateException("if you get this error, show me its legit and ill send you $1");
            };

            fieldIds[field.uid] = id;
            this.fields[i] = new FieldLocal(field, id);

            i++;
        }
    }

    // the size of the whole struct
    protected final int size;
    // the fields
    protected final FieldLocal[] fields;

    // the fields mapped by type
    protected int[] fieldIds;
    protected List<Field> fRef = new ArrayList<>();
    protected List<Field> fByte = new ArrayList<>();
    protected List<Field> fShort = new ArrayList<>();
    protected List<Field> fInt = new ArrayList<>();
    protected List<Field> fLong = new ArrayList<>();
    protected List<Field> fFloat = new ArrayList<>();
    protected List<Field> fDouble = new ArrayList<>();

    public int getTypeLocalFieldID(Field field) {
        return fieldIds[field.uid];
    }

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
        RenderGraphics g = vertex.graphics;
        int l = fields.length;
        for (int i = 0; i < l; i++) {
            FieldLocal fl = fields[i];
            Field f = fl.field;
            int id = fl.id;
            FieldType type = f.type;
            if (type.isReferenceValue) {
                ((ReferenceField)f).write(buf, g.vfvRef[id]);
            } else {
                switch (f.type) {
                    case BYTE   -> { ((ByteField)f).write(buf, g.vfvByte[id]); }
                    case SHORT  -> { ((ShortField)f).write(buf, g.vfvShort[id]); }
                    case INT    -> { ((IntField)f).write(buf, g.vfvInt[id]); }
                    case LONG   -> { ((LongField)f).write(buf, g.vfvLong[id]); }
                    case FLOAT  -> { ((FloatField)f).write(buf, g.vfvFloat[id]); }
                    case DOUBLE -> { ((DoubleField)f).write(buf, g.vfvDouble[id]); }
                };
            }
        }
    }

    public FieldLocal[] getFields() {
        return fields;
    }

    /*
        Field Implementations
     */

    public static class ByteField extends Field {
        public ByteField(String name) { super(name, FieldType.BYTE); }
        public ByteField(String name, boolean n) { super(name, FieldType.BYTE, n); }
        public void writeDefault(ByteBuffer buffer) { buffer.put((byte)0); }
        public void write(ByteBuffer buffer, byte value) { buffer.put(value); }
    }
    
    public static class ShortField extends Field {
        public ShortField(String name) { super(name, FieldType.SHORT); }
        public ShortField(String name, boolean n) { super(name, FieldType.SHORT, n); }
        public void writeDefault(ByteBuffer buffer) { buffer.putShort((short)0); }
        public void write(ByteBuffer buffer, short value) { buffer.putShort(value); }
    }
    
    public static class IntField extends Field {
        public IntField(String name) { super(name, FieldType.INT); }
        public IntField(String name, boolean n) { super(name, FieldType.INT, n); }
        public void writeDefault(ByteBuffer buffer) { buffer.putInt(0); }
        public void write(ByteBuffer buffer, int value) { buffer.putInt(value); }
    }
    
    public static class LongField extends Field {
        public LongField(String name) { super(name, FieldType.LONG); }
        public LongField(String name, boolean n) { super(name, FieldType.LONG, n); }
        public void writeDefault(ByteBuffer buffer) { buffer.putLong(0); }
        public void write(ByteBuffer buffer, long value) { buffer.putLong(value); }
    }
    
    public static class FloatField extends Field {
        public FloatField(String name) { super(name, FieldType.FLOAT); }
        public FloatField(String name, boolean n) { super(name, FieldType.FLOAT, n); }
        public void writeDefault(ByteBuffer buffer) { buffer.putFloat(0); }
        public void write(ByteBuffer buffer, float value) { buffer.putFloat(value); }
    }

    public static class DoubleField extends Field {
        public DoubleField(String name) { super(name, FieldType.DOUBLE); }
        public DoubleField(String name, boolean n) { super(name, FieldType.DOUBLE, n); }
        public void writeDefault(ByteBuffer buffer) { buffer.putDouble(0); }
        public void write(ByteBuffer buffer, double value) { buffer.putDouble(value); }
    }

    public static class Vec2Field extends ReferenceField<Vec2f> {
        public Vec2Field(String name) {
            super(name, FieldType.VEC2);
        }

        @Override
        public void write(ByteBuffer buffer, Vec2f value) {
            if (value == null) {
                buffer.putFloat(0);
                buffer.putFloat(0);
                return;
            }
            buffer.putFloat(value.getX());
            buffer.putFloat(value.getY());
        }
    }

    public static class Vec3Field extends ReferenceField<Vec3f> {
        public Vec3Field(String name) {
            super(name, FieldType.VEC3);
        }

        @Override
        public void write(ByteBuffer buffer, Vec3f value) {
            if (value == null) {
                buffer.putFloat(0);
                buffer.putFloat(0);
                buffer.putFloat(0);
                return;
            }
            buffer.putFloat(value.getX());
            buffer.putFloat(value.getY());
            buffer.putFloat(value.getZ());
        }
    }

    public static class Vec4Field extends ReferenceField<Vec4f> {
        public Vec4Field(String name) {
            super(name, FieldType.VEC4);
        }

        @Override
        public void write(ByteBuffer buffer, Vec4f value) {
            if (value == null) {
                buffer.putFloat(0);
                buffer.putFloat(0);
                buffer.putFloat(0);
                buffer.putFloat(0);
                return;
            }
            buffer.putFloat(value.getX());
            buffer.putFloat(value.getY());
            buffer.putFloat(value.getZ());
            buffer.putFloat(value.getW());
        }
    }

}
