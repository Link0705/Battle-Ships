package com.battleships.gui.engine.shaders;

import com.battleships.gui.engine.entities.Camera;
import com.battleships.gui.engine.entities.Light;
import com.battleships.gui.engine.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class StaticShader extends ShaderProgram {

    private static final Vector3f UP_VECTOR = new Vector3f(0, 1, 0); //Vector looking straight up

    private static final String VERTEX_FILE = "/com/battleships/gui/engine/shaders/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "/com/battleships/gui/engine/shaders/fragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition;
    private int location_lightColor;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_useFakeLighting;
    private int location_skyColor;
    private Map<String, Integer> uniformLocations;


    public StaticShader() {
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
                "reflectivity","useFakeLighting", "skyColor"};
        uniformLocations = new HashMap<>();
//        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
//        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
//        location_viewMatrix = super.getUniformLocation("viewMatrix");
//        location_lightPosition = super.getUniformLocation("lightPosition");
//        location_lightColor = super.getUniformLocation("lightColor");
//        location_shineDamper = super.getUniformLocation("shineDamper");
//        location_reflectivity = super.getUniformLocation("reflectivity");
//        location_useFakeLighting = super.getUniformLocation("useFakeLighting");
//        location_skyColor = super.getUniformLocation("skyColor");
        for(String s : uniformNames){
            uniformLocations.put(s, super.getUniformLocation(s));
        }
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
//        Matrix4f viewMatrix = new Matrix4f();
//        Vector3f position = new Vector3f(0,0,0);
//        Vector3f direction = new Vector3f(0,0,0);
//        Vector3f rotation = new Vector3f(0,0,0);
//        double pitch = Math.toRadians(rotation.x);
//        double yaw = Math.toRadians(rotation.y);
//        //don't convert z rotation, because it's not used as it's close to the same as x rotation
//
//        //convert rotations into direction vectors
//        direction.x = (float) (Math.cos(pitch) * Math.sin(yaw));
//        direction.y = (float) Math.sin(pitch);
//        direction.z = (float) (Math.cos(pitch) * Math.cos(yaw));
//        Vector3f target = new Vector3f(0,0,0);
//        viewMatrix.setLookAt(new Vector3f (0,0,0), position.add(direction, target), UP_VECTOR);
//        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(uniformLocations.get("projectionMatrix"), projection);
    }

    public void loadFakeLightingVariable(boolean useFake){
        super.loadBoolean(uniformLocations.get("useFakeLighting"), useFake);
    }

}
