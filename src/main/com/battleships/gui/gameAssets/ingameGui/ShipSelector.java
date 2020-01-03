package com.battleships.gui.gameAssets.ingameGui;

import com.battleships.gui.fontMeshCreator.GUIText;
import com.battleships.gui.fontRendering.TextMaster;
import com.battleships.gui.gameAssets.GameManager;
import com.battleships.gui.gameAssets.grids.ShipManager;
import com.battleships.gui.gameAssets.testLogic.TestLogic;
import com.battleships.gui.guis.GuiClickCallback;
import com.battleships.gui.guis.GuiManager;
import com.battleships.gui.guis.GuiTexture;
import com.battleships.gui.renderingEngine.Loader;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Overlay during the ship placing phase of the game. Contains all {@link GuiTexture}s and their functions needed for this UI.
 *
 * @author Tim Staudenmaier
 */
public class ShipSelector extends GuiClickCallback {

    /**
     * Color of all {@link GUIText}s on this UI.
     */
    private static final Vector3f BLACK = new Vector3f();
    /**
     * Color of the outline of all {@link GUIText}s oon this UI.
     */
    private static final Vector2f OUTLINEOFFSET = new Vector2f();

    /**
     * Array containing the GuiTextures for all clickable elements of this UI.
     */
    private GuiTexture[] buttons = new GuiTexture[4];
    /**
     * Gui of the background behind the clickable buttons.
     */
    private GuiTexture background;
    /**
     * List containing all texts on this UI.
     */
    private List<GUIText> shipCountTexts = new ArrayList<>();
    /**
     * Array containing how many ships of each size are left to place.
     */
    private int[] shipCounts;
    /**
     * Used to determine which button was the last one that was clicked.
     * ButtonNumbers correspond to ship sizes.
     */
    private int buttonNumber;
    /**
     * ShipManager that handles the placing of the selected ships.
     */
    private ShipManager shipManager;

    /**
     * {@link GuiManager} handling the click functions of this UI.
     */
    private GuiManager guiManager;
    /**
     * List of guis these two symbols should get added to, this list needs to be passed to
     * a {@link com.battleships.gui.guis.GuiRenderer} for these symbols to appear on screen.
     */
    private List<GuiTexture> guis;

    /**
     * Create the gui used for ship selecting.
     * @param loader Loader needed to load textures.
     * @param guiManager GuiManager needed to link click functions to the gui elements.
     * @param shipManager ShipManager of the current game, needed for the click functions.
     * @param guis List of guis this gui should be saved to, this list needs to be rendered later to show this gui
     *             on screen.
     */
    public ShipSelector(Loader loader, GuiManager guiManager, ShipManager shipManager, List<GuiTexture> guis) {
        this.guiManager = guiManager;
        this.guis = guis;
        this.shipManager = shipManager;
        shipManager.setShipSelector(this);
        background = new GuiTexture(loader.loadTexture("IngameGuiShipSelectBackground.png"), new Vector2f(0.5f, 0));
        float space = 0.053125f;
        background.getPositions().y = 1 - background.getScale().y / 2;
        GuiTexture ship1 = new GuiTexture(loader.loadTexture("IngameGuiShipSelectShip1.png"), new Vector2f(0,background.getPositions().y - 0.0204f));
        ship1.getPositions().x = background.getPositions().x - 1.5f * space - 1.5f * ship1.getScale().x;
        GuiTexture ship2 = new GuiTexture(loader.loadTexture("IngameGuiShipSelectShip2.png"), new Vector2f(0,background.getPositions().y - 0.0204f));
        ship2.getPositions().x = background.getPositions().x - 0.5f * space - 0.5f * ship2.getScale().x;
        GuiTexture ship3 = new GuiTexture(loader.loadTexture("IngameGuiShipSelectShip3.png"), new Vector2f(0,background.getPositions().y - 0.0204f));
        ship3.getPositions().x = background.getPositions().x + 0.5f * space + 0.5f * ship3.getScale().x;
        GuiTexture ship4 = new GuiTexture(loader.loadTexture("IngameGuiShipSelectShip4.png"), new Vector2f(0,background.getPositions().y - 0.0204f));
        ship4.getPositions().x = background.getPositions().x + 1.5f * space + 1.5f * ship4.getScale().x;
        guis.add(background);
        guis.add(ship1);
        guis.add(ship2);
        guis.add(ship3);
        guis.add(ship4);
        buttons[0] = ship1;
        buttons[1] = ship2;
        buttons[2] = ship3;
        buttons[3] = ship4;
        guiManager.createClickableGui(ship1, () -> this);
        guiManager.createClickableGui(ship2, () -> this);
        guiManager.createClickableGui(ship3, () -> this);
        guiManager.createClickableGui(ship4, () -> this);

        shipCounts = TestLogic.getShipAmounts(shipManager.getGridSize());
        if(shipCounts == null){
            System.err.println("Something went wrong calculating the ship amounts!");
            return;
        }
        for(int i = 0; i < 4; i++)
            shipCountTexts.add(new GUIText(shipCounts[i] + " Left", 2, GameManager.getPirateFont(), new Vector2f(buttons[i].getPositions().x, buttons[i].getPositions().y + buttons[i].getScale().y / 2 + 0.05f), buttons[i].getScale().x, true, BLACK, 0, 0.1f,BLACK, OUTLINEOFFSET));
}

    /**
     * Function that gets called to test if a click was on this gui.
     * This gui has 4 individual buttons, so depending on which button was pressed, the buttonNumber
     * attribute gets set to the value for that button, so the clickAction() method knows, which button was clicked.
     * @param gui not used in this override, because the gui textures are saved in buttons[] array, so should be null.
     * @param x x position of the click in screen coordinates.
     * @param y y position of the click in screen coordinates.
     * @return {@code true} if the click was on one of the buttons of this gui, {@code false} else.
     */
    @Override
    protected boolean isClickOnGui(GuiTexture gui, double x, double y){
        for(int i = 0; i < buttons.length; i++) {
            if (buttons[i].getPositions().x - 0.5f * buttons[i].getScale().x <= x && buttons[i].getPositions().x + 0.5f *
                    buttons[i].getScale().x >= x && buttons[i].getPositions().y - 0.5f * buttons[i].getScale().y <= y &&
                    buttons[i].getPositions().y + 0.5f * buttons[i].getScale().y >= y) {
                this.buttonNumber = i + 2;
                return true;
            }
        }
        return false;
    }

    /**
     * Gets called if one of the 4 buttons on this gui was clicked.
     * Adds a ship to the cursor and decrements the amount of ships left for that size.
     * The size of the ship changes from 2 5 depending on which button was clicked.
     */
    @Override
    protected void clickAction(){
        if(shipCounts[buttonNumber - 2] > 0) {
            shipManager.stickShipToCursor(buttonNumber);
        }
    }

    /**
     * Change the text under the button for that ship size, so it shows that you can place one
     * ship less of that size than before.
     * @param shipSize Size of the ship for that the count should get decremented.
     */
    public void decrementCount(int shipSize){
        GUIText dummy = shipCountTexts.get(shipSize - 2);
        dummy.remove();
        shipCounts[shipSize - 2]--;
        dummy.setTextString(shipCounts[shipSize - 2] + " Left");
        //TextMaster.loadText(new GUIText(shipCounts[0] + " Left", 2, dummy.getFont(), dummy.getPosition(), dummy.getLineMaxSize(), true, 0, 0.1f,BLACK, OUTLINEOFFSET));
        TextMaster.loadText(dummy);
    }

    /**
     * Removes the gui elements of the ship selector.
     */
    public void remove(){
        guis.remove(background);
        for(GuiTexture t : buttons){
            guis.remove(t);
            guiManager.removeClickableGui(t);
        }
        for(GUIText t : shipCountTexts)
            t.remove();
    }
}
