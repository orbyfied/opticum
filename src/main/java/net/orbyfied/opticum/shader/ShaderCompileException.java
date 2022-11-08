package net.orbyfied.opticum.shader;

/**
 * Represents an error during shader compilation.
 * This can be a Java exception occurring (in which case
 * the error code should always be -2), a check failing
 * (in which case the error code should always be -1) or
 * an error withing the actual platform implementation, like
 * OpenGL, in which the code should be the actual code thrown
 * by that implementation.
 */
public class ShaderCompileException extends RuntimeException {

    public static final int CODE_JAVA_ERROR = -2;
    public static final int CODE_CHECK_FAIL = -1;

    /////////////////////////////////////////////

    public ShaderCompileException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ShaderCompileException(int code, String message, String desc) {
        super(message);
        this.code = code;
        this.description = desc;
    }

    public ShaderCompileException(int code, String message, Throwable cause, String desc) {
        super(message, cause);
        this.code = code;
        this.description = desc;
    }

    public ShaderCompileException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    int code;
    String description;

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ((code != -1 && code != 2) ? ("\n\tErrorCode(" + code + "): " + description) : "");
    }

}
