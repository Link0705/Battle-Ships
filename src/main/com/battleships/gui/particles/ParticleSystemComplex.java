package com.battleships.gui.particles;

import com.battleships.gui.window.WindowManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Random;

/**
 * Complex particle system with more options than {@link ParticleSystemSimple} to create particle effects from particles.
 *
 * @author Tim Staudenmaier
 */
public class ParticleSystemComplex {

    /**
     * pps              - Particles emitted per second.
     * averageSpeed     - Average Speed each particle gets emitted at.
     * gravityComplient - How much emitted particles are affected by gravity (negative for inverted gravity)
     * averageLifeLength- How long in average the particles are alive.
     * averageScale     - Average scale of the emitted particles.
     */
    private float pps, averageSpeed, gravityComplient, averageLifeLength, averageScale;

    /**
     * speedError       - How much the speed can differ from the averageSpeed.
     * lifeError        - How much the lifeLength can differ from the averageLifeLength.
     * scaleError       - How much the scale can differ from the averageScale.
     *
     * Use 0 as value if the values shouldn't differ from the average.
     */
    private float speedError, lifeError, scaleError = 0;
    /**
     * {@code true} if the particles should be rotated randomly.
     */
    private boolean randomRotation = false;
    /**
     * In which direction the particles should be emitted from the position of this system.
     */
    private Vector3f direction;
    /**
     * How much the direction the particles are emitted can differ from the original direction.
     */
    private float directionDeviation = 0;

    /**
     * Texture all particles of this system use.
     */
    private ParticleTexture texture;

    /**
     * Random number generator.
     */
    private Random random = new Random();

    /**
     * Create a new particleSystem.
     * @param texture - Texture all particles of this system should use.
     * @param pps - How many particles should be emitted per second.
     * @param speed - How fast the particles should be emitted on average.
     * @param gravityComplient - How much the particles are influenced by gravity.
     * @param lifeLength - How long the particles should live on average.
     * @param scale - How large the particles should be on average.
     */
    public ParticleSystemComplex(ParticleTexture texture, float pps, float speed, float gravityComplient, float lifeLength, float scale) {
        this.texture = texture;
        this.pps = pps;
        this.averageSpeed = speed;
        this.gravityComplient = gravityComplient;
        this.averageLifeLength = lifeLength;
        this.averageScale = scale;
    }

    /**
     * @param direction - The average direction in which particles are emitted.
     * @param deviation - A value between 0 and 1 indicating how far from the chosen direction particles can deviate.
     */
    public void setDirection(Vector3f direction, float deviation) {
        this.direction = new Vector3f(direction);
        this.directionDeviation = (float) (deviation * Math.PI);
    }

    /**
     * Randomize the rotation of all emitted particles.
     */
    public void randomizeRotation() {
        randomRotation = true;
    }

    /**
     * @param error - A number between 0 and 1, where 0 means no error margin.
     */
    public void setSpeedError(float error) {
        this.speedError = error * averageSpeed;
    }

    /**
     * @param error - A number between 0 and 1, where 0 means no error margin.
     */
    public void setLifeError(float error) {
        this.lifeError = error * averageLifeLength;
    }

    /**
     * @param error - A number between 0 and 1, where 0 means no error margin.
     */
    public void setScaleError(float error) {
        this.scaleError = error * averageScale;
    }

    /**
     * Generate particles using all the settings of this system.
     * @param systemCenter - Center from which the particles should be generated.
     */
    public void generateParticles(Vector3f systemCenter) {
        float delta = WindowManager.getDeltaTime();
        float particlesToCreate = pps * delta;
        int count = (int) Math.floor(particlesToCreate);
        float partialParticle = particlesToCreate % 1;
        for (int i = 0; i < count; i++) {
            emitParticle(systemCenter);
        }
        if (Math.random() < partialParticle) {
            emitParticle(systemCenter);
        }
    }

    /**
     * Emits one particle with the settings of this system.
     * @param center - Position the particle is emitted from.
     */
    private void emitParticle(Vector3f center) {
        Vector3f velocity;
        if(direction!=null){
            velocity = generateRandomUnitVectorWithinCone(direction, directionDeviation);
        }else{
            velocity = generateRandomUnitVector();
        }
        velocity.normalize();
        velocity.mul(generateValue(averageSpeed, speedError));
        float scale = generateValue(averageScale, scaleError);
        float lifeLength = generateValue(averageLifeLength, lifeError);
        new Particle(texture, new Vector3f(center), velocity, gravityComplient, lifeLength, generateRotation(), scale);
    }

    /**
     * Generate a value, where the value is determined by the average value and the specified errorMargin.
     * @param average - Average value
     * @param errorMargin - Error margin for this value
     * @return - A random value between (average - errorMargin) and (average + errorMargin).
     */
    private float generateValue(float average, float errorMargin) {
        float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
        return average + offset;
    }

    /**
     * @return - A random rotation value.
     */
    private float generateRotation() {
        if (randomRotation) {
            return random.nextFloat() * 360f;
        } else {
            return 0;
        }
    }

    /**
     * Generate a random direction at which a particle can be emitted.
     * This direction gets generated by using the general direction of the system and the deviation value, which means
     * the particles get emitted in a cone.
     * @param coneDirection - General direction of the system.
     * @param angle - Maximum allowed deviation angle from coneDirection.
     * @return - A random normalized vector within the cone.
     */
    private static Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, float angle) {
        float cosAngle = (float) Math.cos(angle);
        Random random = new Random();
        float theta = (float) (random.nextFloat() * 2f * Math.PI);
        float z = cosAngle + (random.nextFloat() * (1 - cosAngle));
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * Math.sin(theta));

        Vector4f direction = new Vector4f(x, y, z, 1);
        if (coneDirection.x != 0 || coneDirection.y != 0 || (coneDirection.z != 1 && coneDirection.z != -1)) {
            Vector3f rotateAxis = new Vector3f();
            coneDirection.cross(new Vector3f(0, 0, 1), rotateAxis);
            rotateAxis.normalize();
            float rotateAngle = (float) Math.acos(coneDirection.dot(new Vector3f(0, 0, 1)));
            Matrix4f rotationMatrix = new Matrix4f();
            rotationMatrix.rotate(-rotateAngle, rotateAxis);
            rotationMatrix.transform(direction, direction);
        } else if (coneDirection.z == -1) {
            direction.z *= -1;
        }
        return new Vector3f(direction.x, direction.y, direction.z);
    }

    /**
     * @return - A random normalized vector.
     */
    private Vector3f generateRandomUnitVector() {
        float theta = (float) (random.nextFloat() * 2f * Math.PI);
        float z = (random.nextFloat() * 2) - 1;
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * Math.sin(theta));
        return new Vector3f(x, y, z);
    }
}
