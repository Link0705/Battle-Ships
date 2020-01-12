package com.battleships.gui.gameAssets.MainMenuGui;

import com.battleships.gui.fontMeshCreator.GUIText;
import com.battleships.gui.gameAssets.GameManager;
import com.battleships.gui.guis.GuiManager;
import com.battleships.gui.guis.GuiTexture;
import com.battleships.gui.renderingEngine.Loader;
import com.battleships.logic.SaveFileManager;
import org.joml.Vector2f;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

/**
 * Overlay if ESC was pressed in the game
 *
 * @author Sascha Mößle
 */
public class ESCMenu extends Menu {
    /**
     * Constant value for save button
     */
    private static final int SAVE = 0;
    /**
     * Constant value for resume button
     */
    private static final int RESUME = 1;
    /**
     * Constant value for back button
     */
    private static final int PLAYAI = 2;
    /**
     * Constant value for exit button
     */
    private static final int EXIT = 3;
    private static boolean active;

    /**
     * Creates the menu when you press ESC, sets the color of the {@link GUIText} and creates the {@link GUIText} on the Buttons.
     * @param guiManager GuiManager that should handle the click function of these guis.
     * @param loader Loader needed to load textures
     */
    public ESCMenu(GuiManager guiManager, Loader loader) {
        super(guiManager, loader);

        active = true;

        super.addBackground();

        this.createMenu();

        CreateTextLabels();

    }

    /**
     * Creates {@link GUIText}as labels and adds the {@link GuiTexture} for the buttons.
     */
    private void createMenu(){

        super.CreateButtonTextures(4);

        GameManager.getGuis().addAll(buttons);



        super.guiTexts.add(new GUIText("Save", fontSize, font, new Vector2f(buttons.get(0).getPositions().x,buttons.get(0).getPositions().y), 0.12f, true,outlineColor, 0.0f, 0.1f,outlineColor, new Vector2f()));
        super.guiTexts.add(new GUIText("Resume", fontSize, font,new Vector2f(buttons.get(1).getPositions().x,buttons.get(1).getPositions().y), 0.12f, true,outlineColor, 0.0f, 0.1f,outlineColor, new Vector2f()));
        super.guiTexts.add(new GUIText("Play as AI", fontSize, font,new Vector2f(buttons.get(2).getPositions().x,buttons.get(2).getPositions().y), 0.12f, true,outlineColor, 0.0f, 0.1f,outlineColor, new Vector2f()));
        super.guiTexts.add(new GUIText("Exit", fontSize, font,new Vector2f(buttons.get(3).getPositions().x,buttons.get(3).getPositions().y), 0.12f, true,outlineColor, 0.0f, 0.1f,outlineColor, new Vector2f()));

        super.createClickable();
    }


    /**
     * Tests if the click was on one of the {@link GuiTexture} in the menu
     * @param gui The gui to test for if the click was on it.
     * @param x xPos of the click (left of screen = 0, right of screen = 1)
     * @param y yPos of the click (top of screen = 0, bottom of screen = 1)
     * @return {@code true} if the click was on one of the button textures, {@code false} else.
     */
    @Override
    protected boolean isClickOnGui(GuiTexture gui, double x, double y) {
        if(super.isClickOnGui(super.buttons.get(0), x, y)) {
            super.buttonClicked = 0;
            return true;
        }
        if(super.isClickOnGui(super.buttons.get(1), x, y)) {
            super.buttonClicked = 1;
            return true;
        }
        if(super.isClickOnGui(super.buttons.get(2), x, y)) {
            super.buttonClicked = 2;
            return true;
        }
        if(super.isClickOnGui(super.buttons.get(3), x, y)) {
            super.buttonClicked = 3;
            return true;
        }
        return false;
    }

    public static boolean isActive() {
        return active;
    }
    public void ClearESCMenu(){
        active = false;
        clearMenu();
        cleaBackgournd();
    }

    public static void setActive(boolean active) {
        ESCMenu.active = active;
    }

    /**
     * Toggles state of clicked button.
     */
    @Override
    protected void clickAction() {
        if(buttonClicked == SAVE) {
            long time = System.currentTimeMillis();
            if (!GameManager.getSettings().isOnline()) {
                String filename = TinyFileDialogs.tinyfd_inputBox("Save", "Enter file name", "");
                if(SaveFileManager.saveToFile(filename))
                    GameManager.getMainMenuManager().backToMainMenu();
                else
                    super.guiTexts.add(new GUIText("Error saving file", fontSize, font,new Vector2f(), 0.12f, true,outlineColor, 0.0f, 0.1f,outlineColor, new Vector2f()));
            }
            else{
                if (SaveFileManager.saveToFile(String.valueOf(time))){
                    GameManager.getNetwork().closeConnection();
                    //GameManager.getNetwork().sendSave(time);
                    GameManager.getMainMenuManager().backToMainMenu();
                }
                else
                    super.guiTexts.add(new GUIText("Error saving file", fontSize, font,new Vector2f(), 0.12f, true,outlineColor, 0.0f, 0.1f,outlineColor, new Vector2f()));
            }

        }
        if(buttonClicked == RESUME){
            active=false;
            super.clearMenu();
            super.cleaBackgournd();
        }
        if (super.buttonClicked == PLAYAI){
            super.clearMenu();
            new AiPlayerChooserMenu(guiManager,loader);
        }
        if(buttonClicked == EXIT){
            active=false;
            super.clearMenu();
            if (GameManager.getSettings().isOnline())
                GameManager.getNetwork().closeConnection();
            GameManager.getLogic().setGameState(GameManager.MENU);
            GameManager.getMainMenuManager().backToMainMenu();
        }
    }

}
