package com.battleships.gui.shaders;

import com.battleships.gui.entities.Camera;
import com.battleships.gui.entities.Light;
import com.battleships.gui.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class TerrainShader extends ShaderProgram {

    private static final Vector3f UP_VECTOR = new Vector3f(0, 1, 0); //Vector looking straight up

    private static final String VERTEX_FILE = "/com/battleships/gui/shaders/TerrainVertexShader.glsl";
    private static final String FRAGMENT_FILE = "/com/battleships/gui/shaders/TerrainFragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition;
    private int location_lightColor;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_skyColor;
    private Map<String, Integer> uniformLocations;


    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        String[] uniformNames = {"transformationMatrix", "projectionMatrix", "viewMatrix", "lightPosition", "lightColor", "shineDamper",
                "reflectivity", "skyColor", "pathTexture", "gravelTexture", "grassTexture", "wetSandTexture", "sandTexture", "blendMap", "plane"};
        uniformLocations = new HashMap<>();
        for(String s : uniformNames){
            uniformLocations.put(s, super.getUniformLocation(s));
        }
//        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
//        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
//        location_viewMatrix = super.getUniformLocation("viewMatrix");
//        location_lightPosition = super.getUniformLocation("lightPosition");
//        location_lightColor = super.getUniformLocation("lightColor");
//        location_shineDamper = super.getUniformLocation("shineDamper");
//        location_reflectivity = super.getUniformLocation("reflectivity");
//        location_skyColor = super.getUniformLocation("skyColor");
    }

    public void connectTextureUnits(){
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
     * @param plane - the vector for the clip plane
     */
    public void loadClipPlane(Vector4f plane){
        super.loadVector(uniformLocations.get("plane"), plane);
    }

    public void loadSkyColor(float r, float g, float b){
        super.loadVector(uniformLocations.get("skyColor"), new Vector3f(r,g,b));
    }

    public void loadShineVariables(float damper, float reflectivity){
        super.loadFloat(uniformLocations.get("shineDamper"), damper);
        super.loadFloat(uniformLocations.get("reflectivity"), reflectivity);
    }

    //load light Position and Color vectors into shader uniforms
    public void loadLight(Light light){
        super.loadVector(uniformLocations.get("lightPosition"), light.getPosition());
        super.loadVector(uniformLocations.get("lightColor"), light.getColor());
    }

    //load matrix needed to process scaling, moving or rotating a model
    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(uniformLocations.get("transformationMatrix"), matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(uniformLocations.get("viewMatrix"), viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(uniformLocations.get("projectionMatrix"), projection);
    }
}
