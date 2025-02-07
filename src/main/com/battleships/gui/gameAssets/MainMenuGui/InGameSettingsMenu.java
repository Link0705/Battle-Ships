package com.battleships.gui.gameAssets.MainMenuGui;

import com.battleships.gui.fontMeshCreator.GUIText;
import com.battleships.gui.fontRendering.TextMaster;
import com.battleships.gui.gameAssets.GameManager;
import com.battleships.gui.guis.GuiManager;
import com.battleships.gui.guis.GuiTexture;
import com.battleships.gui.guis.Slider;
import com.battleships.gui.renderingEngine.Loader;
import org.joml.Vector2f;

/**
 * Contains all functions needed for an settings menu
 *
 * @author Sascha Mößle
 */
public class InGameSettingsMenu extends Menu {
    /**
     * Constant value for Easy Ai
     */
    protected static final int EASY = 0;
    /**
     * Constant value for Medium Ai
     */
    protected static final int MEDIUM = 1;
    /**
     * Constant value for Hard Ai
     */
    protected static final int HARD = 2;
    /**
     * Constant value for start button
     */
    private static final int START = 0;
    /**
     * Constant value for back button
     */
    private static final int BACK = 1;
    /**
     * Constant value for singleplayer button
     */
    private static final int SP = 0;
    /**
     * Constant value for multiplayer button
     */
    private static final int MP = 1;
    /**
     * Constant value for AivsAi button
     */
    private static final int AIVSAI = 2;
    /**
     * The offset used too set the {@link GUIText} above the {@link Slider}
     */
    private final static float sliderOffset = 0.04f;
    /**
     * Indicates if the settings are for Singleplayer, Multiplayer or Ai VS Ai
     */
    protected int gameMode; //0 Singleplayer, 1 Multiplayer, 2 Ai VS Ai;

    /**
     * {@link Slider} for the playing field size
     */
    protected Slider playingFieldSize;
    /**
     * {@link Slider} for the ai difficulty
     */
    protected Slider difficulty1;
    /**
     * {@link Slider} for the second ai difficulty only needed if game mode Ai VS Ai is chosen
     */
    protected Slider difficulty2; //needed for Ai VS Ai to remove the slider afterwords

    /**
     * Size for the {@link Slider}
     */
    protected Vector2f sliderSize = new Vector2f(0.2f, 0.01f);

    /**
     * Creates the {@link Slider}, adds and changes the color of the {@link GUIText}as labels and adds the {@link GuiTexture} for the buttons.
     *
     * @param guiManager GuiManager that should handle the click function of these guis.
     * @param loader     Loader needed to load textures
     * @param gameMode   needed to indicate what game mode it is.
     */
    public InGameSettingsMenu(GuiManager guiManager, Loader loader, int gameMode) {
        super(guiManager, loader);

        this.gameMode = gameMode;

        this.createMenu();

        CreateTextLabels();

    }

    /**
     * Refreshes the {@link GUIText} that show the Value of the {@link Slider} in the current Menu
     */
    public void RefreshSliderValue() {
        String difficultyName = "";

        super.guiTexts.get(0).remove();
        super.guiTexts.get(0).setTextString("Size: " + playingFieldSize.getValueAsInt());
        if (gameMode == SP || gameMode == AIVSAI) {
            switch (difficulty1.getValueAsInt()) {
                case EASY:
                    difficultyName = "Easy";
                    break;
                case MEDIUM:
                    difficultyName = "Medium";
                    break;
                case HARD:
                    difficultyName = "Hard";
                    break;
            }
            super.guiTexts.get(1).remove();
            super.guiTexts.get(1).setTextString("Difficulty: " + difficultyName);
            TextMaster.loadText(super.guiTexts.get(1));
        }
        TextMaster.loadText(super.guiTexts.get(0));

    }

    /**
     * Indicates if one of the {@link Slider} is moving
     *
     * @return {@code true} if one of the sliders is moving {@code false} else
     */
    public boolean isRunning() {
        if (gameMode == SP)
            return (playingFieldSize.isRunning() || difficulty1.isRunning());
        else if (gameMode == MP)
            return (playingFieldSize.isRunning());
        else
            return (playingFieldSize.isRunning() || difficulty1.isRunning() || difficulty2.isRunning());
    }

    /**
     * Creates the {@link Slider}, adds {@link GUIText}as labels and adds the {@link GuiTexture} for the buttons.
     */
    protected void createMenu() {
        if (gameMode == MP)
            playingFieldSize = new Slider(loader.loadTexture("Slider.png"), loader.loadTexture("WoodenSlider.jpg"), 5, 30,
                    15, sliderSize, new Vector2f(standardButtonPos.x, standardButtonPos.y + buttonGap / 2), guiManager, GameManager.getGuis());

        else
            playingFieldSize = new Slider(loader.loadTexture("Slider.png"), loader.loadTexture("WoodenSlider.jpg"), 5, 30,
                    15, sliderSize, super.standardButtonPos, guiManager, GameManager.getGuis());

        super.guiTexts.add(new GUIText("Size: " + playingFieldSize.getValueAsInt(), fontSize, font, new Vector2f(playingFieldSize.getPositions().x, playingFieldSize.getPositions().y - sliderOffset), 0.12f, true, outlineColor, 0.0f, 0.1f, outlineColor, new Vector2f()));


        if (gameMode == SP) {
            difficulty1 = new Slider(loader.loadTexture("Slider.png"), loader.loadTexture("WoodenSlider.jpg"), EASY, HARD,
                    MEDIUM, sliderSize, new Vector2f(playingFieldSize.getPositions().x, playingFieldSize.getPositions().y + buttonGap), guiManager, GameManager.getGuis());
            super.guiTexts.add(new GUIText("Difficulty: Medium", fontSize, font, new Vector2f(difficulty1.getPositions().x, difficulty1.getPositions().y - sliderOffset), 0.4f, true, outlineColor, 0.0f, 0.1f, outlineColor, new Vector2f()));
        }

        buttons.add(new GuiTexture(buttonTexture, new Vector2f(standardButtonPos.x, standardButtonPos.y + 2 * buttonGap), buttonSize));
        buttons.add(new GuiTexture(buttonTexture, new Vector2f(buttons.get(0).getPositions().x, buttons.get(0).getPositions().y + buttonGap), buttonSize));
        GameManager.getGuis().addAll(buttons);

        super.guiTexts.add(new GUIText("Play", fontSize, font, new Vector2f(buttons.get(0).getPositions().x, buttons.get(0).getPositions().y), 0.12f, true, outlineColor, 0.0f, 0.1f, outlineColor, new Vector2f()));
        super.guiTexts.add(new GUIText("Back", fontSize, font, new Vector2f(buttons.get(1).getPositions().x, buttons.get(1).getPositions().y), 0.12f, true, outlineColor, 0.0f, 0.1f, outlineColor, new Vector2f()));

        super.createClickable();

    }

    /**
     * Toggles state of clicked button.
     */
    @Override
    protected void clickAction() {
        if (super.buttonClicked == START) {
            super.cleaBackgournd();
            super.clearMenu();
            GameManager.getSettings().setSize(playingFieldSize.getValueAsInt());
            GameManager.resizeGrid();
            playingFieldSize.remove();

            if (gameMode == SP || gameMode == AIVSAI) {
                GameManager.getSettings().setAiLevelO(difficulty1.getValueAsInt());
                difficulty1.remove();
                if (gameMode == AIVSAI) {
                    ESCMenu.setIsPlayerAI(true);
                    GameManager.getSettings().setAiLevelP(difficulty2.getValueAsInt());
                    difficulty2.remove();
                }
                GameManager.getLogic().advanceGamePhase();
            } else {
                GameManager.getSettings().setOnline(true);
                GameManager.getNetwork().start(true, null);
                MainMenuManager.setMenu(new WaitingConnection(guiManager, loader, null));
            }
        }
        if (super.buttonClicked == BACK) {
            super.clearMenu();
            playingFieldSize.remove();
            if (difficulty1 != null)
                difficulty1.remove();
            if (difficulty2 != null)
                difficulty2.remove();
            if (gameMode == SP || gameMode == AIVSAI)
                MainMenuManager.setMenu(new PlayMenu(super.guiManager, super.loader));
            else
                MainMenuManager.setMenu(new MultiplayerMenu(super.guiManager, super.loader));
        }
    }
}
