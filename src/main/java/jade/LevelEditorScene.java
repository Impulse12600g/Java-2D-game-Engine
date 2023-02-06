package jade;

import org.lwjgl.BufferUtils;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene{
    // move fragment and vertex shader info from shaders folder
    private String vertexShaderSrc = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main(){\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main(){\n" +
            "    color = fColor;\n" +
            "}";
    //since were passing data from the cpu to gpu, we need identifiers
    //gl has some to help ID the processes
    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
        //position                 //color
         0.5f, -0.5f, 0.0f,          1.0f, 0.0f, 0.0f, 1.0f, //bottom right   0
        -0.5f , 0.5f, 0.0f,         0.0f, 1.0f, 0.0f, 1.0f, //top left        1
         0.5f, 0.5f, 0.0f,          0.0f, 0.0f, 1.0f, 1.0f, // top right      2
         -0.5f, -0.5f, 0.0f,        1.0f, 1.0f, 0.0f, 1.0f, // bottom left    3
    };
    //IMPORTANT; must be in counter clockwise order to specify triangles
    private int[] elementArray = {
            /*
            x      x


            x      x //making triagles out of these goign counter clockwise
             */
        2, 1, 0, //top right triangle
        0, 1, 3,  // bottom left triangle

    };

    private int vaoID, vboID, eboID; // vertex array obj, v buffer obj, element buffer obj

    public LevelEditorScene(){


    }

    //set the private ids
    @Override
    public void init(){
        //compile and link shaders
        //fist load and compile vertex
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //pass shader to gpu
        glShaderSource(vertexID, vertexShaderSrc);
        glCompileShader(vertexID);

        //check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: default shader.glsl failed vertex");
            System.out.println(glGetShaderInfoLog(vertexID, len));//needs both to get log
            assert false: "";//break out of the program
        }
        //fist load and compile fragment
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //pass shader to gpu
        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);

        //check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: default shader.glsl failed fragment");
            System.out.println(glGetShaderInfoLog(fragmentID, len));//needs both to get log
            assert false: "";//break out of the program
        }

        // link shaders and check for errors
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        //check for linking errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE){
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: default shader.glsl failed: linking shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));//needs both to get log
            assert false: "";//break out of the program
        }

        /*
        GENERATE VAO, VBO, AND EBO buffer objects and send to gpu
         */
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //create a float buffer of verticies
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();// how its formatted for open gl

        //create vbo upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);// send that buffer to the id
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);// drawing it statically

        // create the indicies
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // add vertex attribute pointers aka tell gpu what are colors or positios
        int positionSize = 3; // three postions xyz
        int colorSize = 4; // rgba
        int floatSizeBytes = 4; // 4 bytes for one float (each position is a float)
        //how big the whole vertex is in bytes
        int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);// zero is the pos of vertex in file default.glsl

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, (positionSize * floatSizeBytes));// pointer needs to be in bytes

        glEnableVertexAttribArray(1);

    }

    @Override
    public void update(float dt) {
        // bind shader program
        glUseProgram(shaderProgram);
        // bind the vao that we were using
        glBindVertexArray(vaoID);

        //enable the vertex attb pointer
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        glUseProgram(0);


    }


}
