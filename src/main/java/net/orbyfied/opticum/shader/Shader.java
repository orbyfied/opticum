package net.orbyfied.opticum.shader;

import net.orbyfied.opticum.RenderContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class Shader {

    public enum ShaderType {

        VERTEX,
        FRAGMENT

    }

    ///////////////////////////////////////////

    public Shader(RenderContext context, ShaderType type) {
        this.context = context;
        this.type    = type;
    }

    // the context this shader is bound to
    protected final RenderContext context;

    // if the shader has been compiled successfully
    protected boolean compiled;

    // the type of shader
    protected final ShaderType type;

    public ShaderType type() {
        return type;
    }

    /**
     * Compiles the uploaded source into this shader.
     * Should always set the compiled field to true if successful.
     * @return This.
     * @throws ShaderCompileException If there is an error in compilation.
     */
    public abstract Shader compile();

    /**
     * Uploads the provided source to the shader.
     * @param src The source.
     * @return This.
     * @throws ShaderSourceException If there is an error uploading the source.
     */
    public abstract Shader source(String src);

    /**
     * Clean up this shader and its resources.
     * @return This.
     */
    public abstract Shader clean();

    public boolean isCompiled() {
        return compiled;
    }

    /* QOL */

    public Shader compileSource(String src) {
        source(src);
        compile();
        return this;
    }

    public Shader compileSource(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            compileSource(fis);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    public Shader compileSource(InputStream file) {
        try {
            String str = new String(file.readAllBytes(), StandardCharsets.UTF_8);
            return compileSource(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

}
