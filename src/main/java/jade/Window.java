package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width, height;
    private String title;
    private long glfwWindow;
    public float r,g,b,a;

    private static Window window = null;
    private boolean fadeToBlack = false;
    private static Scene currentScene;


    private Window(){
        this.height = 1080;
        this.width = 1920;
        this.title = "Mario";
        r = 1;
        b = 1;
        g = 1;
        a = 1;
    }

    public static void changeScene(int newScene){
        switch (newScene){
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();  //do it here when ready
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false : "Unknown scene ' " + newScene + "'";
        }
    }

    public static Window get(){
        if (Window.window == null){
            Window.window = new Window();
        }
        return Window.window;
    }
    // run func
    public void run(){
        System.out.println("Hello LWJGL" + Version.getVersion() + "!");
        init();
        loop();

        //ADD THIS IN TO FREE UP MEMORY
        glfwFreeCallbacks(glfwWindow);// free any calbalcks
        glfwDestroyWindow(glfwWindow);
        // terminate window and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    public void init(){
        // set up error callback
        GLFWErrorCallback.createPrint(System.err).set(); // directs all errors to print

        // init GLFW
        if(!glfwInit()){
            throw new IllegalArgumentException("unable to init GLFW");
        }

        //config glfw
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // create window
        //glfw will use above to create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        // returns long memory address where the window is in out memory space
        if (glfwWindow == NULL){
            throw new IllegalStateException("failed to create window");
        }

        // register callbacks using lambda expressions
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        //make opengl context current
        glfwMakeContextCurrent(glfwWindow);
        //enable v-sync
        glfwSwapInterval(1);

        //make window visible
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();
        //critical to use the bindings

        Window.changeScene(0);

    }
    public void loop(){
        float beginTime = Time.getTime();
        float endTime = Time.getTime();
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)){
            //poll events
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

//            if(fadeToBlack){//decrease each every frame till black
//                r = Math.max(r - 0.01f, 0);
//                g = Math.max(g - 0.01f, 0);
//                b = Math.max(b - 0.01f, 0);
//            }
//
//            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
//                fadeToBlack = true;
//
//            }
            if (dt >= 0) {
                currentScene.update(dt);
            }
            glfwSwapBuffers(glfwWindow);

            //time
            endTime = Time.getTime();
            dt = endTime - beginTime;// dt wil be the next time
            beginTime = endTime;// begins next beginTime for loop

        }

    }

}
