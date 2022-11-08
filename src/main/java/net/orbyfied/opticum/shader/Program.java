package net.orbyfied.opticum.shader;

import net.orbyfied.opticum.RenderContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a shader program.
 */
public abstract class Program {

    protected Program(RenderContext context, Class<? extends Shader> shaderClass) {
        this.context     = context;
        this.shaderClass = shaderClass;
    }

    // the context this program is bound to
    protected final RenderContext context;

    // the shader class this program allows
    protected Class<? extends Shader> shaderClass;

    // if this shader has been linked
    protected boolean linked;

    // the shaders to link
    protected final Map<Shader.ShaderType, Shader> shaders = new HashMap<>();

    /**
     * Links the program together.
     * Should always set the linked field to true if successful.
     * @return This.
     * @throws ProgramLinkException If an error occurs when linking.
     */
    public abstract Program link();

    /**
     * Cleans the program and all resources.
     * @return This.
     */
    public abstract Program clean();

    /**
     * Will clean all shaders.
     * Useful after linking.
     * @return This.
     */
    public Program cleanShaders() {
        for (Shader shader : shaders.values())
            shader.clean();
        return this;
    }

    /**
     * Include a shader to be linked in the program.
     * This shader must be compiled before linking.
     * @param shader The shader.
     * @return This.
     */
    public Program withShader(Shader shader) {
        if (!shaderClass.isInstance(shader))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " only supports shaders of class " + shaderClass.getSimpleName());
        shaders.put(shader.type, shader);
        return this;
    }

    public boolean isLinked() {
        return linked;
    }

}
