package net.orbyfied.opticum.shader;

public class ShaderSourceException extends RuntimeException {

    public ShaderSourceException() {
    }

    public ShaderSourceException(String message) {
        super(message);
    }

    public ShaderSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShaderSourceException(Throwable cause) {
        super(cause);
    }

}
