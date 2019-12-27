package com.battleships.gui.guis;

import com.battleships.gui.shaders.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * Shader for Gui elements.
 *
 * @author Tim Staudenmaier
 */
public class GuiShader extends ShaderProgram {

    /**
     * Path for the vertex shader file.
     */
    private static final String VERTEX_FILE = "/com/battleships/gui/guis/guiVertexShader.glsl";
    /**
     * Path for the fragment shader file.
     */
    private static final String FRAGMENT_FILE = "/com/battleships/gui/guis/guiFragmentShader.glsl";

    /**
     * Location value for the uniform variable transformationMatrix, that holds the transformationMatrix of the {@link GuiTexture}.
     */
    private int location_transformationMatrix;
    /**
     * Location value for the uniform variable numberOfRows, that holds the amount of rows in the textureAtlas of the {@link GuiTexture}.
     */
    private int location_numberOfRows;
    /**
     * Location value for the uniform variable offset, that holds the offset of the texture for this {@link GuiTexture} in the textureAtlas.
     */
    private int location_offset;

    /**
     * Initialize the shader for GUI's.
     * This shader uses the two specified glsl files.
     */
    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    /**
     * Load the transformation Matrix of a gui element to the shader, to correctly render it.
     * @param matrix
     */
    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
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
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");
    }

    /**
     * Load the numberOfRows of a texture atlas into the uniform variable.
     * Needed so the shader can calculate the part of a texture atlas, it needs to but
     * on the model.
     * @param numberOfRows Number of rows the used texture atlas contains (1 if it's a normal texture).
     */
    public void loadNumberOfRows(int numberOfRows){
        super.loadInt(location_numberOfRows, numberOfRows);
    }

    /**
     * Load the offset of the texture that should be used in a texture atlas into the uniform variable.
     * @param x column of the texture that should be used (0 if it's a normal texture).
     * @param y row of the texture (0 if it's a normal texture).
     */
    public void loadOffset(float x, float y){
        super.load2DVector(location_offset, new Vector2f(x, y));
    }

    /**
     * Binds the attributes to the shader.
     * If a gui is rendered, it's vertex array object gets loaded. This vao contains
     * 1 attribute the position. This attribute is then passed to the vertexShader as "in value", so it can process it.
     */
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
