package com.battleships.gui.skybox;

import com.battleships.gui.entities.Camera;
import com.battleships.gui.shaders.ShaderProgram;
import com.battleships.gui.toolbox.Maths;
import org.joml.Matrix4f;

/**
 * Shader for the {@link SkyboxRenderer}.
 *
 * @author Tim Staudenmaier
 */
public class SkyboxShader extends ShaderProgram {

    /**
     * Path for the vertex shader file.
     */
    private static final String VERTEX_FILE = "/com/battleships/gui/skybox/skyboxVertexShader.glsl";
    /**
     * Path for the fragment shader file.
     */
    private static final String FRAGMENT_FILE = "/com/battleships/gui/skybox/skyboxFragmentShader.glsl";

    /**
     * Location value for the uniform variable projectionMatrix, that holds the current projectionMatrix of the window}.
     */
    private int location_projectionMatrix;
    /**
     * Location value for the uniform variable viewMatrix, that holds the current viewMatrix of the camera}.
     */
    private int location_viewMatrix;

    /**
     * Creates a new skybox shader by loading the two core shader files, need for this type of shader and binding them to
     * OpenGl so it can access these shader files.
     */
    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    /**
     * Loads the current projection Matrix.
     *
     * @param matrix current projection Matrix, only changes if window is resized.
     */
    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    /**
     * Resets the position of the viewMatrix and then loads the edited view matrix into the uniform variable.
     * Position is reset to make skybox move with camera.
     *
     * @param camera Camera that the scene is viewed through, containing viewMatrix
     *               that manipulates entities, so it looks like the scene is viewed from the camera.
     */
    public void loadViewMatrix(Camera camera) {
        Matrix4f matrix = Maths.createViewMatrix(camera);
        //reset translation of view matrix, so skybox moves with camera
        matrix._m30(0);
        matrix._m31(0);
        matrix._m32(0);
        super.loadMatrix(location_viewMatrix, matrix);
    }

    /**
     * Uniform variables are variables, that are accessible both in the java code and in the
     * shader code. Their values can be set in java code and then be read when the shader renders an object.
     * Their values need to be saved to a location index, so the shader knows where to find the value for the variable.
     * These location indices are generated by OpenGL. This method generates all the location-values so all uniform
     * variables can be saved and accessed by the shader.
     */
    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    /**
     * Binds the attributes to the shader.
     * If a model is rendered, it's vertex array object gets loaded. This vao contains
     * 1 attribute for a skybox, the position. This attribute is then passed to the vertexShader as "in value", so it can be processed.
     */
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
