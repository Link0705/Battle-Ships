package com.battleships.gui.shaders;

import com.battleships.gui.entities.Camera;
import com.battleships.gui.entities.Light;
import com.battleships.gui.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

/**
 * Shader used by the {@link com.battleships.gui.renderingEngine.EntityRenderer}.
 * 
 * @author Tim Staudenmaier
 */
public class StaticShader extends ShaderProgram {

    /**
     * Path for the vertex shader file.
     */
    private static final String VERTEX_FILE = "/com/battleships/gui/shaders/vertexShader.glsl";
    /**
     * Path for the fragment shader file.
     */
    private static final String FRAGMENT_FILE = "/com/battleships/gui/shaders/fragmentShader.glsl";

    /**
     * Map containing the names of all uniform variables and their location.
     */
    private Map<String, Integer> uniformLocations;


    /**
     * Creates a new static shader by loading the two core shader files, need for this type of shader and binding them to
     * OpenGl so it can access these shader files.
     */
    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    /**
     * Binds the attributes to the shader.
     * If a model is rendered, it's vertex array object gets loaded. This vao contains
     * 3 attributes for a model, these need to be in the same order as the attributes this shader uses.
     * The attributes are then passed to the vertexShader as "in values", so it can process them.
     * The first attribute in the vao will be treated as the position for the entity, the second as textureCorrds, ...
     */
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
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
        String[] uniformNames = {"transformationMatrix", "projectionMatrix", "viewMatrix", "lightPosition", "lightColor", "shineDamper",
                "reflectivity","useFakeLighting", "skyColor", "numberOfRows", "offset", "plane", "mixPercentage", "mixColor"};
        uniformLocations = new HashMap<>();

        for(String s : uniformNames){
            uniformLocations.put(s, super.getUniformLocation(s));
        }
    }

    /**
     * Load additional color values for a entity, that are mixed with the given
     * percentage with the normal color of the entity. Can be used for graying of objects for example.
     * @param color The color the entities color should be mixed with.
     * @param percentage How much of this color should be used (0-1).
     */
    public void loadMixColor(Vector3f color, float percentage){
        super.loadVector(uniformLocations.get("mixColor"), color);
        super.loadFloat(uniformLocations.get("mixPercentage"), percentage);
    }

    /**
     * Load ClipPlane vector to shader.
     * All vertices of entities that are on the opposite site the normal vector of the clip plane is
     * facing, will not be rendered.
     * @param plane the vector for the clip plane
     */
    public void loadClipPlane(Vector4f plane){
        super.loadVector(uniformLocations.get("plane"), plane);
    }

    /**
     * Load the numberOfRows of a texture atlas into the uniform variable.
     * Needed so the shader can calculate the part of a texture atlas, it needs to but
     * on the model.
     * @param numberOfRows Number of rows the used texture atlas contains (1 if it's a normal texture).
     */
    public void loadNumberOfRows(float numberOfRows){
        super.loadFloat(uniformLocations.get("numberOfRows"), numberOfRows);
    }

    /**
     * Load the offset of the texture that should be used in a texture atlas into the uniform variable.
     * @param x column of the texture that should be used (0 if it's a normal texture).
     * @param y row of the texture (0 if it's a normal texture).
     */
    public void loadOffset(float x, float y){
        super.load2DVector(uniformLocations.get("offset"), new Vector2f(x, y));
    }

    /**
     * Loads the color that the shader should use for the sky into the uniform variable.
     * @param r red value of the color (0-1)
     * @param g green value of the color (0-1)
     * @param b blue value of the color (0-1)
     */
    public void loadSkyColor(float r, float g, float b){
        super.loadVector(uniformLocations.get("skyColor"), new Vector3f(r,g,b));
    }

    /**
     * Loads the values specifying the entity reflectivity to their uniform variables.
     * @param damper How much the reflected light spreads.
     * @param reflectivity How much light should get reflected.
     */
    public void loadShineVariables(float damper, float reflectivity){
        super.loadFloat(uniformLocations.get("shineDamper"), damper);
        super.loadFloat(uniformLocations.get("reflectivity"), reflectivity);
    }

    /**
     * Loads the position and color of the light into the uniform variables.
     * @param light The light the scene should use.
     */
    public void loadLight(Light light){
        super.loadVector(uniformLocations.get("lightPosition"), light.getPosition());
        super.loadVector(uniformLocations.get("lightColor"), light.getColor());
    }

    /**
     * Loads the transformation Matrix of the currently rendered entity to the uniform variable.
     * @param matrix transformation matrix of the entity, containing position, rotation and scale.
     */
    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(uniformLocations.get("transformationMatrix"), matrix);
    }

    /**
     * Loads the current view matrix into the uniform variable.
     * @param camera Camera that the scene is viewed through, containing viewMatrix
     *               that manipulates entities, so it looks like the scene is viewed from the camera.
     */
    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(uniformLocations.get("viewMatrix"), viewMatrix);
    }

    /**
     * Loads the current projection Matrix.
     * @param projection current projection Matrix, only changes if window is resized.
     */
    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(uniformLocations.get("projectionMatrix"), projection);
    }

    /**
     * Puts a value into the uniform variable that decides whether the object should use fake lighting
     * (mostly used for objects that are only one face thick so they don't appear dark).
     * @param useFake boolean whether to use fake lighting or not.
     */
    public void loadFakeLightingVariable(boolean useFake){
        super.loadBoolean(uniformLocations.get("useFakeLighting"), useFake);
    }

}
