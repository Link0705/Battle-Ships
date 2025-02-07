package com.battleships.gui.shaders;

import com.battleships.gui.entities.Camera;
import com.battleships.gui.entities.Light;
import com.battleships.gui.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

/**
 * Shader used by the {@link com.battleships.gui.renderingEngine.TerrainRenderer}.
 *
 * @author Tim Staudenmaier
 */
public class TerrainShader extends ShaderProgram {

    /**
     * Path for the vertex shader file.
     */
    private static final String VERTEX_FILE = "/com/battleships/gui/shaders/TerrainVertexShader.glsl";
    /**
     * Path for the fragment shader file.
     */
    private static final String FRAGMENT_FILE = "/com/battleships/gui/shaders/TerrainFragmentShader.glsl";

    /**
     * Map containing the names of all uniform variables and their location.
     */
    private Map<String, Integer> uniformLocations;

    /**
     * Creates a new terrain shader by loading the two core shader files, need for this type of shader and binding them to
     * OpenGl so it can access these shader files.
     */
    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    /**
     * Binds the attributes to the shader.
     * If a model is rendered, it's vertex array object gets loaded. This vao contains
     * 3 attributes for a terrain, these need to be in the same order as the attributes this shader uses.
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
                "reflectivity", "skyColor", "pathTexture", "gravelTexture", "grassTexture", "wetSandTexture", "sandTexture", "blendMap", "plane"};
        uniformLocations = new HashMap<>();
        for (String s : uniformNames) {
            uniformLocations.put(s, super.getUniformLocation(s));
        }
    }

    /**
     * Connects all the textures of the terrain to the sampler2D uniform variables in the shader using ID's from 0-5.
     */
    public void connectTextureUnits() {
        super.loadInt(uniformLocations.get("pathTexture"), 0);
        super.loadInt(uniformLocations.get("gravelTexture"), 1);
        super.loadInt(uniformLocations.get("grassTexture"), 2);
        super.loadInt(uniformLocations.get("wetSandTexture"), 3);
        super.loadInt(uniformLocations.get("sandTexture"), 4);
        super.loadInt(uniformLocations.get("blendMap"), 5);
    }

    /**
     * Load ClipPlane vector to shader.
     * All vertices of entities that are on the opposite site the normal vector of the clip plane is
     * facing, will not be rendered.
     *
     * @param plane the vector for the clip plane
     */
    public void loadClipPlane(Vector4f plane) {
        super.loadVector(uniformLocations.get("plane"), plane);
    }

    /**
     * Load the color of the sky as rgb (all values between 0 and 1) color into the uniform variable.
     *
     * @param r red value
     * @param g green value
     * @param b blue value
     */
    public void loadSkyColor(float r, float g, float b) {
        super.loadVector(uniformLocations.get("skyColor"), new Vector3f(r, g, b));
    }

    /**
     * Loads the values specifying the terrains reflectivity to their uniform variables.
     *
     * @param damper       How much the reflected light spreads.
     * @param reflectivity How much light should get reflected.
     */
    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(uniformLocations.get("shineDamper"), damper);
        super.loadFloat(uniformLocations.get("reflectivity"), reflectivity);
    }

    /**
     * Loads the position and color of the light into the uniform variables.
     *
     * @param light The light the scene should use.
     */
    public void loadLight(Light light) {
        super.loadVector(uniformLocations.get("lightPosition"), light.getPosition());
        super.loadVector(uniformLocations.get("lightColor"), light.getColor());
    }

    /**
     * Loads the transformation Matrix of the currently rendered terrain to the uniform variable.
     *
     * @param matrix transformation matrix of the terrain, containing position, rotation and scale.
     */
    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(uniformLocations.get("transformationMatrix"), matrix);
    }

    /**
     * Loads the current view matrix into the uniform variable.
     *
     * @param camera Camera that the scene is viewed through, containing viewMatrix
     *               that manipulates entities, so it looks like the scene is viewed from the camera.
     */
    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(uniformLocations.get("viewMatrix"), viewMatrix);
    }

    /**
     * Loads the current projection Matrix.
     *
     * @param projection current projection Matrix, only changes if window is resized.
     */
    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(uniformLocations.get("projectionMatrix"), projection);
    }
}
