package com.battleships.gui.postProcessing.gaussianBlur;

import com.battleships.gui.postProcessing.ImageRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class VerticalBlur {

    private ImageRenderer renderer;
    private VerticalBlurShader shader;

    /**
     * Create new VerticalBlur post processing effect.
     * @param targetFboWidth - width of the fbo this effect should affect
     * @param targetFboHeight - height of the fbo this effect should affect
     */
    public VerticalBlur(int targetFboWidth, int targetFboHeight){
        shader = new VerticalBlurShader();
        renderer = new ImageRenderer(targetFboWidth, targetFboHeight);
        shader.start();
        shader.loadTargetHeight(targetFboHeight);
        shader.stop();
    }

    /**
     * Render the vertical blur.
     * @param texture - the texture of the current scene (fbo)
     */
    public void render(int texture){
        shader.start();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        renderer.renderQuad();
        shader.stop();
    }

    /**
     * @return the texture of the current scene after the effect was applied.
     */
    public int getOutputTexture(){
        return renderer.getOutputTexture();
    }

    /**
     * Clean up on program exit.
     */
    public void cleanUp(){
        renderer.cleanUp();
        shader.cleanUp();
    }
}
