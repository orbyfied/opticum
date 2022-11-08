package net.orbyfied.opticum.platform.lwjgl;

import net.orbyfied.opticum.RenderContext;
import net.orbyfied.opticum.shader.Program;
import net.orbyfied.opticum.shader.ProgramLinkException;
import net.orbyfied.opticum.shader.Shader;
import net.orbyfied.opticum.shader.ShaderCompileException;
import org.lwjgl.opengl.GL30;

public class GLProgram extends Program {

    protected GLProgram(RenderContext context) {
        super(context, GLShader.class);

        // create program in GL
        ((GLContextLike)context).switchContext();
        this.handle = GL30.glCreateProgram();
    }

    // the handle to the program
    protected int handle;

    @Override
    public Program link() {
        // switch context
        ((GLContextLike)context).switchContext();

        // check if all shaders are compiled
        for (Shader shader : shaders.values())
            if (!shader.isCompiled())
                throw new ProgramLinkException(-1, "Shader " + shader.type().name() + " is not compiled");

        try {
            // attach all shaders
            for (Shader shader : shaders.values()) {
                GLShader glShader = (GLShader)shader;
                GL30.glAttachShader(handle, glShader.handle);
            }

            // link program
            GL30.glLinkProgram(handle);

            // handle errors
            int status = GL30.glGetProgrami(handle, GL30.GL_LINK_STATUS);
            if (status == GL30.GL_FALSE) {
                // get error log
                int logSize = GL30.glGetProgrami(handle, GL30.GL_INFO_LOG_LENGTH);
                String log  = GL30.glGetProgramInfoLog(handle, logSize);

                // clean up program
                clean();

                // throw exception
                throw new ProgramLinkException(1, "OpenGL link error", "\n" + log);
            }
        } catch (Exception e) {
            if (!(e instanceof ProgramLinkException))
                throw new ProgramLinkException(-2, "Java exception", e);
            else throw e;
        }

        return this;
    }

    @Override
    public Program clean() {
        GL30.glDeleteProgram(handle);
        return this;
    }

}
