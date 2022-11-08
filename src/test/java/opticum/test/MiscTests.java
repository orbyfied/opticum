package opticum.test;

import net.orbyfied.opticum.RenderGraphics;
import net.orbyfied.opticum.VertexFormat;
import net.orbyfied.opticum.util.Vec4f;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

public class MiscTests {

    @Test
    void testVertexBuilder() {
        final RenderGraphics g = new RenderGraphics(null) {
            @Override public void push() { }
            @Override public void pop() { }
            @Override protected void switchVertexFormat(VertexFormat oldFormat, VertexFormat newFormat) { }
            @Override protected void drawBuffer(ByteBuffer buf, int len, int i) { }
        };

        g.vertexFormat(VertexFormat.builder()
                .with(VertexFormat.FIELD_POS3D)
                .with(VertexFormat.FIELD_COL).build());
        g.begin(RenderGraphics.Primitive.QUADS, 4);
        g
                .vertex()
                .pos3d(.4f, .1f, .5f)
                .setReference(VertexFormat.FIELD_COL, new Vec4f(1.0f, 0.5f, 0.5f, 1.0f))
                .end();
        ByteBuffer b = g.debug().getVertexDataBuffer();
        b.position(0);
        for (int i = 0; i < 7; i++) {
            System.out.print(b.getFloat() + ", ");
        }


    }

}
