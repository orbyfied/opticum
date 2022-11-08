package net.orbyfied.opticum.platform.lwjgl;

import net.orbyfied.opticum.RenderContext;
import net.orbyfied.opticum.shader.ProgramLinkException;
import net.orbyfied.opticum.shader.Shader;
import net.orbyfied.opticum.shader.ShaderCompileException;
import net.orbyfied.opticum.shader.ShaderSourceException;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;

import java.util.Objects;

public class GLShader extends Shader {

    public static int getGL(ShaderType type) {
        return switch (type) {
            case VERTEX   -> GL30.GL_VERTEX_SHADER;
            case FRAGMENT -> GL30.GL_FRAGMENT_SHADER;
        };
    }

    ////////////////////////////////////

    protected GLShader(RenderContext context, ShaderType type) {
        super(context, type);

        // create shader handle
        this.glType = getGL(type);
        this.handle = GL30.glCreateShader(glType);
    }

    // shader values
    int glType;
    int handle;

    @Override
    public Shader compile() {
        // switch context
        ((GLContextLike)context).switchContext();

        try {
            // try and compile
            GL30.glCompileShader(handle);

            // handle errors
            int status = GL30.glGetShaderi(handle, GL30.GL_COMPILE_STATUS);
            if (status == GL30.GL_FALSE) {
                // get error log
                int logSize = GL30.glGetShaderi(handle, GL30.GL_INFO_LOG_LENGTH);
                String log  = GL30.glGetShaderInfoLog(handle, logSize);

                // clean up shader
                clean();

                // throw exception
                throw new ShaderCompileException(1, "OpenGL compile error", "\n" + log);
            }

            // successful
            this.compiled = true;
        } catch (Exception e) {
            if (!(e instanceof ShaderCompileException)) {
                clean();
                throw new ShaderCompileException(-2, "Java exception", e);
            } else throw e;
        }

        return this;
    }

    @Override
    public Shader source(String src) {
        // check if source is null
        Objects.requireNonNull(src, "source cannot be null");

        // switch context
        ((GLContextLike)context).switchContext();

        try {
            // upload source
            GL30.glShaderSource(handle, src);
        } catch (Exception e) {
            if (!(e instanceof ShaderSourceException))
                throw new ShaderSourceException(e);
            else throw e;
        }

        return this;
    }

    @Override
    public Shader clean() {
        GL30.glDeleteShader(handle);
        return this;
    }

}
