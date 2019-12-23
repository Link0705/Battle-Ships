package com.battleships.gui.gameAssets.MainMenuGui;

import com.battleships.gui.fontMeshCreator.GUIText;
import com.battleships.gui.fontRendering.TextMaster;
import com.battleships.gui.guis.GuiManager;
import com.battleships.gui.guis.GuiTexture;
import com.battleships.gui.guis.Slider;
import com.battleships.gui.main.Inits;
import com.battleships.gui.renderingEngine.Loader;
import org.joml.Vector2f;

public class InGameSettingsMenu extends Menu {
    protected int gameMode; //0 Singleplayer, 1 Multiplayer;

    protected Slider playingFieldSize;

    protected Slider difficulty;

    protected Vector2f sliderSize = new Vector2f(0.2f, 0.01f);

    public Slider getPlayingFieldSize() {
        return playingFieldSize;
    }
    public Slider getDifficulty() {
        return difficulty;
    }

    public InGameSettingsMenu(GuiManager guiManager, Loader loader,int gameMode) {
        super(guiManager, loader);

        this.gameMode = gameMode;

        guiManager.clearClickableGuis();

        TextMaster.clear();

        this.createMenu();

        SetTextColor();

        CreateTextLabels();
    }
    public void RefreshSliderValue(){
        String difficultyName = "";

        super.guiTexts.get(1).remove();
        super.guiTexts.get(1).setTextString(String.format("%d", playingFieldSize.getValueAsInt()));


        switch (difficulty.getValueAsInt()){
            case 1: difficultyName = "Easy";
                    break;
            case 2: difficultyName = "Normal";
                    break;
            case 3: difficultyName = "Hard";
                    break;
        }
        super.guiTexts.get(3).remove();
        super.guiTexts.get(3).setTextString(difficultyName);
        TextMaster.loadText(super.guiTexts.get(1));
        TextMaster.loadText(super.guiTexts.get(3));
    }


    protected void createMenu() {

        playingFieldSize = new Slider(loader.loadTexture("Brick.jpg"), loader.loadTexture("Brick.jpg"), 5, 30,
                15, sliderSize, super.standardButtonPos, guiManager, Inits.getPermanentGuiElements());

        super.guiTexts.add(new GUIText("Size",2.5f, font,new Vector2f(playingFieldSize.getPositions().x-0.14f,playingFieldSize.getPositions().y) , 0.12f, true, outlineColor,0.0f, 0.1f,outlineColor, new Vector2f()));

        difficulty = new Slider(loader.loadTexture("Brick.jpg"), loader.loadTexture("Brick.jpg"), 1, 3,
                2, sliderSize,new Vector2f(playingFieldSize.getPositions().x,playingFieldSize.getPositions().y+buttonGap), guiManager, Inits.getPermanentGuiElements());

        super.guiTexts.add(new GUIText(String.format("%d", playingFieldSize.getValueAsInt()),2.5f, font, new Vector2f(playingFieldSize.getPositions().x+0.16f,playingFieldSize.getPositions().y), 0.12f, true, outlineColor,0.0f, 0.1f,outlineColor, new Vector2f()));


        super.guiTexts.add(new GUIText("Difficulty",2.5f, font, new Vector2f(difficulty.getPositions().x-0.165f,difficulty.getPositions().y), 0.12f, true, outlineColor,0.0f, 0.1f,outlineColor, new Vector2f()));

        super.guiTexts.add(new GUIText("Normal",2.5f, font, new Vector2f(difficulty.getPositions().x+0.16f,difficulty.getPositions().y), 0.12f, true, outlineColor,0.0f, 0.1f,outlineColor, new Vector2f()));

        buttons.add(new GuiTexture(texture,new Vector2f(difficulty.getPositions().x,difficulty.getPositions().y+buttonGap),buttonSize));

        super.guiTexts.add(new GUIText("Fight",2.5f, font, new Vector2f(buttons.get(0).getPositions().x,buttons.get(0).getPositions().y), 0.12f, true, outlineColor,0.0f, 0.1f,outlineColor, new Vector2f()));

        buttons.add(new GuiTexture(texture,new Vector2f(buttons.get(0).getPositions().x,buttons.get(0).getPositions().y+buttonGap),buttonSize));

        super.guiTexts.add(new GUIText("Back",2.5f, font, new Vector2f(buttons.get(1).getPositions().x,buttons.get(1).getPositions().y), 0.12f, true, outlineColor,0.0f, 0.1f,outlineColor, new Vector2f()));

        super.createClickable();

    }
    protected boolean isClickOnGui(GuiTexture gui, double x, double y) {
        if(super.isClickOnGui(super.buttons.get(0), x, y)) {
            super.buttonClicked = 0;
            return true;
        }
        if(super.isClickOnGui(super.buttons.get(1), x, y)) {
            super.buttonClicked = 1;
            return true;
        }
        return false;
    }
    @Override
    protected void clickAction() {
        if(gameMode == 0){
            if (super.buttonClicked == 0){
                playingFieldSize.remove();
                difficulty.remove();
                Inits.setGlobalGameState(1);
                //TODO set difficulty and size for offline game(need logic for that)
            }
            if (super.buttonClicked == 1){
                playingFieldSize.remove();
                difficulty.remove();
                Inits.setStartMenu(new PlayMenu(super.guiManager,super.loader));
            }
        }
        else if(gameMode == 1){
            if (super.buttonClicked == 0){
                playingFieldSize.remove();
                difficulty.remove();
                Inits.setGlobalGameState(1);
                //TODO set difficulty and size for offline game(need logic for that)
                //TODO add Connection overlay
            }
            if (super.buttonClicked == 1){
                playingFieldSize.remove();
                difficulty.remove();
                Inits.setStartMenu(new MultiplayerMenu(super.guiManager,super.loader));
            }
        }
    }

}
