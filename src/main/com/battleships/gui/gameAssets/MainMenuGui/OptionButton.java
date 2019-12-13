package com.battleships.gui.gameAssets.MainMenuGui;

import com.battleships.gui.fontMeshCreator.GUIText;
import com.battleships.gui.fontRendering.TextMaster;
import com.battleships.gui.guis.GuiManager;
import com.battleships.gui.guis.GuiTexture;
import com.battleships.gui.renderingEngine.Loader;
import org.joml.Vector2f;

public class OptionButton extends MainMenuButton {
    private BackButton backButton;

    private GuiTexture back;

    //TODO add options
    public OptionButton(GuiManager guiManager, Loader loader){
        super(guiManager,loader);

        this.creatButtons();

        this.creatLabels();
    }
    @Override
    protected void clickAction() {
        guiManager.clearClickableGuis();

        guiManager.createClickableGui(back, () -> backButton);

        TextMaster.clear();
        super.CreateTextLabels();
    }
    private void creatButtons(){
        backButton = new BackButton(guiManager,loader,0);

        back = new GuiTexture(texture,new Vector2f(standardButtonPos.x,standardButtonPos.y+2*buttonGap),buttonSize);
    }
    private void creatLabels(){
        super.guiTexts.add(new GUIText("Back", 3, font,new Vector2f(back.getPositions().x,back.getPositions().y), 0.12f, true, 0.0f, 0.1f,outlineColor, new Vector2f()));
    }
}
