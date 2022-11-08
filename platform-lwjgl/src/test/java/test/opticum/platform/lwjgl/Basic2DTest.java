package test.opticum.platform.lwjgl;

import net.orbyfied.opticum.RenderDriver;
import net.orbyfied.opticum.platform.lwjgl.GLFWWindow;
import net.orbyfied.opticum.platform.lwjgl.LWJGLRenderDriver;
import net.orbyfied.opticum.platform.lwjgl.LWJGLRenderWorker;
import net.orbyfied.opticum.window.Window;
import net.orbyfied.opticum.window.WindowRenderContext;
import org.junit.jupiter.api.Test;

public class Basic2DTest {

    @Test
    void testBasic() {
        // get driver
        LWJGLRenderDriver driver = RenderDriver.get(LWJGLRenderDriver.class);
        // get worker
        LWJGLRenderWorker worker = driver.newWorker().build();

        // create context
        WindowRenderContext<?> context = worker.newContext(WindowRenderContext.class);
        context.window()
                .setTitle("Test")
                .setSize(500, 500)
                .setResizable(true)
                .create()
                .show(true);

        // start worker
        worker.startSync();
    }

}
